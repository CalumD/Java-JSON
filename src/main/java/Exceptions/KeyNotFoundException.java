package Exceptions;

import Exceptions.JSONException;

public class KeyNotFoundException extends JSONException {

    public KeyNotFoundException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
