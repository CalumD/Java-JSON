package exceptions;

public class KeyDifferentTypeException extends JsonKeyException {

    public KeyDifferentTypeException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
