package api.schema;

import api.JsonParser;
import api.JsonSchemaEnforcer;
import exceptions.schema.InvalidSchemaException;
import exceptions.schema.SchemaViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ObjectTest {

    @Test
    public void requiredConstraintMustBeArray() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'required': ''}")
            );
            fail("Previous method call should have thrown an exception.");
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
            fail("Previous method call should have thrown an exception.");
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
            fail("Previous method call should have thrown an exception.");
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
            fail("Previous method call should have thrown an exception.");
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
            fail("Previous method call should have thrown an exception.");
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

    @Test
    public void minPropertiesMustBeNumber() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'minProperties': []}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.minProperties\n" +
                    "Constraint must provide a non-negative integer.\n" +
                    "Expected: LONG  ->  Received: ARRAY", e.getMessage());
        }
    }

    @Test
    public void minPropertiesMustBeNonNegative() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'minProperties': -5}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.minProperties\n" +
                    "Value must be >= 0.", e.getMessage());
        }
    }

    @Test
    public void minPropertiesMustValidateAgainstAnObject() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'minProperties': 2}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.minProperties\n" +
                    "This constraint can only be used against an object.", e.getMessage());
        }
    }

    @Test
    public void minPropertiesHasTooFew() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{'key1':1}"),
                    JsonParser.parse("{'minProperties': 2}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.minProperties\n" +
                    "Object doesn't have enough properties to pass the constraint.", e.getMessage());
        }
    }

    @Test
    public void minPropertiesHasEnough() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{'key1':1,'key2':2}"),
                JsonParser.parse("{'minProperties': 2}")
        ));
    }

    @Test
    public void maxPropertiesMustBeNumber() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'maxProperties': []}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.maxProperties\n" +
                    "Constraint must provide a non-negative integer.\n" +
                    "Expected: LONG  ->  Received: ARRAY", e.getMessage());
        }
    }

    @Test
    public void maxPropertiesMustBeNonNegative() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'maxProperties': -5}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.maxProperties\n" +
                    "Value must be >= 0.", e.getMessage());
        }
    }

    @Test
    public void maxPropertiesMustValidateAgainstAnObject() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'maxProperties': 2}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.maxProperties\n" +
                    "This constraint can only be used against an object.", e.getMessage());
        }
    }

    @Test
    public void maxPropertiesHasTooMany() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{'key1':1,'key2':2,'key3':3}"),
                    JsonParser.parse("{'maxProperties': 2}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.maxProperties\n" +
                    "Object has more properties than the constraint allows.", e.getMessage());
        }
    }

    @Test
    public void maxPropertiesHasEnough() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{'key1':1,'key2':2}"),
                JsonParser.parse("{'maxProperties': 2}")
        ));
    }

    @Test
    public void propertiesMustBeObject() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'properties': 2}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.properties\n" +
                    "Properties constraint must be an object.\n" +
                    "Expected: OBJECT  ->  Received: LONG", e.getMessage());
        }
    }

    @Test
    public void propertiesShouldIgnoreIfNotAnObject() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("''"),
                JsonParser.parse("{'properties': {'key1': {}}}")
        ));
    }

    @Test
    public void propertiesShouldIgnoreIfValueIsArray() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[]"),
                JsonParser.parse("{'properties': {'key1': {}}}")
        ));
    }

    @Test
    public void ifPropertyDoesntExistInObjectThenContentsDontMatter() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{}"),
                JsonParser.parse("{'properties': {'key1': 12312312312}}")
        ));
    }

    @Test
    public void ifPropertyDoesExistInObjectThenContentMustBeAnObject_withSpacesInKey() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{'prop 1':1}"),
                    JsonParser.parse("{'properties': {'prop 1': 1}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.properties[`prop 1`]\n" +
                    "Expected: OBJECT  ->  Received: LONG", e.getMessage());
        }
    }

    @Test
    public void ifPropertyDoesExistInObjectThenContentMustBeAnObject_withBackSlashInKey() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{'prop\\\\1':1}"),
                    JsonParser.parse("{'properties': {'prop\\\\1': 1}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.properties[`prop\\1`]\n" +
                    "Expected: OBJECT  ->  Received: LONG", e.getMessage());
        }
    }

    @Test
    public void ifPropertyDoesExistInObjectThenContentMustBeAnObject_withoutSpaceInKey() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{'prop1':1}"),
                    JsonParser.parse("{'properties': {'prop1': 1}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.properties.prop1\n" +
                    "Expected: OBJECT  ->  Received: LONG", e.getMessage());
        }
    }

    @Test
    public void allPropertiesValidate() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("{'prop1':1, 'prop2':[]}"),
                JsonParser.parse("{'properties': {'prop1': {'const':1}, 'prop2': {'type':'array'}}}")
        ));
    }

    @Test
    public void notAllPropertiesValidate() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{'prop1':1, 'prop2':123}"),
                    JsonParser.parse("{'properties': {'prop1': {'const':1}, 'prop2': {'type':'array'}}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: properties.prop2.type\n" +
                    "Expected one of [ARRAY], got LONG.", e.getMessage());
        }
    }


    @Test
    public void additionalPropertiesCannotBeString() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'additionalProperties': ''}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.additionalProperties\n" +
                    "Expected one of [BOOLEAN, ARRAY], got STRING.", e.getMessage());
        }
    }
}
