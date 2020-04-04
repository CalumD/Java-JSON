package Exceptions;

public class JSONParseException extends JSONException {

    public JSONParseException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
