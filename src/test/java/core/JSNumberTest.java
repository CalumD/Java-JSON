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

public class JSNumberTest extends JSONTest {

    private JSNumber numberDouble;
    private JSNumber numberLong;

    @BeforeEach
    void setUp() {
        numberDouble = new JSNumber(new JSONTape("1.25"));
        numberLong = new JSNumber(new JSONTape("321"));
    }

    @Test
    @Override
    public void testDataType() {
        assertEquals(JSType.LONG, numberLong.getDataType());
        assertEquals(JSType.DOUBLE, numberDouble.getDataType());
    }

    @Test
    public void testParseException() {
        assertThrows(JSONParseException.class, () -> new JSNumber(new JSONTape("1.1.")));
        assertThrows(JSONParseException.class, () -> new JSNumber(new JSONTape("1.1.1")));
        assertThrows(JSONParseException.class, () -> new JSNumber(new JSONTape("123-1")));
        assertThrows(JSONParseException.class, () -> new JSNumber(new JSONTape("99999999999999999999999999999")));
        assertThrows(JSONParseException.class, () -> new JSNumber(new JSONTape("-9999999999999999999999999999")));
        assertThrows(JSONParseException.class, () -> new JSNumber(new JSONTape("--1")));
        assertThrows(JSONParseException.class, () -> new JSNumber(new JSONTape("++1")));
        assertThrows(JSONParseException.class, () -> new JSNumber(new JSONTape("+-1")));
        assertThrows(JSONParseException.class, () -> new JSNumber(new JSONTape("^1")));
        assertThrows(JSONParseException.class, () -> new JSNumber(new JSONTape("e^1")));
        assertThrows(JSONParseException.class, () -> new JSNumber(new JSONTape("1e-0.05")));
    }

    @Test
    @Override
    public void simpleCreateFromString() {
        try {
            numberLong.createFromString("12345");
            new JSNumber(new JSONTape("1e-005"));
            new JSNumber(new JSONTape("-4.3e10"));
        } catch (JSONParseException e) {
            fail("Create from string should not throw an exception for valid input.", e);
        }
    }

    @Test
    @Override
    public void simpleCreateFromMultilineString() {
        List<String> multiline = new ArrayList<>();
        multiline.add("[");
        multiline.add("1.859");
        multiline.add("]");
        try {
            numberLong.createFromMultilineString(multiline);
        } catch (JSONParseException e) {
            fail("Create from string should not throw an exception for valid input.", e);
        }
    }

    @Test
    @Override
    public void containsTheEmptyDoubleQuoteIsTrue() {
        assertTrue(numberDouble.contains(""));
        assertTrue(numberLong.contains(""));
    }

    @Test
    @Override
    public void containsNullIsFalse() {
        assertFalse(numberDouble.contains(null));
        assertFalse(numberLong.contains(null));
    }

    @Test
    @Override
    public void containsAllTheseKeys() {
        List<String> keys = new ArrayList<>(3);
        keys.add("");
        keys.add("");
        keys.add("");
        assertTrue(numberLong.containsAllKeys(keys));
    }

    @Test
    public void doesntContainAllKeys() {
        List<String> keys = new ArrayList<>(3);
        keys.add("");
        keys.add("someOtherKey");
        assertFalse(numberLong.containsAllKeys(keys));
    }

    @Test
    @Override
    public void getKeys() {
        assertEquals(new ArrayList<>(), numberLong.getKeys());
        assertEquals(new ArrayList<>(), numberDouble.getKeys());
    }

    @Test
    @Override
    public void getValue() {
        assertEquals(321, (long) numberLong.getValue(), 0);
        assertEquals(1.25, (double) numberDouble.getValue(), 0);
    }

    @Test
    @Override
    public void getValues() {
        List<IJson> toEqual = new ArrayList<>();
        toEqual.add(numberLong);
        assertEquals(toEqual, numberLong.getValues());
    }

    @Test
    public void getValuesNotEqual() {
        List<IJson> toEqual = new ArrayList<>();
        toEqual.add(numberLong);
        assertNotEquals(toEqual, numberDouble.getValues());
    }

    @Test
    @Override
    public void getArray() {
        assertThrows(KeyDifferentTypeException.class, () -> numberLong.getArray());
    }

    @Test
    @Override
    public void getBoolean() {
        assertThrows(KeyDifferentTypeException.class, () -> numberLong.getBoolean());
    }

    @Test
    @Override
    public void getDouble() {
        assertThrows(KeyDifferentTypeException.class, () -> numberLong.getDouble());
        assertEquals(1.25, numberDouble.getDouble(), 0);
    }

    @Test
    @Override
    public void getLong() {
        assertThrows(KeyDifferentTypeException.class, () -> numberDouble.getLong());
        assertEquals(321L, numberLong.getLong(), 0);
    }

    @Test
    @Override
    public void getString() {
        assertThrows(KeyDifferentTypeException.class, () -> numberLong.getString());
    }

    @Test
    @Override
    public void getJSONObject() {
        assertThrows(KeyDifferentTypeException.class, () -> numberLong.getJSONObject());
    }

