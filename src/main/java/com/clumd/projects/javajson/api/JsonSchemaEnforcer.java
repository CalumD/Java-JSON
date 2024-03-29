package com.clumd.projects.javajson.api;

import com.clumd.projects.javajson.core.JSType;
import com.clumd.projects.javajson.exceptions.SchemaException;
import com.clumd.projects.javajson.exceptions.json.KeyDifferentTypeException;
import com.clumd.projects.javajson.exceptions.json.KeyNotFoundException;
import com.clumd.projects.javajson.exceptions.schema.InvalidSchemaException;
import com.clumd.projects.javajson.exceptions.schema.SchemaViolationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Default implementation of {@link JsonSchemaEnforceable}, to verify whether a given JSON object satisfies a given JSON
 * schema.
 */
public final class JsonSchemaEnforcer implements JsonSchemaEnforceable {

    private enum SourceOfProblem {SCHEMA, OBJECT_TO_VALIDATE}

    private enum KeysRelevantTo {PROPERTIES, PATTERN_PROPERTIES, ADDITIONAL_PROPERTIES, ITEMS, ADDITIONAL_ITEMS}

    /**
     * A static equivalent of {@link #validateWithOutReasoning(Json, Json)}.
     *
     * @param objectToValidate The JSON object to be evaluated.
     * @param againstSchema    The JSON Schema to verify the object against.
     * @return True if the JSON object satisfies the Schema, False if there is any exception.
     */
    public static boolean validateStrict(Json objectToValidate, Json againstSchema) {
        return new JsonSchemaEnforcer().validateWithOutReasoning(objectToValidate, againstSchema);
    }

    /**
     * A static equivalent of {@link #validateWithReasoning(Json, Json)}.
     *
     * @param objectToValidate The JSON object to be evaluated.
     * @param againstSchema    The JSON Schema to verify the object against.
     * @return True if the JSON object satisfies the Schema. If False, an exception will be thrown with the reason why.
     */
    public static boolean validate(Json objectToValidate, Json againstSchema) {
        return new JsonSchemaEnforcer().validateWithReasoning(objectToValidate, againstSchema);
    }

    @Override
    public boolean validateWithReasoning(Json objectToValidate, Json againstSchema) throws SchemaException {
        return new JsonSchemaEnforcer().instanceValidate(objectToValidate, againstSchema);
    }

    /**
     * The single external accessible entry point into the Schema Validation Logic
     */
    private boolean instanceValidate(Json objectToValidate, Json schema) throws SchemaException {
        if (schema == null) {
            throw new SchemaException("You cannot validate against a null schema.");
        }
        if (objectToValidate == null) {
            throw new SchemaException("A null object cannot be validated against a schema.");
        }
        if (schema.getDataType() != JSType.OBJECT) {
            throw new SchemaException("JSON Schemas MUST be a valid JSON Object with defined mandatory keys and structure. You provided a " + schema.getDataType() + ".");
        }
        Set<String> subSchemaReferencesSeen = Set.of("");

        return (new JsonSchemaEnforcerPart(objectToValidate, schema, schema, "", subSchemaReferencesSeen)).enforce();
    }

    private static final class RefResolvedSchemaPart {
        private final String canonicalPath; // The key in the top level schema to reach this property (not necessarily valid JSON)
        private final String propertyName; // The property being evaluated
        private final Json schema; // The sub-schema at this property.

        private RefResolvedSchemaPart(String canonicalPath, String propertyName, Json schema) {
            this.canonicalPath = canonicalPath.startsWith(".") ? canonicalPath.substring(1) : canonicalPath;
            this.propertyName = propertyName;
            this.schema = schema;
        }

        private RefResolvedSchemaPart(String canonicalPath, Json schema) {
            this(canonicalPath, null, schema);
        }
    }

    private static final class JsonSchemaEnforcerPart {

        private final Json SCHEMA_REFERENCE; // The Full Schema
        private final Json OBJECT_TO_VALIDATE; // The Current object to validate
        private final Set<String> SCHEMA_REFERENCES_SEEN; // The Schema Subset at the given key
        private final HashMap<String, RefResolvedSchemaPart> resolvableConstraints = new HashMap<>();

        private JsonSchemaEnforcerPart(Json objectToValidate, Json fullSchema, Json currentSchemaFragment, String schemaKeySoFar, Set<String> schemaReferencesSeen) {
            this.OBJECT_TO_VALIDATE = objectToValidate;
            this.SCHEMA_REFERENCE = fullSchema;
            this.SCHEMA_REFERENCES_SEEN = new HashSet<>(schemaReferencesSeen);
            pullUp$RefsToTopLevel(new RefResolvedSchemaPart(schemaKeySoFar, currentSchemaFragment));
        }

        private void pullUp$RefsToTopLevel(RefResolvedSchemaPart pathing) {
            List<String> subSchemaKeys = pathing.schema.getKeys();

            // Deal with any lower level $ref constraints
            if (subSchemaKeys.remove("$ref")) {
                try {
                    String referencedSubSchema = convert$RefToJsonKey(pathing.canonicalPath, pathing.schema.getStringAt("$ref"));
                    if (SCHEMA_REFERENCES_SEEN.contains(referencedSubSchema)) {
                        throw valueUnexpected(SourceOfProblem.SCHEMA, pathing.canonicalPath, "$ref",
                                "Schema Reference has a cyclic dependency.");
                    } else {
                        SCHEMA_REFERENCES_SEEN.add(referencedSubSchema);
                        pullUp$RefsToTopLevel(new RefResolvedSchemaPart(
                                pathing.canonicalPath + ".$ref",
                                this.SCHEMA_REFERENCE.getJSONObjectAt(referencedSubSchema))
                        );
                    }
                } catch (KeyNotFoundException e) {
                    throw missingProperty(SourceOfProblem.SCHEMA, pathing.canonicalPath, "$ref", "", e);
                } catch (KeyDifferentTypeException e) {
                    throw valueDifferentType(SourceOfProblem.SCHEMA, pathing.canonicalPath, "$ref",
                            "$ref must link to a valid sub-schema object.", e);
                }
            }

            // Deal with everything else
            String canonicalKey;
            for (String realKey : subSchemaKeys) {
                canonicalKey = (realKey.contains(" ") || realKey.contains("\\")) ? "[`" + realKey + "`]" : realKey;
                resolvableConstraints.put(realKey, new RefResolvedSchemaPart(pathing.canonicalPath, canonicalKey, pathing.schema.getAnyAt(realKey)));
            }
        }

