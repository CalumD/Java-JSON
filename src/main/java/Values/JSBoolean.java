package Values;

import Exceptions.JSONParseException;

import java.util.ArrayList;
import java.util.List;

public class JSBoolean extends JSON {

    private boolean myValue;

    public JSBoolean(String jsonFragment) throws JSONParseException {
        super("", jsonFragment, true);
    }

    JSBoolean(String keyAtElement, String jsonFragment, boolean willSanitise)
        throws JSONParseException {
        super(keyAtElement, jsonFragment, willSanitise);
    }

    @Override
    void init(String keyAtElement) {
        myType = JSType.BOOLEAN;
    }


    @Override
    void parse(String jsonFragment, boolean sanitize) throws JSONParseException {

        if (sanitize) {
            jsonFragment = sanitiseFragment(jsonFragment);
        }

        if (jsonFragment.startsWith("true") || jsonFragment.startsWith("True") || jsonFragment
            .startsWith("TRUE")) {
            myValue = true;
            fragmentSize = 4;
        } else if (jsonFragment.startsWith("false") || jsonFragment.startsWith("False")
            || jsonFragment.startsWith("FALSE")) {
            myValue = false;
            fragmentSize = 5;
        } else {
            throw new JSONParseException("The type of a JSON element in the input was unknown");
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
    public JSON getByKey(String keys) {
        if (keys.equals("")) {
            return this;
        }
        return null;
    }

    public List<String> getKeys() {
        return new ArrayList<>();
    }
}
