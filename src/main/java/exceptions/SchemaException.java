package exceptions;

public class SchemaException extends JsonParseException {

    public SchemaException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
