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
        return (new JSONSchemaEnforcerPart(objectToValidate, schema, "")).enforce();
    }

    private static final class JSONSchemaEnforcerPart {
        private final IJson SCHEMA_REFERENCE; // The Full Schema
        private final IJson OBJECT_TO_VALIDATE; // The Current object to validate
        private final String KEY_SO_FAR; // The Key in the Full Schema we are at
        private final IJson SCHEMA_SUBSET; // The Schema Subset at the given key

        private JSONSchemaEnforcerPart(IJson objectToValidate, IJson schemaReference, String schemaKeySoFar) {
            this.OBJECT_TO_VALIDATE = objectToValidate;
            this.SCHEMA_REFERENCE = schemaReference;
            this.KEY_SO_FAR = schemaKeySoFar.startsWith(".") ? schemaKeySoFar.substring(1) : schemaKeySoFar;
            this.SCHEMA_SUBSET = schemaReference.getAnyAt(this.KEY_SO_FAR);
        }

        private boolean enforce() {
            if (this.SCHEMA_SUBSET.getDataType() != JSType.OBJECT) {
                throw doThrow(SourceOfProblem.SCHEMA, "Schema MUST be an object.");
            }
            Set<String> constraintsSeen = new HashSet<>();
            for (String key : this.SCHEMA_SUBSET.getKeys()) {
                routeConstraint(key, constraintsSeen);
            }
            if (constraintsSeen.contains("$ref")) {
                constraintsSeen.remove("$ref");
                try {
                    String referencedSubSchema = ""/* TODO: Get the $REF referenced; */;
                    String currentReferencedSubSchemaKey = null;
                    IJson ref = this.SCHEMA_SUBSET.getJSONObjectAt(referencedSubSchema);
                    for (String refKey : ref.getKeys()) {
                        currentReferencedSubSchemaKey = refKey;
                        if (!constraintsSeen.contains(refKey)) {
                            routeConstraint(refKey, constraintsSeen);
                        }
                    }
                } catch (KeyNotFoundException e) {
                    throw missingProperty(SourceOfProblem.SCHEMA, KEY_SO_FAR, "$ref", e);
                } catch (KeyDifferentTypeException e) {
                    throw valueDifferentType(SourceOfProblem.SCHEMA, KEY_SO_FAR, "$ref",
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
                    constraintsSeen.add("type");
                    break;
                case "enum":
                    validateEnum(this);
                    constraintsSeen.add("enum");
                    break;
                case "const":
                    if (!this.OBJECT_TO_VALIDATE.equals(this.SCHEMA_SUBSET.getAnyAt("const"))) {
                        throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, this.KEY_SO_FAR, "const",
                                "Value MUST match the schema's constant.");
                    }
                    constraintsSeen.add("const");
                    break;
                case "allOf":
                    validateAllOf(this);
                    constraintsSeen.add("allOf");
                    break;
                case "anyOf":
                    validateAnyOf(this);
                    constraintsSeen.add("anyOf");
                    break;
                case "oneOf":
                    validateOneOf(this);
                    constraintsSeen.add("oneOf");
                    break;
                case "not":
                    validateNot(this);
                    constraintsSeen.add("not");
                    break;
                case "if":
                    validateIf(this);
                    constraintsSeen.add("if");
                    break;
                case "multipleOf":
                    validateMultipleOf(this);
                    constraintsSeen.add("multipleOf");
                    break;
                case "maximum":
                    validateMaximum(this);
                    constraintsSeen.add("maximum");
                    break;
                case "exclusiveMaximum":
                    validateExclusiveMaximum(this);
                    constraintsSeen.add("exclusiveMaximum");
                    break;
                case "minimum":
                    validateMinimum(this);
                    constraintsSeen.add("minimum");
                    break;
                case "exclusiveMinimum":
                    validateExclusiveMinimum(this);
                    constraintsSeen.add("exclusiveMinimum");
                    break;
                case "maxLength":
                    validateMaxLength(this);
                    constraintsSeen.add("maxLength");
                    break;
                case "minLength":
                    validateMinLength(this);
                    constraintsSeen.add("minLength");
                    break;
                case "pattern":
                    validatePattern(this);
                    constraintsSeen.add("pattern");
                    break;
                case "format":
                    validateFormat(this);
                    constraintsSeen.add("format");
                    break;
                case "maxItems":
                    validateMaxItems(this);
                    constraintsSeen.add("maxItems");
                    break;
                case "minItems":
                    validateMinItems(this);
                    constraintsSeen.add("minItems");
                    break;
                case "uniqueItems":
                    validateUniqueItems(this);
                    constraintsSeen.add("uniqueItems");
                    break;
                case "maxContains":
                    validateMaxContains(this);
                    constraintsSeen.add("maxContains");
                    break;
                case "minContains":
                    validateMinContains(this);
                    constraintsSeen.add("minContains");
                    break;
                case "items":
                    validateItems(this);
                    constraintsSeen.add("items");
                    break;
                case "additionalItems":
                    validateAdditionalItems(this);
                    constraintsSeen.add("additionalItems");
                    break;
                case "contains":
                    validateContains(this);
                    constraintsSeen.add("contains");
                    break;
                case "unevaluatedItems":
                    validateUnevaluatedItems(this);
                    constraintsSeen.add("unevaluatedItems");
                    break;
                case "maxProperties":
                    validateMaxProperties(this);
                    constraintsSeen.add("maxProperties");
                    break;
                case "minProperties":
                    validateMinProperties(this);
                    constraintsSeen.add("minProperties");
                    break;
                case "required":
                    validateRequired(this);
                    constraintsSeen.add("required");
                    break;
                case "dependentRequired":
                    validateDependentRequired(this);
                    constraintsSeen.add("dependentRequired");
                    break;
                case "properties":
                    validateProperties(this);
                    constraintsSeen.add("properties");
                    break;
                case "patternProperties":
                    validatePatternProperties(this);
                    constraintsSeen.add("patternProperties");
                    break;
                case "additionalProperties":
                    validateAdditionalProperties(this);
                    constraintsSeen.add("additionalProperties");
                    break;
                case "propertyNames":
                    validatePropertyNames(this);
                    constraintsSeen.add("propertyNames");
                    break;
                case "unevaluatedProperties":
                    validateUnevaluatedProperties(this);
                    constraintsSeen.add("unevaluatedProperties");
                    break;
                case "$ref":
                    constraintsSeen.add("$ref");
                    break;
                case "title":
                case "description":
                case "comment":
                case "then":
                case "else":
                case "examples":
                case "$id":
                case "$schema":
                    // Ignore these keywords
                    break;
                default:
                    throw new InvalidSchemaException("Unrecognised/Unsupported constraint used (" + key + ").\n" +
                            "Therefore unable to verify all schema requirements.");
            }
        }
    }


    /* ANY */
    private static void validateAllOf(JSONSchemaEnforcerPart currentPart) {
        try {
            if (currentPart.SCHEMA_SUBSET.getArrayAt("allOf").size() == 0) {
                throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.KEY_SO_FAR, "allOf",
                        "Array must contain at least 1 sub-schema.");
            } else {
                for (String arrayIndex : currentPart.SCHEMA_SUBSET.getKeysOf("allOf")) {
                    new JSONSchemaEnforcerPart(currentPart.OBJECT_TO_VALIDATE, currentPart.SCHEMA_REFERENCE,
                            currentPart.KEY_SO_FAR + ".allOf[" + arrayIndex + "]").enforce();
                }
            }
        } catch (KeyDifferentTypeException e) {
            throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.KEY_SO_FAR, "allOf", e);
        }
    }

    private static void validateAnyOf(JSONSchemaEnforcerPart currentPart) {
        try {
            if (currentPart.SCHEMA_SUBSET.getArrayAt("anyOf").size() == 0) {
                throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.KEY_SO_FAR, "anyOf",
                        "Array must contain at least 1 sub-schema.");
            } else {
                for (String arrayIndex : currentPart.SCHEMA_SUBSET.getKeysOf("anyOf")) {
                    try {
                        new JSONSchemaEnforcerPart(currentPart.OBJECT_TO_VALIDATE, currentPart.SCHEMA_REFERENCE,
                                currentPart.KEY_SO_FAR + ".anyOf[" + arrayIndex + "]").enforce();
                        return;
                    } catch (SchemaException e) {
                        // Trapping exceptions as we won't necessarily match all of these.
                    }
                }
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.KEY_SO_FAR, "anyOf",
                        "Provided json failed to match any of the sub-schemas provided.");
            }
        } catch (KeyDifferentTypeException e) {
            throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.KEY_SO_FAR, "anyOf", e);
        }
    }

    private static void validateOneOf(JSONSchemaEnforcerPart currentPart) {
        try {
            if (currentPart.SCHEMA_SUBSET.getArrayAt("oneOf").size() == 0) {
                throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.KEY_SO_FAR, "oneOf",
                        "Array must contain at least 1 sub-schema.");
            } else {
                long schemasMatched = 0;
                for (String key : currentPart.SCHEMA_SUBSET.getKeysOf("oneOf")) {
                    try {
                        new JSONSchemaEnforcerPart(currentPart.OBJECT_TO_VALIDATE, currentPart.SCHEMA_REFERENCE,
                                currentPart.KEY_SO_FAR + ".oneOf[" + key + "]").enforce();
                    } catch (SchemaException e) {
                        // Trapping exceptions as we won't necessarily match all of these.
                        schemasMatched--;
                    }
                    if (++schemasMatched > 1) {
                        throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.KEY_SO_FAR, "oneOf",
                                "Json validated against more than one sub-schema.");
                    }
                }
                if (schemasMatched != 1) {
                    throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.KEY_SO_FAR, "oneOf",
                            "Provided json failed to match any of the sub-schemas provided.");
                }
            }
        } catch (KeyDifferentTypeException e) {
            throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.KEY_SO_FAR, "oneOf", e);
        }
    }

    private static void validateNot(JSONSchemaEnforcerPart currentPart) {
        try {
            new JSONSchemaEnforcerPart(currentPart.OBJECT_TO_VALIDATE, currentPart.SCHEMA_REFERENCE,
                    currentPart.KEY_SO_FAR + ".not").enforce();
        } catch (SchemaException e) {
            // Ignore the failure - that's what we wanted!
            return;
        }
        throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.KEY_SO_FAR, "not",
                "Json successfully validated against the sub-schema, but a failure was required.");
    }

    private static void validateIf(JSONSchemaEnforcerPart currentPart) {
        boolean validatedAgainstIf = false;
        try {
            new JSONSchemaEnforcerPart(currentPart.OBJECT_TO_VALIDATE, currentPart.SCHEMA_REFERENCE,
                    currentPart.KEY_SO_FAR + ".if").enforce();
            validatedAgainstIf = true;
        } catch (SchemaException e) {
            // The IF part of a schema shouldn't effect the result - just which of the then/else is relevant
        }

        if (validatedAgainstIf && currentPart.SCHEMA_SUBSET.contains("then")) {
            new JSONSchemaEnforcerPart(currentPart.OBJECT_TO_VALIDATE, currentPart.SCHEMA_REFERENCE,
                    currentPart.KEY_SO_FAR + ".then").enforce();
        } else if (!validatedAgainstIf && currentPart.SCHEMA_SUBSET.contains("else")) {
            new JSONSchemaEnforcerPart(currentPart.OBJECT_TO_VALIDATE, currentPart.SCHEMA_REFERENCE,
                    currentPart.KEY_SO_FAR + ".else").enforce();
        }
    }

    private static void validateType(JSONSchemaEnforcerPart currentPart) {
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
                        throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.KEY_SO_FAR, "type",
                                "All values in the (type) property MUST be a valid, distinct STRING.", e);
                    }
                }
                if (distinctTypes.size() == 0) {
                    throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.KEY_SO_FAR, "type",
                            "You must provide at least one valid data type restriction.");
                }
                validateType(currentPart, new ArrayList<>(distinctTypes));
                break;
            default:
                throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.KEY_SO_FAR, "type",
                        "Expected one of " + Arrays.asList("STRING", "ARRAY") + ", got " + type.getDataType() + ".");
        }
    }

    private static void validateType(JSONSchemaEnforcerPart currentPart, List<String> allowedTypes) {
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
                    throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.KEY_SO_FAR, "type",
                            "Unrecognised/Unsupported type provided (" + allowedType + ").");
            }
        }
        allowedTypes.replaceAll(String::toUpperCase);
        throw valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.KEY_SO_FAR, "type",
                "Expected one of " + allowedTypes + ", got " + currentDataType + ".");
    }

    private static void validateEnum(JSONSchemaEnforcerPart currentPart) {
        try {
            for (IJson instance : currentPart.SCHEMA_SUBSET.getArrayAt("enum")) {
                if (currentPart.OBJECT_TO_VALIDATE.equals(instance)) {
                    return;
                }
            }
            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.KEY_SO_FAR, "enum",
                    "Object did not match any options provided by the enum.");
        } catch (KeyDifferentTypeException e) {
            throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.KEY_SO_FAR, "enum", e);
        }
    }


    /* NUMBERS */
    private static void validateMultipleOf(JSONSchemaEnforcerPart currentPart) {
        BigDecimal numberFromSchema = getComparableNumber(currentPart, "multipleOf", true);
        if (numberFromSchema.equals(BigDecimal.ZERO)) {
            throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.KEY_SO_FAR, "multipleOf",
                    "You cannot use a multiple of 0.");
        }
        if ((getComparableNumber(currentPart, "multipleOf", false).remainder(numberFromSchema))
                .compareTo(BigDecimal.ZERO) != 0) {
            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.KEY_SO_FAR, "multipleOf",
                    "Value was not a multiple of schema value.");
        }
    }

    private static void validateMaximum(JSONSchemaEnforcerPart currentPart) {
        if ((getComparableNumber(currentPart, "maximum", false)
                .compareTo(getComparableNumber(currentPart, "maximum", true))) > 0) {
            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.KEY_SO_FAR, "maximum",
                    "Value was greater than the upper bound.");
        }
    }

    private static void validateExclusiveMaximum(JSONSchemaEnforcerPart currentPart) {
        if ((getComparableNumber(currentPart, "exclusiveMaximum", false)
                .compareTo(getComparableNumber(currentPart, "exclusiveMaximum", true))) >= 0) {
            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.KEY_SO_FAR, "exclusiveMaximum",
                    "Value was greater than or equal to the upper bound.");
        }
    }

    private static void validateMinimum(JSONSchemaEnforcerPart currentPart) {
        if ((getComparableNumber(currentPart, "minimum", false)
                .compareTo(getComparableNumber(currentPart, "minimum", true))) < 0) {
            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.KEY_SO_FAR, "minimum",
                    "Value was lower than the lower bound.");
        }
    }

    private static void validateExclusiveMinimum(JSONSchemaEnforcerPart currentPart) {
        if ((getComparableNumber(currentPart, "exclusiveMinimum", false)
                .compareTo(getComparableNumber(currentPart, "exclusiveMinimum", true))) <= 0) {
            throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.KEY_SO_FAR, "exclusiveMinimum",
                    "Value was lower than or equal to the lower bound.");
        }
    }


    /* OBJECTS */
    private static void validateUnevaluatedProperties(JSONSchemaEnforcerPart currentPart) {

    }

    private static void validatePropertyNames(JSONSchemaEnforcerPart currentPart) {

    }

    private static void validateAdditionalProperties(JSONSchemaEnforcerPart currentPart) {

    }

    private static void validatePatternProperties(JSONSchemaEnforcerPart currentPart) {

    }

    private static void validateProperties(JSONSchemaEnforcerPart currentPart) {

    }

    private static void validateDependentRequired(JSONSchemaEnforcerPart currentPart) {

    }

    private static void validateRequired(JSONSchemaEnforcerPart currentPart) {

    }

    private static void validateMinProperties(JSONSchemaEnforcerPart currentPart) {

    }

    private static void validateMaxProperties(JSONSchemaEnforcerPart currentPart) {

    }


    /* ARRAYS */
    private static void validateUnevaluatedItems(JSONSchemaEnforcerPart currentPart) {

    }

    private static void validateContains(JSONSchemaEnforcerPart currentPart) {

    }

    private static void validateAdditionalItems(JSONSchemaEnforcerPart currentPart) {

    }

    private static void validateItems(JSONSchemaEnforcerPart currentPart) {

    }

    private static void validateMinContains(JSONSchemaEnforcerPart currentPart) {

    }

    private static void validateMaxContains(JSONSchemaEnforcerPart currentPart) {


    }

    private static void validateUniqueItems(JSONSchemaEnforcerPart currentPart) {


    }

    private static void validateMinItems(JSONSchemaEnforcerPart currentPart) {

    }

    private static void validateMaxItems(JSONSchemaEnforcerPart currentPart) {

    }


    /* STRINGS */
    private static void validatePattern(JSONSchemaEnforcerPart currentPart) {

    }

    private static void validateMinLength(JSONSchemaEnforcerPart currentPart) {
    }

    private static void validateMaxLength(JSONSchemaEnforcerPart jsonSchemaEnforcerPart) {
    }

    private static void validateFormat(JSONSchemaEnforcerPart currentPart) {
        // TODO: dates/times/duration
        // TODO: emails
        //            "email REgex".matches("^((([!#$%&'*+\\-/=?^_`{|}~\\w])|([!#$%&'*+\\-/=?^_`{|}~\\w][!#$%&'*+\\-/=?^_`{|}~\\.\\w]{0,}[!#$%&'*+\\-/=?^_`{|}~\\w]))[@]\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*)$");
        // TODO: hostnames
        // TODO: ip addresses
        // TODO: resource identifiers
        // TODO: uri-template
        // TODO: json pointer
        // TODO: regex
    }

    private static BigDecimal getComparableNumber(JSONSchemaEnforcerPart currentPart, String propertyKey, boolean isForSchema) {
        IJson constraint = isForSchema ? currentPart.SCHEMA_SUBSET.getAnyAt(propertyKey) : currentPart.OBJECT_TO_VALIDATE;
        JSType constraintType = constraint.getDataType();
        if (constraintType != JSType.DOUBLE && constraintType != JSType.LONG) {
            throw (isForSchema
                    ? valueDifferentType(SourceOfProblem.SCHEMA, currentPart.KEY_SO_FAR, propertyKey,
                    "Expected NUMBER, got " + constraintType + ".")
                    : valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.KEY_SO_FAR, propertyKey,
                    "Value to verify must be a number.")
            );
        }
        return (constraintType == JSType.LONG)
                ? BigDecimal.valueOf(constraint.getLong())
                : BigDecimal.valueOf(constraint.getDouble());
    }


    /* ERROR MANAGEMENT */
    private static SchemaException missingProperty(SourceOfProblem source, String parentOfMissingProperty, String propertyName) {
        return missingProperty(source, parentOfMissingProperty, propertyName, null);
    }

    private static SchemaException missingProperty(SourceOfProblem source, String parentOfMissingProperty, String propertyName, Throwable cause) {
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
        message += getErrorCauseMessage(cause);
        return cause == null
                ? doThrow(source, message)
                : doThrow(source, message, cause);
    }

    private static SchemaException valueDifferentType(SourceOfProblem source, String parentOfMissingProperty, String propertyName) {
        return valueDifferentType(source, parentOfMissingProperty, propertyName, "");
    }

    private static SchemaException valueDifferentType(SourceOfProblem source, String parentOfMissingProperty, String propertyName, String reason) {
        return valueDifferentType(source, parentOfMissingProperty, propertyName, reason, null);
    }

    private static SchemaException valueDifferentType(SourceOfProblem source, String parentOfMissingProperty, String propertyName, KeyDifferentTypeException cause) {
        return valueDifferentType(source, parentOfMissingProperty, propertyName, "", cause);
    }

    private static SchemaException valueDifferentType(SourceOfProblem source, String parentOfMissingProperty, String propertyName, String reason, KeyDifferentTypeException cause) {
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

    private static SchemaException valueUnexpected(SourceOfProblem source, String parentOfMissingProperty, String propertyName, String reason) {
        return valueUnexpected(source, parentOfMissingProperty, propertyName, reason, null);
    }

    private static SchemaException valueUnexpected(SourceOfProblem source, String parentOfMissingProperty, String propertyName, String reason, Throwable cause) {
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

    private static SchemaException doThrow(SourceOfProblem source, String message) {
        switch (source) {
            case SCHEMA:
                return new InvalidSchemaException(message);
            case OBJECT_TO_VALIDATE:
                return new SchemaViolationException(message);
            default:
                return new SchemaException(message);
        }
    }

    private static SchemaException doThrow(SourceOfProblem source, String message, Throwable cause) {
        switch (source) {
            case SCHEMA:
                return new InvalidSchemaException(message, cause);
            case OBJECT_TO_VALIDATE:
                return new SchemaViolationException(message, cause);
            default:
                return new SchemaException(message, cause);
        }
    }

    private static String getErrorCauseMessage(Throwable cause) {
        if (cause != null) {
            String[] causeMessage = cause.getMessage().split("\\. ");
            if (causeMessage.length >= 2) {
                return "\n" + causeMessage[1];
            } else {
                return "\n" + cause.getMessage();
            }
        }
        return "";
    }

    private static String schemaErrorKeyChain(String propertyName, String path) {
        return (path.equals("") ? "<base element>" : path) + "." + propertyName;
    }
}
