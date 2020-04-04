package Exceptions;

public class KeyDifferentTypeException extends JSONException {

    public KeyDifferentTypeException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
