package api.schema;

import api.JsonParser;
import api.JsonSchemaEnforcer;
import api.JsonSchemaEnforcerTest;
import exceptions.schema.InvalidSchemaException;
import exceptions.schema.SchemaViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
            assertEquals("Wrong type for schema property: <base element>.type\n" +
                    "Expected one of [STRING, ARRAY], got LONG.", e.getMessage());
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
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.type\n" +
                    "Unrecognised/Unsupported type provided (NULL).", e.getMessage());
        }
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': 'undefined'}"));
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.type\n" +
                    "Unrecognised/Unsupported type provided (UNDEFINED).", e.getMessage());
        }

        // Type does not exist
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': 'wrong'}"));
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.type\n" +
                    "Unrecognised/Unsupported type provided (WRONG).", e.getMessage());
        }
    }

    @Test
    public void checkWhenTypeProvidedIsInValidARRAY() {
        // Type not supported
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': ['null']}"));
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.type\n" +
                    "Unrecognised/Unsupported type provided (NULL).", e.getMessage());
        }
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': ['undefined']}"));
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.type\n" +
                    "Unrecognised/Unsupported type provided (UNDEFINED).", e.getMessage());
        }

        // Type does not exist
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': ['wrong']}"));
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.type\n" +
                    "Unrecognised/Unsupported type provided (WRONG).", e.getMessage());
        }
    }

    @Test
    public void checkTypeArrayContainsNonString() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': [1]}"));
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.type\n" +
                    "All values in the (type) property MUST be a valid, distinct STRING.\n" +
                    "Expected: STRING  ->  Received: LONG", e.getMessage());
        }
    }

    @Test
    public void checkTypeArrayIsEmpty() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': []}"));
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.type\n" +
                    "You must provide at least one valid data type restriction.", e.getMessage());
        }
    }

    @Test
    public void checkWhenObjectIsNotWhatSchemaConstrains() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'type': 'long'}"));
            fail("Previous method call should have thrown an exception");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.type\n" +
                    "Expected one of [LONG], got OBJECT.", e.getMessage());
        }
    }
}
