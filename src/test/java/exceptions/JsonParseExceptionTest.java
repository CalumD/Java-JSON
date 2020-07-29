package exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonParseExceptionTest {

    @Test
    public void basicPOJO() {
        JsonException exception = new JsonParseException("Some error text");
        assertEquals("Some error text", exception.getMessage());
    }
}