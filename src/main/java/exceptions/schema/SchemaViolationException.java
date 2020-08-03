package exceptions.schema;

import exceptions.SchemaException;

public class SchemaViolationException extends SchemaException {

    public SchemaViolationException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }

    public SchemaViolationException(String reasonForInvalidity, Throwable subException) {
        super(reasonForInvalidity, subException);
    }
}
