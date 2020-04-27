package exceptions;

public class JSONParseException extends JSONException {

    public JSONParseException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
