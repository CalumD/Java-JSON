package exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyInvalidExceptionTest {

    @Test
    public void basicPOJO() {
        JSONException exception = new KeyInvalidException("Some error text");
        assertEquals("Some error text", exception.getMessage());
    }
}