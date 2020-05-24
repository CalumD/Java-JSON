package core;

import api.IJson;
import exceptions.JSONParseException;
import exceptions.KeyDifferentTypeException;
import exceptions.KeyNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JSStringTest extends JSONTest {

    private JSString string;

    @BeforeEach
    void setUp() {
        string = new JSString(new JSONTape("\"Hello World\""));
    }

    @Test
    @Override
    public void testDataType() {
        assertEquals(JSType.STRING, string.getDataType());
    }

    @Test
    public void testParseException() {
        assertThrows(JSONParseException.class, () -> new JSString(new JSONTape("This String Doesnt Have Delimiters")));
    }

    @Test
    public void testParseSuccess() {
        try {
            assertEquals("\uD83D\uDC4D", new JSString(new JSONTape("'\uD83D\uDC4D'")).getValue());
            assertEquals(" !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~", new JSString(new JSONTape("' !\"#$%&\\'()*+,-./:;<=>?@[\\\\]^_`{|}~'")).getValue());
        } catch (Exception e) {
            fail("The previous String creation should be supported with UTF as well as ascii");
        }
    }

    @Test
    @Override
    public void simpleCreateFromString() {
        try {
            string.createFromString("\"Should Create From String\"");
        } catch (JSONParseException e) {
            fail("Create from string should not throw an exception for valid input.");
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
        } catch (JSONParseException e) {
            fail("Create from string should not throw an exception for valid input.");
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
        toEqual.add(new JSString(new JSONTape("\"Goodbye World\"")));
        assertNotEquals(toEqual, string.getValues());
    }

    @Test
    @Override
    public void getArray() {
        assertThrows(KeyDifferentTypeException.class, () -> string.getArray());
    }

    @Test
    @Override
    public void getBoolean() {
        assertThrows(KeyDifferentTypeException.class, () -> string.getBoolean());
    }

    @Test
    @Override
    public void getDouble() {
        assertThrows(KeyDifferentTypeException.class, () -> string.getDouble());
    }

    @Test
    @Override
    public void getLong() {
        assertThrows(KeyDifferentTypeException.class, () -> string.getLong());
    }

    @Test
    @Override
    public void getString() {
        assertEquals("Hello World", string.getString());
    }

    @Test
    @Override
    public void getJSONObject() {
        assertThrows(KeyDifferentTypeException.class, () -> string.getJSONObject());
    }

    @Test
    @Override
    public void getAny() {
        assertEquals(string, string.getAny());
    }

    @Test
    @Override
    public void getValueAt() {
        assertThrows(KeyNotFoundException.class, () -> string.getValueAt("someKey"));
    }

    @Test
    @Override
    public void getDataTypeOf() {
        assertThrows(KeyNotFoundException.class, () -> string.getDataTypeOf("someKey"));
    }

    @Test
    @Override
    public void getKeysOf() {
        assertThrows(KeyNotFoundException.class, () -> string.getKeysOf("someKey"));
    }

    @Test
    @Override
    public void getValuesOf() {
        assertThrows(KeyNotFoundException.class, () -> string.getValuesOf("someKey"));
    }

    @Test
    @Override
    public void getJSONObjectAt() {
        assertThrows(KeyNotFoundException.class, () -> string.getJSONObjectAt("someKey"));
    }

    @Test
    @Override
    public void getArrayAt() {
        assertThrows(KeyNotFoundException.class, () -> string.getArrayAt("someKey"));
    }

    @Test
    @Override
    public void getBooleanAt() {
        assertThrows(KeyNotFoundException.class, () -> string.getBooleanAt("someKey"));
    }

    @Test
    @Override
    public void getDoubleAt() {
        assertThrows(KeyNotFoundException.class, () -> string.getDoubleAt("someKey"));
    }

    @Test
    public void getDoubleAtMe() {
        assertThrows(KeyDifferentTypeException.class, () -> string.getDoubleAt(""));
    }

    @Test
    @Override
    public void getLongAt() {
        assertThrows(KeyNotFoundException.class, () -> string.getLongAt("someKey"));
    }

    @Test
    public void getLongAtMe() {
        assertThrows(KeyDifferentTypeException.class, () -> string.getLongAt(""));
    }

    @Test
    @Override
    public void getStringAt() {
        assertThrows(KeyNotFoundException.class, () -> string.getStringAt("someKey"));
    }

    @Test
    public void getStringAtMe() {
        assertEquals("Hello World", string.getStringAt(""));
    }

    @Test
    @Override
    public void getAnyAt() {
        assertThrows(KeyNotFoundException.class, () -> string.getAnyAt("someKey"));
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
        assertEquals(string, new JSString(new JSONTape("\"Hello World\"")));
    }

    @Test
    public void checkDifferentDelimitersDontMakeADifference() {
        assertEquals(new JSString(new JSONTape("'Hej'")), new JSString(new JSONTape("\"Hej\"")));
    }

    @Test
    public void checkOtherJSStringDoesNotEqual() {
        assertNotEquals(string, new JSString(new JSONTape("\"I am Different\"")));
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
}