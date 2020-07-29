package exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonKeyExceptionTest {

    @Test
    public void basicPOJO() {
        JsonException exception = new JsonKeyException("Some error text");
        assertEquals("Some error text", exception.getMessage());
    }
}