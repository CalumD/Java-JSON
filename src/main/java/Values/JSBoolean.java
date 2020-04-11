package Values;

import Exceptions.JSONParseException;

public class JSBoolean extends JSON {

    private boolean myValue;

    JSBoolean() {
    }

    JSBoolean(JSONParsingTape parsingTape) throws JSONParseException {
        super(parsingTape);
        jsType = JSType.BOOLEAN;

        if (parsingTape.checkNextFragment("true", true)
                || parsingTape.checkNextFragment("True", true)
                || parsingTape.checkNextFragment("TRUE", true)
        ) {
            myValue = true;
        }

        else if (parsingTape.checkNextFragment("false", true)
                || parsingTape.checkNextFragment("False", true)
                || parsingTape.checkNextFragment("FALSE", true)
        ) {
            myValue = false;
        }

        else {
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

//    @Override
//    public JSON getJSONByKey(String key) {
//        if (contains(key)) {
//            return this;
//        }
//        throw new KeyNotFoundException("Key: " + key + ", not found in JSON.");
//    }
//
//    @Override
//    public List<String> getKeys() {
//        return new ArrayList<>();
//    }
}
