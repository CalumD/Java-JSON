package Exceptions.JSON;

public class KeyNotFoundException extends Exception {

    public KeyNotFoundException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
