package exceptions;

public class KeyInvalidException extends JSONKeyException {
    public KeyInvalidException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
