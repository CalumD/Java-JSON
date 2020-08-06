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
        private final IJson SCHEMA_REFERENCE;
        private final IJson OBJECT_TO_VALIDATE;
        private final String KEY_SO_FAR;
        private final IJson SCHEMA_SUBSET;

        private JSONSchemaEnforcerPart(IJson objectToValidate, IJson schemaReference, String schemaKeySoFar) {
            this.OBJECT_TO_VALIDATE = objectToValidate;
            this.SCHEMA_REFERENCE = schemaReference;
            this.KEY_SO_FAR = schemaKeySoFar.startsWith(".") ? schemaKeySoFar.substring(1) : schemaKeySoFar;
            this.SCHEMA_SUBSET = schemaReference.getAnyAt(this.KEY_SO_FAR);
        }

        private boolean enforce() {
            // TODO: CONVERT EVERY CONSTRAINT KEYWORD INTO A BIG SWITCH STATEMENT ON KEYS
            //  and throw exception if a keyword isn't recognised, as it's not supported.

            Set<String> constraintsSeen = new HashSet<>();
            /*constraintsSeen.addAll(*/
            validateKeywordsForAllInstanceTypes(this)/*)*/;
            /*constraintsSeen.addAll(*/
            validateNumericInstance(this)/*)*/;
            /*constraintsSeen.addAll(*/
            validateStringInstance(this)/*)*/;
            /*constraintsSeen.addAll(*/
            validateArrayInstance(this)/*)*/;
            /*constraintsSeen.addAll(*/
            validateObjectInstance(this)/*)*/;
            Set<String> refConstraints = validate$Ref(constraintsSeen);
            if (refConstraints.size() != 0) {
                // TODO: Do something with the refConstraints
            }
            return true;
        }

        private Set<String> validate$Ref(Set<String> constraintsSeen) {
            Set<String> keysToUse = new HashSet<>();
            if (this.SCHEMA_SUBSET.contains("$ref")) {
                try {
                    IJson ref = this.SCHEMA_SUBSET.getJSONObjectAt(""/* TODO: Get the $REF referenced; */);
                    for (String key : ref.getKeys()) {
                        if (!constraintsSeen.contains(key)) {
                            keysToUse.add(key);
                        }
                    }
                } catch (KeyNotFoundException e) {
                    throw missingProperty(SourceOfProblem.SCHEMA, KEY_SO_FAR, "$ref", e);
                } catch (KeyDifferentTypeException e) {
                    throw valueDifferentType(SourceOfProblem.SCHEMA, KEY_SO_FAR, "$ref",
                            "$ref must link to a valid sub-schema object.", e);
                }
            }
            return keysToUse;
        }
    }

    private static void validateKeywordsForAllInstanceTypes(JSONSchemaEnforcerPart currentPart) {
        if (currentPart.SCHEMA_SUBSET.contains("type")) {
            validateType(currentPart);
        }
        if (currentPart.SCHEMA_SUBSET.contains("enum")) {
            validateEnum(currentPart);
        }
        if (currentPart.SCHEMA_SUBSET.contains("const")) {
            if (!currentPart.OBJECT_TO_VALIDATE.equals(currentPart.SCHEMA_SUBSET.getAnyAt("const"))) {
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.KEY_SO_FAR, "const",
                        "Value MUST match the schema's constant.");
            }
        }

        if (currentPart.SCHEMA_SUBSET.contains("allOf")) {
            validateAllOf(currentPart);
        }
        if (currentPart.SCHEMA_SUBSET.contains("anyOf")) {
            validateAnyOf(currentPart);
        }
        if (currentPart.SCHEMA_SUBSET.contains("oneOf")) {
            validateOneOf(currentPart);
        }
        if (currentPart.SCHEMA_SUBSET.contains("not")) {
            validateNot(currentPart);
        }

        if (currentPart.SCHEMA_SUBSET.contains("if")) {
            validateIf(currentPart);
        }
    }

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

    private static void validateNumericInstance(JSONSchemaEnforcerPart currentPart) {
        if (currentPart.SCHEMA_SUBSET.contains("multipleOf")) {
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
        if (currentPart.SCHEMA_SUBSET.contains("maximum")) {
            if ((getComparableNumber(currentPart, "maximum", false)
                    .compareTo(getComparableNumber(currentPart, "maximum", true))) > 0) {
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.KEY_SO_FAR, "maximum",
                        "Value was greater than the upper bound.");
            }
        }
        if (currentPart.SCHEMA_SUBSET.contains("exclusiveMaximum")) {
            if ((getComparableNumber(currentPart, "exclusiveMaximum", false)
                    .compareTo(getComparableNumber(currentPart, "exclusiveMaximum", true))) >= 0) {
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.KEY_SO_FAR, "exclusiveMaximum",
                        "Value was greater than or equal to the upper bound.");
            }
        }
        if (currentPart.SCHEMA_SUBSET.contains("minimum")) {
            if ((getComparableNumber(currentPart, "minimum", false)
                    .compareTo(getComparableNumber(currentPart, "minimum", true))) < 0) {
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.KEY_SO_FAR, "minimum",
                        "Value was lower than the lower bound.");
            }
        }
        if (currentPart.SCHEMA_SUBSET.contains("exclusiveMinimum")) {
            if ((getComparableNumber(currentPart, "exclusiveMinimum", false)
                    .compareTo(getComparableNumber(currentPart, "exclusiveMinimum", true))) <= 0) {
                throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.KEY_SO_FAR, "exclusiveMinimum",
                        "Value was lower than or equal to the lower bound.");
            }
        }
    }

    private static void validateStringInstance(JSONSchemaEnforcerPart currentPart) {

        // TODO: maxLength
        // TODO: minLength
        // TODO: pattern
        if (currentPart.SCHEMA_SUBSET.contains("format")) {
            validateFormat(currentPart);
        }
    }

    private static void validateArrayInstance(JSONSchemaEnforcerPart currentPart) {
        // TODO: maxItems
        // TODO: minItems
        // TODO: uniqueItems
        // TODO: maxContains
        // TODO: minContains

        // TODO: items
        // TODO: additionalItems
        // TODO: contains
        // TODO: unevaluatedItems
    }

    private static void validateObjectInstance(JSONSchemaEnforcerPart currentPart) {
        // TODO: maxProperties
        // TODO: minProperties
        // TODO: required
        // TODO: dependentRequired

        // TODO: properties
        // TODO: patternProperties
        // TODO: additionalProperties
        // TODO: propertyNames
        // TODO: unevaluatedProperties
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