    @Test
    @Override
    public void getAny() {
        assertEquals(numberLong, numberLong.getAny());
        assertEquals(numberDouble, numberDouble.getAny());
    }

    @Test
    @Override
    public void getValueAt() {
        assertThrows(KeyNotFoundException.class, () -> numberLong.getValueAt("someKey"));
    }

    @Test
    @Override
    public void getDataTypeOf() {
        assertThrows(KeyNotFoundException.class, () -> numberLong.getDataTypeOf("someKey"));
    }

    @Test
    @Override
    public void getKeysOf() {
        assertThrows(KeyNotFoundException.class, () -> numberLong.getKeysOf("someKey"));
    }

    @Test
    @Override
    public void getValuesOf() {
        assertThrows(KeyNotFoundException.class, () -> numberLong.getValuesOf("someKey"));
    }

    @Test
    @Override
    public void getJSONObjectAt() {
        assertThrows(KeyNotFoundException.class, () -> numberLong.getJSONObjectAt("someKey"));
    }

    @Test
    @Override
    public void getArrayAt() {
        assertThrows(KeyNotFoundException.class, () -> numberLong.getArrayAt("someKey"));
    }

    @Test
    @Override
    public void getBooleanAt() {
        assertThrows(KeyNotFoundException.class, () -> numberLong.getBooleanAt("someKey"));
    }

    @Test
    @Override
    public void getDoubleAt() {
        assertThrows(KeyNotFoundException.class, () -> numberLong.getDoubleAt("someKey"));
    }

    @Test
    public void getDoubleAtMe() {
        assertEquals(1.25, numberDouble.getDoubleAt(""), 0);
    }

    @Test
    @Override
    public void getLongAt() {
        assertThrows(KeyNotFoundException.class, () -> numberLong.getLongAt("someKey"));
    }

    @Test
    public void getLongAtMe() {
        assertEquals(321L, numberLong.getLongAt(""), 0);
    }

    @Test
    @Override
    public void getStringAt() {
        assertThrows(KeyNotFoundException.class, () -> numberLong.getStringAt("someKey"));
    }

    @Test
    @Override
    public void getAnyAt() {
        assertThrows(KeyNotFoundException.class, () -> numberLong.getAnyAt("someKey"));
    }

    @Test
    public void getAnyAtMe() {
        assertEquals(numberDouble, numberDouble.getAnyAt(""));
    }

    @Test
    @Override
    public void testToString() {
        assertEquals("321", numberLong.toString());
        assertEquals("1.25", numberDouble.toString());
    }

    @Test
    @Override
    public void toPrettyString() {
        assertEquals("321", numberLong.toPrettyString());
        assertEquals("1.25", numberDouble.toPrettyString());
    }

    @Test
    @Override
    public void asString() {
        assertEquals("321", numberLong.asString());
        assertEquals("1.25", numberDouble.asString());
    }

    @Test
    @Override
    public void asPrettyString() {
        assertEquals("321", numberLong.asPrettyString());
        assertEquals("1.25", numberDouble.asPrettyString());
    }

    @Test
    @Override
    public void asPrettyStringWithDepth() {
        assertEquals("321", numberLong.asPrettyString(5));
        assertEquals("1.25", numberDouble.asPrettyString(5));
    }

    @Test
    @Override
    public void asPrettyStringWithDepthAndIndent() {
        assertEquals("321", numberLong.asPrettyString(5, 4));
        assertEquals("1.25", numberDouble.asPrettyString(5, 4));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "ConstantConditions"})
    @Test
    public void checkNullDoesNotEqual() {
        assertFalse(numberLong.equals(null));
        assertFalse(numberDouble.equals(null));
    }

    @Test
    public void checkSameObjectDoesEqual() {
        assertEquals(numberLong, numberLong);
        assertEquals(numberDouble, numberDouble);
    }

    @Test
    public void checkDoesNotEqualAgainstOppositeJSNumber() {
        assertNotEquals(numberLong, numberDouble);
        assertNotEquals(numberDouble, numberLong);
    }

    @Test
    public void checkDoesEqualAgainstSimilarJSNumber() {
        assertEquals(numberLong, new JSNumber(new JSONTape("321")));
        assertEquals(numberDouble, new JSNumber(new JSONTape("1.25")));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsBetweenInconvertibleTypes"})
    @Test
    public void checkRegularNumberDoesEqual() {
        assertTrue(numberLong.equals(321L));
        assertTrue(numberDouble.equals(1.25));
        assertTrue(numberDouble.equals(1.25D));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsBetweenInconvertibleTypes"})
    @Test
    public void checkRegularNumberDoesNotEqualDifferentType() {
        assertFalse(numberLong.equals(321));
        assertFalse(numberDouble.equals(1.25F));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsBetweenInconvertibleTypes"})
    @Test
    @Override
    public void testEquals() {
        assertTrue(numberLong.equals(321L));
        assertTrue(numberDouble.equals(1.25));
        assertTrue(numberDouble.equals(1.25D));
    }

    @Test
    @Override
    public void getHashCode() {
        assertEquals(Long.hashCode(321L), numberLong.hashCode(), 0);
        assertEquals(Double.hashCode(1.25), numberDouble.hashCode(), 0);
    }
}