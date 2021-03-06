package exceptions.json;

import exceptions.JsonException;

/**
 * A class representing all possible exceptions that could occur with any JSON key.
 *
 * @see JsonException
 * @see KeyNotFoundException
 * @see KeyInvalidException
 * @see KeyDifferentTypeException
 */
public class JsonKeyException extends JsonException {
    public JsonKeyException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
