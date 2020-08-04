package api.schema;

import api.JsonParser;
import api.JsonSchemaEnforcer;
import exceptions.schema.InvalidSchemaException;
import exceptions.schema.SchemaViolationException;
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
            assertEquals("Unexpected value found for a property in json to validate.\n" +
                    "Constraint broken from SCHEMA property (const) @ path: allOf[1]\n" +
                    "Value of json object did not match the constant.", e.getMessage());
        }
    }

    @Test
    public void allOfMustBeArray() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'allOf': {'type': 'long', 'const': 3}}"));
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("The value for (allOf) @ path: <base element> was the wrong type in the SCHEMA.\n" +
                    "Expected one of [ARRAY], got OBJECT.", e.getMessage());
        }
    }

    @Test
    public void allOfArrayMustNotBeEmpty() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'allOf': []}"));
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for (allOf) @ path: <base element> in the SCHEMA.\n" +
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
            assertEquals("Unexpected value found for a property in json to validate.\n" +
                    "Constraint broken from SCHEMA property (anyOf) @ path: <base element>\n" +
                    "Provided json failed to match any of the sub-schemas provided.", e.getMessage());
        }
    }

    @Test
    public void anyOfMustBeArray() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'anyOf': {'type': 'long', 'const': 3}}"));
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("The value for (anyOf) @ path: <base element> was the wrong type in the SCHEMA.\n" +
                    "Expected one of [ARRAY], got OBJECT.", e.getMessage());
        }
    }

    @Test
    public void anyOfArrayMustNotBeEmpty() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'anyOf': []}"));
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for (anyOf) @ path: <base element> in the SCHEMA.\n" +
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
            assertEquals("Unexpected value found for a property in json to validate.\n" +
                    "Constraint broken from SCHEMA property (oneOf) @ path: <base element>\n" +
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
            assertEquals("Unexpected value found for a property in json to validate.\n" +
                    "Constraint broken from SCHEMA property (oneOf) @ path: <base element>\n" +
                    "Provided json failed to match any of the sub-schemas provided.", e.getMessage());
        }
    }

    @Test
    public void oneOfMustBeArray() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'oneOf': {'type': 'long', 'const': 3}}"));
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("The value for (oneOf) @ path: <base element> was the wrong type in the SCHEMA.\n" +
                    "Expected one of [ARRAY], got OBJECT.", e.getMessage());
        }
    }

    @Test
    public void oneOfArrayMustNotBeEmpty() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'oneOf': []}"));
            fail("Previous method call should have thrown an exception");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for (oneOf) @ path: <base element> in the SCHEMA.\n" +
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
            assertEquals("Unexpected value found for a property in json to validate.\n" +
                    "Constraint broken from SCHEMA property (not) @ path: <base element>\n" +
                    "Json successfully validated against the schema, but a failure was required.", e.getMessage());
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
            assertEquals("Value in json to validate had a different type than expected.\n" +
                    "Constraint broken from SCHEMA property (type) @ path: then\n" +
                    "Expected one of [double], got LONG.", e.getMessage());
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
            assertEquals("Value in json to validate had a different type than expected.\n" +
                    "Constraint broken from SCHEMA property (type) @ path: else\n" +
                    "Expected one of [double], got LONG.", e.getMessage());
        }
    }
}
