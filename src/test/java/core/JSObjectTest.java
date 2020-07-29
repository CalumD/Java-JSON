package core;

import api.IJson;
import api.IJsonAble;
import exceptions.JsonParseException;
import exceptions.KeyDifferentTypeException;
import exceptions.KeyNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JSObjectTest extends JsonTest {

    private JSObject object;

    @BeforeEach
    void setUp() {
        object = new JSObject(new JsonTape("{\"array\":[], \"long\":0, \"double\": 0.1, \"string\":'', \"boolean\":true, \"object\":{}}"));
    }

    @Test
    @Override
    public void testDataType() {
        assertEquals(JSType.OBJECT, object.getDataType());
    }

    @Test
    public void testParseException() {
        try {
            new JSObject(new JsonTape("{"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Reached the end of the JSON input before parsing was complete. Are you missing a terminating delimiter?", e.getMessage());
        }
        try {
            new JSObject(new JsonTape("}"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Reached the end of the JSON input before parsing was complete. Are you missing a terminating delimiter?", e.getMessage());
        }
        try {
            new JSObject(new JsonTape(""));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("You cannot create something from nothing. Input was empty.", e.getMessage());
        }
        try {
            new JSObject(new JsonTape("{{}}"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Missing Key at start of Object.\n" +
                    "Line: 1\n" +
                    "Reached: {_\n" +
                    "Expected: \"", e.getMessage());
        }
        try {
            new JSObject(new JsonTape("{[]}"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Missing Key at start of Object.\n" +
                    "Line: 1\n" +
                    "Reached: {_\n" +
                    "Expected: \"", e.getMessage());
        }
        try {
            new JSObject(new JsonTape("{1,}"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Missing Key at start of Object.\n" +
                    "Line: 1\n" +
                    "Reached: {_\n" +
                    "Expected: \"", e.getMessage());
        }
        try {
            new JSObject(new JsonTape("{,1}"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Missing Key at start of Object.\n" +
                    "Line: 1\n" +
                    "Reached: {_\n" +
                    "Expected: \"", e.getMessage());
        }
        try {
            new JSObject(new JsonTape("{1, [}"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Missing Key at start of Object.\n" +
                    "Line: 1\n" +
                    "Reached: {_\n" +
                    "Expected: \"", e.getMessage());
        }
        try {
            new JSObject(new JsonTape("{\"\":1}"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Illegal Object Key (Empty).\n" +
                    "Line: 1\n" +
                    "Reached: {\"\"_\n" +
                    "Expected: <Valid Key>", e.getMessage());
        }
        try {
            new JSObject(new JsonTape("{\"key:1}"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Didn't find matching \", before end of string.\n" +
                    "Line: 1\n" +
                    "Reached: {\"key:1}_\n" +
                    "Expected: \"", e.getMessage());
        }
        try {
            new JSObject(new JsonTape("{key\":1}"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Missing Key at start of Object.\n" +
                    "Line: 1\n" +
                    "Reached: {_\n" +
                    "Expected: \"", e.getMessage());
        }
        try {
            new JSObject(new JsonTape("{\"key\"1}"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Invalid Key:Value separator. Must use a colon(:).\n" +
                    "Line: 1\n" +
                    "Reached: {\"key\"_\n" +
                    "Expected: :", e.getMessage());
        }
        try {
            new JSObject(new JsonTape("{\"key\"}"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Invalid Key:Value separator. Must use a colon(:).\n" +
                    "Line: 1\n" +
                    "Reached: {\"key\"_\n" +
                    "Expected: :", e.getMessage());
        }
        try {
            new JSObject(new JsonTape("{'key':1,}"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Comma suggests more object elements, but object terminates.\n" +
                    "Line: 1\n" +
                    "Reached: {'key':1,_\n" +
                    "Expected: { / [ / \" / <number> / <boolean> ", e.getMessage());
        }
        try {
            new JSObject(new JsonTape("{'key':1]"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Invalid object child delimiter.\n" +
                    "Line: 1\n" +
                    "Reached: {'key':1_\n" +
                    "Expected: , / }", e.getMessage());
        }
        try {
            new JSObject(new JsonTape("{'key'=1}"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Invalid Key:Value separator. Must use a colon(:).\n" +
                    "Line: 1\n" +
                    "Reached: {'key'_\n" +
                    "Expected: :", e.getMessage());
        }
        try {
            new JSObject(new JsonTape("{\"samekey\":1,2: 3}"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Invalid type for object key.\n" +
                    "Line: 1\n" +
                    "Reached: {\"samekey\":1,_\n" +
                    "Expected: \"", e.getMessage());
        }
    }

    @Test
    @Override
    public void simpleCreateFromString() {
        try {
            object.createFromString("{}");
            object.createFromString("{'easy': \"value\"}");
            object.createFromString("{\"easy\": 'value'}");
        } catch (JsonParseException e) {
            fail("Create from string should not throw an exception for valid input.", e);
        }
    }

    @Test
    public void keysWithSpacesAreOkay() {
        try {
            IJson obj = object.createFromString("{\"I have spaces\": 1}");
            assertEquals(1, obj.getLongAt("['I have spaces']"));
        } catch (JsonParseException e) {
            fail("Create from string should not throw an exception for valid input.", e);
        }
    }

    @Test
    public void duplicateKeysNotAllowed() {
        try {
            new JSObject(new JsonTape("{\"samekey\":1,'samekey': 1}"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Illegal Object key (Duplicate): samekey\n" +
                    "Line: 1\n" +
                    "Reached: {\"samekey\":1,'samekey'_\n" +
                    "Expected: <Unique Key>", e.getMessage());
        }
    }

    @Test
    public void keysAreCaseSensitive() {
        assertEquals("{\"Samekey\":1,\"samekey\":1}", new JSObject(new JsonTape("{\"samekey\":1,'Samekey': 1}")).asString());
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
        } catch (JsonParseException e) {
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
        try {
            object.containsAllKeys(keys2);
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("0 is not a valid accessor on element: <base element>", e.getMessage());
        }
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
        values.add(new JSArray(new JsonTape("[]")));
        values.add(new JSNumber(new JsonTape("0")));
        values.add(new JSNumber(new JsonTape("0.1")));
        values.add(new JSString(new JsonTape("''")));
        values.add(new JSBoolean(new JsonTape("true")));
        values.add(new JSObject(new JsonTape("{}")));

        assertTrue(values.containsAll(object.getValues()));
        assertEquals(values.size(), object.getValues().size(), 0);
    }

    @Test
    public void getValuesNotEqual() {
        List<IJson> values = new ArrayList<>(3);
        values.add(new JSArray(new JsonTape("[]")));
        values.add(new JSNumber(new JsonTape("0")));
        values.add(new JSNumber(new JsonTape("0.1")));
        values.add(new JSString(new JsonTape("'THE STRING IS DIFFERENT'")));
        values.add(new JSBoolean(new JsonTape("true")));
        values.add(new JSObject(new JsonTape("{}")));

        assertNotEquals(values, object.getValue());
    }

    @Test
    @Override
    public void getArray() {
        try {
            object.getArray();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found for key  was not expected. Expected: ARRAY  ->  Received: OBJECT", e.getMessage());
        }
    }

    @Test
    @Override
    public void getBoolean() {
        try {
            object.getBoolean();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found for key  was not expected. Expected: BOOLEAN  ->  Received: OBJECT", e.getMessage());
        }
    }

    @Test
    @Override
    public void getDouble() {
        try {
            object.getDouble();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found for key  was not expected. Expected: DOUBLE  ->  Received: OBJECT", e.getMessage());
        }
    }

    @Test
    @Override
    public void getLong() {
        try {
            object.getLong();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found for key  was not expected. Expected: LONG  ->  Received: OBJECT", e.getMessage());
        }
    }

    @Test
    @Override
    public void getString() {
        try {
            object.getString();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found for key  was not expected. Expected: STRING  ->  Received: OBJECT", e.getMessage());
        }
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
        try {
            object.getValueAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("someKey not found on element: <base element>", e.getMessage());
        }
        assertTrue((boolean) object.getValueAt("boolean"));
    }

    @Test
    @Override
    public void getDataTypeOf() {
        try {
            object.getDataTypeOf("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("someKey not found on element: <base element>", e.getMessage());
        }
        assertEquals(JSType.LONG, object.getDataTypeOf("long"));
    }

    @Test
    @Override
    public void getKeysOf() {
        try {
            object.getKeysOf("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("someKey not found on element: <base element>", e.getMessage());
        }
        assertEquals(new ArrayList<>(), object.getKeysOf("array"));
    }

    @Test
    @Override
    public void getValuesOf() {
        try {
            object.getValuesOf("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("someKey not found on element: <base element>", e.getMessage());
        }
        assertEquals(new ArrayList<>(), object.getValuesOf("object"));
    }

    @Test
    @Override
    public void getJSONObjectAt() {
        try {
            object.getJSONObjectAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("someKey not found on element: <base element>", e.getMessage());
        }
        assertEquals(new JSObject(new JsonTape("{}")), object.getJSONObjectAt("object"));
    }

    @Test
    public void getJSONObjectAtMe() {
        try {
            object.getJSONObjectAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("someKey not found on element: <base element>", e.getMessage());
        }
        assertEquals(object, object.getJSONObjectAt(""));
    }

    @SuppressWarnings({"AssertEqualsBetweenInconvertibleTypes"})
    @Test
    @Override
    public void getArrayAt() {
        try {
            object.getArrayAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("someKey not found on element: <base element>", e.getMessage());
        }
        assertEquals(new JSArray(new JsonTape("[]")), object.getArrayAt("array"));
    }

    @Test
    @Override
    public void getBooleanAt() {
        try {
            object.getBooleanAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("someKey not found on element: <base element>", e.getMessage());
        }
        assertTrue(object.getBooleanAt("boolean"));
    }

    @Test
    @Override
    public void getDoubleAt() {
        try {
            object.getDoubleAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("someKey not found on element: <base element>", e.getMessage());
        }
        assertEquals(0.1, object.getDoubleAt("double"), 0);
    }

    @Test
    @Override
    public void getLongAt() {
        try {
            object.getLongAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("someKey not found on element: <base element>", e.getMessage());
        }
        assertEquals(0, object.getLongAt("long"), 0);
    }

    @Test
    @Override
    public void getStringAt() {
        try {
            object.getStringAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("someKey not found on element: <base element>", e.getMessage());
        }
        assertEquals("", object.getStringAt("string"));
    }

    @Test
    @Override
    public void getAnyAt() {
        try {
            object.getAnyAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("someKey not found on element: <base element>", e.getMessage());
        }
        assertEquals("", object.getAnyAt("string").getString());
    }

    @Test
    public void getVeryNestedValue() {
        assertTrue(new JSObject(new JsonTape("{'first':[[{\"nest\": [{'furtherNest': true}]}]]}")).getBooleanAt("first[0][0].nest[0]['furtherNest']"));
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
        assertEquals("{\n  \"top\": {\n    <middle,top,bottom>\n  } \n}", new JSObject(new JsonTape("{'top':{'top':[],'middle':{},'bottom':[]}}")).asPrettyString(1));
    }

    @Test
    @Override
    public void asPrettyStringWithDepthAndIndent() {
        assertEquals("{\n      \"boolean\": true,\n      \"string\": \"\",\n      \"array\": [],\n      \"double\": 0.1,\n      \"long\": 0,\n      \"object\": {} \n}", object.asPrettyString(5, 6));
        assertEquals("{\n      \"first\": [\n            [\n                  {\n                        <nest>\n                  } \n            ] \n      ] \n}", new JSObject(new JsonTape("{'first':[[{\"nest\": [{'furtherNest': true}]}]]}")).asPrettyString(3, 6));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "ConstantConditions"})
    @Test
    public void checkNullDoesNotEqual() {
        assertFalse(object.equals(null));
    }

    @Test
    public void checkDifferentSizeObjectEqual() {
        assertNotEquals(new JSObject(new JsonTape("{'same': 1, 'different': 2}")), new JSObject(new JsonTape("{'same': 1}")));
    }

    @Test
    public void checkSameSizeObjectDoestEqualWtihDifferentKeys() {
        assertNotEquals(new JSObject(new JsonTape("{'same': 1}")), new JSObject(new JsonTape("{'different': 1}")));
    }

    @Test
    public void checkOtherJSObjectDoesNotEqual() {
        assertNotEquals(object, new JSObject(new JsonTape("{\"array\":[], \"long\":0, \"double\": 0.1, \"string\":'I AM DIFFERENT', \"boolean\":true, \"object\":{}}")));
    }

    @Test
    @Override
    public void testEquals() {
        assertEquals(new JSObject(new JsonTape("{'same': 1}")), new JSObject(new JsonTape("{'same': 1}")));
    }

    @Test
    @Override
    public void getHashCode() {
        HashMap<String, IJson> check = new HashMap<>();
        check.put("array", new JSArray(new JsonTape("[]")));
        check.put("long", new JSNumber(new JsonTape("0")));
        check.put("double", new JSNumber(new JsonTape("0.1")));
        check.put("string", new JSString(new JsonTape("''")));
        check.put("boolean", new JSBoolean(new JsonTape("true")));
        check.put("object", new JSObject(new JsonTape("{}")));

        assertEquals(check.hashCode(), object.hashCode(), 0);
    }

    @Test
    @Override
    public void jsonIsConvertible() {
        IJsonAble builder = JsonBuilder.builder().addString("key", "value");
        assertEquals(builder.convertToJSON(), object.convertToJSON(builder));
    }
}