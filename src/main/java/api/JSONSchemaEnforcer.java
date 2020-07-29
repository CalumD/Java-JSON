package api;

import core.JSType;
import exceptions.SchemaException;

public final class JSONSchemaEnforcer implements IJSONSchemaEnforcer {

    private class JSONSchemaEnforcerPart {
        private final String schemaKeySoFar;

        JSONSchemaEnforcerPart(String schemaKeySoFar) {
            this.schemaKeySoFar = schemaKeySoFar;
        }
    }

    private JSONSchemaEnforcer() {
    }

    public static boolean validateWithoutReasoning(IJson objectToValidate, IJson schema) {
        return new JSONSchemaEnforcer().validateStrict(objectToValidate, schema);
    }

    public static boolean validateWithReasoning(IJson objectToValidate, IJson schema) {
        return new JSONSchemaEnforcer().validate(objectToValidate, schema);
    }

    public boolean validate(IJson objectToValidate, IJson schema) throws SchemaException {
        return new JSONSchemaEnforcer().instanceValidate(objectToValidate, schema);
    }

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
