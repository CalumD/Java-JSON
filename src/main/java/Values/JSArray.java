package Values;

import Core.IJson;
import Exceptions.JSONParseException;
import Exceptions.KeyNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class JSArray extends JSON {

    private final List<IJson> myValue;

    JSArray(JSONParsingTape parsingTape) throws JSONParseException {
        super(parsingTape);
        jsType = JSType.ARRAY;
        char checkingChar;

        // Skip over the array opener
        parsingTape.consumeOne();
        parsingTape.consumeWhiteSpace();
        checkingChar = parsingTape.checkCurrentChar();

        // Initial Array parsing Checks
        if (checkingChar == ',') {
            parsingTape.createParseError(
                    JSONParsingTape.VALID_JSON,
                    "Missing Valid JSON at start of array."
            );
        }
        myValue = new ArrayList<>();
        if (checkingChar == ']') {
            parsingTape.consumeOne();
            return;
        }


        // Parse array
        boolean moreChildren = true;
        while (moreChildren) {
            myValue.add(parsingTape.parseNextElement());
            parsingTape.consumeWhiteSpace();
            checkingChar = parsingTape.consumeOne();
            switch (checkingChar) {
                case ']':
                    moreChildren = false;
                    break;
                case ',':
                    // Validate if we see a comma, there are more children to come
                    parsingTape.consumeWhiteSpace();
                    if (parsingTape.checkCurrentChar() == ']') {
                        parsingTape.createParseError(JSONParsingTape.VALID_JSON,
                                "Comma suggests more array elements, but array terminates.");
                    }
                    break;
                default:
                    parsingTape.createParseError(", / ]",
                            "Invalid array child delimiter.");
            }
        }
    }

    @Override
    public List<IJson> getValue() {
        return myValue;
    }

    @Override
    public String asString(int depth) {
        //if this is an empty array, then be sensible
        if (myValue.size() == 0) {
            return "[]";
        }

        StringBuilder ret = new StringBuilder("[");

        //if the depth is already too low, display the number of elements we contain
        if (depth == 0) {
            return ret.append("<").append(myValue.size()).append(">]").toString();
        }
        //pass down the next value of depth to all children and get their strings
        else {
            for (IJson value : myValue) {
                ret.append(value.asString(depth - 1)).append(",");
            }
            if (ret.charAt(ret.length() - 1) == ',') {
                ret.deleteCharAt(ret.length() - 1);
            }
            return ret.append("]").toString();
        }
    }

    @Override
    public boolean contains(String keys) {

        //if the key is for this array, then show that this array exists
        if (keys.equals("")) {
            return true;
        }

        try {
            //else get from children
            getJSONObjectAt(keys);
            return true;
        } catch (KeyNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || (getClass() != other.getClass())) {
            return false;
        }
        if (other == this) {
            return true;
        }

        JSArray o = (JSArray) other;

        if (this.myValue.size() != o.myValue.size()) {
            return false;
        }
        for (int i = 0; i < this.myValue.size(); i++) {
            if (!(this.myValue.get(i).equals(o.myValue.get(i)))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return myValue.hashCode();
    }

    @Override
    public IJson getJSONObjectAt(String key) throws KeyNotFoundException {
        //if the key is simply to show that this index exists then show it exists
        if (key.equals("")) {
            return this;
        }

        //if the next key starts as a sub array index
        if (key.startsWith("[")) {
            //recursive descent through nested arrays
            String[] nestedIndexString = key.split("\\[");

            //for each nest, get the contents of the array index
            for (int i = 0; i < nestedIndexString.length; i++) {
                nestedIndexString[i] = nestedIndexString[i].replace("]", "");
            }

            //find the object at that array index
            IJson ret = this;
            for (String i : nestedIndexString) {
                ret = ret.getJSONObjectAt(i);
            }
            return ret;
        }

        //else, we are looking for a value in THIS array
        try {
            return myValue.get(Integer.parseInt(key));
        } catch (Exception e) {
            throw new KeyNotFoundException(
                    "It looks like you are trying to access an array with invalid id");
        }
    }

    @Override
    protected IJson getInternal(JSONKey keyChain) throws KeyNotFoundException {
        throw new UnsupportedOperationException("Still need to implement this");
    }

    @Override
    void asPrettyString(StringBuilder indent, String tabSize, StringBuilder result, int depth) {
        if (myValue.isEmpty()) {
            result.append("[]");
            return;
        }

        indent.append(tabSize);
        result.append('[').append('\n').append(indent);
        if (depth == 0) {
            result.append("<").append(myValue.size()).append(">");
        } else {
            myValue.forEach(value -> {
                ((JSON)value).asPrettyString(indent, tabSize, result, depth - 1);
                result.append(",\n").append(indent);
            });
            result.delete(result.length() - 2 - indent.length(), result.length() -1);
        }

        indent.delete(0, tabSize.length());
        result.append("\n").append(indent).append(']');
    }

    public List<String> getKeys() {
        ArrayList<String> ret = new ArrayList<>();
        for (int i = 0; i < myValue.size(); i++) {
            ret.add(String.valueOf(i));
        }
        return ret;
    }

    @Override
    public List<IJson> getValues() {
        return getValue();
    }
}
