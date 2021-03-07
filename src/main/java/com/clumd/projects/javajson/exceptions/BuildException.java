package com.clumd.projects.javajson.exceptions;

/**
 * A class representing all possible exceptions that could occur when creating a JSON object from Java values.
 *
 * @see JsonException
 * @see com.clumd.projects.javajson.exceptions.json.JsonKeyException
 * @see com.clumd.projects.javajson.exceptions.json.JsonParseException
 */
public class BuildException extends JsonException {

    public BuildException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }

    public BuildException(String reasonForInvalidity, Throwable subException) {
        super(reasonForInvalidity, subException);
    }
}
