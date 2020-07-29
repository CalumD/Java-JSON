package exceptions;

public class SchemaException extends JsonException {

    public SchemaException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }

    public SchemaException(String reasonForInvalidity, Throwable subException) {
        super(reasonForInvalidity, subException);
    }
}
