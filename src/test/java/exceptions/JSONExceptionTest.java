package exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JSONExceptionTest {

    @Test
    public void basicPOJO() {
        JSONException exception = new JSONException("Some error text");
        assertEquals("Some error text", exception.getMessage());
    }

    @Test
    public void basicPOJO2() {
        Throwable innerException = new Throwable("Some other exception");
        JSONException exception = new JSONException("Some error text", innerException);
        assertEquals("Some error text", exception.getMessage());
        assertEquals(innerException, exception.getCause());
    }

}