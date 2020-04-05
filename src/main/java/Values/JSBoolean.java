package Values;

import Exceptions.JSONParseException;

public class JSBoolean extends JSON {

    private boolean myValue;

    JSBoolean(JSONParsingTape parsingTape) throws JSONParseException {
        super(parsingTape);
        if (parsingTape.checkNext("true") || parsingTape.checkNext("True") || parsingTape.checkNext("TRUE")) {
            myValue = true;
            parsingTape.moveTapeHead(4);
        } else if (parsingTape.checkNext("false") || parsingTape.checkNext("False") || parsingTape.checkNext("FALSE")) {
            myValue = false;
            parsingTape.moveTapeHead(5);
        } else {
            parsingTape.moveTapeHead(1);
            parsingTape.createParseError("true/false");
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
        result.append(asString(1));
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
