package exceptions;

public class JsonKeyException extends JsonException {
    public JsonKeyException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
