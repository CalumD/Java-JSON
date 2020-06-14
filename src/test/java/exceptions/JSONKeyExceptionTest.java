package exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JSONKeyExceptionTest {

    @Test
    public void basicPOJO() {
        JSONException exception = new JSONKeyException("Some error text");
        assertEquals("Some error text", exception.getMessage());
    }

    @Test
    public void basicPOJO2() {
        Throwable throwable = new Throwable("nested");
        JSONException exception = new JSONKeyException("Some error text", throwable);
        assertEquals("Some error text", exception.getMessage());
        assertEquals(throwable, exception.getCause());
    }
}