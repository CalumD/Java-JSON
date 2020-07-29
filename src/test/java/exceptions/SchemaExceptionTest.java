package exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SchemaExceptionTest {

    @Test
    public void basicPOJO() {
        JsonException exception = new SchemaException("Some error text");
        assertEquals("Some error text", exception.getMessage());
    }
}