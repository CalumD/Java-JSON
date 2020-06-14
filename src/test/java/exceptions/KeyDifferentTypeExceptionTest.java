package exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyDifferentTypeExceptionTest {

    @Test
    public void basicPOJO() {
        JSONException exception = new KeyDifferentTypeException("Some error text");
        assertEquals("Some error text", exception.getMessage());
    }
}