        private boolean enforce() throws SchemaException {
            for (Map.Entry<String, RefResolvedSchemaPart> constraint : resolvableConstraints.entrySet()) {
                switch (constraint.getKey()) {
                    case "type" -> validateType(this, constraint.getValue());
                    case "enum" -> validateEnum(this, constraint.getValue());
                    case "const" -> {
                        if (!this.OBJECT_TO_VALIDATE.equals(resolvableConstraints.get("const").schema)) {
                            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, resolvableConstraints.get("const").canonicalPath, "const",
                                    "Value MUST match the schema's constant.");
                        }
                    }
                    case "allOf" -> validateAllOf(this, constraint.getValue());
                    case "anyOf" -> validateAnyOf(this, constraint.getValue());
                    case "oneOf" -> validateOneOf(this, constraint.getValue());
                    case "not" -> validateNot(this, constraint.getValue());
                    case "if" -> validateIf(this);
                    case "multipleOf" -> validateMultipleOf(this, constraint.getValue());
                    case "maximum" -> validateMaximum(this, constraint.getValue());
                    case "exclusiveMaximum" -> validateExclusiveMaximum(this, constraint.getValue());
                    case "minimum" -> validateMinimum(this, constraint.getValue());
                    case "exclusiveMinimum" -> validateExclusiveMinimum(this, constraint.getValue());
                    case "maxLength" -> validateMaxLength(this, constraint.getValue());
                    case "minLength" -> validateMinLength(this, constraint.getValue());
                    case "pattern" -> validatePattern(this, constraint.getValue());
                    case "format" -> validateFormat(this, constraint.getValue());
                    case "maxItems" -> validateMaxItems(this, constraint.getValue());
                    case "minItems" -> validateMinItems(this, constraint.getValue());
                    case "uniqueItems" -> validateUniqueItems(this, constraint.getValue());
                    case "maxContains" -> validateMaxContains(this, constraint.getValue());
                    case "minContains" -> validateMinContains(this, constraint.getValue());
                    case "items" -> validateItems(this, constraint.getValue());
                    case "additionalItems" -> validateAdditionalItems(this, constraint.getValue());
                    case "contains" -> validateContains(this, constraint.getValue());
                    case "unevaluatedItems" ->
                            throw doThrow(SourceOfProblem.SCHEMA, "unevaluatedItems constraint is not supported by " +
                                    "this schema enforcer.\nConsider re-designing your schema to avoid it.");
                    case "maxProperties" -> validateMaxProperties(this, constraint.getValue());
                    case "minProperties" -> validateMinProperties(this, constraint.getValue());
                    case "required" -> validateRequired(this, constraint.getValue());
                    case "dependentRequired" -> validateDependentRequired(this, constraint.getValue());
                    case "properties" -> validateProperties(this, constraint.getValue());
                    case "patternProperties" -> validatePatternProperties(this, constraint.getValue());
                    case "additionalProperties" -> validateAdditionalProperties(this, constraint.getValue());
                    case "propertyNames" -> validatePropertyNames(this, constraint.getValue());
                    case "unevaluatedProperties" ->
                            throw doThrow(SourceOfProblem.SCHEMA, "unevaluatedProperties constraint is not supported by " +
                                    "this schema enforcer.\nConsider re-designing your schema to avoid it.");
                    default -> {
                        // Do nothing with unknown constraints
                    }
                }
            }

            // EVERYTHING HAS VALIDATED NOW
            return true;
        }


        /* CONDITIONAL VALIDATIONS */
        private void validateAllOf(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            validateArrayBasedConditional(partStructure, () -> {
                for (String arrayIndex : partStructure.schema.getKeys()) {
                    subEnforce(currentPart, partStructure.schema.getAnyAt("[" + arrayIndex + "]"),
                            partStructure.canonicalPath + ".allOf[" + arrayIndex + "]");
                }
                return null;
            });
        }

