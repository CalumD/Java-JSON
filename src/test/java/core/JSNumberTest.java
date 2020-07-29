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

public class JSNumberTest extends JsonTest {

    private JSNumber numberDouble;
    private JSNumber numberLong;

    @BeforeEach
    void setUp() {
        numberDouble = new JSNumber(new JsonTape("1.25"));
        numberLong = new JSNumber(new JsonTape("321"));
    }

    @Test
    @Override
    public void testDataType() {
        assertEquals(JSType.LONG, numberLong.getDataType());
        assertEquals(JSType.DOUBLE, numberDouble.getDataType());
    }

    @Test
    public void testParseException() {
        try {
            new JSNumber(new JsonTape("1.1."));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("multiple points\n" +
                    "Line: 1\n" +
                    "Reached: 1.1._\n" +
                    "Expected: <number>", e.getMessage());
        }
        try {
            new JSNumber(new JsonTape("1.1.1"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("multiple points\n" +
                    "Line: 1\n" +
                    "Reached: 1.1.1_\n" +
                    "Expected: <number>", e.getMessage());
        }
        try {
            new JSNumber(new JsonTape("123-1"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Invalid number format: \"123-1\"\n" +
                    "Line: 1\n" +
                    "Reached: 123-1_\n" +
                    "Expected: <number>", e.getMessage());
        }
        try {
            new JSNumber(new JsonTape("99999999999999999999999999999"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Invalid number format: \"99999999999999999999999999999\"\n" +
                    "Line: 1\n" +
                    "Reached: 99999999999999999999999999999_\n" +
                    "Expected: <number>", e.getMessage());
        }
        try {
            new JSNumber(new JsonTape("-9999999999999999999999999999"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Invalid number format: \"-9999999999999999999999999999\"\n" +
                    "Line: 1\n" +
                    "Reached: -9999999999999999999999999999_\n" +
                    "Expected: <number>", e.getMessage());
        }
        try {
            new JSNumber(new JsonTape("--1"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Invalid number format: \"--1\"\n" +
                    "Line: 1\n" +
                    "Reached: --1_\n" +
                    "Expected: <number>", e.getMessage());
        }
        try {
            new JSNumber(new JsonTape("++1"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Invalid number format: \"++1\"\n" +
                    "Line: 1\n" +
                    "Reached: ++1_\n" +
                    "Expected: <number>", e.getMessage());
        }
        try {
            new JSNumber(new JsonTape("+-1"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Invalid number format: \"+-1\"\n" +
                    "Line: 1\n" +
                    "Reached: +-1_\n" +
                    "Expected: <number>", e.getMessage());
        }
        try {
            new JSNumber(new JsonTape("^1"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Invalid number format: \"\"\n" +
                    "Line: 1\n" +
                    "Reached: _\n" +
                    "Expected: <number>", e.getMessage());
        }
        try {
            new JSNumber(new JsonTape("e^1"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Invalid number format: \"e\"\n" +
                    "Line: 1\n" +
                    "Reached: e_\n" +
                    "Expected: <number>", e.getMessage());
        }
        try {
            new JSNumber(new JsonTape("1e-0.05"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Invalid number format: \"1e-0.05\"\n" +
                    "Line: 1\n" +
                    "Reached: 1e-0.05_\n" +
                    "Expected: <number>", e.getMessage());
        }
    }

    @Test
    @Override
    public void simpleCreateFromString() {
        try {
            numberLong.createFromString("12345");
            new JSNumber(new JsonTape("1e-005"));
            new JSNumber(new JsonTape("-4.3e10"));
        } catch (JsonParseException e) {
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
        } catch (JsonParseException e) {
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
        try {
            numberLong.getArray();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found for key  was not expected. Expected: ARRAY  ->  Received: LONG", e.getMessage());
        }
    }

    @Test
    @Override
    public void getBoolean() {
        try {
            numberLong.getBoolean();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found for key  was not expected. Expected: BOOLEAN  ->  Received: LONG", e.getMessage());
        }
    }

    @Test
    @Override
    public void getDouble() {
        try {
            numberLong.getDouble();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("This number is a long, not a double.", e.getMessage());
        }
        assertEquals(1.25, numberDouble.getDouble(), 0);
    }

    @Test
    @Override
    public void getLong() {
        try {
            numberDouble.getLong();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("This number is a double, not a long.", e.getMessage());
        }
        assertEquals(321L, numberLong.getLong(), 0);
    }

    @Test
    @Override
    public void getString() {
        try {
            numberLong.getString();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found for key  was not expected. Expected: STRING  ->  Received: LONG", e.getMessage());
        }
    }

    @Test
    @Override
    public void getJSONObject() {
        try {
            numberLong.getJSONObject();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found for key  was not expected. Expected: OBJECT  ->  Received: LONG", e.getMessage());
        }
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
        try {
            numberLong.getValueAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a LONG", e.getMessage());
        }
    }

    @Test
    @Override
    public void getDataTypeOf() {
        try {
            numberLong.getDataTypeOf("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a LONG", e.getMessage());
        }
    }

    @Test
    @Override
    public void getKeysOf() {
        try {
            numberLong.getKeysOf("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a LONG", e.getMessage());
        }
    }

    @Test
    @Override
    public void getValuesOf() {
        try {
            numberLong.getValuesOf("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a LONG", e.getMessage());
        }
    }

    @Test
    @Override
    public void getJSONObjectAt() {
        try {
            numberLong.getJSONObjectAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a LONG", e.getMessage());
        }
    }

    @Test
    @Override
    public void getArrayAt() {
        try {
            numberLong.getArrayAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a LONG", e.getMessage());
        }
    }

    @Test
    @Override
    public void getBooleanAt() {
        try {
            numberLong.getBooleanAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a LONG", e.getMessage());
        }
    }

    @Test
    @Override
    public void getDoubleAt() {
        try {
            numberLong.getDoubleAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a LONG", e.getMessage());
        }
    }

    @Test
    public void getDoubleAtMe() {
        assertEquals(1.25, numberDouble.getDoubleAt(""), 0);
    }

    @Test
    @Override
    public void getLongAt() {
        try {
            numberLong.getLongAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a LONG", e.getMessage());
        }
    }

    @Test
    public void getLongAtMe() {
        assertEquals(321L, numberLong.getLongAt(""), 0);
    }

    @Test
    @Override
    public void getStringAt() {
        try {
            numberLong.getStringAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a LONG", e.getMessage());
        }
    }

    @Test
    @Override
    public void getAnyAt() {
        try {
            numberLong.getAnyAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a LONG", e.getMessage());
        }
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
        assertEquals(numberLong, new JSNumber(new JsonTape("321")));
        assertEquals(numberDouble, new JSNumber(new JsonTape("1.25")));
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

    @Test
    @Override
    public void jsonIsConvertible() {
        IJsonAble builder = JsonBuilder.builder().addString("key", "value");
        assertEquals(builder.convertToJSON(), numberDouble.convertToJSON(builder));
        assertEquals(builder.convertToJSON(), numberLong.convertToJSON(builder));
    }
}