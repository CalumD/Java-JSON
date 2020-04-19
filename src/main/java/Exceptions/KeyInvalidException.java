package Exceptions;

public class KeyInvalidException extends JSONKeyException {
    public KeyInvalidException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }

    public KeyInvalidException(String reasonForInvalidity, Throwable subException) {
        super(reasonForInvalidity, subException);
    }
}
