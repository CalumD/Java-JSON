package core;

import api.IJson;
import api.IJsonAble;
import exceptions.JsonParseException;
import exceptions.KeyDifferentTypeException;
import exceptions.KeyNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JSStringTest extends JsonTest {

    private JSString string;

    @BeforeEach
    void setUp() {
        string = new JSString(new JsonTape("\"Hello World\""));
    }

    @Test
    @Override
    public void testDataType() {
        assertEquals(JSType.STRING, string.getDataType());
    }

    @Test
    public void testParseException() {
        try {
            new JSString(new JsonTape("This String Doesnt Have Delimiters"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("T is not a valid string delimiter.\n" +
                    "Line: 1\n" +
                    "Reached: _\n" +
                    "Expected: \" / ' / `", e.getMessage());
        }
        try {
            new JSString(new JsonTape("'No Terminating Delimiter"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Didn't find matching ', before end of string.\n" +
                    "Line: 1\n" +
                    "Reached: 'No Terminating Delimiter_\n" +
                    "Expected: '", e.getMessage());
        }
        try {
            new JSString(new JsonTape("`No Terminating Delimiter"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Didn't find matching `, before end of string.\n" +
                    "Line: 1\n" +
                    "Reached: `No Terminating Delimiter_\n" +
                    "Expected: `", e.getMessage());
        }
        try {
            new JSString(new JsonTape("\"No Terminating Delimiter"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Didn't find matching \", before end of string.\n" +
                    "Line: 1\n" +
                    "Reached: \"No Terminating Delimiter_\n" +
                    "Expected: \"", e.getMessage());
        }
        try {
            new JSString(new JsonTape("No Initial Delimiter'"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("N is not a valid string delimiter.\n" +
                    "Line: 1\n" +
                    "Reached: _\n" +
                    "Expected: \" / ' / `", e.getMessage());

        }
    }

    @Test
    public void testSpecialCharactersSuccess() {
        try {
            assertEquals("\uD83D\uDC4D", new JSString(new JsonTape("'\uD83D\uDC4D'")).getValue());
            assertEquals(" !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~", new JSString(new JsonTape("' !\"#$%&\\'()*+,-./:;<=>?@[\\\\]^_`{|}~'")).getValue());
        } catch (Exception e) {
            fail("The previous String creation should be supported with UTF as well as ascii", e);
        }
    }

    @Test
    @Override
    public void simpleCreateFromString() {
        try {
            string.createFromString("\"Should Create From String\"");
        } catch (JsonParseException e) {
            fail("Create from string should not throw an exception for valid input.", e);
        }
    }

    @Test
    @Override
    public void simpleCreateFromMultilineString() {
        List<String> multiline = new ArrayList<>();
        multiline.add("\"");
        multiline.add("Hey");
        multiline.add("\"");
        try {
            string.createFromMultilineString(multiline);
        } catch (JsonParseException e) {
            fail("Create from string should not throw an exception for valid input.", e);
        }
    }

    @Test
    @Override
    public void containsTheEmptyDoubleQuoteIsTrue() {
        assertTrue(string.contains(""));
    }

    @Test
    @Override
    public void containsNullIsFalse() {
        assertFalse(string.contains(null));
    }

    @Test
    @Override
    public void containsAllTheseKeys() {
        List<String> keys = new ArrayList<>(3);
        keys.add("");
        keys.add("");
        keys.add("");
        assertTrue(string.containsAllKeys(keys));
    }

    @Test
    public void doesntContainAllKeys() {
        List<String> keys = new ArrayList<>(3);
        keys.add("");
        keys.add("someOtherKey");
        assertFalse(string.containsAllKeys(keys));
    }

    @Test
    @Override
    public void getKeys() {
        assertEquals(new ArrayList<>(), string.getKeys());
    }

    @Test
    @Override
    public void getValue() {
        assertEquals("Hello World", string.getValue());
    }

    @Test
    @Override
    public void getValues() {
        List<IJson> toEqual = new ArrayList<>();
        toEqual.add(string);
        assertEquals(toEqual, string.getValues());
    }

    @Test
    public void getValuesNotEqual() {
        List<IJson> toEqual = new ArrayList<>();
        toEqual.add(new JSString(new JsonTape("\"Goodbye World\"")));
        assertNotEquals(toEqual, string.getValues());
    }

    @Test
    @Override
    public void getArray() {
        try {
            string.getArray();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found for key  was not expected. Expected: ARRAY  ->  Received: STRING", e.getMessage());
        }
    }

    @Test
    @Override
    public void getBoolean() {
        try {
            string.getBoolean();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found for key  was not expected. Expected: BOOLEAN  ->  Received: STRING", e.getMessage());
        }
    }

    @Test
    @Override
    public void getDouble() {
        try {
            string.getDouble();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found for key  was not expected. Expected: DOUBLE  ->  Received: STRING", e.getMessage());
        }
    }

    @Test
    @Override
    public void getLong() {
        try {
            string.getLong();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found for key  was not expected. Expected: LONG  ->  Received: STRING", e.getMessage());
        }
    }

    @Test
    @Override
    public void getString() {
        assertEquals("Hello World", string.getString());
    }

    @Test
    @Override
    public void getJSONObject() {
        try {
            string.getJSONObject();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found for key  was not expected. Expected: OBJECT  ->  Received: STRING", e.getMessage());
        }
    }

    @Test
    @Override
    public void getAny() {
        assertEquals(string, string.getAny());
    }

    @Test
    @Override
    public void getValueAt() {
        try {
            string.getValueAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a STRING", e.getMessage());
        }
    }

    @Test
    @Override
    public void getDataTypeOf() {
        try {
            string.getDataTypeOf("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a STRING", e.getMessage());
        }
    }

    @Test
    @Override
    public void getKeysOf() {
        try {
            string.getKeysOf("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a STRING", e.getMessage());
        }
    }

    @Test
    @Override
    public void getValuesOf() {
        try {
            string.getValuesOf("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a STRING", e.getMessage());
        }
    }

    @Test
    @Override
    public void getJSONObjectAt() {
        try {
            string.getJSONObjectAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a STRING", e.getMessage());
        }
    }

    @Test
    @Override
    public void getArrayAt() {
        try {
            string.getArrayAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a STRING", e.getMessage());
        }
    }

    @Test
    @Override
    public void getBooleanAt() {
        try {
            string.getBooleanAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a STRING", e.getMessage());
        }
    }

    @Test
    @Override
    public void getDoubleAt() {
        try {
            string.getDoubleAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a STRING", e.getMessage());
        }
    }

    @Test
    @Override
    public void getLongAt() {
        try {
            string.getLongAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a STRING", e.getMessage());
        }
    }

    @Test
    @Override
    public void getStringAt() {
        try {
            string.getStringAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a STRING", e.getMessage());
        }
    }

    @Test
    public void getStringAtMe() {
        assertEquals("Hello World", string.getStringAt(""));
    }

    @Test
    @Override
    public void getAnyAt() {
        try {
            string.getAnyAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a STRING", e.getMessage());
        }
    }

    @Test
    public void getAnyAtMe() {
        assertEquals(string, string.getAnyAt(""));
    }

    @Test
    @Override
    public void testToString() {
        assertEquals("\"Hello World\"", string.toString());
    }

    @Test
    @Override
    public void toPrettyString() {
        assertEquals("\"Hello World\"", string.toPrettyString());
    }

    @Test
    @Override
    public void asString() {
        assertEquals("\"Hello World\"", string.asString());
    }

    @Test
    @Override
    public void asPrettyString() {
        assertEquals("\"Hello World\"", string.asPrettyString());
    }

    @Test
    @Override
    public void asPrettyStringWithDepth() {
        assertEquals("\"Hello World\"", string.asPrettyString(5));
    }

    @Test
    @Override
    public void asPrettyStringWithDepthAndIndent() {
        assertEquals("\"Hello World\"", string.asPrettyString(5, 4));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "ConstantConditions"})
    @Test
    public void checkNullDoesNotEqual() {
        assertFalse(string.equals(null));
    }

    @Test
    public void checkDoesEqualAgainstSimilarJSString() {
        assertEquals(string, new JSString(new JsonTape("\"Hello World\"")));
    }

    @Test
    public void checkDifferentDelimitersDontMakeADifference() {
        assertEquals(new JSString(new JsonTape("'Hej'")), new JSString(new JsonTape("\"Hej\"")));
    }

    @Test
    public void checkOtherJSStringDoesNotEqual() {
        assertNotEquals(string, new JSString(new JsonTape("\"I am Different\"")));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsBetweenInconvertibleTypes"})
    @Test
    public void checkStringDoesNotEqualDifferentType() {
        assertFalse(string.equals(new char[]{'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'D'}));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsBetweenInconvertibleTypes"})
    @Test
    @Override
    public void testEquals() {
        assertTrue(string.equals("Hello World"));
    }

    @Test
    @Override
    public void getHashCode() {
        assertEquals("Hello World".hashCode(), string.hashCode(), 0);
    }

    @Test
    @Override
    public void jsonIsConvertible() {
        IJsonAble builder = JsonBuilder.builder().addString("key", "value");
        assertEquals(builder.convertToJSON(), string.convertToJSON(builder));
    }
}