package api.schema;

import api.JsonParser;
import api.JsonSchemaEnforcer;
import api.JsonSchemaEnforcerTest;
import exceptions.schema.InvalidSchemaException;
import exceptions.schema.SchemaViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypeTest extends JsonSchemaEnforcerTest {

    @Test
    public void typeValueInSchemaMustBeStringOrArray() {
        // Check String OK
        assertTrue(JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': 'object'}")));

        // Check Array OK
        assertTrue(JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': ['object', 'string']}")));

        // Check something else NOT OK
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': 1}"));
        } catch (InvalidSchemaException e) {
            assertEquals("The value for (type) @ path: <base element> was the wrong type in the SCHEMA."
                    + "\nExpected one of [STRING, ARRAY], got LONG.", e.getMessage());
        }
    }

    @Test
    public void checkTypeProvidedIsValid() {
        assertTrue(JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': 'object'}")));
        assertTrue(JsonSchemaEnforcer.validate(JsonParser.parse("[]"), JsonParser.parse("{'type': 'array'}")));
        assertTrue(JsonSchemaEnforcer.validate(JsonParser.parse("[]"), JsonParser.parse("{'type': 'list'}")));
        assertTrue(JsonSchemaEnforcer.validate(JsonParser.parse("true"), JsonParser.parse("{'type': 'boolean'}")));
        assertTrue(JsonSchemaEnforcer.validate(JsonParser.parse("1"), JsonParser.parse("{'type': 'long'}")));
        assertTrue(JsonSchemaEnforcer.validate(JsonParser.parse("1"), JsonParser.parse("{'type': 'integer'}")));
        assertTrue(JsonSchemaEnforcer.validate(JsonParser.parse("1"), JsonParser.parse("{'type': 'number'}")));
        assertTrue(JsonSchemaEnforcer.validate(JsonParser.parse("1.0"), JsonParser.parse("{'type': 'number'}")));
        assertTrue(JsonSchemaEnforcer.validate(JsonParser.parse("1.0"), JsonParser.parse("{'type': 'double'}")));
        assertTrue(JsonSchemaEnforcer.validate(JsonParser.parse("\"okay\""), JsonParser.parse("{'type': 'string'}")));
    }

    @Test
    public void checkWhenTypeProvidedIsInValidSTRING() {
        // Type not supported
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': 'null'}"));
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for (type) @ path: <base element> in the SCHEMA."
                    + "\nUnrecognised/Unsupported type provided (null).", e.getMessage());
        }
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': 'undefined'}"));
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for (type) @ path: <base element> in the SCHEMA."
                    + "\nUnrecognised/Unsupported type provided (undefined).", e.getMessage());
        }

        // Type does not exist
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': 'wrong'}"));
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for (type) @ path: <base element> in the SCHEMA."
                    + "\nUnknown type provided: (wrong)", e.getMessage());
        }
    }

    @Test
    public void checkWhenTypeProvidedIsInValidARRAY() {
        // Type not supported
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': ['null']}"));
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for (type) @ path: <base element> in the SCHEMA."
                    + "\nUnrecognised/Unsupported type provided (null).", e.getMessage());
        }
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': ['undefined']}"));
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for (type) @ path: <base element> in the SCHEMA."
                    + "\nUnrecognised/Unsupported type provided (undefined).", e.getMessage());
        }

        // Type does not exist
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': ['wrong']}"));
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for (type) @ path: <base element> in the SCHEMA."
                    + "\nUnknown type provided: (wrong)", e.getMessage());
        }
    }

    @Test
    public void checkTypeArrayContainsNonString() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': [1]}"));
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for (type) @ path: <base element> in the SCHEMA."
                    + "\nAll values in the (type) property MUST be a valid, distinct String.", e.getMessage());
        }
    }

    @Test
    public void checkTypeArrayIsEmpty() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': []}"));
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for (type) @ path: <base element> in the SCHEMA."
                    + "\nYou must provide at least one valid data type restriction.", e.getMessage());
        }
    }

    @Test
    public void checkWhenObjectIsNotWhatSchemaConstrains() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': 'long'}"));
        } catch (SchemaViolationException e) {
            assertEquals("Value in json to validate had a different type than expected.\n" +
                    "Constraint broken from SCHEMA property (type) @ path: <base element>\n" +
                    "Expected one of [long], got OBJECT.", e.getMessage());
        }
    }
}
