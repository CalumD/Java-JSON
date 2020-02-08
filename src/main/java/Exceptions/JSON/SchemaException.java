package Exceptions.JSON;

public class SchemaException extends ParseException {

    public SchemaException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
