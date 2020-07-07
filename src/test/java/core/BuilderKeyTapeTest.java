package core;

import exceptions.KeyInvalidException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuilderKeyTapeTest extends KeyTapeTest {

    @Override
    @Test
    public void emptyArrayAccess() {
        assertEquals("[append", new JSONKey("[]", true).getNextKey());
    }

    @Override
    @Test
    public void regularArrayAccessOkay() {
        assertEquals("[0", new JSONKey("[0]", true).getNextKey());
    }

    @Override
    @Test
    public void maxIntArrayAccessOkay() {
        assertEquals("[" + Integer.MAX_VALUE, new JSONKey("[" + Integer.MAX_VALUE + "]", true).getNextKey());
    }

    @Override
    @Test
    public void decimalArrayAccessNotOkay() {
        assertThrows(KeyInvalidException.class, () -> new JSONKey("[1.5]", true).getNextKey());
    }

    @Override
    @Test
    public void negativeArrayAccessNotOkay() {
        assertThrows(KeyInvalidException.class, () -> new JSONKey("[-5]", true).getNextKey());
    }

    @Override
    @Test
    public void missingEndOfArrayAccessNotOkay() {
        assertThrows(KeyInvalidException.class, () -> new JSONKey("[5", true).getNextKey());
    }

    @Override
    @Test
    public void arrayDotObjectIsOkay() {
        JSONKey BuilderKeyTape = new JSONKey("[0].key", true);
        assertEquals("[0", BuilderKeyTape.getNextKey());
        assertEquals("{key", BuilderKeyTape.getNextKey());
    }

    @Override
    @Test
    public void arrayAdvancedObjectAccessorIsOkay() {
        JSONKey BuilderKeyTape = new JSONKey("[0]['key']", true);
        assertEquals("[0", BuilderKeyTape.getNextKey());
        assertEquals("<key", BuilderKeyTape.getNextKey());
    }

    @Override
    @Test
    public void missingDotOrAdvancedAccessorSyntaxNotOkay() {
        assertThrows(KeyInvalidException.class, () -> new JSONKey("[0]key", true));
    }

    @Test
    public void ensureCorrectKeyChainOnAppendingArrays() {
        JSONKey builderKeyTape = new JSONKey("what.does[3][].this['mean']", true);

        assertEquals("{what", builderKeyTape.getNextKey());
        assertEquals("{does", builderKeyTape.getNextKey());
        assertEquals("[3", builderKeyTape.getNextKey());
        assertEquals("[append", builderKeyTape.getNextKey());
        assertEquals("{this", builderKeyTape.getNextKey());
        assertEquals("<mean", builderKeyTape.getNextKey());
        assertEquals("", builderKeyTape.getNextKey());
    }
}