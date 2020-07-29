package exceptions;

public class KeyInvalidException extends JsonKeyException {
    public KeyInvalidException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
