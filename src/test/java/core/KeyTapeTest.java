package core;

import exceptions.KeyInvalidException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KeyTapeTest {

    @Test
    public void disallowNullKey() {
        assertThrows(KeyInvalidException.class, () -> new KeyTape(null));
    }

    @Test
    public void disallowEmptyKey() {
        assertThrows(KeyInvalidException.class, () -> new KeyTape(""));
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
        assertThrows(KeyInvalidException.class, () -> new KeyTape("key.key 2.key3").parseAllElements());
        assertThrows(KeyInvalidException.class, () -> new KeyTape("key.'key 2'.key3").parseAllElements());
        assertThrows(KeyInvalidException.class, () -> new KeyTape("key.`key 2`.key3").parseAllElements());
        assertThrows(KeyInvalidException.class, () -> new KeyTape("key.\"key 2\".key3").parseAllElements());
    }

    @Test
    public void mustNotStartWithDotAccessor() {
        assertThrows(KeyInvalidException.class, () -> new KeyTape(".key").parseNextElement());
    }

    @Test
    public void mustNotEndWithDotAccessor() {
        assertThrows(KeyInvalidException.class, () -> new KeyTape("key.").parseNextElement());
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
        assertThrows(KeyInvalidException.class, () -> new KeyTape("[`key key`").parseNextElement());
    }

    @Test
    public void missingTerminatingDelimiterAdvancedObjectAccessorName() {
        assertThrows(KeyInvalidException.class, () -> new KeyTape("[`key key]").parseNextElement());
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
        assertThrows(KeyInvalidException.class, keyTape::parseNextElement);
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
        assertThrows(KeyInvalidException.class, () -> new KeyTape("[1.5]").parseNextElement());
    }

    @Test
    public void negativeArrayAccessNotOkay() {
        assertThrows(KeyInvalidException.class, () -> new KeyTape("[-5]").parseNextElement());
    }

    @Test
    public void missingEndOfArrayAccessNotOkay() {
        assertThrows(KeyInvalidException.class, () -> new KeyTape("[5").parseNextElement());
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
        assertThrows(KeyInvalidException.class, keyTape::parseNextElement);
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
        assertThrows(KeyInvalidException.class, () -> new KeyTape("someinvalidkey['").parseAllElements());
    }

    @Test
    public void parseAllElementsWithTrailingDot() {
        assertThrows(KeyInvalidException.class, () -> new KeyTape("somekey.otherkey.").parseAllElements());
    }

    @Test
    public void advancedObjectAccessorMustNotBeEmpty() {
        assertThrows(KeyInvalidException.class, () -> new KeyTape("['']").parseNextElement());
    }

    @Test
    public void emptyArrayAccess() {
        assertThrows(KeyInvalidException.class, () -> new KeyTape("[]").parseNextElement());
    }
}
