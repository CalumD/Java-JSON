package exceptions.schema;

import exceptions.SchemaException;

/**
 * This exception is used when a provided schema, IS valid JSON, but is NOT a valid Schema.
 * E.g. Missing out a required property on a schema constraint.
 *
 * @see exceptions.JsonException
 * @see SchemaViolationException
 */
public class InvalidSchemaException extends SchemaException {

    public InvalidSchemaException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }

    public InvalidSchemaException(String reasonForInvalidity, Throwable subException) {
        super(reasonForInvalidity, subException);
    }
}
