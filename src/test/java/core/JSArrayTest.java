package core;

import api.IJson;
import exceptions.JSONParseException;
import exceptions.KeyDifferentTypeException;
import exceptions.KeyInvalidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JSArrayTest extends JSONTest {

    private JSArray array;

    @BeforeEach
    void setUp() {
        array = new JSArray(new JSONTape("[[], 0, 0.1, '', true, {}]"));
    }

    @Test
    @Override
    public void testDataType() {
        assertEquals(JSType.ARRAY, array.getDataType());
    }

    @Test
    public void testParseException() {
        assertThrows(JSONParseException.class, () -> new JSArray(new JSONTape("[")));
        assertThrows(JSONParseException.class, () -> new JSArray(new JSONTape("]")));
        assertThrows(JSONParseException.class, () -> new JSArray(new JSONTape("")));
        assertThrows(JSONParseException.class, () -> new JSArray(new JSONTape("[,]")));
        assertThrows(JSONParseException.class, () -> new JSArray(new JSONTape("[1,]")));
        assertThrows(JSONParseException.class, () -> new JSArray(new JSONTape("[,1]")));
        assertThrows(JSONParseException.class, () -> new JSArray(new JSONTape("[1, []")));
        assertThrows(JSONParseException.class, () -> new JSArray(new JSONTape("[[[]]")));
        assertThrows(JSONParseException.class, () -> new JSArray(new JSONTape("[{}[]]")));
        assertThrows(JSONParseException.class, () -> new JSArray(new JSONTape("[1,,1]")));
    }

    @Test
    @Override
    public void simpleCreateFromString() {
        try {
            array.createFromString("[]");
            array.createFromString("[[[[[[]]]]]]");
        } catch (JSONParseException e) {
            fail("Create from string should not throw an exception for valid input.");
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
        } catch (JSONParseException e) {
            fail("Create from string should not throw an exception for valid input.");
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

        assertThrows(KeyDifferentTypeException.class, () -> array.containsAllKeys(keys1));
        assertThrows(KeyInvalidException.class, () -> array.containsAllKeys(keys2));
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
        List<IJson> values = new ArrayList<>(3);
        values.add(new JSArray(new JSONTape("[]")));
        values.add(new JSNumber(new JSONTape("0")));
        values.add(new JSNumber(new JSONTape("0.1")));
        values.add(new JSString(new JSONTape("''")));
        values.add(new JSBoolean(new JSONTape("true")));
        values.add(new JSObject(new JSONTape("{}")));

        assertEquals(values, array.getValue());
    }

    @Test
    @Override
    public void getValues() {
        List<IJson> values = new ArrayList<>(3);
        values.add(new JSArray(new JSONTape("[]")));
        values.add(new JSNumber(new JSONTape("0")));
        values.add(new JSNumber(new JSONTape("0.1")));
        values.add(new JSString(new JSONTape("''")));
        values.add(new JSBoolean(new JSONTape("true")));
        values.add(new JSObject(new JSONTape("{}")));

        assertEquals(values, array.getValues());
    }

    @Test
    public void getValuesNotEqual() {
        List<IJson> values = new ArrayList<>(3);
        values.add(new JSArray(new JSONTape("[]")));
        values.add(new JSNumber(new JSONTape("0")));
        values.add(new JSNumber(new JSONTape("0.1")));
        values.add(new JSString(new JSONTape("'THE STRING IS DIFFERENT'")));
        values.add(new JSBoolean(new JSONTape("true")));
        values.add(new JSObject(new JSONTape("{}")));

        assertNotEquals(values, array.getValue());
    }

    @Test
    @Override
    public void getArray() {
        List<IJson> values = new ArrayList<>(3);
        values.add(new JSArray(new JSONTape("[]")));
        values.add(new JSNumber(new JSONTape("0")));
        values.add(new JSNumber(new JSONTape("0.1")));
        values.add(new JSString(new JSONTape("''")));
        values.add(new JSBoolean(new JSONTape("true")));
        values.add(new JSObject(new JSONTape("{}")));

        assertEquals(values, array.getArray());
    }

    @Test
    @Override
    public void getBoolean() {
        assertThrows(KeyDifferentTypeException.class, () -> array.getBoolean());
    }

    @Test
    @Override
    public void getDouble() {
        assertThrows(KeyDifferentTypeException.class, () -> array.getDouble());
    }

    @Test
    @Override
    public void getLong() {
        assertThrows(KeyDifferentTypeException.class, () -> array.getLong());
    }

    @Test
    @Override
    public void getString() {
        assertThrows(KeyDifferentTypeException.class, () -> array.getString());
    }

    @Test
    @Override
    public void getJSONObject() {
        assertThrows(KeyDifferentTypeException.class, () -> array.getJSONObject());
    }

    @Test
    @Override
    public void getAny() {
        assertEquals(array, array.getAny());
    }

    @Test
    @Override
    public void getValueAt() {
        assertThrows(KeyDifferentTypeException.class, () -> array.getValueAt("someKey"));
        assertTrue((boolean) array.getValueAt("[4]"));
    }

    @Test
    @Override
    public void getDataTypeOf() {
        assertThrows(KeyDifferentTypeException.class, () -> array.getValueAt("someKey"));
        assertEquals(JSType.LONG, array.getDataTypeOf("[1]"));
    }

    @Test
    @Override
    public void getKeysOf() {
        assertThrows(KeyDifferentTypeException.class, () -> array.getKeysOf("someKey"));
        assertEquals(new ArrayList<>(), array.getKeysOf("[0]"));
    }

    @Test
    @Override
    public void getValuesOf() {
        assertThrows(KeyDifferentTypeException.class, () -> array.getValuesOf("someKey"));
        assertEquals(new ArrayList<>(), array.getValuesOf("[0]"));
    }

    @Test
    @Override
    public void getJSONObjectAt() {
        assertThrows(KeyDifferentTypeException.class, () -> array.getJSONObjectAt("someKey"));
        assertEquals(new JSObject(new JSONTape("{}")), array.getJSONObjectAt("[5]"));
    }

    @SuppressWarnings({"AssertEqualsBetweenInconvertibleTypes"})
    @Test
    @Override
    public void getArrayAt() {
        assertThrows(KeyDifferentTypeException.class, () -> array.getArrayAt("someKey"));
        assertEquals(new JSArray(new JSONTape("[]")), array.getArrayAt("[0]"));
    }

    @Test
    public void getArrayAtMe() {
        List<IJson> values = new ArrayList<>(3);
        values.add(new JSArray(new JSONTape("[]")));
        values.add(new JSNumber(new JSONTape("0")));
        values.add(new JSNumber(new JSONTape("0.1")));
        values.add(new JSString(new JSONTape("''")));
        values.add(new JSBoolean(new JSONTape("true")));
        values.add(new JSObject(new JSONTape("{}")));

        assertEquals(values, array.getArrayAt(""));
    }

    @Test
    @Override
    public void getBooleanAt() {
        assertThrows(KeyDifferentTypeException.class, () -> array.getBooleanAt("someKey"));
        assertTrue(array.getBooleanAt("[4]"));
    }

    @Test
    @Override
    public void getDoubleAt() {
        assertThrows(KeyDifferentTypeException.class, () -> array.getDoubleAt("someKey"));
        assertEquals(0.1, array.getDoubleAt("[2]"), 0);
    }

    @Test
    @Override
    public void getLongAt() {
        assertThrows(KeyDifferentTypeException.class, () -> array.getLongAt("someKey"));
        assertEquals(0, array.getLongAt("[1]"), 0);
    }

    @Test
    @Override
    public void getStringAt() {
        assertThrows(KeyDifferentTypeException.class, () -> array.getStringAt("someKey"));
        assertEquals("", array.getStringAt("[3]"));
    }

    @Test
    @Override
    public void getAnyAt() {
        assertThrows(KeyDifferentTypeException.class, () -> array.getAnyAt("someKey"));
        assertEquals("", array.getAnyAt("[3]").getString());
    }

    @Test
    public void getVeryNestedValue() {
        assertTrue(new JSArray(new JSONTape("[[[[{'nest':[{'furtherNest':true}]}]]]]")).getBooleanAt("[0][0][0][0].nest[0]['furtherNest']"));
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
        assertEquals("[\n  [\n    [\n      <1>\n    ] \n  ] \n]", new JSArray(new JSONTape("[[[[1]]]]")).asPrettyString(2));
    }

    @Test
    @Override
    public void asPrettyStringWithDepthAndIndent() {
        assertEquals("[\n      [],\n      0,\n      0.1,\n      \"\",\n      true,\n      {} \n]", array.asPrettyString(5, 6));
        assertEquals("[\n      [\n            [\n                  <1>\n            ] \n      ] \n]", new JSArray(new JSONTape("[[[[1]]]]")).asPrettyString(2, 6));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "ConstantConditions"})
    @Test
    public void checkNullDoesNotEqual() {
        assertFalse(array.equals(null));
    }

    @Test
    public void checkDifferentPhysicalArraysEqual() {
        assertEquals(new JSArray(new JSONTape("[1]")), new JSArray(new JSONTape("[1]")));
    }

    @Test
    public void checkDifferentSizeArraysEqual() {
        assertNotEquals(new JSArray(new JSONTape("[1, 2]")), new JSArray(new JSONTape("[1]")));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsBetweenInconvertibleTypes"})
    @Test
    public void checkLenientEquals() {
        ArrayList<Long> list = new ArrayList<>();
        list.add(1L);
        assertTrue(new JSArray(new JSONTape("[1]")).equals(list));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsBetweenInconvertibleTypes"})
    @Test
    public void checkLenientNotEquals() {
        ArrayList<Long> list = new ArrayList<>();
        list.add(2L);
        assertFalse(new JSArray(new JSONTape("[1]")).equals(list));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsBetweenInconvertibleTypes"})
    @Test
    public void checkLenientDoesntEqualForDifferentLength() {
        ArrayList<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        assertFalse(new JSArray(new JSONTape("[1]")).equals(list));
    }

    @Test
    public void checkOtherJSArrayDoesNotEqual() {
        assertNotEquals(array, new JSArray(new JSONTape("[[], 0, 0.1, 'I AM DIFFERENT', true, {}]")));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsBetweenInconvertibleTypes"})
    @Test
    @Override
    public void testEquals() {
        assertTrue(new JSArray(new JSONTape("[]")).equals(new ArrayList<>()));
    }

    @Test
    @Override
    public void getHashCode() {
        ArrayList<IJson> check = new ArrayList<>();
        check.add(new JSArray(new JSONTape("[]")));
        check.add(new JSNumber(new JSONTape("0")));
        check.add(new JSNumber(new JSONTape("0.1")));
        check.add(new JSString(new JSONTape("''")));
        check.add(new JSBoolean(new JSONTape("true")));
        check.add(new JSObject(new JSONTape("{}")));

        assertEquals(check.hashCode(), array.hashCode(), 0);
    }
}