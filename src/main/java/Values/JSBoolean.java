package Values;

import Exceptions.JSONParseException;

public class JSBoolean extends JSON {

    private boolean myValue;

    JSBoolean(JSONTape parsingTape) throws JSONParseException {
        super(parsingTape);
        jsType = JSType.BOOLEAN;

        if (parsingTape.checkNextFragment("true")
                || parsingTape.checkNextFragment("True")
                || parsingTape.checkNextFragment("TRUE")
        ) {
            myValue = true;
        } else if (parsingTape.checkNextFragment("false")
                || parsingTape.checkNextFragment("False")
                || parsingTape.checkNextFragment("FALSE")
        ) {
            myValue = false;
        } else {
            parsingTape.createParseError("true / false");
        }
    }

    @Override
    public Boolean getValue() {
        return myValue;
    }

    @Override
    public String asString(int depth) {
        return String.valueOf(myValue);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }

        if (other instanceof Boolean) {
            return myValue == (Boolean) other;
        }

        if (getClass() != other.getClass()) {
            return false;
        } else {
            return ((JSBoolean) other).myValue == myValue;
        }
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(myValue);
    }
}
