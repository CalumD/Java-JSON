package com.clumd.projects.javajson.exceptions.schema;

import com.clumd.projects.javajson.exceptions.SchemaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvalidSchemaExceptionTest {

    @Test
    public void basicPOJO() {
        SchemaException exception = new InvalidSchemaException("Some error text");
        assertEquals("Some error text", exception.getMessage());
    }

    @Test
    public void basicPOJOWithSubException() {
        SchemaException exception = new InvalidSchemaException("Some error text", new Throwable("because of this."));
        assertEquals("Some error text", exception.getMessage());
        assertEquals("because of this.", exception.getCause().getMessage());
    }
}