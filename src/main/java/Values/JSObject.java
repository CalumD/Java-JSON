package Values;

import Core.IJsonObject;
import Exceptions.JSON.KeyNotFoundException;
import Exceptions.JSON.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JSObject extends JSON {

    private static final String JSON_ROOT = "JSON_ROOT";
    private HashMap<String, JSON> json;
    private String keyAtElement;


    public JSObject(String jsonFragment) throws ParseException {
        super(JSON_ROOT, jsonFragment, true);
    }

    JSObject(String keyFragment, String jsonFragment, boolean willSanitise) throws ParseException {
        super(keyFragment == null ? JSON_ROOT : keyFragment, jsonFragment, willSanitise);
    }

    @Override
    void init(String keyAtElement) {
        this.keyAtElement = keyAtElement;
        myType = JSType.OBJECT;
        json = new HashMap<>();
    }


    @Override
    void parse(String jsonFragment, boolean sanitize) throws ParseException {

        //check if we need to sanitize input
        if (sanitize) {
            jsonFragment = sanitiseFragment(jsonFragment);
        }

        //check BASIC things about new object being valid
        if (jsonFragment.length() < 2) {
            throw new ParseException("A JSON JSObject is malformed (too short?) to be parsed.");
        }
        if (jsonFragment.charAt(0) != '{') {
            throw new ParseException("A JSON JSObject is missing the opening <{>");
        }
        if (jsonFragment.charAt(jsonFragment.length() - 1) != '}') {
            throw new ParseException("A JSON JSObject is missing the closing <}>");
        }
        if (jsonFragment.charAt(1) != '"' && jsonFragment.charAt(1) != '}') {
            throw new ParseException("A Key in the JSON is missing an enclosing quote");
        }

        //init parse variables
        currentFragmentIndex = 1;
        fragmentSize = 2;
        String nextKey;
        JSON nextElement;

        //parse the json
        while (jsonFragment.charAt(currentFragmentIndex) != '}') {
            //figure out the next KEY for object entry.;
            nextKey = parseNextKey(jsonFragment.substring(currentFragmentIndex));

            //if we have pre-empt reached the end of the string, fail
            if (nextKey.equals("")) {
                throw new ParseException("An empty key is not a valid key.");
            }

            //ensure that there is a ':' to separate key and value
            if (jsonFragment.charAt(currentFragmentIndex) != ':') {
                throw new ParseException(
                    "A JSON Element is missing the <:> to separate key and value");
            }

            //Figure out and parse the type of value
            currentFragmentIndex++;
            nextElement = parseNextElement(jsonFragment.charAt(currentFragmentIndex), keyAtElement,
                nextKey, currentFragmentIndex, jsonFragment);

            //add this object to our local object references
            json.put(nextKey, nextElement);
            currentFragmentIndex += nextElement.fragmentSize;
            fragmentSize = currentFragmentIndex + 1;

            //check we haven't reached an invalid state between elements in the object.
            if (jsonFragment.charAt(currentFragmentIndex) != ','
                && jsonFragment.charAt(currentFragmentIndex) != '}') {
                throw new ParseException("Invalid element separator. (Are you missing a <,> ?)");
            }
            //check if we have a new element by a comma
            if (jsonFragment.charAt(currentFragmentIndex) == ',') {
                currentFragmentIndex++;
            }
        }

        //ensure we see the end of the object.
        if (jsonFragment.charAt(currentFragmentIndex) != '}') {
            throw new ParseException("A JSON JSObject is missing the closing <}>");
        }
    }

    private String parseNextKey(String remainingValue) throws ParseException {
        JSString key;

        //validate the key into the object doesnt contain bad chars
        try {
            key = new JSString(null, remainingValue, false);
        } catch (ParseException e) {
            throw new ParseException("A Key in the JSON is missing an enclosing quote");
        }
        if (key.getValue().contains(".")) {
            throw new ParseException("You have used a reserved character in a JSON key: .");
        }
        if (key.getValue().contains("[")) {
            throw new ParseException("You have used a reserved character in a JSON key: [");
        }
        if (key.getValue().contains("]")) {
            throw new ParseException("You have used a reserved character in a JSON key: ]");
        }
        if (key.getValue().contains("{")) {
            throw new ParseException("You have used a reserved character in a JSON key: {");
        }
        if (key.getValue().contains("}")) {
            throw new ParseException("You have used a reserved character in a JSON key: }");
        }

        currentFragmentIndex += key.fragmentSize;
        return key.getValue();
    }

    @Override
    public IJsonObject getValue() {
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
            getByKey(keys);
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
            if (!o.contains(key)) {
                return false;
            }
            if (!(json.get(key).equals(o.json.get(key)))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public IJsonObject getByKey(String keys) throws KeyNotFoundException {
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
        String genericError = "The Key provided could not be found in the JSON. Reached: " + (
            keyAtElement.length() >= JSON_ROOT.length() + 1 ?
                keyAtElement.substring(JSON_ROOT.length() + 1) + "_" : "_");

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
        IJsonObject ret = json.get(key);
        //if nothing was found, throw
        if (ret == null) {
            throw new KeyNotFoundException(genericError);
        }

        //if there was some kind of array reference going on, then
        if (arrayAccess != null) {
            //if the stuff after the next dot reference matches our substrings, then get the current key and passdown on the array access
            if (keys.substring(keys.indexOf(".") + 1).equals(keys)) {
                ret = json.get(key).getByKey(arrayAccess);
                //if nothing was found, then throw
                if (ret == null) {
                    throw new KeyNotFoundException(
                        "The Key provided could not be found in the JSON. Reached: " +
                            (keyAtElement.equals(JSON_ROOT) ? "" :
                                (keyAtElement.substring(JSON_ROOT.length() + 1) +
                                    ".")) +
                            keys +
                            "    ::Array Index is out of bounds."
                    );
                }
                //continue down the nested key path
                if (nestedArrays) {
                    return ret.getByKey(nestedKey);
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
                    ret = json.get(key).getByKey(arrayAccess).getByKey(nestedKey);
                } else {
                    ret = json.get(key).getByKey(arrayAccess);
                }
                //if no object was found then throw
                if (ret == null) {
                    throw new KeyNotFoundException(
                        "The Key provided could not be found in the JSON. Reached: " +
                            (keyAtElement.equals(JSON_ROOT) ? "" :
                                (keyAtElement.substring(JSON_ROOT.length() + 1) +
                                    ".")) +
                            keys +
                            "    ::Array Index is out of bounds."
                    );
                }
                //else return the contents from the remainder of the key not yet decoded.
                else {
                    return ret.getByKey(keys.substring(keys.indexOf(".") + 1));
                }
            }
        }
        //no further array access
        else {
            try {
                //if the next thing is an array, check it has content based on remaining un-decoded key subs
                if (ret.getDataType() == JSType.ARRAY
                    && (ret = ret.getByKey(keys.substring(key.length() + 1))) == null) {
                    throw new KeyNotFoundException(
                        "The Key provided could not be found in the JSON. Reached: " +
                            (keyAtElement.equals(JSON_ROOT) ? "" :
                                (keyAtElement.substring(JSON_ROOT.length() + 1) +
                                    ".")) +
                            keys.split("\\.")[1] +
                            "_     ::" +
                            "It looks like you are trying to access an array with invalid id"
                    );
                }
                return ret.getByKey(keys.substring(key.length() + 1));
            } catch (StringIndexOutOfBoundsException e) {
                return ret;
            }
        }
    }

    public List<String> getKeys() {
        return new ArrayList<>(json.keySet());
    }
}