package com.clumd.projects.javajson.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonExceptionTest {

    @Test
    public void basicPOJO() {
        JsonException exception = new JsonException("Some error text");
        assertEquals("Some error text", exception.getMessage());
    }

    @Test
    public void basicPOJO2() {
        Throwable innerException = new Throwable("Some other exception");
        JsonException exception = new JsonException("Some error text", innerException);
        assertEquals("Some error text", exception.getMessage());
        assertEquals(innerException, exception.getCause());
    }

}