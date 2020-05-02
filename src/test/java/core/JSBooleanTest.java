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

public class JSBooleanTest extends JSONTest {

    private JSBoolean boolTrue;
    private JSBoolean boolFalse;

    @BeforeEach
    public void setup() {
        boolTrue = new JSBoolean(new JSONTape("true"));
        boolFalse = new JSBoolean(new JSONTape("false"));
    }


    @Test
    public void testTrueRandom() {
        assertThrows(JSONParseException.class, () -> new JSBoolean(new JSONTape("TrUe")));
    }

    @Test
    public void testFalseRandom() {
        assertThrows(JSONParseException.class, () -> new JSBoolean(new JSONTape("FaLsE")));
    }

    @Test
    @Override
    public void testDataType() {
        System.out.println("ree");
    }

    @Test
    @Override
    public void simpleCreateFromString() {
        try {
            boolTrue.createFromString("true");
        } catch (JSONParseException e) {
            fail("Create from string should not throw an exception for valid input.");
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
        } catch (JSONParseException e) {
            fail("Create from string should not throw an exception for valid input.");
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
        assertTrue(new JSBoolean(new JSONTape("TRUE")).getValue());
    }

    @Test
    public void testTrueAlternative() {
        assertTrue(new JSBoolean(new JSONTape("True")).getValue());
    }

    @Test
    public void testFALSEAlternative() {
        assertFalse(new JSBoolean(new JSONTape("FALSE")).getValue());
    }

    @Test
    public void testFalseAlternative() {
        assertFalse(new JSBoolean(new JSONTape("False")).getValue());
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
        assertThrows(KeyDifferentTypeException.class, () -> boolTrue.getArray());
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
        assertThrows(KeyDifferentTypeException.class, () -> boolTrue.getDouble());
    }

    @Test
    @Override
    public void getLong() {
        assertThrows(KeyDifferentTypeException.class, () -> boolTrue.getLong());
    }

    @Test
    @Override
    public void getString() {
        assertThrows(KeyDifferentTypeException.class, () -> boolTrue.getString());
    }

    @Test
    @Override
    public void getJSONObject() {
        assertThrows(KeyDifferentTypeException.class, () -> boolTrue.getJSONObject());
    }

    @Test
    @Override
    public void getAny() {
        assertEquals(boolTrue, boolTrue.getAny());
    }


    @Test
    @Override
    public void getValueAt() {
        assertThrows(KeyNotFoundException.class, () -> boolTrue.getValueAt("someKey"));
    }

    @Test
    @Override
    public void getDataTypeOf() {
        assertThrows(KeyNotFoundException.class, () -> boolTrue.getDataTypeOf("someKey"));
    }

    @Test
    @Override
    public void getKeysOf() {
        assertThrows(KeyNotFoundException.class, () -> boolTrue.getKeysOf("someKey"));
    }

    @Test
    @Override
    public void getValuesOf() {
        assertThrows(KeyNotFoundException.class, () -> boolTrue.getValuesOf("someKey"));
    }

    @Test
    @Override
    public void getJSONObjectAt() {
        assertThrows(KeyNotFoundException.class, () -> boolTrue.getJSONObjectAt("someKey"));
    }

    @Test
    @Override
    public void getArrayAt() {
        assertThrows(KeyNotFoundException.class, () -> boolTrue.getArrayAt("someKey"));
    }

    @Test
    public void getBooleanAtMe() {
        assertTrue(boolTrue.getBooleanAt(""));
    }

    @Test
    @Override
    public void getBooleanAt() {
        assertThrows(KeyNotFoundException.class, () -> boolTrue.getBooleanAt("someKey"));
    }

    @Test
    @Override
    public void getDoubleAt() {
        assertThrows(KeyNotFoundException.class, () -> boolTrue.getDoubleAt("someKey"));
    }

    @Test
    @Override
    public void getLongAt() {
        assertThrows(KeyNotFoundException.class, () -> boolTrue.getLongAt("someKey"));
    }

    @Test
    @Override
    public void getStringAt() {
        assertThrows(KeyNotFoundException.class, () -> boolTrue.getStringAt("someKey"));
    }

    @Test
    public void getAnyAtMe() {
        assertEquals(boolTrue, boolTrue.getAnyAt(""));
    }

    @Test
    @Override
    public void getAnyAt() {
        assertThrows(KeyNotFoundException.class, () -> boolTrue.getAnyAt("someKey"));
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
        assertEquals(boolTrue, new JSBoolean(new JSONTape("TRUE")));
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
}
