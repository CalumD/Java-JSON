package com.clumd.projects.javajson.api.schema;

import com.clumd.projects.javajson.api.JsonParser;
import com.clumd.projects.javajson.api.JsonSchemaEnforcer;
import com.clumd.projects.javajson.exceptions.schema.InvalidSchemaException;
import com.clumd.projects.javajson.exceptions.schema.SchemaViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ConditionalSchemaApplicationTest {

    @Test
    public void testMatchesAll() {
        assertTrue(
                JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                        JsonParser.parse("{'allOf': [{'type': 'long'}, {'const': 1}, {'enum': [1]}]}"))
        );
    }

    @Test
    public void testNotMatchesAll() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'allOf': [{'type': 'long'}, {'const': 3}]}"));
            fail("Previous method call should have thrown an exception");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: allOf[1].const\n" +
                    "Value MUST match the schema's constant.", e.getMessage());
        }
    }

    @Test
    public void allOfMustBeArray() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'allOf': {'type': 'long', 'const': 3}}"));
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.allOf\n" +
                    "Expected: ARRAY  ->  Received: OBJECT", e.getMessage());
        }
    }

    @Test
    public void allOfArrayMustNotBeEmpty() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'allOf': []}"));
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.allOf\n" +
                    "Array must contain at least 1 sub-schema.", e.getMessage());
        }
    }

    @Test
    public void testMatchesAnyOf() {
        assertTrue(
                JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                        JsonParser.parse("{'anyOf': [{'type': 'integer'}, {'const': 'random string'}]}"))
        );
    }

    @Test
    public void testNotMatchesAnyOf() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'anyOf': [{'type': 'double'}, {'const': 1.45}]}"));
            fail("Previous method call should have thrown an exception");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.anyOf\n" +
                    "A provided JSON value failed to match any of the sub-schemas provided.", e.getMessage());
        }
    }

    @Test
    public void anyOfMustBeArray() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'anyOf': {'type': 'long', 'const': 3}}"));
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.anyOf\n" +
                    "Expected: ARRAY  ->  Received: OBJECT", e.getMessage());
        }
    }

    @Test
    public void anyOfArrayMustNotBeEmpty() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'anyOf': []}"));
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.anyOf\n" +
                    "Array must contain at least 1 sub-schema.", e.getMessage());
        }
    }

    @Test
    public void testMatchesOneOf() {
        assertTrue(
                JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                        JsonParser.parse("{'oneOf': [{'type': 'integer'}, {'const': 'someOtherValue'}]}"))
        );
    }

    @Test
    public void testNotMatchesOneOfBecauseMatchAll() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'oneOf': [{'type': 'integer'}, {'const': 1}]}"));
            fail("Previous method call should have thrown an exception");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.oneOf\n" +
                    "Json validated against more than one sub-schema.", e.getMessage());
        }
    }

    @Test
    public void testNotMatchesOneOfBecauseMatchNone() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'oneOf': [{'type': 'double'}, {'const': 1.45}]}"));
            fail("Previous method call should have thrown an exception");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.oneOf\n" +
                    "A provided JSON value failed to match any of the sub-schemas provided.", e.getMessage());
        }
    }

    @Test
    public void oneOfMustBeArray() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'oneOf': {'type': 'long', 'const': 3}}"));
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.oneOf\n" +
                    "Expected: ARRAY  ->  Received: OBJECT", e.getMessage());
        }
    }

    @Test
    public void oneOfArrayMustNotBeEmpty() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'oneOf': []}"));
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.oneOf\n" +
                    "Array must contain at least 1 sub-schema.", e.getMessage());
        }
    }

    @Test
    public void testMatchesNot() {
        assertTrue(
                JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                        JsonParser.parse("{'not': {'const': 5}}"))
        );
    }

    @Test
    public void testNotMatchesNot() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'not': {'const': 1}}"));
            fail("Previous method call should have thrown an exception");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.not\n" +
                    "Json successfully validated against the sub-schema, but a failure was required.", e.getMessage());
        }
    }

    @Test
    public void testIfMatchThenPassesCondition() {
        // Check when if is okay
        assertTrue(JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                JsonParser.parse("{'if': {'const': 1}, 'then': {'type': 'long'}}")));
    }

    @Test
    public void testIfMatchThenFailsCondition() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'if': {'const': 1}, 'then': {'type': 'double'}}"));
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: then.type\n" +
                    "Expected one of [DOUBLE], got LONG.", e.getMessage());
        }
    }

    @Test
    public void testIfNotMatchThenPassesCondition() {
        // Check when if is okay
        assertTrue(JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                JsonParser.parse("{'if': {'const': 2}, 'else': {'type': 'long'}}")));
    }

    @Test
    public void testIfNotMatchThenFailsCondition() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'if': {'const': 2}, 'else': {'type': 'double'}}"));
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: else.type\n" +
                    "Expected one of [DOUBLE], got LONG.", e.getMessage());
        }
    }
}
