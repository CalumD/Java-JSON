package com.clumd.projects.javajson.exceptions.json;

/**
 * This exception is used when a JSON key was malformed in some way.
 *
 * @see JsonKeyException
 * @see KeyNotFoundException
 * @see KeyDifferentTypeException
 */
public class KeyInvalidException extends JsonKeyException {
    public KeyInvalidException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
