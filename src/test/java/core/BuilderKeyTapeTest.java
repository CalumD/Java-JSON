package core;

import exceptions.KeyInvalidException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuilderKeyTapeTest extends KeyTapeTest {

    @Override
    @Test
    public void emptyArrayAccess() {
        assertEquals("[append", new BuilderKeyTape("[]").parseNextElement());
    }

    @Override
    @Test
    public void regularArrayAccessOkay() {
        assertEquals("[0", new BuilderKeyTape("[0]").parseNextElement());
    }

    @Override
    @Test
    public void maxIntArrayAccessOkay() {
        assertEquals("[" + Integer.MAX_VALUE, new BuilderKeyTape("[" + Integer.MAX_VALUE + "]").parseNextElement());
    }

    @Override
    @Test
    public void decimalArrayAccessNotOkay() {
        assertThrows(KeyInvalidException.class, () -> new BuilderKeyTape("[1.5]").parseNextElement());
    }

    @Override
    @Test
    public void negativeArrayAccessNotOkay() {
        assertThrows(KeyInvalidException.class, () -> new BuilderKeyTape("[-5]").parseNextElement());
    }

    @Override
    @Test
    public void missingEndOfArrayAccessNotOkay() {
        assertThrows(KeyInvalidException.class, () -> new BuilderKeyTape("[5").parseNextElement());
    }

    @Override
    @Test
    public void arrayDotObjectIsOkay() {
        BuilderKeyTape BuilderKeyTape = new BuilderKeyTape("[0].key");
        assertEquals("[0", BuilderKeyTape.parseNextElement());
        assertEquals("{key", BuilderKeyTape.parseNextElement());
    }

    @Override
    @Test
    public void arrayAdvancedObjectAccessorIsOkay() {
        BuilderKeyTape BuilderKeyTape = new BuilderKeyTape("[0]['key']");
        assertEquals("[0", BuilderKeyTape.parseNextElement());
        assertEquals("<key", BuilderKeyTape.parseNextElement());
    }

    @Override
    @Test
    public void missingDotOrAdvancedAccessorSyntaxNotOkay() {
        BuilderKeyTape BuilderKeyTape = new BuilderKeyTape("[0]key");
        assertThrows(KeyInvalidException.class, BuilderKeyTape::parseNextElement);
    }
}