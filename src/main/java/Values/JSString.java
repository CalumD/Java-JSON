package Values;

import Exceptions.JSONParseException;

import java.util.ArrayList;
import java.util.List;

public class JSString extends JSON {

    private String myValue;

    public JSString(String jsonFragment) throws JSONParseException {
        super("", jsonFragment, true);
    }

    JSString(String keyAtElement, String jsonFragment, boolean willSanitise) throws JSONParseException {
        super(keyAtElement, jsonFragment, willSanitise);
    }

    @Override
    void init(String keyAtElement) {
        myType = JSType.STRING;
    }

    @Override
    void parse(String jsonFragment, boolean sanitize) throws JSONParseException {
        if (sanitize) {
            jsonFragment = sanitiseFragment(jsonFragment);
        }

        boolean endFound = false;
        int endOfString = 1;
        char endChar = jsonFragment.charAt(0);
        char[] remainingFragment = jsonFragment.toCharArray();

        //assume the initial char is the end char, but start at 1
        for (; endOfString < jsonFragment.length(); endOfString++) {
            if (remainingFragment[endOfString] == endChar
                && remainingFragment[endOfString - 1] != '\\') {
                endFound = true;
                break;
            }
        }

        //if we didn't find the end char, there was a problem
        if (!endFound) {
            throw new JSONParseException("Failed to parse JSON internal string.");
        }

        if (endOfString == 1) {
            myValue = "";
        } else {
            this.myValue = jsonFragment.substring(1, endOfString);
        }
        fragmentSize = endOfString + 1;
    }

    @Override
    public String getValue() {
        return myValue;
    }

    @Override
    public String asString(int depth) {
        return "\"" + myValue + "\"";
    }

    @Override
    public boolean contains(String keys) {
        return keys.equals("");
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }

        if (other instanceof Character[] || other instanceof String) {
            return myValue.equals(other.toString());
        }
        if (other instanceof JSString) {
            return this.myValue.equals(((JSString) other).myValue);
        }

        return false;
    }

    @Override
    public JSON getJSONByKey(String keys) {
        if (contains(keys)) {
            return this;
        }
        return null;
    }

    public List<String> getKeys() {
        return new ArrayList<>();
    }
}
