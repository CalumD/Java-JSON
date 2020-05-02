package core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    }

    @Test
    @Override
    public void simpleCreateFromString() {

    }

    @Test
    @Override
    public void simpleCreateFromMultilineString() {

    }

    @Test
    @Override
    public void containsTheEmptyDoubleQuoteIsTrue() {

    }

    @Test
    @Override
    public void containsNullIsFalse() {

    }

    @Test
    @Override
    public void containsAllTheseKeys() {

    }

    @Test
    @Override
    public void getKeys() {

    }

    @Test
    @Override
    public void getValue() {

    }

    @Test
    @Override
    public void getValues() {

    }

    @Test
    @Override
    public void getArray() {

    }

    @Test
    @Override
    public void getBoolean() {

    }

    @Test
    @Override
    public void getDouble() {

    }

    @Test
    @Override
    public void getLong() {

    }

    @Test
    @Override
    public void getString() {

    }

    @Test
    @Override
    public void getJSONObject() {

    }

    @Test
    @Override
    public void getAny() {

    }

    @Test
    @Override
    public void getValueAt() {

    }

    @Test
    @Override
    public void getDataTypeOf() {

    }

    @Test
    @Override
    public void getKeysOf() {

    }

    @Test
    @Override
    public void getValuesOf() {

    }

    @Test
    @Override
    public void getJSONObjectAt() {

    }

    @Test
    @Override
    public void getArrayAt() {

    }

    @Test
    @Override
    public void getBooleanAt() {

    }

    @Test
    @Override
    public void getDoubleAt() {

    }

    @Test
    @Override
    public void getLongAt() {

    }

    @Test
    @Override
    public void getStringAt() {

    }

    @Test
    @Override
    public void getAnyAt() {

    }

    @Test
    @Override
    public void testToString() {

    }

    @Test
    @Override
    public void toPrettyString() {

    }

    @Test
    @Override
    public void asString() {

    }

    @Test
    @Override
    public void asPrettyString() {

    }

    @Test
    @Override
    public void asPrettyStringWithDepth() {

    }

    @Test
    @Override
    public void asPrettyStringWithDepthAndIndent() {

    }

    @Test
    @Override
    public void testEquals() {

    }

    @Test
    @Override
    public void getHashCode() {

    }
}