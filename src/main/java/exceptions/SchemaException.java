package exceptions;

public class SchemaException extends JSONParseException {

    public SchemaException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
