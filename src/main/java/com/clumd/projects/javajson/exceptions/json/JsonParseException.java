package com.clumd.projects.javajson.exceptions.json;

import com.clumd.projects.javajson.exceptions.JsonException;

/**
 * A class representing all possible exceptions that could occur when parsing a JSON object.
 *
 * @see JsonException
 */
public class JsonParseException extends JsonException {

    public JsonParseException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
