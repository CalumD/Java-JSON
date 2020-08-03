package api;

import exceptions.JsonException;
import exceptions.SchemaException;

public interface IJsonSchemaEnforcer {

    /**
     * Validate but never throw exceptions
     *
     * @param objectToValidate The JSON Object to be validated.
     * @param againstSchema    The Schema JSON Object which defines the object to be validated.
     * @return True if the object passes validation, False if it fails for any reason.
     */
    default boolean validateWithOutReasoning(IJson objectToValidate, IJson againstSchema) {
        try {
            return validateWithReasoning(objectToValidate, againstSchema);
        } catch (JsonException e) {
            return false;
        }
    }

    /**
     * Validate with the potential to throw exceptions to the caller.
     *
     * @param objectToValidate The JSON Object to be validated.
     * @param againstSchema    The Schema JSON Object which defines the object to be validated.
     * @return True if the object passes validation, False if it fails for any reason.
     * @throws SchemaException Thrown to give reason/context as to why an object failed validation.
     */
    boolean validateWithReasoning(IJson objectToValidate, IJson againstSchema) throws SchemaException;
}