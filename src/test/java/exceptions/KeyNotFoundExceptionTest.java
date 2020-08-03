package exceptions;

import exceptions.json.KeyNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyNotFoundExceptionTest {
    @Test
    public void basicPOJO() {
        JsonException exception = new KeyNotFoundException("Some error text");
        assertEquals("Some error text", exception.getMessage());
    }
}