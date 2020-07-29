package exceptions;

public class JsonException extends RuntimeException {
    public JsonException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }

    public JsonException(String reasonForInvalidity, Throwable subException) {
        super(reasonForInvalidity, subException);
    }
}
