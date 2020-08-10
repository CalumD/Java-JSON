package api.schema;

import api.JsonParser;
import api.JsonSchemaEnforcer;
import exceptions.schema.InvalidSchemaException;
import exceptions.schema.SchemaViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ObjectTest {

    @Test
    public void requiredConstraintMustBeArray() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'required': ''}")
            );
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.required\n" +
                    "Required constraint must be a non-empty array of strings representing property names.\n" +
                    "Expected: ARRAY  ->  Received: STRING", e.getMessage());
        }
    }

    @Test
    public void requiredArrayMustHaveAtLeastOneElement() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'required': []}")
            );
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.required\n" +
                    "Must provide at least one required property.", e.getMessage());
        }
    }

    @Test
    public void requiredArrayCanOnlyContainStrings() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'required': ['', '', 123]}")
            );
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.required\n" +
                    "Required constraint must be a non-empty array of strings representing property names.\n" +
                    "Expected: STRING  ->  Received: LONG", e.getMessage());
        }
    }

    @Test
    public void objectToValidateMustBeObject() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'required': ['key1', 'key2']}")
            );
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.required\n" +
                    "Required constraint can only be applied to Object properties.", e.getMessage());
        }
    }

    @Test
    public void objectIsMissingARequiredKey() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{'key1':1,'missing key 2':2}"),
                    JsonParser.parse("{'required': ['key1', 'key2']}")
            );
        } catch (SchemaViolationException e) {
            assertEquals("Missing property.\n" +
                    "Schema constraint violated: <base element>.required\n" +
                    "Property (key2) is mandatory, but couldn't be found.", e.getMessage());
        }
    }

    @Test
    public void objectContainsOneKey() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{'key1':1}"),
                JsonParser.parse("{'required': ['key1']}")
        ));
    }

    @Test
    public void objectContainsAllKeys() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{'key1':1,'otherKey':2}"),
                JsonParser.parse("{'required': ['key1', 'otherKey']}")
        ));
    }

    @Test
    public void objectCanHaveOtherKeys() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{'key1':1,'otherKey':2, 'this key is not validated':3}"),
                JsonParser.parse("{'required': ['key1', 'otherKey']}")
        ));
    }
}
