package Core;

import Exceptions.JSONParseException;
import Exceptions.KeyDifferentTypeException;
import Exceptions.KeyNotFoundException;
import Exceptions.SchemaException;
import Values.*;

import java.io.IOException;
import java.util.HashSet;

/**
 * This is used to check the adherence of a JSON object to a JSON schema. The contents of each
 * specific method are very self explanatory.
 */
public class JSONSchemaEnforcer {

    /**
     * This is used to validate some arbitrary JSON object against a custom-defined json schema on
     * disk
     *
     * @param objectToValidate The object to validate against the schema
     * @param pathToSchema The entire path to the schema file to check the object against.
     * @return True if the object adheres to the schema, false if not.
     * @throws IOException    Thrown if there was a problem accessing the schema.
     * @throws JSONParseException Thrown if there was an issue parsing the schema.
     */
    public static boolean validateGlobal(IJson objectToValidate, String pathToSchema)
        throws IOException, JSONParseException {

        String fileAsString = FileManager.getFileAsString(pathToSchema);
        IJson schema = JSONParser.parse(fileAsString);

        return validate(objectToValidate, schema);
    }

    /**
     * This is used to validate some arbitrary JSON object against a pre-defined and locally shipped
     * schema.
     *
     * @param objectToValidate The object to validate against the schema
     * @param resourceName The path of the internal schema to validate against.
     * @return True if the object adheres to the schema, false if not.
     * @throws IOException    Thrown if there was a problem accessing the schema.
     * @throws JSONParseException Thrown if there was an issue parsing the schema.
     */
    public static boolean validateInternal(IJson objectToValidate, String resourceName)
        throws IOException, JSONParseException {

        String fileAsString = FileManager.getLocalResourceAsString(resourceName);
        IJson schema = JSONParser.parse(fileAsString);

        return validate(objectToValidate, schema);
    }

    /**
     * Used as a pass through of the parsed schema to check the object with.
     *
     * @param objectToValidate The object to check the validity of.
     * @param schema The schema to enforce on the object (As a JSON object itself)
     * @return True if the object satisfies the schema
     * @throws SchemaException Thrown if the object does not conform to the schema.
     */
    private static boolean validate(IJson objectToValidate, IJson schema)
        throws SchemaException {
        validateByType(objectToValidate, schema, "");
        return true;
    }

    /**
     * A Cover method for the system specification, since the path to the internal resource is
     * known.
     *
     * @param config The JSON object to check is a valid system specification.
     * @throws IOException    Thrown if there was some error accessing the Schema (Should never be
     *                        thrown for this method)
     * @throws JSONParseException Thrown if there was some error parsing the Schema (Should never be
     *                        thrown for this method)
     */
    public static void validateSystem(IJson config) throws IOException, JSONParseException {
        validateInternal(config, "/SystemSpecification.schema.json");
    }


