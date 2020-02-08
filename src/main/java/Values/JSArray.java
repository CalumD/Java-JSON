package Values;

import Core.IJsonObject;
import Exceptions.JSON.KeyNotFoundException;
import Exceptions.JSON.ParseException;

import java.util.ArrayList;
import java.util.List;

public class JSArray extends JSON {

    private List<IJsonObject> myValue;
    private String keyAtElement;

    public JSArray(String jsonFragment) throws ParseException {
        super("", jsonFragment, true);
    }

    JSArray(String keyAtElement, String jsonFragment, boolean willSanitise) throws ParseException {
        super(keyAtElement, jsonFragment, willSanitise);
    }


    @Override
    void init(String keyAtElement) {
        this.keyAtElement = keyAtElement;
        myType = JSType.ARRAY;
        myValue = new ArrayList<>();
    }

    @Override
    void parse(String jsonFragment, boolean sanitize) throws ParseException {

        //check if we need to sanitise the contents of the fragment
        if (sanitize) {
            jsonFragment = sanitiseFragment(jsonFragment);
        }

        //mark which index in the fragment we are at
        int currentFragmentIndex = 1, arrayElems = 0;
        JSON nextElement;
        fragmentSize = 2;

        //while there are more elements in the current level array
        while (jsonFragment.charAt(currentFragmentIndex) != ']') {

            //Figure out and parse the type of value
            nextElement = parseNextElement(jsonFragment.charAt(currentFragmentIndex), keyAtElement,
                "[" + arrayElems++ + "]", currentFragmentIndex, jsonFragment);

            //add the next element to the array
            myValue.add(nextElement);
            //increment the fragment pointer by the previous elements size in the fragment
            currentFragmentIndex += nextElement.fragmentSize;
            //add one to the fragment size
            fragmentSize = currentFragmentIndex + 1;

            //check that there is a separating comma between array elements.
            if (jsonFragment.charAt(currentFragmentIndex) != ','
                && jsonFragment.charAt(currentFragmentIndex) != ']') {
                throw new ParseException("Invalid element separator. (Are you missing a <,> ?)");
            }
            //skip the comma
            if (jsonFragment.charAt(currentFragmentIndex) == ',') {
                currentFragmentIndex++;
            }
        }
        //check for a missing closing array bracket
        if (jsonFragment.charAt(currentFragmentIndex) != ']') {
            throw new ParseException("A JSArray is missing the closing <]>");
        }
    }

    @Override
    public List<IJsonObject> getValue() {
        return myValue;
    }


    @Override
    public String asString(int depth) {
        StringBuilder ret = new StringBuilder("[");

        //if this is an empty array, then be sensible
        if (myValue.size() == 0) {
            return "[]";
        }

        //if the depth is already too low, display the number of elements we contain
        if (depth == 0) {
            return ret.append("<").append(myValue.size()).append(">]").toString();
        }
        //pass down the next value of depth to all children and get their strings
        else {
            for (IJsonObject value : myValue) {
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
    public IJsonObject getByKey(String key) throws KeyNotFoundException {
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
            IJsonObject ret = this;
            for (String i : nestedIndexString) {
                ret = ret.getByKey(i);
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


    public List<String> getKeys() {
        ArrayList<String> ret = new ArrayList<>();
        for (int i = 0; i < myValue.size(); i++) {
            ret.add(String.valueOf(i));
        }
        return ret;
    }
}
