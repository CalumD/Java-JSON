package core;

import exceptions.KeyInvalidException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
        try {
            new JSONKey("[1.5]", true).getNextKey();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyInvalidException e) {
            assertEquals("Failed to parse array accessor in key. Element was not a valid integer.\n" +
                    "Line: 1\n" +
                    "Reached: [_\n" +
                    "Expected: <positive integer>", e.getMessage());
        }
    }

    @Override
    @Test
    public void negativeArrayAccessNotOkay() {
        try {
            new JSONKey("[-5]", true).getNextKey();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyInvalidException e) {
            assertEquals("Array accessor in key was negative integer. Must be positive.\n" +
                    "Line: 1\n" +
                    "Reached: [_\n" +
                    "Expected: <positive integer>", e.getMessage());
        }
    }

    @Override
    @Test
    public void missingEndOfArrayAccessNotOkay() {
        try {
            new JSONKey("[5", true).getNextKey();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyInvalidException e) {
            assertEquals("Failed to parse array accessor in key. Reached end of key before delimiter ']' was found.\n" +
                    "Line: 1\n" +
                    "Reached: [_\n" +
                    "Expected: <positive integer>", e.getMessage());
        }
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
        try {
            new JSONKey("[0]key", true);
            fail("The previous method call should have thrown an exception.");
        } catch (KeyInvalidException e) {
            assertEquals("Invalid continuation from array key\n" +
                    "Line: 1\n" +
                    "Reached: [0]_\n" +
                    "Expected: [ / .", e.getMessage());
        }
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