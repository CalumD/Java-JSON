package Exceptions.JSON;

public class KeyDifferentTypeException extends Exception {

    public KeyDifferentTypeException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
