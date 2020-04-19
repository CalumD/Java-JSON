package Exceptions;

public class KeyDifferentTypeException extends JSONKeyException {

    public KeyDifferentTypeException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
