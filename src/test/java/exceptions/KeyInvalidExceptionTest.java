package exceptions;

import exceptions.json.KeyInvalidException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyInvalidExceptionTest {

    @Test
    public void basicPOJO() {
        JsonException exception = new KeyInvalidException("Some error text");
        assertEquals("Some error text", exception.getMessage());
    }
}