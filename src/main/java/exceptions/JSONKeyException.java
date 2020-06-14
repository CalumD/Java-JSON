package exceptions;

public class JSONKeyException extends JSONException {
    public JSONKeyException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
