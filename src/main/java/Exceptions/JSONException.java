package Exceptions;

public class JSONException extends RuntimeException {
    public JSONException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
