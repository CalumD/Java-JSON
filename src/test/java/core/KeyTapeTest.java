package core;

import exceptions.KeyInvalidException;
import org.junit.jupiter.api.Test;

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
    public void mustNotEndWithWhiteSpaceAsWeCantTellEndOfKeyOtherwise() {
        assertThrows(KeyInvalidException.class, () -> new KeyTape("key \n\r\t").parseNextElement());
    }

    @Test
    public void mustNotStartWithDotAccessor() {
        assertThrows(KeyInvalidException.class, () -> new KeyTape(".key").parseNextElement());
    }

    @Test
    public void mustNotEndWithDotAccessor() {
        assertThrows(KeyInvalidException.class, () -> new KeyTape("key.").parseNextElement());
    }
}
