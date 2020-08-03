package exceptions.schema;

import exceptions.SchemaException;

public class InvalidSchemaException extends SchemaException {

    public InvalidSchemaException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }

    public InvalidSchemaException(String reasonForInvalidity, Throwable subException) {
        super(reasonForInvalidity, subException);
    }
}
