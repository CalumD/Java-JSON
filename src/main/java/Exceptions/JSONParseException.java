package Exceptions;

import Exceptions.JSONException;

public class JSONParseException extends JSONException {

    public JSONParseException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
