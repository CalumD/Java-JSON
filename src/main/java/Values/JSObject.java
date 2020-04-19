package Values;

import Core.IJson;
import Exceptions.JSONParseException;
import Exceptions.KeyNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JSObject extends JSON {

    private final HashMap<String, IJson> json;

    JSObject(JSONTape parsingTape) throws JSONParseException {
        super(parsingTape);
        jsType = JSType.OBJECT;
        char checkingChar;

        // Skip over the object opener
        parsingTape.consumeOne();
        parsingTape.consumeWhiteSpace();
        checkingChar = parsingTape.checkCurrentChar();

        // Initial Object parsing Checks
        json = new HashMap<>();
        if (checkingChar == '}') {
            parsingTape.consumeOne();
            return;
        }
        if (checkingChar != '"') {
            parsingTape.createParseError("\"", "Missing Key at start of Object.");
        }


        // Parse object
        boolean moreChildren = true;
        while (moreChildren) {
            // Get the Key
            String key = null;
            try {
                key = ((JSString)parsingTape.parseNextElement()).getValue();
            } catch (ClassCastException e) {
                parsingTape.createParseError("\"", "Invalid type for object key.");
            }
            assert key != null;
            validateObjectKey(key, parsingTape);

            // Validate Colon
            parsingTape.consumeWhiteSpace();
            if (parsingTape.consumeOne() != ':') {
                parsingTape.createParseError(":", "Invalid Key:Value separator. Must use a colon(:).");
            }

            // Parse value
            JSON nextChild = parsingTape.parseNextElement();
            json.put(key, nextChild);

            // Check delimiters.
            parsingTape.consumeWhiteSpace();
            checkingChar = parsingTape.consumeOne();
            switch (checkingChar) {
                case '}':
                    moreChildren = false;
                    break;
                case ',':
                    // Validate if we see a comma, there are more children to come
                    parsingTape.consumeWhiteSpace();
                    if (parsingTape.checkCurrentChar() == '}') {
                        parsingTape.createParseError(JSONTape.VALID_JSON,
                                "Comma suggests more object elements, but object terminates.");
                    }
                    break;
                default:
                    parsingTape.createParseErrorFromOffset(
                            -1,
                            ", / }",
                            "Invalid object child delimiter."
                    );
            }
        }
    }

    private void validateObjectKey(String key, JSONTape parsingTape) {
        if (key.equals("")) {
            parsingTape.createParseError("<Valid Key>", "Illegal Object Key (Empty).");
        }
        if (json.containsKey(key)) {
            parsingTape.createParseError("<Unique Key>", "Illegal Object key (Duplicate): " + key);
        }

        Character reservedCharacter = null;
        if (key.contains(".")) {
            reservedCharacter = '.';
        }
        if (key.contains("[")) {
            reservedCharacter = '[';
        }
        if (key.contains("]")) {
            reservedCharacter = ']';
        }
        if (key.contains("\\")) {
            reservedCharacter = '\\';
        }
        if (reservedCharacter != null) {
            parsingTape.createParseError("<Valid Key>", "Illegal Object Key (Reserved/Illegal character found): " + reservedCharacter);
        }
    }

    @Override
    public IJson getValue() {
        return this;
    }

    @Override
    public String asString(int depth) {
        StringBuilder ret = new StringBuilder("{");

        //empty object
        if (json.size() == 0) {
            return ret.append('}').toString();
        }
        //just print the boiler plate object stuff
        else if (depth == 0) {
            ret.append('<');

            String[] keys = new String[json.size()];
            json.keySet().toArray(keys);
            for (int i = 0; i < keys.length - 1; i++) {
                ret.append(keys[i]).append(',');
            }
            ret.append(keys[keys.length - 1]).append(">}");
            return ret.toString();
        }

        //print the full internals based on the next depth though
        for (String key : json.keySet()) {
            ret.append("\"").append(key).append("\"").append(":")
                .append(json.get(key).asString(depth - 1)).append(",");
        }

        if (ret.charAt(ret.length() - 1) == ',') {
            ret.deleteCharAt(ret.length() - 1);
        }

        return ret.append("}").toString();
    }

    @Override
    public boolean contains(String keys) {
        if (keys.equals("")) {
            return false;
        }

        try {
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

        JSObject o = (JSObject) other;

        if (this.getKeys().size() != o.getKeys().size()) {
            return false;
        }
        for (String key : this.getKeys()) {
            if (!o.json.containsKey(key)) {
                return false;
            }
            if (!(json.get(key).equals(o.json.get(key)))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return json.hashCode();
    }

    @Override
    public IJson getJSONObjectAt(String keys) throws KeyNotFoundException {
        String temporaryErrorText = "SOMETHING";
        if (keys.equals("")) {
            return this;
        }
        //invalid key checks
        if (keys.endsWith(".")) {
            throw new KeyNotFoundException(
                    "The Key provided could not be found in the JSON.  The Key ends with an additional <.>");
        }

        //generate basic stuff
        String arrayAccess = null;
        String genericError = "The Key provided could not be found in the JSON. Reached: " + temporaryErrorText;

        String key = keys.split("\\.")[0];
        boolean nestedArrays = false;
        String nestedKey = "";

        try {
            //check to see if we have array accesses in this object, and that the next char is not another array reference
            if (key.contains("]") && key.indexOf("]") + 1 != key.length()
                && key.charAt(key.indexOf("]") + 1) != '[') {
                throw new KeyNotFoundException(genericError);
            }

            //count and match our brackets
            int bracketCount = 0;
            for (Character c : key.toCharArray()) {
                if (c == '[') {
                    bracketCount++;
                } else if (c == ']') {
                    bracketCount--;
                }
            }
            if (bracketCount != 0) {
                throw new KeyNotFoundException(
                    "The input has mismatched square brackets, wont attempt to parse.");
            }

            //split on brackets
            String[] arrayKeys = key.split("\\[");

            //if we have at least a single key
            if (arrayKeys.length > 1) {
                //mark the key internals as the first reference
                key = arrayKeys[0];

                //if there are more than one reference, then traverse all sub references
                if (arrayKeys.length > 2) {
                    nestedArrays = true;
                    boolean seenFirstOpen = false;
                    int indexcounter = 0;

                    //for all characters inside the array reference, get the nested key until the dot reference
                    for (char c : keys.toCharArray()) {
                        if (c == '[') {
                            if (!seenFirstOpen) {
                                seenFirstOpen = true;
                            } else {
                                nestedKey = keys.substring(indexcounter).split("\\.")[0];
                                break;
                            }
                        }
                        indexcounter++;
                    }
                }

                //if we didn't see the end, fail here again
                if (!arrayKeys[1].contains("]")) {
                    throw new KeyNotFoundException(genericError);
                }

                //split on the close
                arrayKeys = arrayKeys[1].split("]");

                //check for invalid dot reference ordering
                if (arrayKeys.length > 1 && !arrayKeys[1].startsWith(".")) {
                    throw new KeyNotFoundException(genericError);
                }

                //mark the array reference
                arrayAccess = arrayKeys[0];
            }

        } catch (Exception e) {
            throw new KeyNotFoundException(genericError);
        }

        //get the base object at that key
        IJson ret = json.get(key);
        //if nothing was found, throw
        if (ret == null) {
            throw new KeyNotFoundException(genericError);
        }

        //if there was some kind of array reference going on, then
        if (arrayAccess != null) {
            //if the stuff after the next dot reference matches our substrings, then get the current key and passdown on the array access
            if (keys.substring(keys.indexOf(".") + 1).equals(keys)) {
                ret = json.get(key).getJSONObjectAt(arrayAccess);
                //if nothing was found, then throw
                if (ret == null) {
                    throw new KeyNotFoundException(
                        "The Key provided could not be found in the JSON. Reached: " +temporaryErrorText +
                            keys +
                            "    ::Array Index is out of bounds."
                    );
                }
                //continue down the nested key path
                if (nestedArrays) {
                    return ret.getJSONObjectAt(nestedKey);
                }
                //return this found object
                else {
                    return ret;
                }
            }
            //no match after dot reference
            else {
                //straight pass through the nested keys, based on current key and array access
                if (nestedArrays) {
                    ret = json.get(key).getJSONObjectAt(arrayAccess).getJSONObjectAt(nestedKey);
                } else {
                    ret = json.get(key).getJSONObjectAt(arrayAccess);
                }
                //if no object was found then throw
                if (ret == null) {
                    throw new KeyNotFoundException(
                        "The Key provided could not be found in the JSON. Reached: " +temporaryErrorText +
                                    "." +
                            keys +
                            "    ::Array Index is out of bounds."
                    );
                }
                //else return the contents from the remainder of the key not yet decoded.
                else {
                    return ret.getJSONObjectAt(keys.substring(keys.indexOf(".") + 1));
                }
            }
        }
        //no further array access
        else {
            try {
                //if the next thing is an array, check it has content based on remaining un-decoded key subs
                if (ret.getDataType() == JSType.ARRAY
                        && (ret = ret.getJSONObjectAt(keys.substring(key.length() + 1))) == null) {
                    throw new KeyNotFoundException(
                            "The Key provided could not be found in the JSON. Reached: " + temporaryErrorText +
                                    keys.split("\\.")[1] +
                                    "_     ::" +
                                    "It looks like you are trying to access an array with invalid id"
                    );
                }
                return ret.getJSONObjectAt(keys.substring(key.length() + 1));
            } catch (StringIndexOutOfBoundsException e) {
                return ret;
            }
        }
    }

    @Override
    protected IJson getInternal(JSONKey keyChain) throws KeyNotFoundException {
        throw new UnsupportedOperationException("Still need to implement this");
    }

    @Override
    void asPrettyString(StringBuilder indent, String tabSize, StringBuilder result, int depth) {
        if (json.isEmpty()) {
            result.append("{}");
            return;
        }

        indent.append(tabSize);
        result.append('{').append('\n').append(indent);
        if (depth == 0) {
            result.append("<").append(json.size()).append(">");
        } else {
            json.forEach((key, value) -> {
                result.append('"').append(key).append("\": ");
                ((JSON)value).asPrettyString(indent, tabSize, result, depth - 1);
                result.append(",\n").append(indent);
            });
            result.delete(result.length() - 2 - indent.length(), result.length() -1);
        }

        indent.delete(0, tabSize.length());
        result.append("\n").append(indent).append('}');
    }

    @Override
    public List<String> getKeys() {
        return new ArrayList<>(json.keySet());
    }

    @Override
    public List<IJson> getValues() {
        return new ArrayList<>(json.values());
    }
}