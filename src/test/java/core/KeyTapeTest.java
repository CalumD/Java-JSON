package core;

import exceptions.json.KeyInvalidException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class KeyTapeTest {

    @Test
    public void disallowNullKey() {
        try {
            new KeyTape(null);
            fail("The previous method call should have thrown an exception.");
        } catch (KeyInvalidException e) {
            assertEquals("You cannot create something from nothing. Input was null.", e.getMessage());
        }
    }

    @Test
    public void disallowEmptyKey() {
        try {
            new KeyTape("");
            fail("The previous method call should have thrown an exception.");
        } catch (KeyInvalidException e) {
            assertEquals("You cannot create something from nothing. Input was empty.", e.getMessage());
        }
    }

    @Test
    public void canStartWithWhiteSpace() {
        assertEquals("{key", new KeyTape(" \n\r\tkey").parseNextElement());
    }

    @Test
    public void firstKeyCanHaveSpaces() {
        assertEquals("{key \n\r\t", new KeyTape("key \n\r\t").parseNextElement());
    }

    @Test
    public void anyAdditionalKeysMustNotHaveSpaces() {
        try {
            new KeyTape("key.key 2 .key3").parseAllElements();
        } catch (KeyInvalidException e) {
            assertEquals("Spaces are invalid in dot separated keys. Use obj[\"key\"] notation if key contains spaces.\n" +
                    "Line: 1\n" +
                    "Reached: key.key 2 _\n" +
                    "Expected: <valid key segment>", e.getMessage());
        }
        try {
            new KeyTape("key.'key 2'.key3").parseAllElements();
        } catch (KeyInvalidException e) {
            assertEquals("Complex keys require square bracket delimiters in addition. (e.g. [`key`])\n" +
                    "Line: 1\n" +
                    "Reached: key._\n" +
                    "Expected: <valid object key>", e.getMessage());
        }
        try {
            new KeyTape("key.`key 2`.key3").parseAllElements();
        } catch (KeyInvalidException e) {
            assertEquals("Complex keys require square bracket delimiters in addition. (e.g. [`key`])\n" +
                    "Line: 1\n" +
                    "Reached: key._\n" +
                    "Expected: <valid object key>", e.getMessage());
        }
        try {
            new KeyTape("key.\"key 2\".key3").parseAllElements();
        } catch (KeyInvalidException e) {
            assertEquals("Complex keys require square bracket delimiters in addition. (e.g. [`key`])\n" +
                    "Line: 1\n" +
                    "Reached: key._\n" +
                    "Expected: <valid object key>", e.getMessage());
        }
    }

    @Test
    public void mustNotStartWithDotAccessor() {
        try {
            new KeyTape(".key").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyInvalidException e) {
            assertEquals("Bad use of '.' separator in key\n" +
                    "Line: 1\n" +
                    "Reached: _\n" +
                    "Expected: [ / <Object Key>", e.getMessage());
        }
    }

    @Test
    public void mustNotEndWithDotAccessor() {
        try {
            new KeyTape("key.").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyInvalidException e) {
            assertEquals("Trailing dot separator in key suggests more elements, but end of string was found.\n" +
                    "Line: 1\n" +
                    "Reached: key_\n" +
                    "Expected: <object ref> / <end of key>", e.getMessage());
        }
    }

    @Test
    public void shouldAllowWhiteSpaceBeforeAdvancedObjectAccessorRegularQuote() {
        assertEquals("<key key", new KeyTape("[ \n\r\t\"key key\"]").parseNextElement());
    }

    @Test
    public void shouldAllowWhiteSpaceAfterAdvancedObjectAccessorRegularQuote() {
        assertEquals("<key key", new KeyTape("[\"key key\" \n\r\t]").parseNextElement());
    }

    @Test
    public void shouldAllowWhiteSpaceBeforeAdvancedObjectAccessorSingleQuote() {
        assertEquals("<key key", new KeyTape("[ \n\r\t'key key']").parseNextElement());
    }

    @Test
    public void shouldAllowWhiteSpaceAfterAdvancedObjectAccessorSingleQuote() {
        assertEquals("<key key", new KeyTape("['key key' \n\r\t]").parseNextElement());
    }

    @Test
    public void shouldAllowWhiteSpaceBeforeAdvancedObjectAccessorBacktick() {
        assertEquals("<key key", new KeyTape("[ \n\r\t`key key`]").parseNextElement());
    }

    @Test
    public void shouldAllowWhiteSpaceAfterAdvancedObjectAccessorBacktick() {
        assertEquals("<key key", new KeyTape("[`key key` \n\r\t]").parseNextElement());
    }

    @Test
    public void missingTerminatingDelimiterAdvancedObjectAccessorWrapper() {
        try {
            new KeyTape("[`key key`").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyInvalidException e) {
            assertEquals("Reached end of input before parsing was complete. Are you missing a terminating delimiter?", e.getMessage());
        }
    }

    @Test
    public void missingTerminatingDelimiterAdvancedObjectAccessorName() {
        try {
            new KeyTape("[`key key]").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyInvalidException e) {
            assertEquals("Reached end of key before resolving all parts. Are you missing a delimiter?\n" +
                    "Line: 1\n" +
                    "Reached: [`key key]_\n" +
                    "Expected: <key reference>", e.getMessage());
        }
    }

    @Test
    public void superDuperAdvancedKeyAccess() {
        KeyTape keyTape = new KeyTape("['key1'].['key2']");
        assertEquals("<key1", keyTape.parseNextElement());
        assertEquals("<key2", keyTape.parseNextElement());
    }

    @Test
    public void superDuperAdvancedKeyAccessDoesntRequireDotSeparator() {
        KeyTape keyTape = new KeyTape("['key1']['key2']");
        assertEquals("<key1", keyTape.parseNextElement());
        assertEquals("<key2", keyTape.parseNextElement());
    }

    @Test
    public void regularChainedObjectOnlyKey() {
        KeyTape keyTape = new KeyTape("key1.key2");
        assertEquals("{key1", keyTape.parseNextElement());
        assertEquals("{key2", keyTape.parseNextElement());
    }

    @Test
    public void tryToParseBeyondTheEndOfKey() {
        KeyTape keyTape = new KeyTape("key1");
        assertEquals("{key1", keyTape.parseNextElement());
        try {
            keyTape.parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyInvalidException e) {
            assertEquals("Reached end of input before parsing was complete. Are you missing a terminating delimiter?", e.getMessage());
        }
    }

    @Test
    public void tryParseNextElementThenParseAllElements() {
        KeyTape keyTape = new KeyTape("key1.key2.key3.key4");
        assertEquals("{key1", keyTape.parseNextElement());
        assertEquals("{key2", keyTape.parseNextElement());

        List<String> otherKeys = new ArrayList<>();
        otherKeys.add("{key3");
        otherKeys.add("{key4");
        otherKeys.add("");
        assertEquals(otherKeys, keyTape.parseAllElements());
    }

    @Test
    public void objectCanBeOfTypeArrayAndNeedIndexing() {
        KeyTape keyTape = new KeyTape("object[0]");
        assertEquals("{object", keyTape.parseNextElement());
        assertEquals("[0", keyTape.parseNextElement());
    }

    @Test
    public void regularArrayAccessOkay() {
        assertEquals("[0", new KeyTape("[0]").parseNextElement());
    }

    @Test
    public void maxIntArrayAccessOkay() {
        assertEquals("[" + Integer.MAX_VALUE, new KeyTape("[" + Integer.MAX_VALUE + "]").parseNextElement());
    }

    @Test
    public void decimalArrayAccessNotOkay() {
        try {
            new KeyTape("[1.5]").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyInvalidException e) {
            assertEquals("Failed to parse array accessor in key. Element was not a valid integer.\n" +
                    "Line: 1\n" +
                    "Reached: [_\n" +
                    "Expected: <positive integer>", e.getMessage());
        }
    }

    @Test
    public void negativeArrayAccessNotOkay() {
        try {
            new KeyTape("[-5]").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyInvalidException e) {
            assertEquals("Array accessor in key was negative integer. Must be positive.\n" +
                    "Line: 1\n" +
                    "Reached: [_\n" +
                    "Expected: <positive integer>", e.getMessage());
        }
    }

    @Test
    public void missingEndOfArrayAccessNotOkay() {
        try {
            new KeyTape("[5").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyInvalidException e) {
            assertEquals("Failed to parse array accessor in key. Reached end of key before delimiter ']' was found.\n" +
                    "Line: 1\n" +
                    "Reached: [_\n" +
                    "Expected: <positive integer>", e.getMessage());
        }
    }

    @Test
    public void arrayDotObjectIsOkay() {
        KeyTape keyTape = new KeyTape("[0].key");
        assertEquals("[0", keyTape.parseNextElement());
        assertEquals("{key", keyTape.parseNextElement());
    }

    @Test
    public void arrayAdvancedObjectAccessorIsOkay() {
        KeyTape keyTape = new KeyTape("[0]['key']");
        assertEquals("[0", keyTape.parseNextElement());
        assertEquals("<key", keyTape.parseNextElement());
    }

    @Test
    public void missingDotOrAdvancedAccessorSyntaxNotOkay() {
        KeyTape keyTape = new KeyTape("[0]key");
        try {
            keyTape.parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyInvalidException e) {
            assertEquals("Invalid continuation from array key\n" +
                    "Line: 1\n" +
                    "Reached: [0]_\n" +
                    "Expected: [ / .", e.getMessage());
        }
    }

    @Test
    public void parseAllElements() {
        List<String> keys = new ArrayList<>(10);
        keys.add("{key1");
        keys.add("{other");
        keys.add("<advanced");
        keys.add("[3");
        keys.add("<more with spaces");
        keys.add("{obj");
        keys.add("{arr");
        keys.add("[4");
        // check for self reference on last part of key
        keys.add("");

        List<String> actual = new KeyTape("key1.other['advanced'][3][`more with spaces`].obj.arr[4]").parseAllElements();
        assertEquals(keys, actual);
    }

    @Test
    public void parseAllElementsCanStillThrowoutExceptions() {
        try {
            new KeyTape("someinvalidkey['").parseAllElements();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyInvalidException e) {
            assertEquals("Reached end of input before parsing was complete. Are you missing a terminating delimiter?", e.getMessage());
        }
    }

    @Test
    public void parseAllElementsWithTrailingDot() {
        try {
            new KeyTape("somekey.otherkey.").parseAllElements();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyInvalidException e) {
            assertEquals("Trailing dot separator in key suggests more elements, but end of string was found.\n" +
                    "Line: 1\n" +
                    "Reached: somekey.otherkey_\n" +
                    "Expected: <object ref> / <end of key>", e.getMessage());
        }
    }

    @Test
    public void advancedObjectAccessorMustNotBeEmpty() {
        try {
            new KeyTape("['']").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyInvalidException e) {
            assertEquals("You cannot address a JSON object with an empty key.\n" +
                    "Line: 1\n" +
                    "Reached: ['']_\n" +
                    "Expected: <Valid JSON Object Key>", e.getMessage());
        }
    }

    @Test
    public void emptyArrayAccess() {
        try {
            new KeyTape("[]").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (KeyInvalidException e) {
            assertEquals("Failed to parse array accessor in key. Element was not a valid integer.\n" +
                    "Line: 1\n" +
                    "Reached: [_\n" +
                    "Expected: <positive integer>", e.getMessage());
        }
    }
}