        private void validateAnyOf(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            validateArrayBasedConditional(partStructure, () -> {
                for (String arrayIndex : partStructure.schema.getKeys()) {
                    try {
                        subEnforce(currentPart, partStructure.schema.getAnyAt("[" + arrayIndex + "]"),
                                partStructure.canonicalPath + ".anyOf[" + arrayIndex + "]");
                        return null;
                    } catch (
                            SchemaException e) {/*Trapping exceptions as we won't necessarily match all the sub-schemas in an anyOf.*/}
                }
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "A provided JSON value failed to match any of the sub-schemas provided.");
            });
        }

        private void validateOneOf(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            validateArrayBasedConditional(partStructure, () -> {
                long schemasMatched = 0;
                for (String arrayIndex : partStructure.schema.getKeys()) {
                    try {
                        subEnforce(currentPart, partStructure.schema.getAnyAt("[" + arrayIndex + "]"),
                                partStructure.canonicalPath + ".oneOf[" + arrayIndex + "]");
                    } catch (SchemaException e) {
                        // Trapping exceptions as we won't necessarily match all of these.
                        schemasMatched--;
                    }
                    if (++schemasMatched > 1) {
                        throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                                "Json validated against more than one sub-schema.");
                    }
                }
                if (schemasMatched != 1) {
                    throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                            "A provided JSON value failed to match any of the sub-schemas provided.");
                }
                return null;
            });
        }

        private void validateNot(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            try {
                subEnforce(currentPart, partStructure.schema, partStructure.canonicalPath + ".not");
            } catch (SchemaException e) {
                // Ignore the failure - that's what we wanted!
                return;
            }
            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                    "Json successfully validated against the sub-schema, but a failure was required.");
        }

        private void validateIf(final JsonSchemaEnforcerPart currentPart) {
            boolean validatedAgainstIf = false;
            try {
                subEnforce(currentPart, currentPart.resolvableConstraints.get("if").schema,
                        currentPart.resolvableConstraints.get("if").canonicalPath + ".if"
                );
                validatedAgainstIf = true;
            } catch (SchemaException e) {
                // The IF part of a schema shouldn't affect the result - just which of the then/else is relevant
            }

            if (validatedAgainstIf && currentPart.resolvableConstraints.containsKey("then")) {
                subEnforce(currentPart, currentPart.resolvableConstraints.get("then").schema,
                        currentPart.resolvableConstraints.get("then").canonicalPath + ".then");
            } else if (!validatedAgainstIf && currentPart.resolvableConstraints.containsKey("else")) {
                subEnforce(currentPart, currentPart.resolvableConstraints.get("else").schema,
                        currentPart.resolvableConstraints.get("else").canonicalPath + ".else");
            }
        }

        private void validateType(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            switch (partStructure.schema.getDataType()) {
                case STRING ->
                        validateType(currentPart, partStructure, Collections.singletonList(partStructure.schema.getString().toUpperCase()));
                case ARRAY -> {
                    Set<String> distinctTypes = new HashSet<>(10);
                    for (Json distinctType : partStructure.schema.getArray()) {
                        try {
                            distinctTypes.add(distinctType.getString().toUpperCase());
                        } catch (KeyDifferentTypeException e) {
                            throw valueUnexpected(SourceOfProblem.SCHEMA, partStructure.canonicalPath, partStructure.propertyName,
                                    "All values in the (type) property MUST be a valid, distinct STRING.", e);
                        }
                    }
                    if (distinctTypes.isEmpty()) {
                        throw valueUnexpected(SourceOfProblem.SCHEMA, partStructure.canonicalPath, partStructure.propertyName,
                                "You must provide at least one valid data type restriction.");
                    }
                    validateType(currentPart, partStructure, new ArrayList<>(distinctTypes));
                }
                default ->
                        throw valueDifferentType(SourceOfProblem.SCHEMA, partStructure.canonicalPath, partStructure.propertyName,
                                "Expected one of " + Arrays.asList("STRING", "ARRAY") + ", got " + partStructure.schema.getDataType() + ".");
            }
        }

        private void validateType(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure, List<String> allowedTypes) {
            JSType currentDataType = currentPart.OBJECT_TO_VALIDATE.getDataType();
            for (String allowedType : allowedTypes) {
                switch (allowedType) {
                    case "ARRAY", "LIST" -> {
                        if (currentDataType == JSType.ARRAY) return;
                    }
                    case "BOOLEAN" -> {
                        if (currentDataType == JSType.BOOLEAN) return;
                    }
                    case "LONG", "INTEGER" -> {
                        if (currentDataType == JSType.LONG) return;
                    }
                    case "DOUBLE" -> {
                        if (currentDataType == JSType.DOUBLE) return;
                    }
                    case "NUMBER" -> {
                        if (currentDataType == JSType.DOUBLE || currentDataType == JSType.LONG) return;
                    }
                    case "STRING" -> {
                        if (currentDataType == JSType.STRING) return;
                    }
                    case "OBJECT" -> {
                        if (currentDataType == JSType.OBJECT) return;
                    }
                    default ->
                            throw valueUnexpected(SourceOfProblem.SCHEMA, partStructure.canonicalPath, partStructure.propertyName,
                                    "Unrecognised/Unsupported type provided (" + allowedType + ").");
                }
            }
            throw valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                    "Expected one of " + allowedTypes + ", got " + currentDataType + ".");
        }

        private void validateEnum(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            tryForSchema(partStructure, () -> {
                for (Json instance : partStructure.schema.getArray()) {
                    if (currentPart.OBJECT_TO_VALIDATE.equals(instance)) {
                        return null;
                    }
                }
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "Object did not match any options provided by the enum.");
            });
        }


        /* NUMBERS */
        private void validateMultipleOf(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            BigDecimal numberFromSchema = getNumberAsBigDecimal(currentPart, partStructure.propertyName, true);
            if (numberFromSchema.equals(BigDecimal.ZERO)) {
                throw valueUnexpected(SourceOfProblem.SCHEMA, partStructure.canonicalPath, partStructure.propertyName,
                        "You cannot use a multiple of 0.");
            }
            if ((getNumberAsBigDecimal(currentPart, partStructure.propertyName, false)
                    .remainder(numberFromSchema))
                    .compareTo(BigDecimal.ZERO) != 0) {
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "Value was not a multiple of schema value.");
            }
        }

        private void validateMaximum(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            if (compareNumbers(currentPart, partStructure.propertyName) > 0) {
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "Value was greater than the upper bound.");
            }
        }

        private void validateExclusiveMaximum(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            if (compareNumbers(currentPart, partStructure.propertyName) >= 0) {
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "Value was greater than or equal to the upper bound.");
            }
        }

        private void validateMinimum(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            if (compareNumbers(currentPart, partStructure.propertyName) < 0) {
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "Value was lower than the lower bound.");
            }
        }

        private void validateExclusiveMinimum(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            if (compareNumbers(currentPart, partStructure.propertyName) <= 0) {
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "Value was lower than or equal to the lower bound.");
            }
        }


        /* OBJECTS */
        private void validateProperties(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            final Set<String> keysToValidate = getKeysRelevantToConstraint(currentPart, partStructure, KeysRelevantTo.PROPERTIES);
            if (currentPart.OBJECT_TO_VALIDATE.getDataType() != JSType.OBJECT) {
                return;
            }
            for (String key : keysToValidate) {
                if (currentPart.OBJECT_TO_VALIDATE.contains(key)) {
                    String keyInSchema = (key.contains(" ") || key.contains("\\")) ? "[`" + key + "`]" : "." + key;
                    try {
                        subEnforce(currentPart, currentPart.OBJECT_TO_VALIDATE.getAnyAt(key),
                                partStructure.schema.getJSONObjectAt(key),
                                partStructure.canonicalPath + ".properties" + keyInSchema);
                    } catch (KeyDifferentTypeException e) {
                        throw valueDifferentType(SourceOfProblem.SCHEMA, partStructure.canonicalPath, partStructure.propertyName + keyInSchema, e);
                    }
                }
            }
        }

        private void validateRequired(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            List<String> requiredKeys = new ArrayList<>();
            try {
                List<Json> keysAsJson = partStructure.schema.getArray();
                if (keysAsJson.isEmpty()) {
                    throw valueUnexpected(SourceOfProblem.SCHEMA, partStructure.canonicalPath, partStructure.propertyName,
                            "Must provide at least one required property.");
                }
                for (Json key : keysAsJson) {
                    requiredKeys.add(key.getString());
                }
            } catch (KeyDifferentTypeException e) {
                throw valueDifferentType(SourceOfProblem.SCHEMA, partStructure.canonicalPath, partStructure.propertyName,
                        "Required constraint must be a non-empty array of strings representing property names.", e);
            }
            if (currentPart.OBJECT_TO_VALIDATE.getDataType() != JSType.OBJECT) {
                throw valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "Required constraint can only be applied to Object properties.");
            }

            for (String requiredKey : requiredKeys) {
                if (!currentPart.OBJECT_TO_VALIDATE.contains(requiredKey)) {
                    throw missingProperty(partStructure.canonicalPath, partStructure.propertyName,
                            "Property (" + requiredKey + ") is mandatory, but couldn't be found.");
                }
            }
        }

        private void validateMinProperties(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            long minProperties = getNonNegativeInteger(currentPart, partStructure.propertyName);
            if (currentPart.OBJECT_TO_VALIDATE.getDataType() != JSType.OBJECT) {
                throw valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "This constraint can only be used against an object.");
            }
            if (currentPart.OBJECT_TO_VALIDATE.getKeys().size() < minProperties) {
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "Object doesn't have enough properties to pass the constraint.");
            }
        }

        private void validateMaxProperties(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            long maxProperties = getNonNegativeInteger(currentPart, partStructure.propertyName);
            if (currentPart.OBJECT_TO_VALIDATE.getDataType() != JSType.OBJECT) {
                throw valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "This constraint can only be used against an object.");
            }
            if (currentPart.OBJECT_TO_VALIDATE.getKeys().size() > maxProperties) {
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "Object has more properties than the constraint allows.");
            }
        }

        private void validatePropertyNames(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            Json subSchema = getNextSchemaAsObject(partStructure);

            for (String key : currentPart.OBJECT_TO_VALIDATE.getKeys()) {
                subEnforce(currentPart,
                        JsonParser.parse("\""
                                + key.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"")
                                + "\""),
                        subSchema, partStructure.canonicalPath + ".propertyNames"
                );
            }
        }

        private void validatePatternProperties(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            Json propertiesObject = getNextSchemaAsObject(partStructure);
            if (currentPart.OBJECT_TO_VALIDATE.getDataType() != JSType.OBJECT) {
                return;
            }
            for (String regexKey : propertiesObject.getKeys()) {
                Pattern regexPattern = getRegexPattern(partStructure, regexKey);
                for (String objectKey : currentPart.OBJECT_TO_VALIDATE.getKeys()) {
                    if (regexPattern.matcher(objectKey).find()) {
                        String keyInSchema = "[`" + regexKey + "`]";
                        try {
                            subEnforce(currentPart, currentPart.OBJECT_TO_VALIDATE.getAnyAt(objectKey),
                                    partStructure.schema.getJSONObjectAt(keyInSchema),
                                    partStructure.canonicalPath + ".patternProperties" + keyInSchema);
                        } catch (KeyDifferentTypeException e) {
                            throw valueDifferentType(SourceOfProblem.SCHEMA, partStructure.canonicalPath, partStructure.propertyName + keyInSchema, e);
                        }
                    }
                }
            }
        }

        private void validateDependentRequired(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            Json dependentDefinitions = getNextSchemaAsObject(partStructure);
            if (currentPart.OBJECT_TO_VALIDATE.getDataType() != JSType.OBJECT) {
                return;
            }
            List<Json> dependentDefinition;
            for (String key : dependentDefinitions.getKeys()) {
                String keyInSchema = (key.contains(" ") || key.contains("\\")) ? "[`" + key + "`]" : "." + key;
                try {
                    dependentDefinition = partStructure.schema.getArrayAt(key);
                } catch (KeyDifferentTypeException e) {
                    throw valueDifferentType(SourceOfProblem.SCHEMA, partStructure.canonicalPath, partStructure.propertyName + keyInSchema,
                            "Dependents can only be specified in an ARRAY.", e);
                }
                if (currentPart.OBJECT_TO_VALIDATE.contains(key)) {
                    try {
                        for (int index = 0; index < dependentDefinition.size(); index++) {
                            if (!currentPart.OBJECT_TO_VALIDATE.contains(dependentDefinition.get(index).getString())) {
                                throw missingProperty(partStructure.canonicalPath,
                                        partStructure.propertyName + keyInSchema + "[" + index + "]",
                                        "Missing dependent property (" + dependentDefinition.get(index).getString()
                                                + ") required because property (" + key + ") present.");
                            }
                        }
                    } catch (KeyDifferentTypeException e) {
                        throw valueDifferentType(SourceOfProblem.SCHEMA, partStructure.canonicalPath, partStructure.propertyName + keyInSchema,
                                "Constraint must be the keys of dependent properties.", e);
                    }
                }
            }
        }

        private void validateAdditionalProperties(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            JSType schemaType = partStructure.schema.getDataType();
            if (schemaType != JSType.BOOLEAN && schemaType != JSType.OBJECT) {
                throw valueDifferentType(SourceOfProblem.SCHEMA, partStructure.canonicalPath, partStructure.propertyName,
                        "Expected one of " + Arrays.asList("BOOLEAN", "ARRAY") + ", got " + schemaType + ".");
            } else {
                Set<String> otherwiseCheckedKeys = getKeysRelevantToConstraint(currentPart, null, KeysRelevantTo.ADDITIONAL_PROPERTIES);
                if (schemaType == JSType.BOOLEAN) {
                    if (!partStructure.schema.getBoolean()) {
                        ArrayList<String> disallowedKeys = new ArrayList<>();
                        for (String key : currentPart.OBJECT_TO_VALIDATE.getKeys()) {
                            if (!otherwiseCheckedKeys.contains(key)) {
                                disallowedKeys.add(key);
                            }
                        }
                        if (!disallowedKeys.isEmpty()) {
                            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                                    "Additional Properties prohibited, but found " + disallowedKeys);
                        }
                    }
                } else {
                    for (String key : currentPart.OBJECT_TO_VALIDATE.getKeys()) {
                        if (!otherwiseCheckedKeys.contains(key)) {
                            try {
                                subEnforce(currentPart, currentPart.OBJECT_TO_VALIDATE.getAnyAt(key), partStructure.schema,
                                        partStructure.canonicalPath + ".additionalProperties"
                                );
                            } catch (SchemaException e) {
                                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                                        "Additional property (" + key + ") did not validate against schema.", e);
                            }
                        }
                    }
                }
            }
        }

        /* ARRAYS */
        private long validateContains(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            Json subSchema = getNextSchemaAsObject(partStructure);
            if (currentPart.OBJECT_TO_VALIDATE.getDataType() != JSType.ARRAY) {
                throw valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "This constraint can only be used against an array.");
            }
            long timesMatched = 0;
            for (Json element : currentPart.OBJECT_TO_VALIDATE.getArray()) {
                try {
                    subEnforce(currentPart, element, subSchema, partStructure.canonicalPath + ".contains");
                    timesMatched++;
                } catch (SchemaException e) {
                    // Ignore, we won't necessarily match everything
                }
            }
            if (timesMatched == 0) {
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "Found no match against the contains property in the value array.");
            }
            return timesMatched;
        }

        private void validateItems(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            JSType schemaType = partStructure.schema.getDataType();
            if (schemaType != JSType.OBJECT && schemaType != JSType.ARRAY) {
                throw valueDifferentType(SourceOfProblem.SCHEMA, partStructure.canonicalPath, partStructure.propertyName,
                        "Expected one of " + Arrays.asList("OBJECT", "ARRAY") + ", got " + schemaType + ".");
            }
            if (currentPart.OBJECT_TO_VALIDATE.getDataType() != JSType.ARRAY) {
                throw valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "This constraint can only be used against an array.");
            }
            if (schemaType == JSType.OBJECT) {
                for (Json element : currentPart.OBJECT_TO_VALIDATE.getArray()) {
                    try {
                        subEnforce(currentPart, element, partStructure.schema.getJSONObject(), partStructure.canonicalPath + ".items");
                    } catch (SchemaException e) {
                        throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                                "An element in the value array did not match against the constraint.", e);
                    }
                }
            } else {
                String key;
                for (String index : getKeysRelevantToConstraint(currentPart, partStructure, KeysRelevantTo.ITEMS)) {
                    key = "[" + index + "]";
                    try {
                        partStructure.schema.getJSONObjectAt(key);
                    } catch (KeyDifferentTypeException e) {
                        throw valueDifferentType(SourceOfProblem.SCHEMA, partStructure.canonicalPath, partStructure.propertyName + key, e);
                    }
                    try {
                        subEnforce(currentPart, currentPart.OBJECT_TO_VALIDATE.getAnyAt("[" + index + "]"),
                                partStructure.schema.getJSONObjectAt(key), partStructure.canonicalPath + ".items" + key);
                    } catch (SchemaException e) {
                        throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName + key,
                                "Element in value array did not match against matching index in sub-schema.", e);
                    }
                }
            }
        }

        private void validateUniqueItems(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            boolean shouldValidate = tryForSchema(partStructure, partStructure.schema::getBoolean);
            tryForObject(partStructure, currentPart.OBJECT_TO_VALIDATE::getArray);
            if (shouldValidate) {
                Json outerObject;
                for (int outer = 0; outer < currentPart.OBJECT_TO_VALIDATE.getArray().size(); outer++) {
                    outerObject = currentPart.OBJECT_TO_VALIDATE.getAnyAt("[" + outer + "]");
                    for (int inner = outer + 1; inner < currentPart.OBJECT_TO_VALIDATE.getArray().size(); inner++) {
                        if (outerObject.equals(currentPart.OBJECT_TO_VALIDATE.getAnyAt("[" + inner + "]"))) {
                            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                                    "Index [" + outer + "] in value to verify was not unique.");
                        }
                    }
                }
            }
        }

        private void validateMinItems(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            long minProperties = getNonNegativeInteger(currentPart, partStructure.propertyName);
            if (currentPart.OBJECT_TO_VALIDATE.getDataType() != JSType.ARRAY) {
                throw valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "This constraint can only be used against an array.");
            }
            if (currentPart.OBJECT_TO_VALIDATE.getArray().size() < minProperties) {
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "Array doesn't have enough elements to pass the constraint.");
            }
        }

        private void validateMaxItems(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            long maxProperties = getNonNegativeInteger(currentPart, partStructure.propertyName);
            if (currentPart.OBJECT_TO_VALIDATE.getDataType() != JSType.ARRAY) {
                throw valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "This constraint can only be used against an array.");
            }
            if (currentPart.OBJECT_TO_VALIDATE.getArray().size() > maxProperties) {
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "Array has more elements than the constraint allows.");
            }
        }

        private void validateAdditionalItems(JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            if (currentPart.resolvableConstraints.containsKey("items")
                    && currentPart.resolvableConstraints.get("items").schema.getDataType() != JSType.ARRAY
            ) {
                // If there is also an 'items' constraint which is anything other
                // than an array, then this constraint should be ignored.
                return;
            }
            if (currentPart.OBJECT_TO_VALIDATE.getDataType() != JSType.ARRAY) {
                throw valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "This constraint can only be used against an array.");
            }
            Set<String> keysToCheck;
            if (currentPart.resolvableConstraints.containsKey("items")) {
                keysToCheck = getKeysRelevantToConstraint(currentPart, currentPart.resolvableConstraints.get("items"), KeysRelevantTo.ADDITIONAL_ITEMS);
            } else {
                keysToCheck = new HashSet<>(currentPart.OBJECT_TO_VALIDATE.getKeys());
            }
            for (String key : keysToCheck) {
                key = "[" + key + "]";
                try {
                    subEnforce(currentPart, currentPart.OBJECT_TO_VALIDATE.getAnyAt(key),
                            partStructure.schema, partStructure.canonicalPath + ".additionalItems");
                } catch (SchemaException e) {
                    throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                            "Element " + key + " in value array did not satisfy.", e);
                }
            }
        }

        private void validateMinContains(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            if (countContains(currentPart, partStructure) <
                    tryForSchema(currentPart.resolvableConstraints.get("contains"), partStructure.schema::getLong)) {
                throw missingProperty(partStructure.canonicalPath, partStructure.propertyName,
                        "Minimum quantity of matches against the contains constraint not satisfied.");
            }
        }

        private void validateMaxContains(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            if (countContains(currentPart, partStructure) >
                    tryForSchema(currentPart.resolvableConstraints.get("contains"), partStructure.schema::getLong)) {
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "Maximum quantity of matches against the contains constraint was exceeded.");
            }
        }


        /* STRINGS */
        private void validatePattern(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            Pattern verifiedPattern;
            try {
                verifiedPattern = Pattern.compile(partStructure.schema.getString());
            } catch (KeyDifferentTypeException e) {
                throw valueDifferentType(SourceOfProblem.SCHEMA, partStructure.canonicalPath, partStructure.propertyName, e);
            } catch (Exception e) {
                throw valueUnexpected(SourceOfProblem.SCHEMA, partStructure.canonicalPath, partStructure.propertyName,
                        "Pattern provided was not a valid regex pattern.", e);
            }
            String objectToConstrain = tryForObject(partStructure, currentPart.OBJECT_TO_VALIDATE::getString);
            if (!verifiedPattern.matcher(objectToConstrain).find()) {
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "Value did not match the provided pattern: " + verifiedPattern);
            }
        }

        private void validateMinLength(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            long minLength = getNonNegativeInteger(currentPart, partStructure.propertyName);
            String objectToConstrain = tryForObject(partStructure, currentPart.OBJECT_TO_VALIDATE::getString);
            if (objectToConstrain.length() < minLength) {
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "String length was shorter than the minimum bound: " + minLength);
            }
        }

        private void validateMaxLength(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            long maxLength = getNonNegativeInteger(currentPart, partStructure.propertyName);
            String objectToConstrain = tryForObject(partStructure, currentPart.OBJECT_TO_VALIDATE::getString);
            if (objectToConstrain.length() > maxLength) {
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "String length was longer than the maximum bound: " + maxLength);
            }
        }

        private void validateFormat(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            String formatToVerify = tryForSchema(partStructure, partStructure.schema::getString);
            String objectToConstrain = tryForObject(partStructure, currentPart.OBJECT_TO_VALIDATE::getString);
            boolean matched = false;
            try {
                switch (formatToVerify) {
                    case "date" ->
                        // https://rgxdb.com/r/2V9BOC58
                            matched = objectToConstrain.matches("^(?:(?:(?:[1-9]\\d)(?:0[48]|[2468][048]|[13579][26])|" +
                                    "(?:(?:[2468][048]|[13579][26])00))([/\\-.])(?:0?2\\1(?:29)))|(?:(?:\\d\\d{3})([/\\-.])" +
                                    "(?:(?:(?:0?[13578]|1[02])\\2(?:31))|(?:(?:0?[13-9]|1[0-2])\\2(?:29|30))|(?:(?:0?[1-9])|" +
                                    "(?:1[0-2]))\\2(?:0?[1-9]|1\\d|2[0-8])))$");
                    case "time" ->
                        // https://rgxdb.com/r/2LE6429J with additional nudging
                            matched = objectToConstrain.matches("^(((0?[1-9]|1[0-2])[:.][0-5]\\d([:.][0-5]\\d)?" +
                                    "(\\.\\d{1,10}[zZ])?( )?[aApP][mM])|((0?\\d|1\\d|2[0-3])[:.][0-5]\\d([:.][0-5]\\d)?" +
                                    "(\\.\\d{1,10}[zZ])?))$");
                    case "date-time", "datetime" ->
                        // https://rgxdb.com/r/526K7G5W
                            matched = objectToConstrain.matches("^(?:[+-]?\\d{4}(?!\\d{2}\\b))(?:(-?)(?:(?:0[1-9]|1[0-2])" +
                                    "(?:\\1(?:[12]\\d|0[1-9]|3[01]))?|W(?:[0-4]\\d|5[0-2])(?:-?[1-7])?|(?:00[1-9]|0[1-9]\\d|" +
                                    "[12]\\d{2}|3(?:[0-5]\\d|6[1-6])))(?:[Tt\\s](?:(?:(?:[01]\\d|2[0-3])(?:(:?)[0-5]\\d)?|24:?00)" +
                                    "(?:[.,]\\d+(?!:))?)?(?:\\2[0-5]\\d(?:[.,]\\d+)?)?(?:[zZ]|([+-])(?:[01]\\d|2[0-3]):?(?:[0-5]\\d)?)?)?)?$");
                    case "duration" ->
                        // https://rgxdb.com/r/MD2234J
                            matched = objectToConstrain.matches("^(-?)P(?=\\d|T\\d)(?:(\\d+)Y)?(?:(\\d+)M)?(?:(\\d+)([DW]))?" +
                                    "(?:T(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+(?:\\.\\d+)?)S)?(?<!T))?$");
                    case "regex" -> {
                        Pattern.compile(objectToConstrain);
                        matched = true;
                    }
                    case "email" ->
                        // https://regexlib.com/REDetails.aspx?regexp_id=2558
                            matched = objectToConstrain.matches("^((([!#$%&'*+\\-/=?^_`{|}~\\w])|([!#$%&'*+\\-/=?^_`{|}~\\w]" +
                                    "([!#$%&'*+\\-/=?^_`{|}~\\w]|((?<!\\.)\\.(?!\\.)))*" +
                                    "[!#$%&'*+\\-/=?^_`{|}~\\w]))@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*)$");
                    case "phone", "phone-number" ->
                        // Me
                            matched = objectToConstrain.matches("^(?:\\([+]?\\d{1,3}\\)|[+]?\\d{1,3})?(?:[ -]?" +
                                    "(\\(\\d{1,5}\\)|\\d{1,5}))?(?:([ -]?\\d{1,15})|([ -]?\\d{1,4}){1,3})" +
                                    "(?: ext\\.? ?(?:(\\d{1,5})|([ .]\\d{1,2}){1,5}))?$");
                    case "version", "sem-ver", "semVer" ->
                        // ME :D
                            matched = objectToConstrain.matches("^([vV](ersion)?([: /-])?)?(\\d+)" +
                                    "([.]\\d+)?([.]\\d+)?(-([0-9a-zA-Z-]+(\\.([0-9a-zA-Z-]+))*)(?<![-.]))?" +
                                    "(\\+([0-9a-zA-Z-]+(\\.[0-9a-zA-Z-]+)*)(?<![-.]))?$");
                    case "hostname" ->
                        // ME - Fairly loose matcher :/
                            matched = objectToConstrain.matches("^(?![-.])+([a-zA-Z\\d-%]{0,63})(((?<!\\.)\\.(?!\\.))(?:[a-zA-Z\\d-%]{0,63}))*(?<![-.])$");
                    case "ipv4", "ip" ->
                        // https://regexlib.com/REDetails.aspx?regexp_id=2685 comment: 5/26/2011 5:37:09 AM
                            matched = objectToConstrain.matches("^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|\\d)){3}$");
                    case "ipv6" ->
                        // https://rgxdb.com/r/1DZHMUSH with some from ipv4 implementation
                            matched = objectToConstrain.matches("^((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|\\d)" +
                                    "(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|(:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|\\d)" +
                                    "(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|\\d)" +
                                    "(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|\\d)" +
                                    "(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|\\d)" +
                                    "(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:)(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|\\d)" +
                                    "(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|\\d)" +
                                    "(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|\\d)){3}))|:)))((%.+)|(/\\d{1,2}))?$");
                    case "mac" ->
                        // https://rgxdb.com/r/4QCDJ0QF
                            matched = objectToConstrain.matches("^(?:[0-9A-Fa-f]{2}([:-]?)[0-9A-Fa-f]{2})(?:(?:\\1|\\.)([0-9A-Fa-f]{2}([:-]?)[0-9A-Fa-f]{2})){2}$");
                    case "uri", "url" ->
                        // https://rgxdb.com/r/2MQXJD5 with some nudging
                            matched = objectToConstrain.matches("^(([a-zA-Z][a-zA-Z\\d]*):)(//(([a-zA-Z\\d-._~!$&'()*+,;=%]*)" +
                                    "(:([a-zA-Z\\d-._~!$&'()*+,;=:%]*))?@)?(([a-zA-Z\\d-.%]+)|(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|" +
                                    "(\\[([a-fA-F\\d.:]+)]))?(:(\\d*))?((/[a-zA-Z\\d-._~!$&'()*+,;=:@%]*)*)|(/?([a-zA-Z\\d-._~!$&'()*+,;=:@%]+" +
                                    "(/[a-zA-Z\\d-._~!$&'()*+,;=:@%]*)*)))?(\\?([a-zA-Z\\d-._~!$&'()*+,;=:@%/?]*))?(#([a-zA-Z\\d-._~!$&'()*+,;=:@%/?]*))?$");
                    case "uri-reference", "url-reference" ->
                        // https://rgxdb.com/r/2MQXJD5 with some nudging
                            matched = objectToConstrain.matches("^(([a-zA-Z][a-zA-Z\\d]*):)?(?://(?:([a-zA-Z\\d-._~!$&'()*+,;=%]*)" +
                                    "(:([a-zA-Z\\d-._~!$&'()*+,;=:%]*))?@)?(([a-zA-Z\\d-.%]+)|(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|" +
                                    "(\\[([a-fA-F\\d.:]+)]))?(:(\\d*))?((/[a-zA-Z\\d-._~!$&'()*+,;=:@%]*)*)|(/?([a-zA-Z\\d-._~!$&'()*+,;=:@%]+" +
                                    "(/[a-zA-Z\\d-._~!$&'()*+,;=:@%]*)*)))?(?:\\?([a-zA-Z\\d-._~!$&'()*+,;=:@%/?]*))?(?:#([a-zA-Z\\d-._~!$&'()*+,;=:@%/?]*))?$");
                    case "uuid" ->
                            matched = objectToConstrain.matches("^([0-9A-Fa-f]{8}(?:-[0-9A-Fa-f]{4}){3}-[0-9A-Fa-f]{12})$");
                    default ->
                            throw valueUnexpected(SourceOfProblem.SCHEMA, partStructure.canonicalPath, partStructure.propertyName,
                                    "Unrecognised/Unsupported format provided (" + formatToVerify + ").");
                }
            } catch (InvalidSchemaException e) {
                throw e;
            } catch (Exception e) {/*Ignore as we will throw below*/}

            if (!matched) {
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, partStructure.canonicalPath, partStructure.propertyName,
                        "Value failed to match against the format (" + formatToVerify + ") constraint.");
            }
        }

        /* Utils */
        private int compareNumbers(final JsonSchemaEnforcerPart currentPart, String propertyKey) {
            return getNumberAsBigDecimal(currentPart, propertyKey, false)
                    .compareTo(getNumberAsBigDecimal(currentPart, propertyKey, true));
        }

        private long getNonNegativeInteger(final JsonSchemaEnforcerPart currentPart, String propertyKey) {
            RefResolvedSchemaPart resolvableConstraint = currentPart.resolvableConstraints.get(propertyKey);
            try {
                long value = resolvableConstraint.schema.getLong();
                if (value < 0) {
                    throw valueUnexpected(SourceOfProblem.SCHEMA, resolvableConstraint.canonicalPath, propertyKey,
                            "Value must be >= 0.");
                }
                return value;
            } catch (KeyDifferentTypeException e) {
                throw valueDifferentType(SourceOfProblem.SCHEMA, resolvableConstraint.canonicalPath, propertyKey,
                        "Constraint must provide a non-negative integer.", e);
            }
        }

        private String convert$RefToJsonKey(String canonicalPathing, String reference) {
            String resolvedRef = reference;
            if (resolvedRef.equals("")) {
                return resolvedRef;
            }

            // Absolute
            if (resolvedRef.startsWith("#/")) {
                resolvedRef = convert$RefPathToJsonKeyEncoding(resolvedRef.substring(2));
            } else if (resolvedRef.startsWith("#") || resolvedRef.startsWith("/")) {
                resolvedRef = convert$RefPathToJsonKeyEncoding(resolvedRef.substring(1));
            }
            // Relative
            else {
                throw valueUnexpected(SourceOfProblem.SCHEMA, canonicalPathing, "$ref",
                        "Relative sub-schema keys are not supported in this implementation.");
            }

            return resolvedRef;
        }

        private String convert$RefPathToJsonKeyEncoding(String refPath) {
            StringBuilder resolvedPath = new StringBuilder();
            String[] steps = refPath.split("/");

            for (String step : steps) {
                step = step
                        .replaceAll("~0", "~")
                        .replaceAll("~1", "/");
                try {
                    int arrayRef = Integer.parseInt(step);
                    resolvedPath.append("[").append(arrayRef).append("]");
                } catch (NumberFormatException e) {
                    if (step.contains(" ") || step.contains("\\")) {
                        resolvedPath.append("[`").append(step).append("`]");
                    } else {
                        resolvedPath.append(".").append(step);
                    }
                }
            }
            if (resolvedPath.length() > 0 && resolvedPath.charAt(0) == '.') {
                resolvedPath.deleteCharAt(0);
            }

            return resolvedPath.toString();
        }

        private BigDecimal getNumberAsBigDecimal(final JsonSchemaEnforcerPart currentPart, String propertyKey, boolean isForSchema) {
            RefResolvedSchemaPart resolvableConstraint = currentPart.resolvableConstraints.get(propertyKey);
            Json constraint = isForSchema ? resolvableConstraint.schema : currentPart.OBJECT_TO_VALIDATE;
            JSType constraintType = constraint.getDataType();
            if (constraintType != JSType.DOUBLE && constraintType != JSType.LONG) {
                throw (isForSchema
                        ? valueDifferentType(SourceOfProblem.SCHEMA, resolvableConstraint.canonicalPath, propertyKey,
                        "Expected NUMBER, got " + constraintType + ".")
                        : valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, resolvableConstraint.canonicalPath, propertyKey,
                        "Value to verify must be a number.")
                );
            }
            return (constraintType == JSType.LONG)
                    ? BigDecimal.valueOf(constraint.getLong())
                    : BigDecimal.valueOf(constraint.getDouble());
        }

        private void subEnforce(JsonSchemaEnforcerPart currentPart, Json updatedObjectToValidate, Json updatedSubSchema, String updatedPathInSchema) throws SchemaException {
            new JsonSchemaEnforcerPart(
                    updatedObjectToValidate,
                    currentPart.SCHEMA_REFERENCE,
                    updatedSubSchema,
                    updatedPathInSchema,
                    currentPart.SCHEMA_REFERENCES_SEEN
            ).enforce();
        }

        private void subEnforce(JsonSchemaEnforcerPart currentPart, Json updatedSubSchema, String updatedPathInSchema) throws SchemaException {
            subEnforce(currentPart, currentPart.OBJECT_TO_VALIDATE, updatedSubSchema, updatedPathInSchema);
        }

        private void validateArrayBasedConditional(RefResolvedSchemaPart partStructure, Lambda<Void> worker) {
            tryForSchema(partStructure, () -> {
                if (partStructure.schema.getArray().isEmpty()) {
                    throw valueUnexpected(SourceOfProblem.SCHEMA, partStructure.canonicalPath, partStructure.propertyName,
                            "Array must contain at least 1 sub-schema.");
                } else {
                    worker.doWork();
                }
                return null;
            });
        }

        private <E> E tryForSchema(RefResolvedSchemaPart partStructure, Lambda<E> worker) {
            return unwrapJsonType(partStructure, worker, SourceOfProblem.SCHEMA);
        }

        private <E> E tryForObject(RefResolvedSchemaPart partStructure, Lambda<E> worker) {
            return unwrapJsonType(partStructure, worker, SourceOfProblem.OBJECT_TO_VALIDATE);
        }

        private <E> E unwrapJsonType(RefResolvedSchemaPart partStructure, Lambda<E> worker, SourceOfProblem sourceOfProblem) {
            final E returnValue;
            try {
                returnValue = worker.doWork();
            } catch (KeyDifferentTypeException e) {
                throw valueDifferentType(sourceOfProblem, partStructure.canonicalPath, partStructure.propertyName, e);
            }
            return returnValue;
        }

        private Json getNextSchemaAsObject(RefResolvedSchemaPart partStructure) {
            return tryForSchema(partStructure, partStructure.schema::getJSONObject);
        }

        private Set<String> getKeysRelevantToConstraint(final JsonSchemaEnforcerPart currentPart,
                                                        final RefResolvedSchemaPart partStructure,
                                                        final KeysRelevantTo constraint
        ) {
            Set<String> keysForConstraint = new HashSet<>();
            switch (constraint) {
                case PROPERTIES -> keysForConstraint.addAll(getNextSchemaAsObject(partStructure).getKeys());
                case PATTERN_PROPERTIES -> {
                    for (String regexKey : getNextSchemaAsObject(partStructure).getKeys()) {
                        Pattern regexPattern = getRegexPattern(partStructure, regexKey);
                        for (String objectKey : currentPart.OBJECT_TO_VALIDATE.getKeys()) {
                            if (regexPattern.matcher(objectKey).find()) keysForConstraint.add(objectKey);
                        }
                    }
                }
                case ADDITIONAL_PROPERTIES -> {
                    if (currentPart.resolvableConstraints.containsKey("properties")) {
                        keysForConstraint.addAll(getKeysRelevantToConstraint(currentPart, currentPart.resolvableConstraints.get("properties"), KeysRelevantTo.PROPERTIES));
                    }
                    if (currentPart.resolvableConstraints.containsKey("patternProperties")) {
                        keysForConstraint.addAll(getKeysRelevantToConstraint(currentPart, currentPart.resolvableConstraints.get("patternProperties"), KeysRelevantTo.PATTERN_PROPERTIES));
                    }
                }
                case ITEMS -> keysForConstraint.addAll(
                        currentPart.OBJECT_TO_VALIDATE.getArray().size() < partStructure.schema.getArray().size()
                                ? currentPart.OBJECT_TO_VALIDATE.getKeys()
                                : partStructure.schema.getKeys()
                );
                case ADDITIONAL_ITEMS -> {
                    boolean objectIsBigger = currentPart.OBJECT_TO_VALIDATE.getArray().size() > partStructure.schema.getArray().size();
                    if (objectIsBigger) {
                        keysForConstraint.addAll(currentPart.OBJECT_TO_VALIDATE.getKeys());
                        partStructure.schema.getKeys().forEach(keysForConstraint::remove);
                    }
                }
            }
            return keysForConstraint;
        }

        private long countContains(final JsonSchemaEnforcerPart currentPart, final RefResolvedSchemaPart partStructure) {
            if (!currentPart.resolvableConstraints.containsKey("contains")) {
                throw missingProperty(SourceOfProblem.SCHEMA, partStructure.canonicalPath, partStructure.propertyName,
                        partStructure.propertyName + " requires the (contains) constraint to run.", null);
            }
            return validateContains(currentPart, currentPart.resolvableConstraints.get("contains"));
        }

        private Pattern getRegexPattern(final RefResolvedSchemaPart partStructure, final String regexKey) {
            try {
                return Pattern.compile(regexKey);
            } catch (Exception e) {
                throw valueUnexpected(SourceOfProblem.SCHEMA, partStructure.canonicalPath, partStructure.propertyName,
                        "Key in patternProperties (" + regexKey + ") was not a valid regex.", e);
            }
        }

        /* ERROR MANAGEMENT */
        private SchemaException missingProperty(String parentOfMissingProperty, String propertyName, String reason) {
            return missingProperty(SourceOfProblem.OBJECT_TO_VALIDATE, parentOfMissingProperty, propertyName, reason, null);
        }

        private SchemaException missingProperty(SourceOfProblem source, String parentOfMissingProperty, String propertyName, String reason, Throwable cause) {
            String message = switch (source) {
                case SCHEMA ->
                        "Missing property in schema at: " + schemaErrorKeyChain(propertyName, parentOfMissingProperty);
                case OBJECT_TO_VALIDATE -> "Missing property.\n" +
                        "Schema constraint violated: " + schemaErrorKeyChain(propertyName, parentOfMissingProperty);
            };
            return enhanceErrorMessageReasoning(source, message, reason, cause);
        }

        private SchemaException valueDifferentType(SourceOfProblem source, String parentOfMissingProperty, String propertyName, String reason) {
            return valueDifferentType(source, parentOfMissingProperty, propertyName, reason, null);
        }

        private SchemaException valueDifferentType(SourceOfProblem source, String parentOfMissingProperty, String propertyName, KeyDifferentTypeException cause) {
            return valueDifferentType(source, parentOfMissingProperty, propertyName, "", cause);
        }

        private SchemaException valueDifferentType(SourceOfProblem source, String parentOfMissingProperty, String propertyName, String reason, KeyDifferentTypeException cause) {
            String message = switch (source) {
                case SCHEMA ->
                        "Wrong type for schema property: " + schemaErrorKeyChain(propertyName, parentOfMissingProperty);
                case OBJECT_TO_VALIDATE -> "Mismatched data type.\n" +
                        "Schema constraint violated: " + schemaErrorKeyChain(propertyName, parentOfMissingProperty);
            };
            return enhanceErrorMessageReasoning(source, message, reason, cause);
        }

        private SchemaException valueUnexpected(SourceOfProblem source, String parentOfMissingProperty, String propertyName, String reason) {
            return valueUnexpected(source, parentOfMissingProperty, propertyName, reason, null);
        }

        private SchemaException valueUnexpected(SourceOfProblem source, String parentOfMissingProperty, String propertyName, String reason, Throwable cause) {
            String message = switch (source) {
                case SCHEMA ->
                        "Unexpected value for schema property: " + schemaErrorKeyChain(propertyName, parentOfMissingProperty);
                case OBJECT_TO_VALIDATE -> "Unexpected value.\n" +
                        "Schema constraint violated: " + schemaErrorKeyChain(propertyName, parentOfMissingProperty);
            };
            return enhanceErrorMessageReasoning(source, message, reason, cause);
        }

        private SchemaException enhanceErrorMessageReasoning(SourceOfProblem source, String message, String reason, Throwable cause) {
            message += (reason.equals("") ? "" : "\n" + reason);
            message += getErrorCauseMessage(cause);
            return doThrow(source, message);
        }

        private SchemaException doThrow(SourceOfProblem source, String message) {
            if (source == SourceOfProblem.SCHEMA) {
                return new InvalidSchemaException(message);
            }
            return new SchemaViolationException(message);
        }

        private String getErrorCauseMessage(Throwable cause) {
            if (cause != null) {
                if (cause instanceof KeyDifferentTypeException) {
                    String[] causeMessage = cause.getMessage().split("\\. ");
                    if (causeMessage.length >= 2) {
                        return "\n" + causeMessage[1];
                    }
                }
                return "\n\nCaused By:\n"
                        + cause
                        .getMessage()
                        .replaceAll("\\r?", "")
                        .replaceFirst("Unexpected value\\.(\\n?)", "")
                        .stripTrailing();
            }
            return "";
        }

        private String schemaErrorKeyChain(String propertyName, String path) {
            return (path.equals("") ? "<base element>" : path) + "." + propertyName;
        }
    }

    private interface Lambda<E> {
        E doWork();
    }
}
