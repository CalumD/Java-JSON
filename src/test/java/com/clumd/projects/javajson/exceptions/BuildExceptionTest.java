package com.clumd.projects.javajson.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuildExceptionTest {

    @Test
    public void basicPOJO() {
        JsonException exception = new BuildException("Some error text");
        assertEquals("Some error text", exception.getMessage());
    }

    @Test
    public void basicPOJOWithSubException() {
        JsonException exception = new BuildException("Some error text", new Throwable("because of this."));
        assertEquals("Some error text", exception.getMessage());
        assertEquals("because of this.", exception.getCause().getMessage());
    }
}
