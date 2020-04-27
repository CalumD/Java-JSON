package exceptions;

public class JSONException extends RuntimeException {
    public JSONException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }

    public JSONException(String reasonForInvalidity, Throwable subException) {
        super(reasonForInvalidity, subException);
    }
}
