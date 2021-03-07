package com.clumd.projects.javajson.api.schema;

import com.clumd.projects.javajson.api.JsonParser;
import com.clumd.projects.javajson.api.JsonSchemaEnforcer;
import com.clumd.projects.javajson.exceptions.schema.InvalidSchemaException;
import com.clumd.projects.javajson.exceptions.schema.SchemaViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class NumericTest {

    @Test
    public void testMultipleOfLongMatch() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("10"),
                JsonParser.parse("{'multipleOf': 5}"))
        );
    }

    @Test
    public void testMultipleOfIsGreaterThan0_0() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("10"),
                    JsonParser.parse("{'multipleOf': 0}"));
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Unexpected value for schema property: <base element>.multipleOf\n" +
                    "You cannot use a multiple of 0.", e.getMessage());
        }
    }

    @Test
    public void testMultipleOfIsGreaterThan0_1() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("10"),
                JsonParser.parse("{'multipleOf': -1}"))
        );
    }

    @Test
    public void testMultipleOfLongNOTMatch() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("10"),
                    JsonParser.parse("{'multipleOf': 3}"));
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.multipleOf\n" +
                    "Value was not a multiple of schema value.", e.getMessage());
        }
    }

    @Test
    public void testMultipleOfDoubleMatch() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("1.50"),
                JsonParser.parse("{'multipleOf': 0.5}"))
        );
    }

    @Test
    public void testMultipleOfDoubleNOTMatch() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("1.50"),
                    JsonParser.parse("{'multipleOf': 0.70}"));
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.multipleOf\n" +
                    "Value was not a multiple of schema value.", e.getMessage());
        }
    }

    @Test
    public void testMultipleOfAgainstDifferentDataTypeJSON() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("\"string\""),
                    JsonParser.parse("{'multipleOf': 2}"));
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.multipleOf\n" +
                    "Value to verify must be a number.", e.getMessage());
        }
    }

    @Test
    public void testMultipleOfAgainstDifferentDataTypeSCHEMA() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("5"),
                    JsonParser.parse("{'multipleOf': 'something'}"));
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.multipleOf\n" +
                    "Expected NUMBER, got STRING.", e.getMessage());
        }
    }

    @Test
    public void testMaximumLongMatch() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("10"),
                JsonParser.parse("{'maximum': 10}"))
        );
    }

    @Test
    public void testMaximumLongNOTMatch() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("11"),
                    JsonParser.parse("{'maximum': 10}"));
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.maximum\n" +
                    "Value was greater than the upper bound.", e.getMessage());
        }
    }

    @Test
    public void testMaximumCanBeNegativeLong() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("-3"),
                JsonParser.parse("{'maximum': -1}"))
        );
    }

    @Test
    public void testMaximumCanBeNegativeDouble() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("-3.5"),
                JsonParser.parse("{'maximum': -1.5}"))
        );
    }

    @Test
    public void testMaximumDoubleMatch() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("4.5"),
                JsonParser.parse("{'maximum': 4.5}"))
        );
    }

    @Test
    public void testMaximumDoubleNOTMatch() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("4.51"),
                    JsonParser.parse("{'maximum': 4.5}"));
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.maximum\n" +
                    "Value was greater than the upper bound.", e.getMessage());
        }
    }

    @Test
    public void testMaximumAgainstDifferentDataTypeJSON() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("\"string\""),
                    JsonParser.parse("{'maximum': 2}"));
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.maximum\n" +
                    "Value to verify must be a number.", e.getMessage());
        }
    }

    @Test
    public void testMaximumAgainstDifferentDataTypeSCHEMA() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("5"),
                    JsonParser.parse("{'maximum': 'something'}"));
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.maximum\n" +
                    "Expected NUMBER, got STRING.", e.getMessage());
        }
    }

    @Test
    public void testExclusiveMaximumLongMatch() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("9"),
                JsonParser.parse("{'exclusiveMaximum': 10}"))
        );
    }

    @Test
    public void testExclusiveMaximumLongNOTMatch() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("10"),
                    JsonParser.parse("{'exclusiveMaximum': 10}"));
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.exclusiveMaximum\n" +
                    "Value was greater than or equal to the upper bound.", e.getMessage());
        }
    }

    @Test
    public void testExclusiveMaximumCanBeNegativeLong() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("-3"),
                JsonParser.parse("{'exclusiveMaximum': -1}"))
        );
    }

    @Test
    public void testExclusiveMaximumCanBeNegativeDouble() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("-3.5"),
                JsonParser.parse("{'exclusiveMaximum': -1.5}"))
        );
    }

    @Test
    public void testExclusiveMaximumDoubleMatch() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("4.499"),
                JsonParser.parse("{'exclusiveMaximum': 4.5}"))
        );
    }

    @Test
    public void testExclusiveMaximumDoubleNOTMatch() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("4.5"),
                    JsonParser.parse("{'exclusiveMaximum': 4.5}"));
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.exclusiveMaximum\n" +
                    "Value was greater than or equal to the upper bound.", e.getMessage());
        }
    }

    @Test
    public void testExclusiveMaximumAgainstDifferentDataTypeJSON() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("\"string\""),
                    JsonParser.parse("{'exclusiveMaximum': 2}"));
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.exclusiveMaximum\n" +
                    "Value to verify must be a number.", e.getMessage());
        }
    }

    @Test
    public void testExclusiveMaximumAgainstDifferentDataTypeSCHEMA() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("5"),
                    JsonParser.parse("{'exclusiveMaximum': 'something'}"));
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.exclusiveMaximum\n" +
                    "Expected NUMBER, got STRING.", e.getMessage());
        }
    }

    @Test
    public void testMinimumLongMatch() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("10"),
                JsonParser.parse("{'minimum': 10}"))
        );
    }

    @Test
    public void testMinimumLongNOTMatch() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("9"),
                    JsonParser.parse("{'minimum': 10}"));
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.minimum\n" +
                    "Value was lower than the lower bound.", e.getMessage());
        }
    }

    @Test
    public void testMinimumCanBeNegativeLong() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("-1"),
                JsonParser.parse("{'minimum': -1}"))
        );
    }

    @Test
    public void testMinimumCanBeNegativeDouble() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("-1.5"),
                JsonParser.parse("{'minimum': -1.5}"))
        );
    }

    @Test
    public void testMinimumDoubleMatch() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("4.5"),
                JsonParser.parse("{'minimum': 4.5}"))
        );
    }

    @Test
    public void testMinimumDoubleNOTMatch() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("4.499"),
                    JsonParser.parse("{'minimum': 4.5}"));
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.minimum\n" +
                    "Value was lower than the lower bound.", e.getMessage());
        }
    }

    @Test
    public void testMinimumAgainstDifferentDataTypeJSON() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("\"string\""),
                    JsonParser.parse("{'minimum': 2}"));
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.minimum\n" +
                    "Value to verify must be a number.", e.getMessage());
        }
    }

    @Test
    public void testMinimumAgainstDifferentDataTypeSCHEMA() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("5"),
                    JsonParser.parse("{'minimum': 'something'}"));
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.minimum\n" +
                    "Expected NUMBER, got STRING.", e.getMessage());
        }
    }

    @Test
    public void testExclusiveMinimumLongMatch() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("11"),
                JsonParser.parse("{'exclusiveMinimum': 10}"))
        );
    }

    @Test
    public void testExclusiveMinimumLongNOTMatch() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("10"),
                    JsonParser.parse("{'exclusiveMinimum': 10}"));
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.exclusiveMinimum\n" +
                    "Value was lower than or equal to the lower bound.", e.getMessage());
        }
    }

    @Test
    public void testExclusiveMinimumCanBeNegativeLong() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("0"),
                JsonParser.parse("{'exclusiveMinimum': -1}"))
        );
    }

    @Test
    public void testExclusiveMinimumCanBeNegativeDouble() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("-1.499"),
                JsonParser.parse("{'exclusiveMinimum': -1.5}"))
        );
    }

    @Test
    public void testExclusiveMinimumDoubleMatch() {
        assertTrue(JsonSchemaEnforcer.validate(
                JsonParser.parse("4.501"),
                JsonParser.parse("{'exclusiveMinimum': 4.5}"))
        );
    }

    @Test
    public void testExclusiveMinimumDoubleNOTMatch() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("4.5"),
                    JsonParser.parse("{'exclusiveMinimum': 4.5}"));
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Unexpected value.\n" +
                    "Schema constraint violated: <base element>.exclusiveMinimum\n" +
                    "Value was lower than or equal to the lower bound.", e.getMessage());
        }
    }

    @Test
    public void testExclusiveMinimumAgainstDifferentDataTypeJSON() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("\"string\""),
                    JsonParser.parse("{'exclusiveMinimum': 2}"));
            fail("Previous method call should have thrown an exception.");
        } catch (SchemaViolationException e) {
            assertEquals("Mismatched data type.\n" +
                    "Schema constraint violated: <base element>.exclusiveMinimum\n" +
                    "Value to verify must be a number.", e.getMessage());
        }
    }

    @Test
    public void testExclusiveMinimumAgainstDifferentDataTypeSCHEMA() {
        try {
            JsonSchemaEnforcer.validate(
                    JsonParser.parse("5"),
                    JsonParser.parse("{'exclusiveMinimum': 'something'}"));
            fail("Previous method call should have thrown an exception.");
        } catch (InvalidSchemaException e) {
            assertEquals("Wrong type for schema property: <base element>.exclusiveMinimum\n" +
                    "Expected NUMBER, got STRING.", e.getMessage());
        }
    }
}
