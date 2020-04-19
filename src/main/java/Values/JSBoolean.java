package Values;

import Core.IJson;
import Exceptions.JSONParseException;
import Exceptions.KeyNotFoundException;

public class JSBoolean extends JSON {

    private boolean myValue;

    JSBoolean(JSONParsingTape parsingTape) throws JSONParseException {
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
    public boolean contains(String keys) {
        return keys.equals("");
    }

    @Override
    public String asString(int depth) {
        return String.valueOf(myValue);
    }

    @Override
    protected void asPrettyString(StringBuilder indent, String tabSize, StringBuilder result, int depth) {
        result.append(asString(depth));
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

    @Override
    public JSON getJSONObjectAt(String key) {
        if (contains(key)) {
            return this;
        }
        throw new KeyNotFoundException("Key: " + key + ", not found in JSON.");
    }

    @Override
    protected IJson getInternal(JSONKey keyChain) throws KeyNotFoundException {
        throw new UnsupportedOperationException("Still need to implement this");
    }
}
