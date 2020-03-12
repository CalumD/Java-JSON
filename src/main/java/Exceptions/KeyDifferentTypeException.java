package Exceptions;

import Exceptions.JSONException;

public class KeyDifferentTypeException extends JSONException {

    public KeyDifferentTypeException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
