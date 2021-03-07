package com.clumd.projects.javajson.exceptions;

import com.clumd.projects.javajson.exceptions.json.KeyDifferentTypeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyDifferentTypeExceptionTest {

    @Test
    public void basicPOJO() {
        JsonException exception = new KeyDifferentTypeException("Some error text");
        assertEquals("Some error text", exception.getMessage());
    }
}