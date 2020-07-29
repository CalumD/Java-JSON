package exceptions;

public class JsonParseException extends JsonException {

    public JsonParseException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
