package Values;

import Core.IJsonObject;
import Exceptions.JSON.KeyDifferentTypeException;
import Exceptions.JSON.KeyNotFoundException;
import Exceptions.JSON.ParseException;

import java.util.List;

public abstract class JSON implements IJsonObject {

    private static final String PRETTY_TAB_SIZE = "  ";
    long fragmentSize = -1;
    int currentFragmentIndex = 0;
    JSType myType = JSType.UNDEFINED;

    JSON(String keyAtElement, String jsonFragment, boolean willSanitise) throws ParseException {
        init(keyAtElement);
        parse(jsonFragment, willSanitise);
    }

    //CREATION/////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public JSON createFromString(String jsonFragment) throws ParseException {
        return new JSObject("", jsonFragment, true);
    }

    abstract void init(String keyAtElement);

    abstract void parse(String jsonFragment, boolean withSanitisation) throws ParseException;


    //PARSING NEW//////////////////////////////////////////////////////////////////////////////////////////////////////////
    JSON parseNextElement(char idChar, String keyAtElement, String nextKey,
        int currentFragmentIndex, String jsonFragment) throws ParseException {
        JSON nextElement;

        //find out the next element based on the json fragment
        switch (idChar) {
            //we are seeing a new object
            case '{':
                nextElement = new JSObject(
                    keyAtElement + (nextKey.startsWith("[") ? "" : ".") + nextKey,
                    jsonFragment.substring(currentFragmentIndex), false);
                break;
            //we are seeing a new array
            case '[':
                //adjust for object references
                if (myType == JSType.ARRAY) {
                    nextElement = new JSArray(keyAtElement + nextKey,
                        jsonFragment.substring(currentFragmentIndex), false);
                } else {
                    nextElement = new JSArray(keyAtElement + "." + nextKey,
                        jsonFragment.substring(currentFragmentIndex), false);
                }
                break;
            //new strings
            case '"':
            case '\'':
                nextElement = new JSString(keyAtElement,
                    jsonFragment.substring(currentFragmentIndex), false);
                break;
            //must be something else basic
            default:
                idChar = jsonFragment.substring(currentFragmentIndex, currentFragmentIndex + 1)
                    .charAt(0);

                if ((idChar >= '0' && idChar <= '9') || idChar == '-') {
                    nextElement = new JSNumber(keyAtElement,
                        jsonFragment.substring(currentFragmentIndex), false);
                } else {
                    nextElement = new JSBoolean(keyAtElement,
                        jsonFragment.substring(currentFragmentIndex), false);
                }
                break;
        }

        return nextElement;
    }

    String sanitiseFragment(String jsonFragment) throws ParseException {

        StringBuilder strippedJSON = new StringBuilder();

        boolean inDoubleQuote = false, inSingleQuote = false, inComment = false, inComment2 = false, exitComment2 = false;
        int curlyCount = 0, squareCount = 0, slashcount = 0;

        //check that all brackets and quotes match up
        for (char c : jsonFragment.toCharArray()) {

            //ignore content in comments
            if (inComment) {
                if (c == '\n') {
                    inComment = false;
                    slashcount = 0;
                }
            } else if (inComment2) {
                if (c == '*') {
                    exitComment2 = true;
                } else if (c == '/' && exitComment2) {
                    inComment2 = false;
                } else {
                    exitComment2 = false;
                }
            } else {
                switch (c) {
                    case '/':
                        //check entry into single line comment
                        if (!inSingleQuote && !inDoubleQuote) {
                            slashcount++;
                            if (slashcount == 2) {
                                inComment = true;
                            }
                            continue;
                        }
                        break;
                    case '*':
                        //check entry into multiple line comment
                        if (!inSingleQuote && !inDoubleQuote) {
                            if (slashcount > 0) {
                                inComment2 = true;
                            }
                            continue;
                        }
                        break;
                    case '"':
                        inDoubleQuote = !inDoubleQuote;
                        break;
                    case '\'':
                        inSingleQuote = !inSingleQuote;
                        break;
                    case '{':
                        curlyCount++;
                        break;
                    case '}':
                        curlyCount--;
                        break;
                    case '[':
                        squareCount++;
                        break;
                    case ']':
                        squareCount--;
                        break;
                    case ' ':
                    case '\n':
                        if (inSingleQuote || inDoubleQuote) {
                            strippedJSON.append(c);
                        }
                        slashcount = 0;
                        continue;
                }
                slashcount = 0;
                strippedJSON.append(c);
            }
        }
        if (inDoubleQuote) {
            throw new ParseException("JSON Has mismatched <\"> opening quote");
        }
        if (inSingleQuote) {
            throw new ParseException("JSON Has mismatched <'> opening quote");
        }
        if (curlyCount != 0) {
            throw new ParseException(
                "The input has mismatched curly brackets, wont attempt to parse.");
        }
        if (squareCount != 0) {
            throw new ParseException(
                "The input has mismatched square brackets, wont attempt to parse.");
        }

        return strippedJSON.toString();
    }

    //GENERAL GETS/////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public abstract Object getValue();

    @Override
    public abstract boolean contains(String keys);

    @Override
    public abstract boolean equals(Object other);

    @Override
    public IJsonObject get() {
        return this;
    }

    @Override
    public abstract IJsonObject getByKey(String keys) throws KeyNotFoundException;

    @Override
    public JSType getDataType() {
        return myType;
    }


    //SPECIFIC GETTERS/////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object getObject() throws KeyDifferentTypeException, KeyNotFoundException {
        return getObject("");
    }

    @Override
    public Object getObject(String key) throws KeyNotFoundException, KeyDifferentTypeException {
        JSType castType = JSType.UNDEFINED;
        IJsonObject ret;
        try {
            ret = getByKey(key);
            castType = ret.getDataType();
            return ((JSObject) ret).getValue();
        } catch (ClassCastException e) {
            throw generateGetTypedError(JSType.OBJECT, castType);
        }
    }

    @Override
    public List<String> getKeys() throws KeyNotFoundException, KeyDifferentTypeException {
        return getKeys("");
    }

    @Override
    public List<String> getKeys(String key) throws KeyNotFoundException, KeyDifferentTypeException {
        JSType castType = JSType.UNDEFINED;
        IJsonObject ret;
        try {
            ret = getByKey(key);
            castType = ret.getDataType();
            return ret.getKeys();
        } catch (ClassCastException e) {
            throw generateGetTypedError(JSType.ARRAY, castType);
        }
    }

    @Override
    public List<IJsonObject> getList() throws KeyNotFoundException, KeyDifferentTypeException {
        return getList("");
    }

    @Override
    public List<IJsonObject> getList(String key)
        throws KeyNotFoundException, KeyDifferentTypeException {
        JSType castType = JSType.UNDEFINED;
        IJsonObject ret;
        try {
            ret = getByKey(key);
            castType = ret.getDataType();
            return ((JSArray) ret).getValue();
        } catch (ClassCastException e) {
            throw generateGetTypedError(JSType.ARRAY, castType);
        }
    }

    @Override
    public boolean getBoolean() throws KeyNotFoundException, KeyDifferentTypeException {
        return getBoolean("");
    }

    @Override
    public boolean getBoolean(String key) throws KeyNotFoundException, KeyDifferentTypeException {
        JSType castType = JSType.UNDEFINED;
        IJsonObject ret;
        try {
            ret = getByKey(key);
            castType = ret.getDataType();
            return ((JSBoolean) ret).getValue();
        } catch (ClassCastException e) {
            throw generateGetTypedError(JSType.BOOLEAN, castType);
        }
    }

    @Override
    public double getDouble() throws KeyNotFoundException, KeyDifferentTypeException {
        return getDouble("");
    }

    @Override
    public double getDouble(String key) throws KeyNotFoundException, KeyDifferentTypeException {
        JSType castType = JSType.UNDEFINED;
        IJsonObject ret;
        try {
            ret = getByKey(key);
            castType = ret.getDataType();

            if (castType != JSType.DOUBLE) {
                throw generateGetTypedError(JSType.DOUBLE, castType);
            }

            return (double) ret.getValue();
        } catch (ClassCastException e) {
            throw generateGetTypedError(JSType.DOUBLE, castType);
        }
    }

    @Override
    public long getLong() throws KeyNotFoundException, KeyDifferentTypeException {
        return getLong("");
    }

    @Override
    public long getLong(String key) throws KeyNotFoundException, KeyDifferentTypeException {
        JSType castType = JSType.UNDEFINED;
        IJsonObject ret;
        try {
            ret = getByKey(key);
            castType = ret.getDataType();

            if (castType != JSType.LONG) {
                throw generateGetTypedError(JSType.LONG, castType);
            }

            return (long) ret.getValue();
        } catch (ClassCastException e) {
            throw generateGetTypedError(JSType.LONG, castType);
        }
    }

    @Override
    public String getString() throws KeyNotFoundException, KeyDifferentTypeException {
        return getString("");
    }

    @Override
    public String getString(String key) throws KeyNotFoundException, KeyDifferentTypeException {
        JSType castType = JSType.UNDEFINED;
        IJsonObject ret;
        try {
            ret = getByKey(key);
            castType = ret.getDataType();
            return ((JSString) ret).getValue();
        } catch (ClassCastException e) {
            throw generateGetTypedError(JSType.STRING, castType);
        }
    }

    @Override
    public IJsonObject getAny() throws KeyNotFoundException, KeyDifferentTypeException {
        return getAny("");
    }

    @Override
    public IJsonObject getAny(String key) throws KeyNotFoundException, KeyDifferentTypeException {
        JSType castType = JSType.UNDEFINED;
        IJsonObject ret;
        try {
            ret = getByKey(key);
            castType = ret.getDataType();
            return ret;
        } catch (ClassCastException e) {
            throw generateGetTypedError(JSType.UNDEFINED, castType);
        }
    }


    private KeyDifferentTypeException generateGetTypedError(JSType expected, JSType actual) {
        return new KeyDifferentTypeException(
            "The Type of Object found for that key was not expected." + " Expected: " + expected
                + "  ->  Received: " + actual);
    }


    //STRING OUTPUTS///////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
        return asString(0);
    }

    @Override
    public String asString() {
        return asString(Integer.MAX_VALUE);
    }

    @Override
    public abstract String asString(int depth);

    @Override
    public String toPrettyString() {
        return asPrettyString(0);
    }

    @Override
    public String asPrettyString() {
        return asPrettyString(Integer.MAX_VALUE);
    }

    @Override
    public String asPrettyString(int depth) {
        StringBuilder spacing = new StringBuilder();
        StringBuilder ret = new StringBuilder();

        char[] self = asString(depth).toCharArray();
        char prev = self[0];
        boolean inString = false;
        boolean inAngle = false;

        //depending on the string we need to do things differently to make it nice
        for (char c : self) {

            switch (c) {
                //see the start of an object or array, add a newline unless in string
                case '{':
                case '[':
                    if (inString) {
                        ret.append(c);
                    } else {
                        spacing.append(PRETTY_TAB_SIZE);
                        ret.append(c).append("\n").append(spacing);
                    }
                    break;
                //When we see the end of the array or object, reduce the tab size again
                case '}':
                case ']':
                    if (inString) {
                        ret.append(c);
                    } else {
                        if (prev == '{' || prev == '[') {
                            ret.delete(ret.length() - 1 - spacing.length(), ret.length()).append(c);
                        } else {
                            spacing.delete(0, PRETTY_TAB_SIZE.length());
                            ret.append("\n").append(spacing).append(c);
                            continue;
                        }
                        spacing.delete(0, PRETTY_TAB_SIZE.length());
                    }
                    break;
                //replace for a space unless in a string
                case ':':
                    if (inString) {
                        ret.append(c);
                    } else {
                        ret.append(c).append(" ");
                    }
                    break;
                //check for space breaking in strings
                case ',':
                    if (!inString && !inAngle) {
                        ret.append(c).append("\n").append(spacing);
                        break;
                    }
                    if (inAngle) {
                        ret.append(c).append(' ');
                    }
                    break;
                //toggle in angle brackets too
                case '<':
                case '>':
                    inAngle = !inAngle;
                    ret.append(c);
                    break;
                //toggle in string
                case '"':
                    inString = !inString;
                default:
                    ret.append(c);
            }
            prev = c;
        }

        return ret.toString();
    }
}
