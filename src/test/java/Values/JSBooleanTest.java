package Values;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JSBooleanTest extends JSONTest {

    private JSBoolean boolTrue;
    private JSBoolean boolFalse;

    @BeforeEach
    public void setup() {
        boolTrue = new JSBoolean(new JSONTape("true"));
        boolFalse = new JSBoolean(new JSONTape("false"));
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

    @Test
    public void checkOtherClassTypeDoesNotEqual() {
        assertNotEquals(boolTrue, 1);
        assertNotEquals(boolFalse, 0);
        assertNotEquals(boolFalse, -1);
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
    public void hashcodeEqualsForTrue() {
        assertEquals(Boolean.hashCode(true), boolTrue.hashCode());
    }

    @Test
    public void hashcodeEqualsForFalse() {
        assertEquals(Boolean.hashCode(false), boolFalse.hashCode());
    }

    @Test
    public void asStringForTrue() {
        assertEquals("true", boolTrue.asString(1));
    }

    @Test
    public void asStringForFalse() {
        assertEquals("false", boolFalse.asString(1));
    }

    @Test
    public void asPrettyStringForFalse() {
        StringBuilder result = new StringBuilder();

        boolFalse.asPrettyString(null, null, result, 1);

        assertEquals("false", result.toString());
    }

    @Test
    public void asPrettyStringForTrue() {
        StringBuilder result = new StringBuilder();

        boolTrue.asPrettyString(null, null, result, 1);

        assertEquals("true", result.toString());
    }

}