    /**
     * Used to send the sub-object to the correct checker method.
     *
     * @param toValidate The JSON object to send to the checker method.
     * @param schema The whole schema checking against.
     * @param pathSoFar The path inside the schema we have reached so far.
     * @throws SchemaException Thrown if the object does not satisfy the schema.
     */
    private static void validateByType(IJson toValidate, IJson schema, String pathSoFar)
        throws SchemaException {

        IJson localSchema = getSchema(schema, pathSoFar);

        if (schema.contains(resolvePath(pathSoFar, "type"))) {
            try {
                switch (schema.getString(resolvePath(pathSoFar, "type"))) {
                    case "number":
                    case "integer":
                        validateNumber(toValidate, localSchema);
                        break;
                    case "string":
                        validateString(toValidate, localSchema);
                        break;
                    case "object":
                        validateObject(toValidate, schema, pathSoFar, localSchema);
                        break;
                    case "array":
                        validateArray(toValidate, schema, pathSoFar, localSchema);
                        break;
                    case "boolean":
                        validateBoolean(toValidate);
                        break;
                    case "null":
                        validateNull();
                        break;
                    default:
                        throw new KeyDifferentTypeException("");
                }
            } catch (KeyDifferentTypeException e) {
                //JsonSchema is invalid.
                throw new SchemaException(
                    "The JSON schema is invalid, type field is broken: " + e.getMessage());
            } catch (KeyNotFoundException e) {
                //something really, REALLY wrong has happened.
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            missingField(resolvePath(pathSoFar, "type"));
        }
    }

    /**
     * Used to validate a JSON object representing a number against the provided schema.
     *
     * @param number The Number to validate.
     * @param schema The schema for this number instance.
     * @throws SchemaException Thrown if the object does not satisfy the schema.
     */
    private static void validateNumber(IJson number, IJson schema)
        throws SchemaException {

        if (number instanceof JSNumber) {
            Object value = number.getValue();

            try {
                if (schema.contains("multipleOf")) {
                    if (value instanceof Long) {
                        if ((((Long) value) % schema.getLong("multipleOf")) != 0) {
                            valueUnexpected("multipleOf: " + schema.getLong("multipleOf"),
                                value.toString());
                        }
                    } else {
                        if ((((Double) value) % schema.getDouble("multipleOf")) != 0) {
                            valueUnexpected("multipleOf: " + schema.getDouble("multipleOf"),
                                value.toString());
                        }
                    }
                }
                if (schema.contains("minimum")) {
                    if (value instanceof Long) {
                        if (((Long) value) < schema.getLong("minimum")) {
                            valueUnexpected("minimum: " + schema.getLong("minimum"),
                                value.toString());
                        }
                    } else {
                        if (((Double) value) < schema.getDouble("minimum")) {
                            valueUnexpected("minimum: " + schema.getDouble("minimum"),
                                value.toString());
                        }
                    }
                }
                if (schema.contains("exclusiveMinimum")) {
                    if (value instanceof Long) {
                        if (((Long) value) <= schema.getLong("exclusiveMinimum")) {
                            valueUnexpected(
                                "exclusiveMinimum: " + schema.getLong("exclusiveMinimum"),
                                value.toString());
                        }
                    } else {
                        if (((Double) value) <= schema.getDouble("exclusiveMinimum")) {
                            valueUnexpected(
                                "exclusiveMinimum: " + schema.getDouble("exclusiveMinimum"),
                                value.toString());
                        }
                    }
                }
                if (schema.contains("maximum")) {
                    if (value instanceof Long) {
                        if (((Long) value) > schema.getLong("maximum")) {
                            valueUnexpected("maximum: " + schema.getLong("maximum"),
                                value.toString());
                        }
                    } else {
                        if (((Double) value) > schema.getDouble("maximum")) {
                            valueUnexpected("maximum: " + schema.getDouble("maximum"),
                                value.toString());
                        }
                    }
                }
                if (schema.contains("exclusiveMaximum")) {
                    if (value instanceof Long) {
                        if (((Long) value) >= schema.getLong("exclusiveMaximum")) {
                            valueUnexpected(
                                "exclusiveMaximum: " + schema.getLong("exclusiveMaximum"),
                                value.toString());
                        }
                    } else {
                        if (((Double) value) >= schema.getDouble("exclusiveMaximum")) {
                            valueUnexpected(
                                "exclusiveMaximum: " + schema.getDouble("exclusiveMaximum"),
                                value.toString());
                        }
                    }
                }
            } catch (ClassCastException | KeyNotFoundException | KeyDifferentTypeException e) {
                throw new SchemaException(
                    "Schema validation exception: " + e.getClass() + ", " + e.getMessage());
            }

        } else {
            throw new SchemaException("Value found in " + number + " is not the declared type.");
        }
    }

    /**
     * Used to validate a JSON object representing a String against the provided schema.
     *
     * @param string The String to validate.
     * @param schema The schema for this String instance.
     * @throws SchemaException Thrown if the object does not satisfy the schema.
     */
    private static void validateString(IJson string, IJson schema)
        throws SchemaException {
        if (string instanceof JSString) {
            String value = ((JSString) string).getValue();

            try {
                String limiter = value.length() > 100 ? value.substring(0, 100) + "..." : value;
                if (schema.contains("minLength")) {
                    if (value.length() < schema.getLong("minLength")) {
                        valueUnexpected("minLength: " + schema.getLong("minLength"), limiter);
                    }
                }
                if (schema.contains("maxLength")) {
                    if (value.length() > schema.getLong("maxLength")) {
                        valueUnexpected("maxLength: " + schema.getLong("maxLength"), limiter);
                    }
                }
                if (schema.contains("pattern")) {
                    if (!(value.matches(schema.getString("pattern")))) {
                        valueUnexpected("pattern: " + schema.getString("pattern"), limiter);
                    }
                }
                if (schema.contains("enum")) {
                    HashSet<String> allowed = new HashSet<>();
                    for (IJson elem : ((JSArray) schema.getJSONByKey("enum")).getValue()) {
                        allowed.add((String) elem.getValue());
                    }
                    if (!allowed.contains(value)) {
                        valueUnexpected("enum: " + schema.getJSONByKey("enum"), limiter);
                    }
                }
            } catch (ClassCastException | KeyNotFoundException | KeyDifferentTypeException e) {
                throw new SchemaException(
                    "Schema validation exception: " + e.getClass() + ", " + e.getMessage());
            }
        } else {
            throw new SchemaException("Value found in " + string + " is not the declared type.");
        }
    }

    /**
     * This is used to validate a JSON object against the schema PART for it.
     *
     * @param obj This is the object to validate against the schema
     * @param schema This is the WHOLE schema object, for pass-through sake.
     * @param pathSoFar This is the path we are through the schema so far
     * @param localSchema This is the current part of the schema we are on for validating the given
     * object.
     * @throws SchemaException Thrown if the object does not satisfy the schema.
     */
    private static void validateObject(IJson obj, IJson schema, String pathSoFar,
                                       IJson localSchema) throws SchemaException {

        if (obj instanceof JSObject) {
            JSObject value = (JSObject) obj;

            try {
                if (localSchema.contains("required")) {
                    if (!localSchema.contains("properties")) {
                        throw new SchemaException(
                            "Required attribute declared but no properties attribute found.");
                    }
                    HashSet<String> requiredKeys = new HashSet<>();
                    for (IJson key : ((JSArray) localSchema.getJSONByKey("required"))
                            .getValue()) {
                        requiredKeys.add(((JSString) key).getValue());
                    }
                    if (requiredKeys.size() == 0) {
                        throw new SchemaException(
                            "Required attribute declared, but no values provided for: " + obj);
                    }

                    for (String reqKey : requiredKeys) {
                        if (!value.contains(reqKey)) {
                            throw new SchemaException(
                                "Object {" + value + "} is missing required key: " + reqKey);
                        }
                    }
                }
                if (localSchema.contains("additionalProperties")) {
                    if (localSchema.getJSONByKey("additionalProperties") instanceof JSBoolean) {
                        if (!localSchema.getBoolean("additionalProperties")) {
                            HashSet<String> allowedKeys = new HashSet<>(
                                    localSchema.getJSONByKey("properties").getKeys());

                            for (String key : value.getKeys()) {
                                if (!allowedKeys.contains(key)) {
                                    throw new SchemaException("Additional property '" + key
                                            + "' not defined in schema found, " +
                                            "when additionalProperties was set to false.");
                                }
                            }
                            if (value.getKeys().size() > allowedKeys.size()) {
                                throw new SchemaException("Additional property in {" + obj
                                    + "} not defined in schema found, " +
                                    "when additionalProperties was set to false.");
                            }
                        }
                    } else {
                        HashSet<String> allowedKeys = new HashSet<>(
                                localSchema.getJSONByKey("properties").getKeys());

                        if (localSchema.contains("additionalProperties.$ref")) {
                            for (String key : value.getKeys()) {
                                if (!allowedKeys.contains(key)) {
                                    validateByType(value.getJSONByKey(key), schema,
                                            localSchema.getString("additionalProperties.$ref")
                                                    .substring(2));
                                }
                            }
                        } else {
                            for (String key : value.getKeys()) {
                                if (!allowedKeys.contains(key)) {
                                    validateByType(value.getJSONByKey(key), schema,
                                            resolvePath(pathSoFar, "additionalProperties"));
                                }
                            }
                        }
                    }
                }
                if (localSchema.contains("properties")) {
                    if (!(localSchema.getJSONByKey("properties") instanceof JSObject)) {
                        throw new KeyDifferentTypeException(
                                "Properties declaration in object must be an object.");
                    }
                    for (String key : localSchema.getJSONByKey("properties").getKeys()) {
                        if (obj.contains(key)) {
                            validateByType(obj.getJSONByKey(key), schema,
                                    resolvePath(pathSoFar, "properties." + key));
                        }
                    }
                }
                if (localSchema.contains("propertyNames.pattern")) {
                    for (String key : value.getKeys()) {
                        if (!(key.matches(localSchema.getString("propertyNames.pattern")))) {
                            valueUnexpected(
                                "pattern: " + localSchema.getString("propertyNames.pattern"), key);
                        }
                    }
                }
                if (localSchema.contains("minProperties")) {
                    if (value.getKeys().size() < (Long) localSchema.getJSONByKey("minProperties")
                            .getValue()) {
                        valueUnexpected("minProperties: " + localSchema.getLong("minProperties"),
                                String.valueOf(value.getKeys().size()));
                    }
                }
                if (localSchema.contains("maxProperties")) {
                    if (value.getKeys().size() > (Long) localSchema.getJSONByKey("maxProperties")
                            .getValue()) {
                        valueUnexpected("maxProperties: " + localSchema.getLong("maxProperties"),
                                String.valueOf(value.getKeys().size()));
                    }
                }
                if (localSchema.contains("dependencies")) {
                    throw new SchemaException("dependencies keyword not supported in validator.");
                }
            } catch (ClassCastException | KeyNotFoundException | KeyDifferentTypeException e) {
                throw new SchemaException(
                    "Schema validation exception: " + e.getClass() + ", " + e.getMessage());
            }
        } else {
            throw new SchemaException("Value found in " + obj + " is not the declared type.");
        }

    }

    /**
     * This is used to validate a JSON array against the schema PART for it.
     *
     * @param arr This is the Array to validate against the schema
     * @param schema This is the WHOLE schema object, for pass-through sake.
     * @param pathSoFar This is the path we are through the schema so far
     * @param localSchema This is the current part of the schema we are on for validating the given
     * array.
     * @throws SchemaException Thrown if the object does not satisfy the schema.
     */
    private static void validateArray(IJson arr, IJson schema, String pathSoFar,
                                      IJson localSchema) throws SchemaException {

        if (arr instanceof JSArray) {
            JSArray value = ((JSArray) arr);

            try {
                if (localSchema.contains("uniqueItems")) {
                    if (localSchema.getBoolean("uniqueItems")) {
                        //for all elements except the last
                        for (int i = 0; i < value.getValue().size() - 1; i++) {
                            //ensure that the rest are different.
                            for (int j = i + 1; j < value.getValue().size(); j++) {
                                if (value.getValue().get(i).equals(value.getValue().get(j))) {
                                    throw new SchemaException(
                                        "Found a non unique item in an array, index{" + i
                                            + "}, when unique items should be enforced.");
                                }
                            }
                        }
                    }
                }
                if (localSchema.contains("minItems")) {
                    if (value.getValue().size() < localSchema.getLong("minItems")) {
                        valueUnexpected("minItems: " + localSchema.getLong("minItems"),
                            String.valueOf(value.getValue().size()));
                    }
                }
                if (localSchema.contains("maxItems")) {
                    if (value.getValue().size() > localSchema.getLong("maxItems")) {
                        valueUnexpected("maxItems: " + localSchema.getLong("maxItems"),
                            String.valueOf(value.getValue().size()));
                    }
                }
                if (localSchema.contains("items")) {
                    if (localSchema.getJSONByKey("items") instanceof JSArray) {
                        for (int i = 0; i < value.getValue().size(); i++) {
                            validateByType(value.getValue().get(i), schema,
                                    resolvePath(pathSoFar, "items[" + i + "]"));
                        }
                        if (localSchema.contains("additionalItems")) {
                            if (localSchema.getJSONByKey("additionalItems") instanceof JSBoolean) {
                                if (!(localSchema.getBoolean("additionalItems"))) {
                                    if (value.getValue().size() > ((JSArray) localSchema
                                            .getJSONByKey("items")).getValue().size()) {
                                        throw new SchemaException(
                                                "Found additional attribute in {" + arr
                                                        + "}, schema enforced no additional items in that array.");
                                    }
                                }
                            } else {
                                if (localSchema.contains("additionalItems.$ref")) {
                                    for (
                                            int i = ((JSArray) localSchema.getJSONByKey("items")).getValue()
                                                    .size(); i < value.getValue().size(); i++) {
                                        validateByType(value.getValue().get(i), schema,
                                            localSchema.getString("additionalItems.$ref")
                                                .substring(2));
                                    }
                                } else {
                                    for (
                                            int i = ((JSArray) localSchema.getJSONByKey("items")).getValue()
                                                    .size(); i < value.getValue().size(); i++) {
                                        validateByType(value.getValue().get(i), schema,
                                                resolvePath(pathSoFar, "items"));
                                    }
                                }
                            }
                        }
                    }
                    if (localSchema.getJSONByKey("items") instanceof JSObject) {
                        if (localSchema.contains("items.$ref")) {
                            for (IJson elem : value.getValue()) {
                                validateByType(elem, schema,
                                        localSchema.getString("items.$ref").substring(2));
                            }
                        } else {
                            for (IJson elem : value.getValue()) {
                                validateByType(elem, localSchema, resolvePath(pathSoFar, "items"));
                            }
                        }
                    }
                }
            } catch (ClassCastException | KeyNotFoundException | KeyDifferentTypeException e) {
                throw new SchemaException(
                    "Schema validation exception: " + e.getClass() + ", " + e.getMessage());
            }
        } else {
            throw new SchemaException("Value found in " + arr + " is not the declared type.");
        }
    }

    /**
     * Used to validate a JSON object representing a boolean value against the provided schema.
     *
     * @param bool The Boolean to validate.
     * @throws SchemaException Thrown if the object does not satisfy the schema.
     */
    private static void validateBoolean(IJson bool) throws SchemaException {
        if (!(bool instanceof JSBoolean)) {
            throw new SchemaException("Value found in " + bool + " is not the declared type.");
        }
    }

    /**
     * Invalidates any JSON objects containing a NULL element, as unsupported.
     *
     * @throws SchemaException Thrown no matter what.
     */
    private static void validateNull() throws SchemaException {
        throw new SchemaException("Nulls are not supported in JSON for this framework.");
    }

    /**
     * This is used to throw an error for a required field.
     *
     * @param fieldName The name of the field that was missing
     * @throws SchemaException Thrown to describe an error of a missing key in the JSON object being
     *                         validated.
     */
    private static void missingField(String fieldName) throws SchemaException {
        throw new SchemaException("Missing field: " + fieldName);
    }

    /**
     * This is used to throw an error for an invalid/unexpected value to a key.
     *
     * @param key This is the key which was expected
     * @param valueName The name of the value which provided unexpected results
     * @throws SchemaException Thrown no matter what to describe an error of a value which does not
     *                         match the schema.
     */
    private static void valueUnexpected(String key, String valueName) throws SchemaException {
        throw new SchemaException("Unexpected value for key '" + key + "'  : " + valueName);
    }

    /**
     * Used to get the sub schema relevant to the caller from a whole object.
     *
     * @param schema The whole schema object
     * @param path The path to get from within the schema
     * @return The Schema object at the given path from the given whole schema
     * @throws SchemaException Thrown if that path is not found in the given schema.
     */
    private static IJson getSchema(IJson schema, String path) throws SchemaException {
        path = path.replaceAll("/", ".");
        try {
            return schema.getJSONByKey(path);
        } catch (KeyNotFoundException e) {
            throw new SchemaException("Path not found in schema. " + e.getMessage());
        }
    }

    /**
     * Used to correct indexes into the schema to use the correct syntax and not overflow 'dot'
     * references.
     *
     * @param pathSoFar The path we are at so far.
     * @param destination The destination object after the path so far.
     * @return The corrected string as the key into the object.
     */
    private static String resolvePath(String pathSoFar, String destination) {
        pathSoFar = pathSoFar.replaceAll("/", ".");
        return (pathSoFar.equals("")) ? destination : pathSoFar + "." + destination;
    }
}
