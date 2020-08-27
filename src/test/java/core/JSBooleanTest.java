package core;

import api.IJson;
import exceptions.json.JsonParseException;
import exceptions.json.KeyDifferentTypeException;
import exceptions.json.KeyNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JSBooleanTest extends JsonTest {

    private JSBoolean boolTrue;
    private JSBoolean boolFalse;

    @BeforeEach
    public void setup() {
        boolTrue = new JSBoolean(new JsonTape("true"));
        boolFalse = new JSBoolean(new JsonTape("false"));
    }


    @Test
    public void testTrueRandom() {
        try {
            new JSBoolean(new JsonTape("TrUe"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Unexpected symbol found while parsing.\n" +
                    "Line: 1\n" +
                    "Reached: _\n" +
                    "Expected: true / false", e.getMessage());
        }
    }

    @Test
    public void testFalseRandom() {
        try {
            new JSBoolean(new JsonTape("FaLsE"));
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Unexpected symbol found while parsing.\n" +
                    "Line: 1\n" +
                    "Reached: _\n" +
                    "Expected: true / false", e.getMessage());
        }
    }

    @Test
    @Override
    public void testDataType() {
        assertEquals(JSType.BOOLEAN, boolTrue.jsType);
        assertEquals(JSType.BOOLEAN, boolFalse.jsType);
    }

    @Test
    @Override
    public void simpleCreateFromString() {
        try {
            boolTrue.createFromString("true");
        } catch (JsonParseException e) {
            fail("Create from string should not throw an exception for valid input.", e);
        }
    }

    @Test
    @Override
    public void simpleCreateFromMultilineString() {
        List<String> multiline = new ArrayList<>();
        multiline.add("[");
        multiline.add("true");
        multiline.add("]");
        try {
            boolTrue.createFromMultilineString(multiline);
        } catch (JsonParseException e) {
            fail("Create from string should not throw an exception for valid input.", e);
        }
    }

    @Test
    @Override
    public void containsTheEmptyDoubleQuoteIsTrue() {
        assertTrue(boolTrue.contains(""));
        assertTrue(boolFalse.contains(""));
    }

    @Test
    @Override
    public void containsNullIsFalse() {
        assertFalse(boolTrue.contains(null));
        assertFalse(boolFalse.contains(null));
    }

    @Test
    @Override
    public void containsAllTheseKeys() {
        List<String> keys = new ArrayList<>();
        keys.add("");
        assertTrue(boolTrue.containsAllKeys(keys));
    }

    @Test
    public void doesntContainAllKeysNull() {
        List<String> keys = new ArrayList<>();
        keys.add("");
        keys.add(null);
        assertFalse(boolTrue.containsAllKeys(keys));
    }

    @Test
    public void doesntContainAllKeysNoMatch() {
        List<String> keys = new ArrayList<>();
        keys.add("");
        keys.add("someKey");
        assertFalse(boolTrue.containsAllKeys(keys));
    }

    @Test
    @Override
    public void getKeys() {
        assertEquals(new ArrayList<>(), boolTrue.getKeys());
    }

    @Override
    public void getValue() {
        assertTrue(new JSBoolean(new JsonTape("TRUE")).getValue());
    }

    @Test
    public void testTrueAlternative() {
        assertTrue(new JSBoolean(new JsonTape("True")).getValue());
    }

    @Test
    public void testFALSEAlternative() {
        assertFalse(new JSBoolean(new JsonTape("FALSE")).getValue());
    }

    @Test
    public void testFalseAlternative() {
        assertFalse(new JSBoolean(new JsonTape("False")).getValue());
    }

    @Test
    @Override
    public void getValues() {
        List<IJson> toEqual = new ArrayList<>();
        toEqual.add(boolTrue);
        assertEquals(toEqual, boolTrue.getValues());
    }

    @Test
    @Override
    public void getArray() {
        try {
            boolTrue.getArray();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found was not expected. Expected: ARRAY  ->  Received: BOOLEAN", e.getMessage());
        }
    }

    @Test
    @Override
    public void getBoolean() {
        assertTrue(boolTrue.getBoolean());
        assertFalse(boolFalse.getBoolean());
    }

    @Test
    @Override
    public void getDouble() {
        try {
            boolTrue.getDouble();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found was not expected. Expected: DOUBLE  ->  Received: BOOLEAN", e.getMessage());
        }
    }

    @Test
    @Override
    public void getLong() {
        try {
            boolTrue.getLong();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found was not expected. Expected: LONG  ->  Received: BOOLEAN", e.getMessage());
        }
    }

    @Test
    @Override
    public void getString() {
        try {
            boolTrue.getString();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found was not expected. Expected: STRING  ->  Received: BOOLEAN", e.getMessage());
        }
    }

    @Test
    @Override
    public void getJSONObject() {
        try {
            boolTrue.getJSONObject();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyDifferentTypeException e) {
            assertEquals("The Type of Object found was not expected. Expected: OBJECT  ->  Received: BOOLEAN", e.getMessage());
        }
    }

    @Test
    @Override
    public void getAny() {
        assertEquals(boolTrue, boolTrue.getAny());
    }


    @Test
    @Override
    public void getValueAt() {
        try {
            boolTrue.getValueAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a BOOLEAN", e.getMessage());
        }
    }

    @Test
    @Override
    public void getDataTypeOf() {
        try {
            boolTrue.getDataTypeOf("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a BOOLEAN", e.getMessage());
        }
    }

    @Test
    @Override
    public void getKeysOf() {
        try {
            boolTrue.getKeysOf("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a BOOLEAN", e.getMessage());
        }
    }

    @Test
    @Override
    public void getValuesOf() {
        try {
            boolTrue.getValuesOf("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a BOOLEAN", e.getMessage());
        }
    }

    @Test
    @Override
    public void getJSONObjectAt() {
        try {
            boolTrue.getJSONObjectAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a BOOLEAN", e.getMessage());
        }
    }

    @Test
    @Override
    public void getArrayAt() {
        try {
            boolTrue.getAnyAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a BOOLEAN", e.getMessage());
        }
    }

    @Test
    public void getBooleanAtMe() {
        assertTrue(boolTrue.getBooleanAt(""));
    }

    @Test
    @Override
    public void getBooleanAt() {
        try {
            boolTrue.getBooleanAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a BOOLEAN", e.getMessage());
        }
    }

    @Test
    @Override
    public void getDoubleAt() {
        try {
            boolTrue.getDoubleAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a BOOLEAN", e.getMessage());
        }
    }

    @Test
    @Override
    public void getLongAt() {
        try {
            boolTrue.getLongAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a BOOLEAN", e.getMessage());
        }
    }

    @Test
    @Override
    public void getStringAt() {
        try {
            boolTrue.getStringAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a BOOLEAN", e.getMessage());
        }
    }

    @Test
    public void getAnyAtMe() {
        assertEquals(boolTrue, boolTrue.getAnyAt(""));
    }

    @Test
    @Override
    public void getAnyAt() {
        try {
            boolTrue.getAnyAt("someKey");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals("No child elements on a BOOLEAN", e.getMessage());
        }
    }

    @Test
    @Override
    public void testToString() {
        assertEquals("true", boolTrue.toString());
        assertEquals("false", boolFalse.toString());
    }

    @Test
    @Override
    public void toPrettyString() {
        assertEquals("true", boolTrue.toPrettyString());
        assertEquals("false", boolFalse.toPrettyString());
    }

    @Test
    @Override
    public void asString() {
        assertEquals("true", boolTrue.asString());
        assertEquals("false", boolFalse.asString());
    }

    @Test
    @Override
    public void asPrettyString() {
        assertEquals("true", boolTrue.asPrettyString());
        assertEquals("false", boolFalse.asPrettyString());
    }

    @Test
    @Override
    public void asPrettyStringWithDepth() {
        assertEquals("true", boolTrue.asPrettyString(5));
        assertEquals("false", boolFalse.asPrettyString(5));
    }

    @Test
    @Override
    public void asPrettyStringWithDepthAndIndent() {
        // Indents shouldn't make a difference on base objects.
        assertEquals("true", boolTrue.asPrettyString(5, 4));
        assertEquals("false", boolFalse.asPrettyString(5, 4));
    }


    @SuppressWarnings({"SimplifiableJUnitAssertion", "ConstantConditions"})
    @Test
    public void checkNullDoesNotEqual() {
        assertFalse(boolTrue.equals(null));
        assertFalse(boolFalse.equals(null));
    }

    @Test
    public void checkSameObjectDoesEqual() {
        assertEquals(boolTrue, boolTrue);
        assertEquals(boolFalse, boolFalse);
    }

    @Test
    public void checkDoesNotEqualAgainstOppositeJSBoolean() {
        assertNotEquals(boolTrue, boolFalse);
        assertNotEquals(boolFalse, boolTrue);
    }

    @Test
    public void checkDoesEqualAgainstSimilarJSBoolean() {
        assertEquals(boolTrue, new JSBoolean(new JsonTape("TRUE")));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsBetweenInconvertibleTypes"})
    @Test
    public void checkRegularBooleanDoesEqualWithTrue() {
        assertTrue(boolTrue.equals(true));
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsBetweenInconvertibleTypes"})
    @Test
    public void checkRegularBooleanDoesEqualWithFalse() {
        assertTrue(boolFalse.equals(false));
    }

    @Test
    @Override
    public void testEquals() {
        assertNotEquals(1, boolTrue);
        assertNotEquals(0, boolFalse);
        assertNotEquals(-1, boolFalse);
    }

    @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsBetweenInconvertibleTypes"})
    @Test
    public void testEqualsWithDifferentClass() {
        assertFalse(boolTrue.equals("true"));
        assertFalse(boolFalse.equals("false"));
    }

    @Test
    public void hashcodeEqualsForFalse() {
        assertEquals(Boolean.hashCode(false), boolFalse.hashCode());
    }

    @Test
    @Override
    public void getHashCode() {
        assertEquals(Boolean.hashCode(true), boolTrue.hashCode());
    }

    @Test
    @Override
    public void jsonIsConvertible() {
        assertEquals("true", boolTrue.convertToJSON().asString());
        assertEquals("false", boolFalse.convertToJSON().asString());
    }
}
