package exceptions;

public class JSONKeyException extends JSONException {
    public JSONKeyException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }

    public JSONKeyException(String reasonForInvalidity, Throwable subException) {
        super(reasonForInvalidity, subException);
    }
}
