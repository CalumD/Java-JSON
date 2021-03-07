package com.clumd.projects.javajson.exceptions.json;

/**
 * This exception is used when there was a value found at a given key,
 * but the datatype of the value found was not suitable for the calling method.
 *
 * @see JsonKeyException
 * @see KeyNotFoundException
 * @see KeyInvalidException
 */
public class KeyDifferentTypeException extends JsonKeyException {

    public KeyDifferentTypeException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
