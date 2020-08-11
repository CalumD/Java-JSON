package api;

import core.JSType;
import exceptions.SchemaException;
import exceptions.json.KeyDifferentTypeException;
import exceptions.json.KeyNotFoundException;
import exceptions.schema.InvalidSchemaException;
import exceptions.schema.SchemaViolationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class JsonSchemaEnforcer implements IJsonSchemaEnforcer {

    private enum SourceOfProblem {SCHEMA, OBJECT_TO_VALIDATE}

    public static boolean validateStrict(IJson objectToValidate, IJson againstSchema) {
        return new JsonSchemaEnforcer().validateWithOutReasoning(objectToValidate, againstSchema);
    }

    public static boolean validate(IJson objectToValidate, IJson againstSchema) {
        return new JsonSchemaEnforcer().validateWithReasoning(objectToValidate, againstSchema);
    }

    @Override
    public boolean validateWithReasoning(IJson objectToValidate, IJson againstSchema) throws SchemaException {
        return new JsonSchemaEnforcer().instanceValidate(objectToValidate, againstSchema);
    }

    /**
     * The single external accessible entry point into the Schema Validation Logic
     */
    private boolean instanceValidate(IJson objectToValidate, IJson schema) throws SchemaException {
        if (schema == null) {
            throw new SchemaException("You cannot validate against a null schema.");
        }
        if (objectToValidate == null) {
            throw new SchemaException("A null object cannot be validated against a schema.");
        }
        if (schema.getDataType() != JSType.OBJECT) {
            throw new SchemaException("JSON Schemas MUST be a valid JSON Object with defined mandatory keys and structure. You provided a " + schema.getDataType() + ".");
        }
        Set<String> subSchemaReferencesSeen = Stream.of("").collect(Collectors.toSet());
        return (new JsonSchemaEnforcerPart(objectToValidate, schema, schema, "", subSchemaReferencesSeen, new HashSet<>())).enforce();
    }

    private final class JsonSchemaEnforcerPart {
        private final IJson SCHEMA_REFERENCE; // The Full Schema
        private final IJson OBJECT_TO_VALIDATE; // The Current object to validate
        private final String PATH_IN_SCHEMA; // The Key in the Full Schema we are at (Not Necessarily valid json key)
        private final IJson SCHEMA_SUBSET; // The Schema Subset at the given key
        private final Set<String> SCHEMA_REFERENCES_SEEN; // The Schema Subset at the given key
        private final Set<String> CONSTRAINTS_SEEN;


        private JsonSchemaEnforcerPart(IJson objectToValidate, IJson fullSchema, IJson currentSchemaFragment, String schemaKeySoFar, Set<String> schemaReferencesSeen, Set<String> constraintsSeen) {
            this.OBJECT_TO_VALIDATE = objectToValidate;
            this.SCHEMA_REFERENCE = fullSchema;
            this.PATH_IN_SCHEMA = schemaKeySoFar.startsWith(".") ? schemaKeySoFar.substring(1) : schemaKeySoFar;
            this.SCHEMA_SUBSET = currentSchemaFragment;
            this.SCHEMA_REFERENCES_SEEN = schemaReferencesSeen;
            this.CONSTRAINTS_SEEN = constraintsSeen;
        }

        private boolean enforce() {
            if (this.SCHEMA_SUBSET.getDataType() != JSType.OBJECT) {
                throw doThrow(SourceOfProblem.SCHEMA, "Schema MUST be an object.");
            }
            for (String key : this.SCHEMA_SUBSET.getKeys()) {
                if (!CONSTRAINTS_SEEN.contains(key)) {
                    routeConstraint(key, CONSTRAINTS_SEEN);
                }
            }
            if (CONSTRAINTS_SEEN.contains("$ref")) {
                CONSTRAINTS_SEEN.remove("$ref");
                try {
                    String referencedSubSchema = convert$RefToJsonKey(this.SCHEMA_SUBSET.getStringAt("$ref"));
                    if (SCHEMA_REFERENCES_SEEN.contains(referencedSubSchema)) {
                        throw valueUnexpected(SourceOfProblem.SCHEMA, PATH_IN_SCHEMA, "$ref",
                                "Schema Reference has a cyclic dependency.");
                    } else {
                        SCHEMA_REFERENCES_SEEN.add(referencedSubSchema);
                        subEnforce(
                                this,
                                this.SCHEMA_REFERENCE.getJSONObjectAt(referencedSubSchema), // TODO: Refactor this assumption that the object is in THIS schema to support web-based schema references.
                                this.PATH_IN_SCHEMA + ".$ref",
                                CONSTRAINTS_SEEN
                        );
                    }
                } catch (KeyNotFoundException e) {
                    throw missingProperty(SourceOfProblem.SCHEMA, PATH_IN_SCHEMA, "$ref", e);
                } catch (KeyDifferentTypeException e) {
                    throw valueDifferentType(SourceOfProblem.SCHEMA, PATH_IN_SCHEMA, "$ref",
                            "$ref must link to a valid sub-schema object.", e);
                }
            }

            // EVERYTHING HAS VALIDATED NOW
            return true;
        }

        private void routeConstraint(String key, Set<String> constraintsSeen) {
            switch (key) {
                case "type":
                    validateType(this);
                    break;
                case "enum":
                    validateEnum(this);
                    break;
                case "const":
                    if (!this.OBJECT_TO_VALIDATE.equals(this.SCHEMA_SUBSET.getAnyAt("const"))) {
                        throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, this.PATH_IN_SCHEMA, "const",
                                "Value MUST match the schema's constant.");
                    }
                    break;
                case "allOf":
                    validateAllOf(this);
                    break;
                case "anyOf":
                    validateAnyOf(this);
                    break;
                case "oneOf":
                    validateOneOf(this);
                    break;
                case "not":
                    validateNot(this);
                    break;
                case "if":
                    validateIf(this);
                    break;
                case "multipleOf":
                    validateMultipleOf(this);
                    break;
                case "maximum":
                    validateMaximum(this);
                    break;
                case "exclusiveMaximum":
                    validateExclusiveMaximum(this);
                    break;
                case "minimum":
                    validateMinimum(this);
                    break;
                case "exclusiveMinimum":
                    validateExclusiveMinimum(this);
                    break;
                case "maxLength":
                    validateMaxLength(this);
                    break;
                case "minLength":
                    validateMinLength(this);
                    break;
                case "pattern":
                    validatePattern(this);
                    break;
                case "format":
                    validateFormat(this);
                    break;
                case "maxItems":
                    validateMaxItems(this);
                    break;
                case "minItems":
                    validateMinItems(this);
                    break;
                case "uniqueItems":
                    validateUniqueItems(this);
                    break;
                case "maxContains":
                    validateMaxContains(this);
                    break;
                case "minContains":
                    validateMinContains(this);
                    break;
                case "items":
                    validateItems(this);
                    break;
                case "additionalItems":
                    validateAdditionalItems(this);
                    break;
                case "contains":
                    validateContains(this);
                    break;
                case "unevaluatedItems":
                    validateUnevaluatedItems(this);
                    break;
                case "maxProperties":
                    validateMaxProperties(this);
                    break;
                case "minProperties":
                    validateMinProperties(this);
                    break;
                case "required":
                    validateRequired(this);
                    break;
                case "dependentRequired":
                    validateDependentRequired(this);
                    break;
                case "properties":
                    validateProperties(this);
                    break;
                case "patternProperties":
                    validatePatternProperties(this);
                    break;
                case "additionalProperties":
                    validateAdditionalProperties(this);
                    break;
                case "propertyNames":
                    validatePropertyNames(this);
                    break;
                case "unevaluatedProperties":
                    validateUnevaluatedProperties(this);
                    break;
                case "$ref":
                    break;
//                case "Some Unsupported Keyword!":
//                    throw new InvalidSchemaException("Unsupported constraint used (" + key + ").\n" +
//                            "Unable to verify all schema requirements, so failing.");
//                    break;
                default:
                    // Ignore unknown properties.
                    return;
            }
            constraintsSeen.add(key);
        }
    }


    /* CONDITIONAL VALIDATIONS */
    private void validateAllOf(JsonSchemaEnforcerPart currentPart) {
        try {
            if (currentPart.SCHEMA_SUBSET.getArrayAt("allOf").size() == 0) {
                throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "allOf",
                        "Array must contain at least 1 sub-schema.");
            } else {
                for (String arrayIndex : currentPart.SCHEMA_SUBSET.getKeysOf("allOf")) {
                    subEnforce(currentPart, currentPart.SCHEMA_SUBSET.getAnyAt("allOf[" + arrayIndex + "]"),
                            currentPart.PATH_IN_SCHEMA + ".allOf[" + arrayIndex + "]");
                }
            }
        } catch (KeyDifferentTypeException e) {
            throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "allOf", e);
        }
    }

    private void validateAnyOf(JsonSchemaEnforcerPart currentPart) {
        try {
            if (currentPart.SCHEMA_SUBSET.getArrayAt("anyOf").size() == 0) {
                throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "anyOf",
                        "Array must contain at least 1 sub-schema.");
            } else {
                for (String arrayIndex : currentPart.SCHEMA_SUBSET.getKeysOf("anyOf")) {
                    try {
                        subEnforce(currentPart, currentPart.SCHEMA_SUBSET.getAnyAt("anyOf[" + arrayIndex + "]"),
                                currentPart.PATH_IN_SCHEMA + ".anyOf[" + arrayIndex + "]");
                        return;
                    } catch (SchemaException e) {
                        // Trapping exceptions as we won't necessarily match all of these.
                    }
                }
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "anyOf",
                        "Provided json failed to match any of the sub-schemas provided.");
            }
        } catch (KeyDifferentTypeException e) {
            throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "anyOf", e);
        }
    }

    private void validateOneOf(JsonSchemaEnforcerPart currentPart) {
        try {
            if (currentPart.SCHEMA_SUBSET.getArrayAt("oneOf").size() == 0) {
                throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "oneOf",
                        "Array must contain at least 1 sub-schema.");
            } else {
                long schemasMatched = 0;
                for (String arrayIndex : currentPart.SCHEMA_SUBSET.getKeysOf("oneOf")) {
                    try {
                        subEnforce(currentPart, currentPart.SCHEMA_SUBSET.getAnyAt("oneOf[" + arrayIndex + "]"),
                                currentPart.PATH_IN_SCHEMA + ".oneOf[" + arrayIndex + "]");
                    } catch (SchemaException e) {
                        // Trapping exceptions as we won't necessarily match all of these.
                        schemasMatched--;
                    }
                    if (++schemasMatched > 1) {
                        throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "oneOf",
                                "Json validated against more than one sub-schema.");
                    }
                }
                if (schemasMatched != 1) {
                    throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "oneOf",
                            "Provided json failed to match any of the sub-schemas provided.");
                }
            }
        } catch (KeyDifferentTypeException e) {
            throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "oneOf", e);
        }
    }

    private void validateNot(JsonSchemaEnforcerPart currentPart) {
        try {
            subEnforce(currentPart, currentPart.SCHEMA_SUBSET.getAnyAt("not"), currentPart.PATH_IN_SCHEMA + ".not");
        } catch (SchemaException e) {
            // Ignore the failure - that's what we wanted!
            return;
        }
        throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "not",
                "Json successfully validated against the sub-schema, but a failure was required.");
    }

    private void validateIf(JsonSchemaEnforcerPart currentPart) {
        boolean validatedAgainstIf = false;
        try {
            subEnforce(currentPart, currentPart.SCHEMA_SUBSET.getAnyAt("if"), currentPart.PATH_IN_SCHEMA + ".if");
            validatedAgainstIf = true;
        } catch (SchemaException e) {
            // The IF part of a schema shouldn't effect the result - just which of the then/else is relevant
        }

        if (validatedAgainstIf && currentPart.SCHEMA_SUBSET.contains("then")) {
            subEnforce(currentPart, currentPart.SCHEMA_SUBSET.getAnyAt("then"), currentPart.PATH_IN_SCHEMA + ".then");
        } else if (!validatedAgainstIf && currentPart.SCHEMA_SUBSET.contains("else")) {
            subEnforce(currentPart, currentPart.SCHEMA_SUBSET.getAnyAt("else"), currentPart.PATH_IN_SCHEMA + ".else");
        }
    }

    private void validateType(JsonSchemaEnforcerPart currentPart) {
        IJson type = currentPart.SCHEMA_SUBSET.getAnyAt("type");
        switch (type.getDataType()) {
            case STRING:
                ArrayList<String> allowedType = new ArrayList<>(1);
                allowedType.add(currentPart.SCHEMA_SUBSET.getStringAt("type").toLowerCase());
                validateType(currentPart, allowedType);
                break;
            case ARRAY:
                Set<String> distinctTypes = new HashSet<>(10);
                for (IJson distinctType : currentPart.SCHEMA_SUBSET.getArrayAt("type")) {
                    try {
                        distinctTypes.add(distinctType.getString());
                    } catch (KeyDifferentTypeException e) {
                        throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "type",
                                "All values in the (type) property MUST be a valid, distinct STRING.", e);
                    }
                }
                if (distinctTypes.size() == 0) {
                    throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "type",
                            "You must provide at least one valid data type restriction.");
                }
                validateType(currentPart, new ArrayList<>(distinctTypes));
                break;
            default:
                throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "type",
                        "Expected one of " + Arrays.asList("STRING", "ARRAY") + ", got " + type.getDataType() + ".");
        }
    }

    private void validateType(JsonSchemaEnforcerPart currentPart, List<String> allowedTypes) {
        JSType currentDataType = currentPart.OBJECT_TO_VALIDATE.getDataType();
        for (String allowedType : allowedTypes) {
            switch (allowedType) {
                case "array":
                case "list":
                    if (currentDataType == JSType.ARRAY) return;
                    break;
                case "boolean":
                    if (currentDataType == JSType.BOOLEAN) return;
                    break;
                case "long":
                case "integer":
                    if (currentDataType == JSType.LONG) return;
                    break;
                case "double":
                    if (currentDataType == JSType.DOUBLE) return;
                    break;
                case "number":
                    if (currentDataType == JSType.DOUBLE || currentDataType == JSType.LONG) return;
                    break;
                case "string":
                    if (currentDataType == JSType.STRING) return;
                    break;
                case "object":
                    if (currentDataType == JSType.OBJECT) return;
                    break;
                case "null":
                case "undefined":
                default:
                    throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "type",
                            "Unrecognised/Unsupported type provided (" + allowedType + ").");
            }
        }
        allowedTypes.replaceAll(String::toUpperCase);
        throw valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "type",
                "Expected one of " + allowedTypes + ", got " + currentDataType + ".");
    }

    private void validateEnum(JsonSchemaEnforcerPart currentPart) {
        try {
            for (IJson instance : currentPart.SCHEMA_SUBSET.getArrayAt("enum")) {
                if (currentPart.OBJECT_TO_VALIDATE.equals(instance)) {
                    return;
                }
            }
            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "enum",
                    "Object did not match any options provided by the enum.");
        } catch (KeyDifferentTypeException e) {
            throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "enum", e);
        }
    }


    /* NUMBERS */
    private void validateMultipleOf(JsonSchemaEnforcerPart currentPart) {
        BigDecimal numberFromSchema = getComparableNumber(currentPart, "multipleOf", true);
        if (numberFromSchema.equals(BigDecimal.ZERO)) {
            throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "multipleOf",
                    "You cannot use a multiple of 0.");
        }
        if ((getComparableNumber(currentPart, "multipleOf", false).remainder(numberFromSchema))
                .compareTo(BigDecimal.ZERO) != 0) {
            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "multipleOf",
                    "Value was not a multiple of schema value.");
        }
    }

    private void validateMaximum(JsonSchemaEnforcerPart currentPart) {
        if ((getComparableNumber(currentPart, "maximum", false)
                .compareTo(getComparableNumber(currentPart, "maximum", true))) > 0) {
            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "maximum",
                    "Value was greater than the upper bound.");
        }
    }

    private void validateExclusiveMaximum(JsonSchemaEnforcerPart currentPart) {
        if ((getComparableNumber(currentPart, "exclusiveMaximum", false)
                .compareTo(getComparableNumber(currentPart, "exclusiveMaximum", true))) >= 0) {
            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "exclusiveMaximum",
                    "Value was greater than or equal to the upper bound.");
        }
    }

    private void validateMinimum(JsonSchemaEnforcerPart currentPart) {
        if ((getComparableNumber(currentPart, "minimum", false)
                .compareTo(getComparableNumber(currentPart, "minimum", true))) < 0) {
            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "minimum",
                    "Value was lower than the lower bound.");
        }
    }

    private void validateExclusiveMinimum(JsonSchemaEnforcerPart currentPart) {
        if ((getComparableNumber(currentPart, "exclusiveMinimum", false)
                .compareTo(getComparableNumber(currentPart, "exclusiveMinimum", true))) <= 0) {
            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "exclusiveMinimum",
                    "Value was lower than or equal to the lower bound.");
        }
    }


    /* OBJECTS */
    private void validateProperties(JsonSchemaEnforcerPart currentPart) {
        IJson propertiesObject;
        try {
            propertiesObject = currentPart.SCHEMA_SUBSET.getJSONObjectAt("properties");
        } catch (KeyDifferentTypeException e) {
            throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "properties",
                    "Properties constraint must be an object.", e);
        }
        if (currentPart.OBJECT_TO_VALIDATE.getDataType() != JSType.OBJECT) {
            throw valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "properties",
                    "Properties constraint can only be run against an Object.");
        }
        for (String key : propertiesObject.getKeys()) {
            if (currentPart.OBJECT_TO_VALIDATE.contains(key)) {
                String keyInSchema = (key.contains(" ") || key.contains("\\")) ? "[`" + key + "`]" : "." + key;
                IJson subSchema;
                try {
                    subSchema = currentPart.SCHEMA_SUBSET.getJSONObjectAt("properties" + keyInSchema);
                } catch (KeyDifferentTypeException e) {
                    throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "properties" + keyInSchema, e);
                }
                subEnforce(currentPart, currentPart.OBJECT_TO_VALIDATE.getAnyAt(key), subSchema,
                        currentPart.PATH_IN_SCHEMA + ".properties" + keyInSchema);
            }
        }
    }

    private void validateAdditionalProperties(JsonSchemaEnforcerPart currentPart) {
        JSType schemaType = currentPart.SCHEMA_SUBSET.getDataTypeOf("additionalProperties");
        if (schemaType != JSType.BOOLEAN && schemaType != JSType.OBJECT) {
            throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "additionalProperties",
                    "Expected one of " + Arrays.asList("BOOLEAN", "ARRAY") + ", got " + schemaType + ".");
        }
    }

    private void validateRequired(JsonSchemaEnforcerPart currentPart) {
        List<String> requiredKeys = new ArrayList<>();
        try {
            List<IJson> keysAsJson = currentPart.SCHEMA_REFERENCE.getArrayAt("required");
            if (keysAsJson.size() == 0) {
                throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "required",
                        "Must provide at least one required property.");
            }
            for (IJson key : keysAsJson) {
                requiredKeys.add(key.getString());
            }
        } catch (KeyDifferentTypeException e) {
            throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "required",
                    "Required constraint must be a non-empty array of strings representing property names.", e);
        }
        if (currentPart.OBJECT_TO_VALIDATE.getDataType() != JSType.OBJECT) {
            throw valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "required",
                    "Required constraint can only be applied to Object properties.");
        }

        for (String requiredKey : requiredKeys) {
            if (!currentPart.OBJECT_TO_VALIDATE.contains(requiredKey)) {
                throw missingProperty(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "required",
                        "Property (" + requiredKey + ") is mandatory, but couldn't be found.");
            }
        }
    }

    private void validateMinProperties(JsonSchemaEnforcerPart currentPart) {
        long minProperties = getNonNegativeInteger(currentPart, "minProperties");
        if (currentPart.OBJECT_TO_VALIDATE.getDataType() != JSType.OBJECT) {
            throw valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "minProperties",
                    "This constraint can only be used against an object.");
        }
        if (currentPart.OBJECT_TO_VALIDATE.getKeys().size() < minProperties) {
            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "minProperties",
                    "Object doesn't have enough properties to pass the constraint.");
        }
    }

    private void validateMaxProperties(JsonSchemaEnforcerPart currentPart) {
        long maxProperties = getNonNegativeInteger(currentPart, "maxProperties");
        if (currentPart.OBJECT_TO_VALIDATE.getDataType() != JSType.OBJECT) {
            throw valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "maxProperties",
                    "This constraint can only be used against an object.");
        }
        if (currentPart.OBJECT_TO_VALIDATE.getKeys().size() > maxProperties) {
            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "maxProperties",
                    "Object has more properties than the constraint allows.");
        }
    }

    private void validateUnevaluatedProperties(JsonSchemaEnforcerPart currentPart) {

    }

    private void validatePropertyNames(JsonSchemaEnforcerPart currentPart) {

    }

    private void validatePatternProperties(JsonSchemaEnforcerPart currentPart) {

    }

    private void validateDependentRequired(JsonSchemaEnforcerPart currentPart) {

    }


    /* ARRAYS */
    private void validateUnevaluatedItems(JsonSchemaEnforcerPart currentPart) {

    }

    private void validateContains(JsonSchemaEnforcerPart currentPart) {

    }

    private void validateAdditionalItems(JsonSchemaEnforcerPart currentPart) {

    }

    private void validateItems(JsonSchemaEnforcerPart currentPart) {

    }

    private void validateMinContains(JsonSchemaEnforcerPart currentPart) {

    }

    private void validateMaxContains(JsonSchemaEnforcerPart currentPart) {


    }

    private void validateUniqueItems(JsonSchemaEnforcerPart currentPart) {


    }

    private void validateMinItems(JsonSchemaEnforcerPart currentPart) {

    }

    private void validateMaxItems(JsonSchemaEnforcerPart currentPart) {

    }


    /* STRINGS */
    private void validatePattern(JsonSchemaEnforcerPart currentPart) {
        Pattern verifiedPattern;
        String objectToConstrain;
        try {
            verifiedPattern = Pattern.compile(currentPart.SCHEMA_SUBSET.getStringAt("pattern"));
        } catch (KeyDifferentTypeException e) {
            throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "pattern", e);
        } catch (Exception e) {
            throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "pattern",
                    "Pattern provided was not a valid regex pattern.", e);
        }
        try {
            objectToConstrain = currentPart.OBJECT_TO_VALIDATE.getString();
        } catch (KeyDifferentTypeException e) {
            throw valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "pattern", e);
        }
        if (!objectToConstrain.matches(verifiedPattern.pattern())) {
            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "pattern",
                    "Value did not match the provided pattern.");
        }
    }

    private void validateMinLength(JsonSchemaEnforcerPart currentPart) {
        long minLength;
        String objectToConstrain;
        try {
            minLength = currentPart.SCHEMA_SUBSET.getLongAt("minLength");
            if (minLength < 0) {
                throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "minLength",
                        "Cannot constrain string length using a negative number.");
            }
        } catch (KeyDifferentTypeException e) {
            throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "minLength", e);
        }
        try {
            objectToConstrain = currentPart.OBJECT_TO_VALIDATE.getString();
        } catch (KeyDifferentTypeException e) {
            throw valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "minLength", e);
        }
        if (objectToConstrain.length() < minLength) {
            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "minLength",
                    "String length was shorter than the minimum bound.");
        }
    }

    private void validateMaxLength(JsonSchemaEnforcerPart currentPart) {
        long maxLength;
        String objectToConstrain;
        try {
            maxLength = currentPart.SCHEMA_SUBSET.getLongAt("maxLength");
            if (maxLength < 0) {
                throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "maxLength",
                        "Cannot constrain string length using a negative number.");
            }
        } catch (KeyDifferentTypeException e) {
            throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "maxLength", e);
        }
        try {
            objectToConstrain = currentPart.OBJECT_TO_VALIDATE.getString();
        } catch (KeyDifferentTypeException e) {
            throw valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "maxLength", e);
        }
        if (objectToConstrain.length() > maxLength) {
            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "maxLength",
                    "String length was longer than the maximum bound.");
        }
    }

    private void validateFormat(JsonSchemaEnforcerPart currentPart) {
        String formatToVerify, objectToConstrain;
        try {
            formatToVerify = currentPart.SCHEMA_SUBSET.getStringAt("format");
        } catch (KeyDifferentTypeException e) {
            throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "format", e);
        }
        try {
            objectToConstrain = currentPart.OBJECT_TO_VALIDATE.getString();
        } catch (KeyDifferentTypeException e) {
            throw valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "format", e);
        }

        boolean matched = false;
        try {
            switch (formatToVerify) {
                case "date":
                    // https://rgxdb.com/r/2V9BOC58
                    matched = objectToConstrain.matches("^(?:(?:(?:[1-9]\\d)(?:0[48]|[2468][048]|[13579][26])|(?:(?:[2468][048]|[13579][26])00))([/\\-.])(?:0?2\\1(?:29)))|(?:(?:[0-9]\\d{3})([/\\-.])(?:(?:(?:0?[13578]|" +
                            "1[02])\\2(?:31))|(?:(?:0?[13-9]|1[0-2])\\2(?:29|30))|(?:(?:0?[1-9])|(?:1[0-2]))\\2(?:0?[1-9]|1\\d|2[0-8])))$");
                    break;
                case "time":
                    // https://rgxdb.com/r/2LE6429J with additional nudging
                    matched = objectToConstrain.matches("^((([0]?[1-9]|1[0-2])[:.][0-5][0-9]([:.][0-5][0-9])?(\\.[0-9]{1,10}[zZ])?([ ])?[aApP][mM])|(([0]?[0-9]|1[0-9]|2[0-3])[:.][0-5][0-9]([:.][0-5][0-9])?(\\.[0-9]{1,10}[zZ])?))$");
                    break;
                case "date-time":
                case "datetime":
                    // https://rgxdb.com/r/526K7G5W
                    matched = objectToConstrain.matches("^(?:[+-]?\\d{4}(?!\\d{2}\\b))(?:(-?)(?:(?:0[1-9]|1[0-2])(?:\\1(?:[12]\\d|0[1-9]|3[01]))?|W(?:[0-4]\\d|5[0-2])(?:-?[1-7])?|(?:00[1-9]|0[1-9]\\d|[12]\\d{2}|3(?:[0-5]\\d|6[1-6])))" +
                            "(?:[Tt\\s](?:(?:(?:[01]\\d|2[0-3])(?:(:?)[0-5]\\d)?|24:?00)(?:[.,]\\d+(?!:))?)?(?:\\2[0-5]\\d(?:[.,]\\d+)?)?(?:[zZ]|(?:[+-])(?:[01]\\d|2[0-3]):?(?:[0-5]\\d)?)?)?)?$");
                    break;
                case "duration":
                    // https://rgxdb.com/r/MD2234J
                    matched = objectToConstrain.matches("^(-?)P(?=\\d|T\\d)(?:(\\d+)Y)?(?:(\\d+)M)?(?:(\\d+)([DW]))?(?:T(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+(?:\\.\\d+)?)S)?(?<!T))?$");
                    break;

                case "regex":
                    Pattern.compile(objectToConstrain);
                    matched = true;
                    break;
                case "email":
                    // https://regexlib.com/REDetails.aspx?regexp_id=2558
                    matched = objectToConstrain.matches("^((([!#$%&'*+\\-/=?^_`{|}~\\w])|([!#$%&'*+\\-/=?^_`{|}~\\w]([!#$%&'*+\\-/=?^_`{|}~\\w]|((?<!\\.)\\.(?!\\.)))*[!#$%&'*+\\-/=?^_`{|}~\\w]))[@]\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*)$");
                    break;
                case "phone":
                case "phone-number":
                    // Me
                    matched = objectToConstrain.matches("^(?:\\([+]?\\d{1,3}\\)|[+]?\\d{1,3})?(?:[ -]?(\\(\\d{1,5}\\)|\\d{1,5}))?(?:([ -]?\\d{1,15})|([ -]?\\d{1,4}){1,3})(?:[ ]ext\\.?[ ]?(?:(\\d{1,5})|([ .]\\d{1,2}){1,5}))?$");
                    break;
                case "version":
                case "sem-ver":
                case "semVer":
                    // ME :D
                    matched = objectToConstrain.matches("^(?:[vV](?:ersion)?(?:[: /-])?)?(?<major>[0-9]+)(?<minor>[.][0-9]+)?(?<patch>[.][0-9]+)?" +
                            "(?:-(?<prerelease>[0-9a-zA-Z-]+(?:\\.(?:[0-9a-zA-Z-]+))*)(?<![-.]))?(?:\\+(?<metadata>[0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*)(?<![-.]))?$");
                    break;

                case "hostname":
                    // ME - Fairly loose matcher :/
                    matched = objectToConstrain.matches("^(?![-.])+(?:[a-zA-Z\\d-%]{0,63})(((?<!\\.)\\.(?!\\.))(?:[a-zA-Z\\d-%]{0,63}))*(?<![-.])$");
                    break;
                case "ipv4":
                case "ip":
                    // https://regexlib.com/REDetails.aspx?regexp_id=2685 comment: 5/26/2011 5:37:09 AM
                    matched = objectToConstrain.matches("^(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])){3}$");
                    break;
                case "ipv6":
                    // https://rgxdb.com/r/1DZHMUSH with some from ipv4 implementation
                    matched = objectToConstrain.matches("^(?:(?:(?:[0-9A-Fa-f]{1,4}:){7}(?:[0-9A-Fa-f]{1,4}|:))|(?:(?:[0-9A-Fa-f]{1,4}:){6}(?::[0-9A-Fa-f]{1,4}|(?:(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])" +
                            "(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])){3})|:))|(?:(?:[0-9A-Fa-f]{1,4}:){5}(?:(?:(?::[0-9A-Fa-f]{1,4}){1,2})|(?::(?:(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])" +
                            "(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])){3}))|:))|(?:(?:[0-9A-Fa-f]{1,4}:){4}(?:(?:(?::[0-9A-Fa-f]{1,4}){1,3})|(?:(?::[0-9A-Fa-f]{1,4})?:(?:(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])" +
                            "(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])){3}))|:))|(?:(?:[0-9A-Fa-f]{1,4}:){3}(?:(?:(?::[0-9A-Fa-f]{1,4}){1,4})|(?:(?::[0-9A-Fa-f]{1,4}){0,2}:(?:(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])" +
                            "(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])){3}))|:))|(?:(?:[0-9A-Fa-f]{1,4}:){2}(?:(?:(?::[0-9A-Fa-f]{1,4}){1,5})|(?:(?::[0-9A-Fa-f]{1,4}){0,3}:(?:(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])" +
                            "(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])){3}))|:))|(?:(?:[0-9A-Fa-f]{1,4}:)(?:(?:(?::[0-9A-Fa-f]{1,4}){1,6})|(?:(?::[0-9A-Fa-f]{1,4}){0,4}:(?:(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])" +
                            "(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])){3}))|:))|(?::(?:(?:(?::[0-9A-Fa-f]{1,4}){1,7})|(?:(?::[0-9A-Fa-f]{1,4}){0,5}:(?:(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])" +
                            "(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])){3}))|:)))(?:(%.+)|(/[0-9]{1,2}))?$");
                    break;
                case "mac":
                    // https://rgxdb.com/r/4QCDJ0QF
                    matched = objectToConstrain.matches("^(?:[0-9A-Fa-f]{2}([:-]?)[0-9A-Fa-f]{2})(?:(?:\\1|\\.)(?:[0-9A-Fa-f]{2}([:-]?)[0-9A-Fa-f]{2})){2}$");
                    break;

                case "uri":
                case "url":
                    // https://rgxdb.com/r/2MQXJD5 with some nudging
                    matched = objectToConstrain.matches("^(?:(?:[a-zA-Z][a-zA-Z\\d]*):)(?://(?:(?:[a-zA-Z\\d-._~!$&'()*+,;=%]*)(?::(?:[a-zA-Z\\d-._~!$&'()*+,;=:%]*))?@)?(?:(?:[a-zA-Z\\d-.%]+)|(?:\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|(?:\\[(?:[a-fA-F\\d.:]+)]))?(?::(?:\\d*))?(?:(?:/[a-zA-Z\\d-._~!$&'()*+,;=:@%]*)*)|([/]?(?:[a-zA-Z\\d-._~!$&'()*+,;=:@%]+(?:/[a-zA-Z\\d-._~!$&'()*+,;=:@%]*)*)))?(?:\\?(?:[a-zA-Z\\d-._~!$&'()*+,;=:@%/?]*))?(?:#(?:[a-zA-Z\\d-._~!$&'()*+,;=:@%/?]*))?$");
                    break;
                case "uri-reference":
                case "url-reference":
                    // https://rgxdb.com/r/2MQXJD5 with some nudging
                    matched = objectToConstrain.matches("^(?:(?:[a-zA-Z][a-zA-Z\\d]*):)?(?://(?:(?:[a-zA-Z\\d-._~!$&'()*+,;=%]*)(?::(?:[a-zA-Z\\d-._~!$&'()*+,;=:%]*))?@)?(?:(?:[a-zA-Z\\d-.%]+)|(?:\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|(?:\\[(?:[a-fA-F\\d.:]+)]))?(?::(?:\\d*))?(?:(?:/[a-zA-Z\\d-._~!$&'()*+,;=:@%]*)*)|([/]?(?:[a-zA-Z\\d-._~!$&'()*+,;=:@%]+(?:/[a-zA-Z\\d-._~!$&'()*+,;=:@%]*)*)))?(?:\\?(?:[a-zA-Z\\d-._~!$&'()*+,;=:@%/?]*))?(?:#(?:[a-zA-Z\\d-._~!$&'()*+,;=:@%/?]*))?$");
                    break;
                case "uuid":
                    matched = objectToConstrain.matches("^([0-9A-Fa-f]{8}(?:-[0-9A-Fa-f]{4}){3}-[0-9A-Fa-f]{12})$");
                    break;
                default:
                    throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, "format",
                            "Unrecognised/Unsupported format provided (" + formatToVerify + ").");
            }
        } catch (Exception e) {/*Ignore as we will throw below*/}

        if (!matched) {
            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, "format",
                    "Value failed to match against the format constraint.");
        }
    }


    /* Utils */
    private BigDecimal getComparableNumber(JsonSchemaEnforcerPart currentPart, String propertyKey, boolean isForSchema) {
        IJson constraint = isForSchema ? currentPart.SCHEMA_SUBSET.getAnyAt(propertyKey) : currentPart.OBJECT_TO_VALIDATE;
        JSType constraintType = constraint.getDataType();
        if (constraintType != JSType.DOUBLE && constraintType != JSType.LONG) {
            throw (isForSchema
                    ? valueDifferentType(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, propertyKey,
                    "Expected NUMBER, got " + constraintType + ".")
                    : valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.PATH_IN_SCHEMA, propertyKey,
                    "Value to verify must be a number.")
            );
        }
        return (constraintType == JSType.LONG)
                ? BigDecimal.valueOf(constraint.getLong())
                : BigDecimal.valueOf(constraint.getDouble());
    }

    private long getNonNegativeInteger(JsonSchemaEnforcerPart currentPart, String propertyKey) {
        try {
            long value = currentPart.SCHEMA_REFERENCE.getLongAt(propertyKey);
            if (value < 0) {
                throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, propertyKey,
                        "Value must be >= 0.");
            }
            return value;
        } catch (KeyDifferentTypeException e) {
            throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.PATH_IN_SCHEMA, propertyKey,
                    "Constraint must provide a non-negative integer.", e);
        }
    }

    private String convert$RefToJsonKey(String $ref) {

        // TODO:  ACTUALLY IMPLEMENT THIS METHOD
        return $ref;
    }

    private void subEnforce(JsonSchemaEnforcerPart currentPart, IJson updatedObjectToValidate, IJson updatedSubSchema, String updatedPathInSchema) {
        new JsonSchemaEnforcerPart(
                updatedObjectToValidate,
                currentPart.SCHEMA_REFERENCE,
                updatedSubSchema,
                updatedPathInSchema,
                currentPart.SCHEMA_REFERENCES_SEEN,
                new HashSet<>()
        ).enforce();
    }

    private void subEnforce(JsonSchemaEnforcerPart currentPart, IJson updatedSubSchema, String updatedPathInSchema) {
        subEnforce(currentPart, updatedSubSchema, updatedPathInSchema, new HashSet<>());
    }

    private void subEnforce(JsonSchemaEnforcerPart currentPart, IJson updatedSubSchema, String updatedPathInSchema, Set<String> constraintsSeen) {
        new JsonSchemaEnforcerPart(
                currentPart.OBJECT_TO_VALIDATE,
                currentPart.SCHEMA_REFERENCE,
                updatedSubSchema,
                updatedPathInSchema,
                currentPart.SCHEMA_REFERENCES_SEEN,
                constraintsSeen
        ).enforce();
    }


    /* ERROR MANAGEMENT */
    private SchemaException missingProperty(SourceOfProblem source, String parentOfMissingProperty, String propertyName) {
        return missingProperty(source, parentOfMissingProperty, propertyName, "", null);
    }

    private SchemaException missingProperty(SourceOfProblem source, String parentOfMissingProperty, String propertyName, String reason) {
        return missingProperty(source, parentOfMissingProperty, propertyName, reason, null);
    }

    private SchemaException missingProperty(SourceOfProblem source, String parentOfMissingProperty, String propertyName, Throwable cause) {
        return missingProperty(source, parentOfMissingProperty, propertyName, "", cause);
    }

    private SchemaException missingProperty(SourceOfProblem source, String parentOfMissingProperty, String propertyName, String reason, Throwable cause) {
        String message = "Invalid.";
        switch (source) {
            case SCHEMA:
                message = "Missing property in schema at: " + schemaErrorKeyChain(propertyName, parentOfMissingProperty);
                break;
            case OBJECT_TO_VALIDATE:
                message = "Missing property.\n" +
                        "Schema constraint violated: " + schemaErrorKeyChain(propertyName, parentOfMissingProperty);
                break;
        }
        message += (reason.equals("") ? "" : "\n" + reason);
        message += getErrorCauseMessage(cause);
        return cause == null
                ? doThrow(source, message)
                : doThrow(source, message, cause);
    }

    private SchemaException valueDifferentType(SourceOfProblem source, String parentOfMissingProperty, String propertyName) {
        return valueDifferentType(source, parentOfMissingProperty, propertyName, "");
    }

    private SchemaException valueDifferentType(SourceOfProblem source, String parentOfMissingProperty, String propertyName, String reason) {
        return valueDifferentType(source, parentOfMissingProperty, propertyName, reason, null);
    }

    private SchemaException valueDifferentType(SourceOfProblem source, String parentOfMissingProperty, String propertyName, KeyDifferentTypeException cause) {
        return valueDifferentType(source, parentOfMissingProperty, propertyName, "", cause);
    }

    private SchemaException valueDifferentType(SourceOfProblem source, String parentOfMissingProperty, String propertyName, String reason, KeyDifferentTypeException cause) {
        String message = "Invalid.";
        switch (source) {
            case SCHEMA:
                message = "Wrong type for schema property: " + schemaErrorKeyChain(propertyName, parentOfMissingProperty);
                break;
            case OBJECT_TO_VALIDATE:
                message = "Mismatched data type.\n" +
                        "Schema constraint violated: " + schemaErrorKeyChain(propertyName, parentOfMissingProperty);
                break;
        }
        message += (reason.equals("") ? "" : "\n" + reason);
        message += getErrorCauseMessage(cause);
        return cause == null
                ? doThrow(source, message)
                : doThrow(source, message, cause);
    }

    private SchemaException valueUnexpected(SourceOfProblem source, String parentOfMissingProperty, String propertyName, String reason) {
        return valueUnexpected(source, parentOfMissingProperty, propertyName, reason, null);
    }

    private SchemaException valueUnexpected(SourceOfProblem source, String parentOfMissingProperty, String propertyName, String reason, Throwable cause) {
        String message = "Invalid.";
        switch (source) {
            case SCHEMA:
                message = "Unexpected value for schema property: " + schemaErrorKeyChain(propertyName, parentOfMissingProperty);
                break;
            case OBJECT_TO_VALIDATE:
                message = "Unexpected value.\n" +
                        "Schema constraint violated: " + schemaErrorKeyChain(propertyName, parentOfMissingProperty);
                break;
        }
        message += (reason.equals("") ? "" : "\n" + reason);
        message += getErrorCauseMessage(cause);
        return cause == null
                ? doThrow(source, message)
                : doThrow(source, message, cause);
    }

    private SchemaException doThrow(SourceOfProblem source, String message) {
        switch (source) {
            case SCHEMA:
                return new InvalidSchemaException(message);
            case OBJECT_TO_VALIDATE:
                return new SchemaViolationException(message);
            default:
                return new SchemaException(message);
        }
    }

    private SchemaException doThrow(SourceOfProblem source, String message, Throwable cause) {
        switch (source) {
            case SCHEMA:
                return new InvalidSchemaException(message, cause);
            case OBJECT_TO_VALIDATE:
                return new SchemaViolationException(message, cause);
            default:
                return new SchemaException(message, cause);
        }
    }

    private String getErrorCauseMessage(Throwable cause) {
        if (cause != null) {
            if (cause instanceof KeyDifferentTypeException) {
                String[] causeMessage = cause.getMessage().split("\\. ");
                if (causeMessage.length >= 2) {
                    return "\n" + causeMessage[1];
                }
            }
            return "\n(" + cause.getMessage().split("\\r?\\n")[0] + ")";
        }
        return "";
    }

    private String schemaErrorKeyChain(String propertyName, String path) {
        return (path.equals("") ? "<base element>" : path) + "." + propertyName;
    }
}
