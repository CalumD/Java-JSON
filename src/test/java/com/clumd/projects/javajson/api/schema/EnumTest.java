package com.clumd.projects.javajson.api.schema;

import com.clumd.projects.javajson.api.JsonParser;
import com.clumd.projects.javajson.api.JsonSchemaEnforcer;
import com.clumd.projects.javajson.exceptions.schema.InvalidSchemaException;
import com.clumd.projects.javajson.exceptions.schema.SchemaViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class EnumTest {

    @Test
    public void checkEnumMustBeArray() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("{}"), JsonParser.parse("{'enum': 'wrong'}"));
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.enum\n" +
                    "Expected: ARRAY  ->  Received: STRING", e.getMessage());
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
            fail("Previous method call should have thrown an exception");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.enum\n" +
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
            fail("Previous method call should have thrown an exception");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.const\n" +
                    "Value MUST match the schema's constant.", e.getMessage());
        }
    }
}
