package com.clumd.projects.javajson.core;

import com.clumd.projects.javajson.exceptions.json.KeyInvalidException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class BasicJsonBuilderKeyTapeTest extends KeyTapeTest {

    @Override
    @Test
    public void emptyArrayAccess() {
        assertEquals("[append", new JsonKey("[]", true).getNextKey());
    }

    @Override
    @Test
    public void regularArrayAccessOkay() {
        assertEquals("[0", new JsonKey("[0]", true).getNextKey());
    }

    @Override
    @Test
    public void maxIntArrayAccessOkay() {
        assertEquals("[" + Integer.MAX_VALUE, new JsonKey("[" + Integer.MAX_VALUE + "]", true).getNextKey());
    }

    @Override
    @Test
    public void decimalArrayAccessNotOkay() {
        try {
            new JsonKey("[1.5]", true).getNextKey();
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
            new JsonKey("[-5]", true).getNextKey();
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
            new JsonKey("[5", true).getNextKey();
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
        JsonKey BuilderKeyTape = new JsonKey("[0].key", true);
        assertEquals("[0", BuilderKeyTape.getNextKey());
        assertEquals("{key", BuilderKeyTape.getNextKey());
    }

    @Override
    @Test
    public void arrayAdvancedObjectAccessorIsOkay() {
        JsonKey BuilderKeyTape = new JsonKey("[0]['key']", true);
        assertEquals("[0", BuilderKeyTape.getNextKey());
        assertEquals("<key", BuilderKeyTape.getNextKey());
    }

    @Override
    @Test
    public void missingDotOrAdvancedAccessorSyntaxNotOkay() {
        try {
            new JsonKey("[0]key", true);
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
        JsonKey builderKeyTape = new JsonKey("what.does[3][].this['mean']", true);

        assertEquals("{what", builderKeyTape.getNextKey());
        assertEquals("{does", builderKeyTape.getNextKey());
        assertEquals("[3", builderKeyTape.getNextKey());
        assertEquals("[append", builderKeyTape.getNextKey());
        assertEquals("{this", builderKeyTape.getNextKey());
        assertEquals("<mean", builderKeyTape.getNextKey());
        assertEquals("", builderKeyTape.getNextKey());
    }
}