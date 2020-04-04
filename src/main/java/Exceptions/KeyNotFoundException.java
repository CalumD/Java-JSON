package Exceptions;

public class KeyNotFoundException extends JSONException {

    public KeyNotFoundException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
