package core;

import api.IJson;
import exceptions.JSONParseException;
import exceptions.KeyDifferentTypeException;
import exceptions.KeyNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JSObjectTest extends JSONTest {

    private JSObject object;

    @BeforeEach
    void setUp() {
        object = new JSObject(new JSONTape("{\"array\":[], \"long\":0, \"double\": 0.1, \"string\":'', \"boolean\":true, \"object\":{}}"));
    }

    @Test
    @Override
    public void testDataType() {
        assertEquals(JSType.OBJECT, object.getDataType());
    }

    @Test
    public void testParseException() {
        assertThrows(JSONParseException.class, () -> new JSObject(new JSONTape("{")));
        assertThrows(JSONParseException.class, () -> new JSObject(new JSONTape("}")));
        assertThrows(JSONParseException.class, () -> new JSObject(new JSONTape("")));
        assertThrows(JSONParseException.class, () -> new JSObject(new JSONTape("{{}}")));
        assertThrows(JSONParseException.class, () -> new JSObject(new JSONTape("{[]}")));
        assertThrows(JSONParseException.class, () -> new JSObject(new JSONTape("{1,}")));
        assertThrows(JSONParseException.class, () -> new JSObject(new JSONTape("{,1}")));
        assertThrows(JSONParseException.class, () -> new JSObject(new JSONTape("{1, [}")));
        assertThrows(JSONParseException.class, () -> new JSObject(new JSONTape("{\"\":1}")));
        assertThrows(JSONParseException.class, () -> new JSObject(new JSONTape("{\"key:1}")));
        assertThrows(JSONParseException.class, () -> new JSObject(new JSONTape("{key\":1}")));
        assertThrows(JSONParseException.class, () -> new JSObject(new JSONTape("{\"key\"1}")));
        assertThrows(JSONParseException.class, () -> new JSObject(new JSONTape("{\"key\"}")));
        assertThrows(JSONParseException.class, () -> new JSObject(new JSONTape("{'key':1,}")));
        assertThrows(JSONParseException.class, () -> new JSObject(new JSONTape("{'key':1]")));
        assertThrows(JSONParseException.class, () -> new JSObject(new JSONTape("{'key'=1}")));
        assertThrows(JSONParseException.class, () -> new JSObject(new JSONTape("{\"samekey\":1,2: 3}")));
    }

    @Test
    @Override
    public void simpleCreateFromString() {
        try {
            object.createFromString("{}");
            object.createFromString("{'easy': \"value\"}");
            object.createFromString("{\"easy\": 'value'}");
        } catch (JSONParseException e) {
            fail("Create from string should not throw an exception for valid input.", e);
        }
    }

    @Test
    public void duplicateKeysNotAllowed() {
        assertThrows(JSONParseException.class, () -> new JSObject(new JSONTape("{\"samekey\":1,'samekey': 1}")));
    }

    @Test
    public void keysAreCaseSensitive() {
        assertEquals("{\"Samekey\":1,\"samekey\":1}", new JSObject(new JSONTape("{\"samekey\":1,'Samekey': 1}")).asString());
    }

    @Test
    @Override
    public void simpleCreateFromMultilineString() {
        List<String> multiline = new ArrayList<>(7);
        multiline.add("{");
        multiline.add("'key'");
        multiline.add(":");
        multiline.add("1");
        multiline.add("}");
        try {
            object.createFromMultilineString(multiline);
        } catch (JSONParseException e) {
            fail("Create from string should not throw an exception for valid input.", e);
        }
    }

    @Test
    @Override
    public void containsTheEmptyDoubleQuoteIsTrue() {
        assertTrue(object.contains(""));
    }

    @Test
    @Override
    public void containsNullIsFalse() {
        assertFalse(object.contains(null));
    }

    @Test
    @Override
    public void containsAllTheseKeys() {
        List<String> keys = new ArrayList<>(7);
        keys.add("");
        keys.add("array");
        keys.add("long");
        keys.add("double");
        keys.add("string");
        keys.add("boolean");
        keys.add("object");
        assertTrue(object.containsAllKeys(keys));
    }

    @Test
    public void doesntContainAllKeys() {
        List<String> keys1 = new ArrayList<>(2);
        keys1.add(""); // Known Good Key from test above
        keys1.add("someOtherKey");

        List<String> keys2 = new ArrayList<>(2);
        keys2.add(""); // Known Good Key from test above
        keys2.add("[0]"); // array accessor

        List<String> keys3 = new ArrayList<>(2);
        keys3.add(""); // Known Good Key from test above
        keys3.add("1");

        assertFalse(object.containsAllKeys(keys1));
        assertThrows(KeyDifferentTypeException.class, () -> object.containsAllKeys(keys2));
        assertFalse(object.containsAllKeys(keys3));
    }

    @Test
    @Override
    public void getKeys() {
        List<String> keys = new ArrayList<>(5);
        keys.add("array");
        keys.add("long");
        keys.add("double");
        keys.add("string");
        keys.add("boolean");
        keys.add("object");

        assertTrue(keys.containsAll(object.getKeys()));
        assertEquals(keys.size(), object.getKeys().size(), 0);
    }

    @Test
    @Override
    public void getValue() {
        assertEquals(object, object.getValue());
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

        assertTrue(values.containsAll(object.getValues()));
        assertEquals(values.size(), object.getValues().size(), 0);
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

        assertNotEquals(values, object.getValue());
    }

    @Test
    @Override
    public void getArray() {
        assertThrows(KeyDifferentTypeException.class, () -> object.getArray());
    }

    @Test
    @Override
    public void getBoolean() {
        assertThrows(KeyDifferentTypeException.class, () -> object.getBoolean());
    }

    @Test
    @Override
    public void getDouble() {
        assertThrows(KeyDifferentTypeException.class, () -> object.getDouble());
    }

    @Test
    @Override
    public void getLong() {
        assertThrows(KeyDifferentTypeException.class, () -> object.getLong());
    }

    @Test
    @Override
    public void getString() {
        assertThrows(KeyDifferentTypeException.class, () -> object.getString());
    }

    @Test
    @Override
    public void getJSONObject() {
        assertEquals(object, object.getJSONObject());
    }

    @Test
    @Override
    public void getAny() {
        assertEquals(object, object.getAny());
    }

    @Test
    @Override
    public void getValueAt() {
        assertThrows(KeyNotFoundException.class, () -> object.getValueAt("someKey"));
        assertTrue((boolean) object.getValueAt("boolean"));
    }

    @Test
    @Override
    public void getDataTypeOf() {
        assertThrows(KeyNotFoundException.class, () -> object.getValueAt("someKey"));
        assertEquals(JSType.LONG, object.getDataTypeOf("long"));
    }

    @Test
    @Override
    public void getKeysOf() {
        assertThrows(KeyNotFoundException.class, () -> object.getKeysOf("someKey"));
        assertEquals(new ArrayList<>(), object.getKeysOf("array"));
    }

    @Test
    @Override
    public void getValuesOf() {
        assertThrows(KeyNotFoundException.class, () -> object.getValuesOf("someKey"));
        assertEquals(new ArrayList<>(), object.getValuesOf("object"));
    }

    @Test
    @Override
    public void getJSONObjectAt() {
        assertThrows(KeyNotFoundException.class, () -> object.getJSONObjectAt("someKey"));
        assertEquals(new JSObject(new JSONTape("{}")), object.getJSONObjectAt("object"));
    }

    @Test
    public void getJSONObjectAtMe() {
        assertThrows(KeyNotFoundException.class, () -> object.getJSONObjectAt("someKey"));
        assertEquals(object, object.getJSONObjectAt(""));
    }

    @SuppressWarnings({"AssertEqualsBetweenInconvertibleTypes"})
    @Test
    @Override
    public void getArrayAt() {
        assertThrows(KeyNotFoundException.class, () -> object.getArrayAt("someKey"));
        assertEquals(new JSArray(new JSONTape("[]")), object.getArrayAt("array"));
    }

    @Test
    @Override
    public void getBooleanAt() {
        assertThrows(KeyNotFoundException.class, () -> object.getBooleanAt("someKey"));
        assertTrue(object.getBooleanAt("boolean"));
    }

    @Test
    @Override
    public void getDoubleAt() {
        assertThrows(KeyNotFoundException.class, () -> object.getDoubleAt("someKey"));
        assertEquals(0.1, object.getDoubleAt("double"), 0);
    }

    @Test
    @Override
    public void getLongAt() {
        assertThrows(KeyNotFoundException.class, () -> object.getLongAt("someKey"));
        assertEquals(0, object.getLongAt("long"), 0);
    }

    @Test
    @Override
    public void getStringAt() {
        assertThrows(KeyNotFoundException.class, () -> object.getStringAt("someKey"));
        assertEquals("", object.getStringAt("string"));
    }

    @Test
    @Override
    public void getAnyAt() {
        assertThrows(KeyNotFoundException.class, () -> object.getAnyAt("someKey"));
        assertEquals("", object.getAnyAt("string").getString());
    }

    @Test
    public void getVeryNestedValue() {
        assertTrue(new JSObject(new JSONTape("{'first':[[{\"nest\": [{'furtherNest': true}]}]]}")).getBooleanAt("first[0][0].nest[0]['furtherNest']"));
    }

    @Test
    public void getAnyAtMe() {
        assertEquals(object, object.getAnyAt(""));
    }

    @Test
    @Override
    public void testToString() {
        assertEquals("{<boolean,string,array,double,long,object>}", object.toString());
    }

    @Test
    @Override
    public void toPrettyString() {
        assertEquals("{\n  <boolean,string,array,double,long,object>\n}", object.toPrettyString());
    }

    @Test
    @Override
    public void asString() {
        assertEquals("{\"boolean\":true,\"string\":\"\",\"array\":[],\"double\":0.1,\"long\":0,\"object\":{}}", object.asString());
    }

    @Test
    @Override
    public void asPrettyString() {
        assertEquals("{\n  \"boolean\": true,\n  \"string\": \"\",\n  \"array\": [],\n  \"double\": 0.1,\n  \"long\": 0,\n  \"object\": {} \n}", object.asPrettyString());
    }

    @Test
    @Override
    public void asPrettyStringWithDepth() {
        assertEquals("{\n  \"boolean\": true,\n  \"string\": \"\",\n  \"array\": [],\n  \"double\": 0.1,\n  \"long\": 0,\n  \"object\": {} \n}", object.asPrettyString(5));
        assertEquals("{\n  \"top\": {\n    <middle,top,bottom>\n  } \n}", new JSObject(new JSONTape("{'top':{'top':[],'middle':{},'bottom':[]}}")).asPrettyString(1));
    }

    @Test
    @Override
    public void asPrettyStringWithDepthAndIndent() {
        assertEquals("{\n      \"boolean\": true,\n      \"string\": \"\",\n      \"array\": [],\n      \"double\": 0.1,\n      \"long\": 0,\n      \"object\": {} \n}", object.asPrettyString(5, 6));
        assertEquals("{\n      \"first\": [\n            [\n                  {\n                        <nest>\n                  } \n            ] \n      ] \n}", new JSObject(new JSONTape("{'first':[[{\"nest\": [{'furtherNest': true}]}]]}")).asPrettyString(3, 6));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "ConstantConditions"})
    @Test
    public void checkNullDoesNotEqual() {
        assertFalse(object.equals(null));
    }

    @Test
    public void checkDifferentSizeObjectEqual() {
        assertNotEquals(new JSObject(new JSONTape("{'same': 1, 'different': 2}")), new JSObject(new JSONTape("{'same': 1}")));
    }

    @Test
    public void checkSameSizeObjectDoestEqualWtihDifferentKeys() {
        assertNotEquals(new JSObject(new JSONTape("{'same': 1}")), new JSObject(new JSONTape("{'different': 1}")));
    }

    @Test
    public void checkOtherJSObjectDoesNotEqual() {
        assertNotEquals(object, new JSObject(new JSONTape("{\"array\":[], \"long\":0, \"double\": 0.1, \"string\":'I AM DIFFERENT', \"boolean\":true, \"object\":{}}")));
    }

    @Test
    @Override
    public void testEquals() {
        assertEquals(new JSObject(new JSONTape("{'same': 1}")), new JSObject(new JSONTape("{'same': 1}")));
    }

    @Test
    @Override
    public void getHashCode() {
        HashMap<String, IJson> check = new HashMap<>();
        check.put("array", new JSArray(new JSONTape("[]")));
        check.put("long", new JSNumber(new JSONTape("0")));
        check.put("double", new JSNumber(new JSONTape("0.1")));
        check.put("string", new JSString(new JSONTape("''")));
        check.put("boolean", new JSBoolean(new JSONTape("true")));
        check.put("object", new JSObject(new JSONTape("{}")));

        assertEquals(check.hashCode(), object.hashCode(), 0);
    }
}