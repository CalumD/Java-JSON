package exceptions.schema;

import exceptions.SchemaException;

/**
 * This exception is used validating a JSON object against a schema,
 * a violation of one of the Schema's constraints was found.
 *
 * @see exceptions.JsonException
 * @see InvalidSchemaException
 */
public class SchemaViolationException extends SchemaException {

    public SchemaViolationException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }

    public SchemaViolationException(String reasonForInvalidity, Throwable subException) {
        super(reasonForInvalidity, subException);
    }
}
