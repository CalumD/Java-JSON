package api;

import exceptions.SchemaException;

public interface IJSONSchemaEnforcer {

    /**
     * Validate but never throw exceptions
     *
     * @param objectToValidate The JSON Object to be validated.
     * @param schema           The Schema JSON Object which defines the object to be validated.
     * @return True if the object passes validation, False if it fails for any reason.
     */
    default boolean validateStrict(IJson objectToValidate, IJson schema) {
        try {
            return validate(objectToValidate, schema);
        } catch (SchemaException e) {
            return false;
        }
    }

    /**
     * Validate with the potential to throw exceptions to the caller.
     *
     * @param objectToValidate The JSON Object to be validated.
     * @param schema           The Schema JSON Object which defines the object to be validated.
     * @return True if the object passes validation, False if it fails for any reason.
     * @throws SchemaException Thrown to give reason/context as to why an object failed validation.
     */
    boolean validate(IJson objectToValidate, IJson schema) throws SchemaException;
}