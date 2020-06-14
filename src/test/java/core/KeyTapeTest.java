package core;

import exceptions.KeyInvalidException;
import org.junit.jupiter.api.Test;

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
}
