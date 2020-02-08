package Values;

import Exceptions.JSON.ParseException;

import java.util.ArrayList;
import java.util.List;

public class JSNumber extends JSON {

    private Long myLongValue;
    private Double myDoubleValue;

    public JSNumber(String jsonFragment) throws ParseException {
        super("", jsonFragment, true);
    }

    JSNumber(String keyAtElement, String jsonFragment, boolean willSanitise) throws ParseException {
        super(keyAtElement, jsonFragment, willSanitise);
    }

    @Override
    void init(String keyAtElement) {

    }

    @Override
    void parse(String jsonFragment, boolean sanitize) throws ParseException {
        int endOfNumber = 0;
        //check if we need to sanitize the fragment
        if (sanitize) {
            jsonFragment = sanitiseFragment(jsonFragment);
        }

        //for all characters in the remaining fragment, find out how long a number is
        for (char c : jsonFragment.toCharArray()) {
            if (c == ',' || c == '}' || c == ']' || c == '"' || c == '\'') {
                break;
            }
            endOfNumber++;
        }
        //ensure that there is at least a single character
        if (endOfNumber == 0) {
            throw new ParseException("There is a value missing in the JSON string.");
        }

        try {
            //check to see if that number was a decimal
            if (jsonFragment.substring(0, endOfNumber).contains(".")) {
                this.myDoubleValue = Double.parseDouble(jsonFragment.substring(0, endOfNumber));
                myType = JSType.DOUBLE;
            } else {
                this.myLongValue = Long.parseLong(jsonFragment.substring(0, endOfNumber));
                myType = JSType.LONG;
            }
            //get the size of the number to add to fragment
            fragmentSize = endOfNumber;
        } catch (NumberFormatException e) {
            throw new ParseException(e.getMessage());
        }
    }

    @Override
    public Object getValue() {
        if (myLongValue == null) {
            return myDoubleValue;
        }

        return myLongValue;
    }

    @Override
    public String asString(int depth) {
        return myLongValue == null ? String.valueOf(myDoubleValue) : String.valueOf(myLongValue);
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

        if (myLongValue != null) {
            if (other instanceof Long) {
                return myLongValue.equals(other);
            } else if (other instanceof JSNumber) {
                return myLongValue.equals(other);
            }
        }
        if (myDoubleValue != null) {
            if (other instanceof Double) {
                return myDoubleValue.equals(other);
            } else if (other instanceof JSNumber) {
                return myDoubleValue.equals(other);
            }
        }
        return false;
    }

    @Override
    public JSON getByKey(String keys) {
        if (contains(keys)) {
            return this;
        }

        return null;
    }

    public List<String> getKeys() {
        return new ArrayList<>();
    }
}
