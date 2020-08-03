package api.schema;

import api.JsonParser;
import api.JsonSchemaEnforcer;
import exceptions.schema.SchemaViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConditionalSchemaApplicationTest {

    @Test
    public void testMatchesAll() {
        assertTrue(
                JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                        JsonParser.parse("{'allOf': [{'type': 'long'}, {'const': 1}, {'enum': 1}]}"))
        );
    }

    @Test
    public void testNotMatchesAll() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'allOf': [{'type': 'long'}, {'const': 3}]}"));
        } catch (SchemaViolationException e) {
            assertEquals("", e.getMessage());
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
        } catch (SchemaViolationException e) {
            assertEquals("", e.getMessage());
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
        } catch (SchemaViolationException e) {
            assertEquals("", e.getMessage());
        }
    }

    @Test
    public void testNotMatchesOneOfBecauseMatchNone() {
        try {
            JsonSchemaEnforcer.validate(JsonParser.parse("1"),
                    JsonParser.parse("{'oneOf': [{'type': 'double'}, {'const': 1.45}]}"));
        } catch (SchemaViolationException e) {
            assertEquals("", e.getMessage());
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
        } catch (SchemaViolationException e) {
            assertEquals("", e.getMessage());
        }
    }
}
