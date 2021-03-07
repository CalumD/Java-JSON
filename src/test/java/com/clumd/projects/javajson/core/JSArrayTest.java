package com.clumd.projects.javajson.core;

import com.clumd.projects.javajson.api.Json;
import com.clumd.projects.javajson.api.JsonParser;
import com.clumd.projects.javajson.exceptions.json.JsonParseException;
import com.clumd.projects.javajson.exceptions.json.KeyDifferentTypeException;
import com.clumd.projects.javajson.exceptions.json.KeyInvalidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JSArrayTest extends JsonTest {

    private JSArray array;

    @BeforeEach
    void setUp() {
        array = new JSArray(new JsonTape("[[], 0, 0.1, '', true, {}]"));
    }

    @Test
    @Override
    public void testDataType() {
        assertEquals(JSType.ARRAY, array.getDataType());
    }

    @Test
    public void testParseException() {
        try {
            new JSArray(new JsonTape("["));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Reached the end of the JSON input before parsing was complete. Are you missing a terminating delimiter?", e.getMessage());
        }
        try {
            new JSArray(new JsonTape("]"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Reached the end of the JSON input before parsing was complete. Are you missing a terminating delimiter?", e.getMessage());
        }
        try {
            new JSArray(new JsonTape(""));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("You cannot create something from nothing. Input was empty.", e.getMessage());
        }
        try {
            new JSArray(new JsonTape("[,]"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Missing Valid JSON at start of array.\n" +
                    "Line: 1\n" +
                    "Reached: [_\n" +
                    "Expected: { / [ / \" / <number> / <boolean> ", e.getMessage());
        }
        try {
            new JSArray(new JsonTape("[1,]"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Comma suggests more array elements, but array terminates.\n" +
                    "Line: 1\n" +
                    "Reached: [1,_\n" +
                    "Expected: { / [ / \" / <number> / <boolean> ", e.getMessage());
        }
        try {
            new JSArray(new JsonTape("[,1]"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Missing Valid JSON at start of array.\n" +
                    "Line: 1\n" +
                    "Reached: [_\n" +
                    "Expected: { / [ / \" / <number> / <boolean> ", e.getMessage());
        }
        try {
            new JSArray(new JsonTape("[1, []"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Reached the end of the JSON input before parsing was complete. Are you missing a terminating delimiter?", e.getMessage());
        }
        try {
            new JSArray(new JsonTape("[[[]]"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Reached the end of the JSON input before parsing was complete. Are you missing a terminating delimiter?", e.getMessage());
        }
        try {
            new JSArray(new JsonTape("[{}[]]"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Invalid array child delimiter.\n" +
                    "Line: 1\n" +
                    "Reached: [{}_\n" +
                    "Expected: , / ]", e.getMessage());
        }
        try {
            new JSArray(new JsonTape("[1,,1]"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Unexpected symbol found while parsing.\n" +
                    "Line: 1\n" +
                    "Reached: [1,_\n" +
                    "Expected: { / [ / \" / <number> / <boolean> ", e.getMessage());
        }
    }

    @Test
    @Override
    public void simpleCreateFromString() {
        try {
            array.createFromString("[]");
            array.createFromString("[[[[[[]]]]]]");
        } catch (JsonParseException e) {
            fail("Create from string should not throw an exception for valid input.", e);
        }
    }

    @Test
    @Override
    public void simpleCreateFromMultilineString() {
        List<String> multiline = new ArrayList<>(7);
        multiline.add("[");
        multiline.add("");
        multiline.add("");
        multiline.add("1");
        multiline.add("");
        multiline.add("");
        multiline.add("]");
        try {
            array.createFromMultilineString(multiline);
        } catch (JsonParseException e) {
            fail("Create from string should not throw an exception for valid input.", e);
        }
    }

    @Test
    @Override
    public void containsTheEmptyDoubleQuoteIsTrue() {
        assertTrue(array.contains(""));
    }

    @Test
    @Override
    public void containsNullIsFalse() {
        assertFalse(array.contains(null));
    }

    @Test
    @Override
    public void containsAllTheseKeys() {
        List<String> keys = new ArrayList<>(6);
        keys.add("");
        keys.add("[0]");
        keys.add("[1]");
        keys.add("[2]");
        keys.add("[3]");
        keys.add("[4]");
        keys.add("[5]");
        assertTrue(array.containsAllKeys(keys));
    }

    @Test
    public void doesntContainAllKeys() {
        List<String> keys1 = new ArrayList<>(4);
        keys1.add(""); // Known Good Key from test above
        keys1.add("someOtherKey");

        List<String> keys2 = new ArrayList<>(2);
        keys2.add(""); // Known Good Key from test above
        keys2.add("[-1]"); // OOB

        List<String> keys3 = new ArrayList<>(2);
        keys3.add(""); // Known Good Key from test above
        keys3.add("[6]");// OOB

        try {
            array.containsAllKeys(keys1);
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("someOtherKey is not a valid accessor on element: <base element>", e.getMessage());
        }
        try {
            array.containsAllKeys(keys2);
            fail("The previous method call should have thrown an exception.");
        } catch (KeyInvalidException e) {
            assertEquals("Array accessor in key was negative integer. Must be positive.\n" +
                    "Line: 1\n" +
                    "Reached: [_\n" +
                    "Expected: <positive integer>", e.getMessage());
        }
        assertFalse(array.containsAllKeys(keys3));
    }

    @Test
    @Override
    public void getKeys() {
        List<String> keys = new ArrayList<>(5);
        keys.add("0");
        keys.add("1");
        keys.add("2");
        keys.add("3");
        keys.add("4");
        keys.add("5");

        assertEquals(keys, array.getKeys());
    }

    @Test
    @Override
    public void getValue() {
        List<Json> values = new ArrayList<>(3);
        values.add(new JSArray(new JsonTape("[]")));
        values.add(new JSNumber(new JsonTape("0")));
        values.add(new JSNumber(new JsonTape("0.1")));
        values.add(new JSString(new JsonTape("''")));
        values.add(new JSBoolean(new JsonTape("true")));
        values.add(new JSObject(new JsonTape("{}")));

        assertEquals(values, array.getValue());
    }

    @Test
    @Override
    public void getValues() {
        List<Json> values = new ArrayList<>(3);
        values.add(new JSArray(new JsonTape("[]")));
        values.add(new JSNumber(new JsonTape("0")));
        values.add(new JSNumber(new JsonTape("0.1")));
        values.add(new JSString(new JsonTape("''")));
        values.add(new JSBoolean(new JsonTape("true")));
        values.add(new JSObject(new JsonTape("{}")));

        assertEquals(values, array.getValues());
    }

    @Test
    public void getValuesNotEqual() {
        List<Json> values = new ArrayList<>(3);
        values.add(new JSArray(new JsonTape("[]")));
        values.add(new JSNumber(new JsonTape("0")));
        values.add(new JSNumber(new JsonTape("0.1")));
        values.add(new JSString(new JsonTape("'THE STRING IS DIFFERENT'")));
        values.add(new JSBoolean(new JsonTape("true")));
        values.add(new JSObject(new JsonTape("{}")));

        assertNotEquals(values, array.getValue());
    }

    @Test
    @Override
    public void getArray() {
        List<Json> values = new ArrayList<>(3);
        values.add(new JSArray(new JsonTape("[]")));
        values.add(new JSNumber(new JsonTape("0")));
        values.add(new JSNumber(new JsonTape("0.1")));
        values.add(new JSString(new JsonTape("''")));
        values.add(new JSBoolean(new JsonTape("true")));
        values.add(new JSObject(new JsonTape("{}")));

        assertEquals(values, array.getArray());
    }

    @Test
    @Override
    public void getBoolean() {
        try {
            array.getBoolean();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found was not expected. Expected: BOOLEAN  ->  Received: ARRAY", e.getMessage());
        }
    }

    @Test
    @Override
    public void getDouble() {
        try {
            array.getDouble();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found was not expected. Expected: DOUBLE  ->  Received: ARRAY", e.getMessage());
        }
    }

    @Test
    @Override
    public void getLong() {
        try {
            array.getLong();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found was not expected. Expected: LONG  ->  Received: ARRAY", e.getMessage());
        }
    }

    @Test
    @Override
    public void getString() {
        try {
            array.getString();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found was not expected. Expected: STRING  ->  Received: ARRAY", e.getMessage());
        }
    }

    @Test
    @Override
    public void getJSONObject() {
        try {
            array.getJSONObject();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found was not expected. Expected: OBJECT  ->  Received: ARRAY", e.getMessage());
        }
    }

    @Test
    @Override
    public void getAny() {
        assertEquals(array, array.getAny());
    }

    @Test
    @Override
    public void getValueAt() {
        try {
            array.getValueAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("someKey is not a valid accessor on element: <base element>", e.getMessage());
        }
        assertTrue((boolean) array.getValueAt("[4]"));
    }

    @Test
    @Override
    public void getDataTypeOf() {
        try {
            array.getDataTypeOf("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("someKey is not a valid accessor on element: <base element>", e.getMessage());
        }
        assertEquals(JSType.LONG, array.getDataTypeOf("[1]"));
    }

    @Test
    @Override
    public void getKeysOf() {
        try {
            array.getKeysOf("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("someKey is not a valid accessor on element: <base element>", e.getMessage());
        }
        assertEquals(new ArrayList<>(), array.getKeysOf("[0]"));
    }

    @Test
    @Override
    public void getValuesOf() {
        try {
            array.getValuesOf("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("someKey is not a valid accessor on element: <base element>", e.getMessage());
        }
        assertEquals(new ArrayList<>(), array.getValuesOf("[0]"));
    }

    @Test
    @Override
    public void getJSONObjectAt() {
        try {
            array.getJSONObjectAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("someKey is not a valid accessor on element: <base element>", e.getMessage());
        }
        assertEquals(new JSObject(new JsonTape("{}")), array.getJSONObjectAt("[5]"));
    }

    @SuppressWarnings({"AssertEqualsBetweenInconvertibleTypes"})
    @Test
    @Override
    public void getArrayAt() {
        try {
            array.getArrayAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("someKey is not a valid accessor on element: <base element>", e.getMessage());
        }
        assertEquals(new JSArray(new JsonTape("[]")), array.getArrayAt("[0]"));
    }

    @Test
    public void getArrayAtMe() {
        List<Json> values = new ArrayList<>(3);
        values.add(new JSArray(new JsonTape("[]")));
        values.add(new JSNumber(new JsonTape("0")));
        values.add(new JSNumber(new JsonTape("0.1")));
        values.add(new JSString(new JsonTape("''")));
        values.add(new JSBoolean(new JsonTape("true")));
        values.add(new JSObject(new JsonTape("{}")));

        assertEquals(values, array.getArrayAt(""));
    }

    @Test
    @Override
    public void getBooleanAt() {
        try {
            array.getBooleanAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("someKey is not a valid accessor on element: <base element>", e.getMessage());
        }
        assertTrue(array.getBooleanAt("[4]"));
    }

    @Test
    @Override
    public void getDoubleAt() {
        try {
            array.getDoubleAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("someKey is not a valid accessor on element: <base element>", e.getMessage());
        }
        assertEquals(0.1, array.getDoubleAt("[2]"), 0);
    }

    @Test
    @Override
    public void getLongAt() {
        try {
            array.getLongAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("someKey is not a valid accessor on element: <base element>", e.getMessage());
        }
        assertEquals(0, array.getLongAt("[1]"), 0);
    }

    @Test
    @Override
    public void getStringAt() {
        try {
            array.getStringAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("someKey is not a valid accessor on element: <base element>", e.getMessage());
        }
        assertEquals("", array.getStringAt("[3]"));
    }

    @Test
    @Override
    public void getAnyAt() {
        try {
            array.getAnyAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("someKey is not a valid accessor on element: <base element>", e.getMessage());
        }
        assertEquals("", array.getAnyAt("[3]").getString());
    }

    @Test
    public void getVeryNestedValue() {
        assertTrue(new JSArray(new JsonTape("[[[[{'nest':[{'furtherNest':true}]}]]]]")).getBooleanAt("[0][0][0][0].nest[0]['furtherNest']"));
    }

    @Test
    public void getAnyAtMe() {
        assertEquals(array, array.getAnyAt(""));
    }

    @Test
    @Override
    public void testToString() {
        assertEquals("[<6>]", array.toString());
    }

    @Test
    @Override
    public void toPrettyString() {
        assertEquals("[\n  <6>\n]", array.toPrettyString());
    }

    @Test
    @Override
    public void asString() {
        assertEquals("[[],0,0.1,\"\",true,{}]", array.asString());
    }

    @Test
    @Override
    public void asPrettyString() {
        assertEquals("[\n  [],\n  0,\n  0.1,\n  \"\",\n  true,\n  {} \n]", array.asPrettyString());
    }

    @Test
    @Override
    public void asPrettyStringWithDepth() {
        assertEquals("[\n  [],\n  0,\n  0.1,\n  \"\",\n  true,\n  {} \n]", array.asPrettyString(5));
        assertEquals("[\n  [\n    [\n      <1>\n    ] \n  ] \n]", new JSArray(new JsonTape("[[[[1]]]]")).asPrettyString(2));
    }

    @Test
    @Override
    public void asPrettyStringWithDepthAndIndent() {
        assertEquals("[\n      [],\n      0,\n      0.1,\n      \"\",\n      true,\n      {} \n]", array.asPrettyString(5, 6));
        assertEquals("[\n      [\n            [\n                  <1>\n            ] \n      ] \n]", new JSArray(new JsonTape("[[[[1]]]]")).asPrettyString(2, 6));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "ConstantConditions"})
    @Test
    public void checkNullDoesNotEqual() {
        assertFalse(array.equals(null));
    }

    @Test
    public void checkDifferentPhysicalArraysEqual() {
        assertEquals(new JSArray(new JsonTape("[1]")), new JSArray(new JsonTape("[1]")));
    }

    @Test
    public void checkDifferentSizeArraysEqual() {
        assertNotEquals(new JSArray(new JsonTape("[1, 2]")), new JSArray(new JsonTape("[1]")));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsBetweenInconvertibleTypes"})
    @Test
    public void checkLenientEquals() {
        ArrayList<Long> list = new ArrayList<>();
        list.add(1L);
        assertTrue(new JSArray(new JsonTape("[1]")).equals(list));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsBetweenInconvertibleTypes"})
    @Test
    public void checkLenientNotEquals() {
        ArrayList<Long> list = new ArrayList<>();
        list.add(2L);
        assertFalse(new JSArray(new JsonTape("[1]")).equals(list));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsBetweenInconvertibleTypes"})
    @Test
    public void checkLenientDoesntEqualForDifferentLength() {
        ArrayList<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        assertFalse(new JSArray(new JsonTape("[1]")).equals(list));
    }

    @Test
    public void checkOtherJSArrayDoesNotEqual() {
        assertNotEquals(array, new JSArray(new JsonTape("[[], 0, 0.1, 'I AM DIFFERENT', true, {}]")));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsBetweenInconvertibleTypes"})
    @Test
    @Override
    public void testEquals() {
        assertTrue(new JSArray(new JsonTape("[]")).equals(new ArrayList<>()));
    }

    @Test
    @Override
    public void getHashCode() {
        ArrayList<Json> check = new ArrayList<>();
        check.add(new JSArray(new JsonTape("[]")));
        check.add(new JSNumber(new JsonTape("0")));
        check.add(new JSNumber(new JsonTape("0.1")));
        check.add(new JSString(new JsonTape("''")));
        check.add(new JSBoolean(new JsonTape("true")));
        check.add(new JSObject(new JsonTape("{}")));

        assertEquals(check.hashCode(), array.hashCode(), 0);
    }

    @Test
    @Override
    public void jsonIsConvertible() {
        assertEquals("[[],0,0.1,\"\",true,{}]", array.convertToJSON().asString());
    }

    @Test
    public void getDifferentType() {
        try {
            JsonParser.parse("{\"test\":{}}").getArrayAt("test");
            fail("The previous method call should have thrown an exception.");
        } catch (Exception e) {
            assertEquals("The Type of Object found for key (test) was not expected. Expected: ARRAY  ->  Received: OBJECT", e.getMessage());
        }
    }

}