package com.clumd.projects.javajson.exceptions.json;

/**
 * This exception is used when the value at some location provided by some key does not exist.
 *
 * @see JsonKeyException
 * @see KeyInvalidException
 * @see KeyDifferentTypeException
 */
public class KeyNotFoundException extends JsonKeyException {

    public KeyNotFoundException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
