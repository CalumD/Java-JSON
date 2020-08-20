package api.schema;

import api.JsonParser;
import api.JsonSchemaEnforcer;
import exceptions.schema.InvalidSchemaException;
import exceptions.schema.SchemaViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ArrayTest {

    @Test
    public void minItemsMustBeNonNegative() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'minItems': -5}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.minItems\n" +
                    "Value must be >= 0.", e.getMessage());
        }
    }

    @Test
    public void minItemsMustValidateAgainstAnObject() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'minItems': 2}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.minItems\n" +
                    "This constraint can only be used against an array.", e.getMessage());
        }
    }

    @Test
    public void minItemsHasTooFew() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("[1]"),
                    JsonParser.parse("{'minItems': 2}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.minItems\n" +
                    "Array doesn't have enough elements to pass the constraint.", e.getMessage());
        }
    }

    @Test
    public void minItemsHasEnough() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[1,2]"),
                JsonParser.parse("{'minItems': 2}")
        ));
    }

    @Test
    public void maxItemsMustBeNumber() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'maxItems': []}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.maxItems\n" +
                    "Constraint must provide a non-negative integer.\n" +
                    "Expected: LONG  ->  Received: ARRAY", e.getMessage());
        }
    }

    @Test
    public void maxItemsMustBeNonNegative() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'maxItems': -5}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.maxItems\n" +
                    "Value must be >= 0.", e.getMessage());
        }
    }

    @Test
    public void maxItemsMustValidateAgainstAnObject() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("''"),
                    JsonParser.parse("{'maxItems': 2}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.maxItems\n" +
                    "This constraint can only be used against an array.", e.getMessage());
        }
    }

    @Test
    public void maxItemsHasTooMany() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("[1,2,3]"),
                    JsonParser.parse("{'maxItems': 2}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.maxItems\n" +
                    "Array has more elements than the constraint allows.", e.getMessage());
        }
    }

    @Test
    public void maxItemsHasEnough() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[1,2]"),
                JsonParser.parse("{'maxItems': 2}")
        ));
    }

    @Test
    public void uniqueItemsMustBeBoolean() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'uniqueItems': 'true'}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.uniqueItems\n" +
                    "Expected: BOOLEAN  ->  Received: STRING", e.getMessage());
        }
    }

    @Test
    public void uniqueItemsMustBeAgainstArray() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'uniqueItems': false}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.uniqueItems\n" +
                    "Expected: ARRAY  ->  Received: OBJECT", e.getMessage());
        }
    }

    @Test
    public void uniqueItemsIsFalseThenDontMindDupes() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[1,1,1,1,1,1]"),
                JsonParser.parse("{'uniqueItems': false}")
        ));
    }

    @Test
    public void uniqueItemsMustBeUnique() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("[0,1,2,3,4,5,6,6,7,8,9]"),
                    JsonParser.parse("{'uniqueItems': true}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.uniqueItems\n" +
                    "Index [6] in value to verify was not unique.", e.getMessage());
        }
    }
}
