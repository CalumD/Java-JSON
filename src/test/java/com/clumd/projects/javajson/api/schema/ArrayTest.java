package com.clumd.projects.javajson.api.schema;

import com.clumd.projects.javajson.api.JsonParser;
import com.clumd.projects.javajson.api.JsonSchemaEnforcer;
import com.clumd.projects.javajson.exceptions.schema.InvalidSchemaException;
import com.clumd.projects.javajson.exceptions.schema.SchemaViolationException;
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

    @Test
    public void containsShouldBeObject() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'contains': 1}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.contains\n" +
                    "Expected: OBJECT  ->  Received: LONG", e.getMessage());
        }
    }

    @Test
    public void containsCanOnlyBeUsedAgainstArray() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'contains': {}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.contains\n" +
                    "This constraint can only be used against an array.", e.getMessage());
        }
    }

    @Test
    public void containsAndDoesMatch() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[1,2,3,4,5]"),
                JsonParser.parse("{'contains': {'const':3}}")
        ));
    }

    @Test
    public void containsAndDoesNotMatch() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("[1,2,3.0,4,5]"),
                    JsonParser.parse("{'contains': {'const':3}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.contains\n" +
                    "Found no match against the contains property in the value array.", e.getMessage());
        }
    }

    @Test
    public void itemsCanOnlyBeObjectOrArray() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[]"),
                JsonParser.parse("{'items': []}")
        ));
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[]"),
                JsonParser.parse("{'items': {}}")
        ));
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("[]"),
                    JsonParser.parse("{'items': ''}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.items\n" +
                    "Expected one of [OBJECT, ARRAY], got STRING.", e.getMessage());
        }
    }

    @Test
    public void itemsMustBeUsedAgainstArray() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'items': []}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.items\n" +
                    "This constraint can only be used against an array.", e.getMessage());
        }
    }

    @Test
    public void itemsWhenUsingObjectMatchAll() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[1,1,1,1,1,1,1,1,1,1]"),
                JsonParser.parse("{'items': {'const':1}}")
        ));
    }

    @Test
    public void itemsWhenUsingObjectDoesNotMatchAll() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("[1,1,1,1,1,1,1,2,1,1]"),
                    JsonParser.parse("{'items': {'const':1}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\nSchema constraint violated: <base element>.items\n" +
                    "An element in the value array did not match against the constraint.\n\n" +
                    "Caused By:\nSchema constraint violated: items.const\n" +
                    "Value MUST match the schema's constant.", e.getMessage());
        }
    }

    @Test
    public void itemsWhenUsingArrayHasNonObject() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("[1]"),
                    JsonParser.parse("{'items': [1]}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.items[0]\n" +
                    "Expected: OBJECT  ->  Received: LONG", e.getMessage());
        }
    }

    @Test
    public void itemsWhenUsingArrayHasAllMatches() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[1]"),
                JsonParser.parse("{'items': [{'const':1}]}")
        ));
    }

    @Test
    public void itemsWhenUsingArrayHasMisMatch() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("[1, 2, 3]"),
                    JsonParser.parse("{'items': [{'const':1}, {'const':2}, {'const':3.0}]}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.items[2]\n" +
                    "Element in value array did not match against matching index in sub-schema.\n\n" +
                    "Caused By:\n" +
                    "Schema constraint violated: items[2].const\n" +
                    "Value MUST match the schema's constant.", e.getMessage());
        }
    }

    @Test
    public void itemsWhenUsingArrayHasMoreThanValueArray() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[1]"),
                JsonParser.parse("{'items': [{'const':1}, {'const':2}, {'const':3}]}")
        ));
    }

    @Test
    public void itemsWhenUsingArrayHasLessThanValueArray() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[1,2,3]"),
                JsonParser.parse("{'items': [{'const':1}]}")
        ));
    }

    @Test
    public void unevaluatedItemsAlwaysThrows() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'unevaluatedItems':{}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("unevaluatedItems constraint is not supported by this schema enforcer." +
                    "\nConsider re-designing your schema to avoid it.", e.getMessage());
        }
    }

    @Test
    public void additionalItemsIsIgnoredIfItemsIsAnObject() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[2]"),
                JsonParser.parse("{'additionalItems':{'const':1},'items': {}}")
        ));
    }

    @Test
    public void additionalItemsMustBeUsedAgainstArray() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'additionalItems':{'const':1}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.additionalItems\n" +
                    "This constraint can only be used against an array.", e.getMessage());
        }
    }

    @Test
    public void additionalItemsPasses() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[1]"),
                JsonParser.parse("{'additionalItems':{'const':1}}")
        ));
    }

    @Test
    public void additionalItemsFails() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("[1]"),
                    JsonParser.parse("{'additionalItems':{'const':2}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.additionalItems\n" +
                    "Element [0] in value array did not satisfy.\n\n" +
                    "Caused By:\n" +
                    "Schema constraint violated: additionalItems.const\n" +
                    "Value MUST match the schema's constant.", e.getMessage());
        }
    }

    @Test
    public void additionalItemsPassesWhenSomeMatchItems() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[3,2,1]"),
                JsonParser.parse("{'additionalItems':{'const':1},'items':[{'const':3},{'const':2}]}")
        ));
    }

    @Test
    public void additionalItemsDoesntNeedToVerifyAnything() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[3,2]"),
                JsonParser.parse("{'additionalItems':{'const':1},'items':[{'const':3},{'const':2}]}")
        ));
    }

    @Test
    public void additionalItemsFailsWhenSomeMatchItems() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("[1,2,3]"),
                    JsonParser.parse("{'additionalItems':{'const':1},'items':[{'const':1},{'const':2}]}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.additionalItems\n" +
                    "Element [2] in value array did not satisfy.\n\n" +
                    "Caused By:\n" +
                    "Schema constraint violated: additionalItems.const\n" +
                    "Value MUST match the schema's constant.", e.getMessage());
        }
    }

    @Test
    public void minContainsRequiresContains() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'minContains':5}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Missing property in schema at: <base element>.minContains\n" +
                    "minContains requires the (contains) constraint to run.", e.getMessage());
        }
    }

    @Test
    public void minContainsMustBeUsedAgainstArray() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'minContains':5, 'contains':{'type':'long'}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.contains\n" +
                    "This constraint can only be used against an array.", e.getMessage());
        }
    }

    @Test
    public void minContainsTooSmall() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("[1]"),
                    JsonParser.parse("{'minContains':2, 'contains':{'type':'long'}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Missing property.\n" +
                    "Schema constraint violated: <base element>.minContains\n" +
                    "Minimum quantity of matches against the contains constraint not satisfied.", e.getMessage());
        }
    }

    @Test
    public void minContainsEquals() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[1,2]"),
                JsonParser.parse("{'minContains':2, 'contains':{'type':'long'}}")
        ));
    }

    @Test
    public void minContainsMore() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[1,2,3]"),
                JsonParser.parse("{'minContains':2, 'contains':{'type':'long'}}")
        ));
    }

    @Test
    public void maxContainsRequiresContains() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'maxContains':5}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Missing property in schema at: <base element>.maxContains\n" +
                    "maxContains requires the (contains) constraint to run.", e.getMessage());
        }
    }

    @Test
    public void maxContainsMustBeUsedAgainstArray() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("{}"),
                    JsonParser.parse("{'maxContains':5, 'contains':{'type':'long'}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.contains\n" +
                    "This constraint can only be used against an array.", e.getMessage());
        }
    }

    @Test
    public void maxContainsSmallEnough() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[1]"),
                JsonParser.parse("{'maxContains':2, 'contains':{'type':'long'}}")
        ));
    }

    @Test
    public void maxContainsEquals() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("[1,2]"),
                JsonParser.parse("{'maxContains':2, 'contains':{'type':'long'}}")
        ));
    }

    @Test
    public void maxContainsTooMany() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("[1,2,3]"),
                    JsonParser.parse("{'maxContains':2, 'contains':{'type':'long'}}")
            );
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.maxContains\n" +
                    "Maximum quantity of matches against the contains constraint was exceeded.", e.getMessage());
        }
    }
}
