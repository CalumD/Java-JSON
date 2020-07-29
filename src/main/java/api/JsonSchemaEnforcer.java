package api;

import core.JSType;
import exceptions.SchemaException;

public final class JsonSchemaEnforcer implements IJsonSchemaEnforcer {

    public static boolean validateStrict(IJson objectToValidate, IJson schema) {
        return new JsonSchemaEnforcer().validateWithOutReasoning(objectToValidate, schema);
    }

    public static boolean validate(IJson objectToValidate, IJson schema) {
        return new JsonSchemaEnforcer().validateWithReasoning(objectToValidate, schema);
    }

    public boolean validateWithReasoning(IJson objectToValidate, IJson schema) throws SchemaException {
        return new JsonSchemaEnforcer().instanceValidate(objectToValidate, schema);
    }


    private class JSONSchemaEnforcerPart {
        private final String schemaKeySoFar;

        JSONSchemaEnforcerPart(String schemaKeySoFar) {
            this.schemaKeySoFar = schemaKeySoFar;
        }

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
        validateByType(objectToValidate, schema);
        return true;
    }

    private void validateByType(IJson objectToValidate, IJson schema) {
        JSONSchemaEnforcerOLD.validate(objectToValidate, schema);
        System.out.println();
    }
}
