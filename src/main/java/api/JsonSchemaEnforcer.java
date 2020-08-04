package api;

import core.JSType;
import exceptions.SchemaException;
import exceptions.json.KeyDifferentTypeException;
import exceptions.schema.InvalidSchemaException;
import exceptions.schema.SchemaViolationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class JsonSchemaEnforcer implements IJsonSchemaEnforcer {

    private enum SourceOfProblem {SCHEMA, OBJECT_TO_VALIDATE}

    private static final Set<String> ALLOWED_DATA_TYPE_NAMES = new HashSet<>(Arrays.asList(
            "array", "list", "boolean", "long", "integer", "double", "number", "string", "object", "null", "undefined"));

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

    private final class JSONSchemaEnforcerPart {
        private final IJson SCHEMA_REFERENCE;
        private final IJson OBJECT_TO_VALIDATE;
        private final String KEY_SO_FAR;
        private final IJson SCHEMA_SUBSET;

        private JSONSchemaEnforcerPart(IJson objectToValidate, IJson schemaReference, String schemaKeySoFar) {
            this.OBJECT_TO_VALIDATE = objectToValidate;
            this.SCHEMA_REFERENCE = schemaReference;
            this.KEY_SO_FAR = schemaKeySoFar;
            this.SCHEMA_SUBSET = schemaReference.getAnyAt(schemaKeySoFar);
        }

        private boolean enforce() {
            validateAnyInstanceType(this);
            return true;
        }

        private void validateAnyInstanceType(JSONSchemaEnforcerPart currentPart) {
            if (currentPart.SCHEMA_SUBSET.contains("type")) {
                validateType(this);
            }
            if (currentPart.SCHEMA_SUBSET.contains("enum")) {
                validateEnum(this);
            }
            if (currentPart.SCHEMA_SUBSET.contains("const")) {
                if (!currentPart.OBJECT_TO_VALIDATE.equals(currentPart.SCHEMA_SUBSET.getAnyAt("const"))) {
                    throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.KEY_SO_FAR, "const",
                            "Value of json object did not match the constant.");
                }
            }

            if (currentPart.SCHEMA_SUBSET.contains("allOf")) {
                validateAllOf(this);
            }
            if (currentPart.SCHEMA_SUBSET.contains("anyOf")) {
                validateAnyOf(this);
            }
            if (currentPart.SCHEMA_SUBSET.contains("oneOf")) {
                validateOneOf(this);
            }
            if (currentPart.SCHEMA_SUBSET.contains("not")) {
                validateNot(this);
            }

            if (currentPart.SCHEMA_SUBSET.contains("if")) {
                boolean validatedAgainstIf = false;
                boolean shouldError = false;
                SchemaException problemWithIfSchemaValidation = null;
                try {
                    validateIf(this);
                    validatedAgainstIf = true;
                } catch (SchemaException e) {
                    problemWithIfSchemaValidation = e;
                    shouldError = true;
                }

                if (validatedAgainstIf && currentPart.SCHEMA_SUBSET.contains("then")) {
                    validateThen(this);
                } else if (!validatedAgainstIf && currentPart.SCHEMA_SUBSET.contains("else")) {
                    validateElse(this);
                    shouldError = false;
                }

                if (shouldError) {
                    throw problemWithIfSchemaValidation;
                }
            }
        }
    }

    private static void validateType(JSONSchemaEnforcerPart currentPart) {
        IJson type = currentPart.SCHEMA_SUBSET.getAnyAt("type");
        switch (type.getDataType()) {
            case STRING:
                String dataType = currentPart.SCHEMA_SUBSET.getStringAt("type").toLowerCase();
                if (!ALLOWED_DATA_TYPE_NAMES.contains(dataType)) {
                    throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.KEY_SO_FAR, "type",
                            "Unknown type provided: (" + dataType + ")");
                }
                validateType(currentPart, Collections.singletonList(dataType));
                break;
            case ARRAY:
                Set<String> distinctTypes = new HashSet<>(10);
                for (IJson distinctType : currentPart.SCHEMA_SUBSET.getArrayAt("type")) {
                    try {
                        distinctTypes.add(distinctType.getString());
                    } catch (KeyDifferentTypeException e) {
                        throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.KEY_SO_FAR, "type",
                                "All values in the (type) property MUST be a valid, distinct String.");
                    }
                    if (!ALLOWED_DATA_TYPE_NAMES.contains(distinctType.getString().toLowerCase())) {
                        throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.KEY_SO_FAR, "type",
                                "Unknown type provided: (" + distinctType.getString() + ")");
                    }
                }
                if (distinctTypes.size() == 0) {
                    throw valueUnexpected(SourceOfProblem.SCHEMA, currentPart.KEY_SO_FAR, "type",
                            "You must provide at least one valid data type restriction.");
                }
                validateType(currentPart, new ArrayList<>(distinctTypes));
                break;
            default:
                throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.KEY_SO_FAR, "type", Arrays.asList("STRING", "ARRAY"), type.getDataType());
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
        throw valueDifferentType(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.KEY_SO_FAR, "type", allowedTypes, currentDataType);
    }

    private static void validateEnum(JSONSchemaEnforcerPart currentPart) {
        if (!(currentPart.SCHEMA_SUBSET.getDataTypeOf("enum") == JSType.ARRAY)) {
            throw valueDifferentType(SourceOfProblem.SCHEMA, currentPart.KEY_SO_FAR, "enum", JSType.ARRAY, currentPart.SCHEMA_SUBSET.getDataTypeOf("enum"));
        }
        for (IJson instance : currentPart.SCHEMA_SUBSET.getArrayAt("enum")) {
            if (currentPart.OBJECT_TO_VALIDATE.equals(instance)) {
                return;
            }
        }
        throw valueUnexpected(SourceOfProblem.OBJECT_TO_VALIDATE, currentPart.KEY_SO_FAR, "enum", "Object did not match any options provided by the enum.");
    }


    private static void validateNumericInstance(JSONSchemaEnforcerPart currentPart) {
        // TODO: multipleOf
        // TODO: maximum
        // TODO: exclusiveMaximum
        // TODO: minimum
        // TODO: exclusiveMinimum
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


    private static SchemaException missingProperty(SourceOfProblem source, String parentOfMissingProperty, String propertyName) {
        String path = parentOfMissingProperty.equals("") ? "<base element>" : parentOfMissingProperty;
        String message = "Invalid.";
        switch (source) {
            case SCHEMA:
                message = "Missing property (" + propertyName + ") @ path: " + path + " within the SCHEMA.";
                break;
            case OBJECT_TO_VALIDATE:
                message = "Missing property in json to validate." + schemaConstraintBrokenMessage(propertyName, path);
                break;
        }
        return doThrow(source, message);
    }

    private static SchemaException missingProperty(SourceOfProblem source, String parentOfMissingProperty, String propertyName, Throwable cause) {
        return doThrow(source, missingProperty(source, parentOfMissingProperty, propertyName).getMessage(), cause);
    }

    private static SchemaException valueDifferentType(SourceOfProblem source, String parentOfMissingProperty, String propertyName, List<String> expectedTypes, JSType gotType) {
        String path = parentOfMissingProperty.equals("") ? "<base element>" : parentOfMissingProperty;
        String message = "Invalid.";
        switch (source) {
            case SCHEMA:
                message = "The value for (" + propertyName + ") @ path: " + path + " was the wrong type in the SCHEMA.";
                break;
            case OBJECT_TO_VALIDATE:
                message = "Value in json to validate had a different type than expected." + schemaConstraintBrokenMessage(propertyName, path);
                break;
        }
        new ArrayList<>(expectedTypes).replaceAll(String::toUpperCase);
        message += "\nExpected one of " + expectedTypes + ", got " + gotType + ".";
        return doThrow(source, message);
    }

    private static SchemaException valueDifferentType(SourceOfProblem source, String parentOfMissingProperty, String propertyName, JSType expectedType, JSType gotType) {
        return valueDifferentType(source, parentOfMissingProperty, propertyName, Collections.singletonList(expectedType.toString()), gotType);
    }

    private static SchemaException valueDifferentType(SourceOfProblem source, String parentOfMissingProperty, String propertyName, JSType expectedType, JSType gotType, Throwable cause) {
        return doThrow(source, valueDifferentType(source, parentOfMissingProperty, propertyName, expectedType, gotType).getMessage(), cause);
    }

    private static SchemaException valueUnexpected(SourceOfProblem source, String parentOfMissingProperty, String propertyName, String reason) {
        String path = parentOfMissingProperty.equals("") ? "<base element>" : parentOfMissingProperty;
        String message = "Invalid.";
        switch (source) {
            case SCHEMA:
                message = "Unexpected value for (" + propertyName + ") @ path: " + path + " in the SCHEMA.";
                break;
            case OBJECT_TO_VALIDATE:
                message = "Unexpected value found for a property in json to validate." + schemaConstraintBrokenMessage(propertyName, path);
                break;
        }
        message += "\n" + reason;
        return doThrow(source, message);
    }

    private static SchemaException valueUnexpected(SourceOfProblem source, String parentOfMissingProperty, String propertyName, String reason, Throwable cause) {
        return doThrow(source, valueUnexpected(source, parentOfMissingProperty, propertyName, reason).getMessage(), cause);
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

    private static String schemaConstraintBrokenMessage(String propertyName, String path) {
        return "\nConstraint broken from SCHEMA property (" + propertyName + ") @ path: " + path;
    }
}
