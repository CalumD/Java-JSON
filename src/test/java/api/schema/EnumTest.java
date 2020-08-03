package api.schema;

import api.JsonParser;
import api.JsonSchemaEnforcer;
import exceptions.schema.InvalidSchemaException;
import exceptions.schema.SchemaViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnumTest {

    @Test
    public void checkEnumMustBeArray() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'enum': 'wrong'}"));
        } catch (InvalidSchemaException e) {
            assertEquals("The value for (enum) @ path: <base element> was the wrong type in the SCHEMA.\n" +
                    "Expected one of [ARRAY], got STRING.", e.getMessage());
        }
    }

    @Test
    public void checkValueEqualsOneInEnum() {
        assertTrue(
                JsonSchemaEnforcer.validate(JsonParser.parse("{}"),
                        JsonParser.parse("{'enum': ['wrong', {}, 'also wrong']}"))
        );
    }

    @Test
    public void checkValueEqualsOnlyOneInEnum() {
        assertTrue(
                JsonSchemaEnforcer.validate(JsonParser.parse("{'complex':[1]}"),
                        JsonParser.parse("{'enum': [{'complex':[1]}]}"))
        );
    }

    @Test
    public void checkWhenValueDoesntEqualAnyInEnum() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"),
                    JsonParser.parse("{'enum': [1,2,3,4,5]}"));
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value found for a property in json to validate.\n" +
                    "Constraint broken from SCHEMA property (enum) @ path: <base element>\n" +
                    "Object did not match any options provided by the enum.", e.getMessage());
        }
    }

    @Test
    public void checkConstEqualsSimple() {
        assertTrue(
                JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                        JsonParser.parse("{'const': 1}"))
        );
    }

    @Test
    public void checkConstEqualsComplex() {
        assertTrue(
                JsonSchemaEnforcer.validate(JsonParser.parse("{'complex':[1]}"),
                        JsonParser.parse("{'const': {'complex':[1]}}"))
        );
    }

    @Test
    public void checkConstNotEquals() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("true"),
                    JsonParser.parse("{'const': 'some string.'}"));
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value found for a property in json to validate.\n" +
                    "Constraint broken from SCHEMA property (const) @ path: <base element>\n" +
                    "Value of json object did not match the constant.", e.getMessage());
        }
    }
}
