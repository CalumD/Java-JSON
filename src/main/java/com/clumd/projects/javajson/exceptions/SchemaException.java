package com.clumd.projects.javajson.exceptions;

/**
 * A class representing all possible exceptions that could occur when validating a JSON object against a Schema.
 *
 * @see JsonException
 * @see com.clumd.projects.javajson.exceptions.schema.SchemaViolationException
 * @see com.clumd.projects.javajson.exceptions.schema.InvalidSchemaException
 */
public class SchemaException extends JsonException {

    public SchemaException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }

    public SchemaException(String reasonForInvalidity, Throwable subException) {
        super(reasonForInvalidity, subException);
    }
}
