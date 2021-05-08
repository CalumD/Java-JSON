package com.clumd.projects.javajson.api.schema;

import com.clumd.projects.javajson.api.JsonParser;
import com.clumd.projects.javajson.api.JsonSchemaEnforcer;
import com.clumd.projects.javajson.exceptions.schema.InvalidSchemaException;
import com.clumd.projects.javajson.exceptions.schema.SchemaViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class StringTest {

    @Test
    public void constraintMinShouldBeANumber() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'minLength': []}")
            );
            fail("The previous method should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.minLength\n" +
                    "Constraint must provide a non-negative integer.\n" +
                    "Expected: LONG  ->  Received: ARRAY", e.getMessage());
        }
    }

    @Test
    public void constraintMinShouldBePositive() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'minLength': -3}")
            );
            fail("The previous method should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.minLength\n" +
                    "Value must be >= 0.", e.getMessage());
        }
    }

    @Test
    public void valueMinShouldBeAString() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'minLength': 4}")
            );
            fail("The previous method should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.minLength\n" +
                    "Expected: STRING  ->  Received: OBJECT", e.getMessage());
        }
    }

    @Test
    public void valueMinShouldntBeSmallerThanConstraint() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("'four'"),
                    JsonParser.parse("{'minLength': 5}")
            );
            fail("The previous method should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.minLength\n" +
                    "String length was shorter than the minimum bound: 5", e.getMessage());
        }
    }

    @Test
    public void valueMinShouldPassIfEqualLength() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("'four'"),
                JsonParser.parse("{'minLength': 4}"))
        );
    }

    @Test
    public void valueMinShouldPassIfGreaterThanConstraint() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("'seven'"),
                JsonParser.parse("{'minLength': 4}"))
        );
    }


    @Test
    public void constraintMaxShouldBeANumber() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'maxLength':{}}")
            );
            fail("The previous method should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.maxLength\n" +
                    "Constraint must provide a non-negative integer.\n" +
                    "Expected: LONG  ->  Received: OBJECT", e.getMessage());
        }
    }

    @Test
    public void constraintMaxShouldBePositive() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'maxLength': -6}")
            );
            fail("The previous method should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.maxLength\n" +
                    "Value must be >= 0.", e.getMessage());
        }
    }

    @Test
    public void valueMaxShouldBeAString() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("[]"),
                    JsonParser.parse("{'maxLength': 5}")
            );
            fail("The previous method should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.maxLength\n" +
                    "Expected: STRING  ->  Received: ARRAY", e.getMessage());
        }
    }

    @Test
    public void valueMaxShouldntBeLargerThanConstraint() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("'seven'"),
                    JsonParser.parse("{'maxLength': 4}")
            );
            fail("The previous method should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.maxLength\n" +
                    "String length was longer than the maximum bound: 4", e.getMessage());
        }
    }

    @Test
    public void valueMaxShouldPassIfEqualLength() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("'four'"),
                JsonParser.parse("{'maxLength': 4}"))
        );
    }

    @Test
    public void valueMaxShouldPassIfLessThanConstraint() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("'four'"),
                JsonParser.parse("{'maxLength': 7}"))
        );
    }


    @Test
    public void patternMustBeString() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'pattern': 4}")
            );
            fail("The previous method should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.pattern\n" +
                    "Expected: STRING  ->  Received: LONG", e.getMessage());
        }
    }

    @Test
    public void patternNotAValidRegex() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'pattern': '[0-9+'}")
            );
            fail("The previous method should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.pattern\n" +
                    "Pattern provided was not a valid regex pattern.\n" +
                    "(Unclosed character class near index 4)", e.getMessage());
        }
    }

    @Test
    public void valueToVerifyMustBeAString() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'pattern': 'abc+'}")
            );
            fail("The previous method should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.pattern\n" +
                    "Expected: STRING  ->  Received: OBJECT", e.getMessage());
        }
    }

    @Test
    public void valueDoesntMatchPattern() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("'ab'"),
                    JsonParser.parse("{'pattern': 'abc+'}")
            );
            fail("The previous method should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.pattern\n" +
                    "Value did not match the provided pattern: abc+", e.getMessage());
        }
    }

    @Test
    public void valueDoesntMatchPattern_2() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("'.'"),
                    JsonParser.parse("{'pattern': '^/(.+)/$'}")
            );
            fail("The previous method should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.pattern\n" +
                    "Value did not match the provided pattern: ^/(.+)/$", e.getMessage());
        }
    }

    @Test
    public void valueDoesntMatchPattern_3() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("'some text/'"),
                    JsonParser.parse("{'pattern': '^/(.+)/$'}")
            );
            fail("The previous method should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.pattern\n" +
                    "Value did not match the provided pattern: ^/(.+)/$", e.getMessage());
        }
    }

    @Test
    public void valueDoesntMatchPattern_4() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("'/some text'"),
                    JsonParser.parse("{'pattern': '^/(.+)/$'}")
            );
            fail("The previous method should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.pattern\n" +
                    "Value did not match the provided pattern: ^/(.+)/$", e.getMessage());
        }
    }

    @Test
    public void valueDoesMatchPattern_v1() {
        assertTrue(
                JsonSchemaEnforcer.validate(
                        JsonParser.parse("'abc'"),
                        JsonParser.parse("{'pattern': 'abc+'}")
                )
        );
    }

    @Test
    public void valueDoesMatchPattern_v2() {
        assertTrue(
                JsonSchemaEnforcer.validate(
                        JsonParser.parse("'abccccc'"),
                        JsonParser.parse("{'pattern': 'abc+'}")
                )
        );
    }

    @Test
    public void valueDoesMatchPattern_v3() {
        assertTrue(
                JsonSchemaEnforcer.validate(
                        JsonParser.parse("'/some/full/path/'"),
                        JsonParser.parse("{'pattern': '^/(.+)/$'}")
                )
        );
    }
}
