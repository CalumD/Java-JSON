package Values;

import Core.IJsonObject;
import Exceptions.JSONException;
import Exceptions.JSONParseException;
import Exceptions.KeyDifferentTypeException;

import java.util.List;

public abstract class JSON implements IJsonObject {

    public static final int DEFAULT_PRETTY_JSON_INDENT_WIDTH = 2;
    JSType myType = JSType.UNDEFINED;

    private JSON() {
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public IJsonObject createFromString(String jsonFragment) throws JSONParseException {
        // TODO this method
    }

    @Override
    public IJsonObject createFromStringList(List<String> jsonFragment) throws JSONParseException {
        // TODO this method
    }


//////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean contains(List<String> keys) {
        for (String key : keys) {
            if (!contains(key)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object getValue() {
        return getValue("");
    }

    @Override
    public JSType getDataType() {
        return myType;
    }

    @Override
    public List<String> getKeys() {
        return getKeys("");
    }

    @Override
    public List<IJsonObject> getValues() {
        return getValues("");
    }

    @Override
    public List<IJsonObject> getList() throws KeyDifferentTypeException {
        return getList("");
    }

    @Override
    public boolean getBoolean() throws KeyDifferentTypeException {
        return getBoolean("");
    }

    @Override
    public double getDouble() throws KeyDifferentTypeException {
        return getDouble("");
    }

    @Override
    public long getLong() throws KeyDifferentTypeException {
        return getLong("");
    }

    @Override
    public String getString() throws KeyDifferentTypeException {
        return getString("");
    }

    @Override
    public IJsonObject getAny() {
        return getAny("");
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object getValue(String key) throws JSONException {
        return getInternal(key).getValue();
    }

    @Override
    public IJsonObject getJSONByKey(String key) throws JSONException {
        return ((JSObject) getInternal(key, JSType.OBJECT)).getValue();
    }

    @Override
    public JSType getDataType(String key) throws JSONException {
        return getJSONByKey(key).getDataType();
    }

    @Override
    public List<String> getKeys(String key) throws JSONException {
        return getInternal(key).getKeys();
    }

    @Override
    public List<IJsonObject> getValues(String key) throws JSONException {
        return getInternal(key).getValues();
    }

    @Override
    public List<IJsonObject> getList(String key) throws JSONException {
        return ((JSArray) getInternal(key, JSType.ARRAY)).getValue();
    }

    @Override
    public boolean getBoolean(String key) throws JSONException {
        return ((JSBoolean) getInternal(key, JSType.BOOLEAN)).getValue();
    }

    @Override
    public double getDouble(String key) throws JSONException {
        return getInternal(key, JSType.DOUBLE).getDouble();
    }

    @Override
    public long getLong(String key) throws JSONException {
        return getInternal(key, JSType.LONG).getLong();
    }

    @Override
    public String getString(String key) throws JSONException {
        return ((JSString) getInternal(key, JSType.STRING)).getValue();
    }

    @Override
    public IJsonObject getAny(String key) throws JSONException {
        return getInternal(key);
    }


    private IJsonObject getInternal(String key, JSType requiredType) {

        // Acquire accurate typing
        IJsonObject ret = getInternal(key);
        JSType actualTyping = ret.getDataType();

        // check cast to asked typing
        if (!actualTyping.equals(requiredType)) {
            throw getKeyDifferentTypeException(key, requiredType, actualTyping);
        }

        return ret;
    }

    private IJsonObject getInternal(String key) {
        // Attempt retrieval of object from structure.
        return getJSONByKey(key); // Throws KeyNotFoundException
    }

    private KeyDifferentTypeException getKeyDifferentTypeException(String key, JSType expected, JSType actual) {
        return new KeyDifferentTypeException(
                "The Type of Object found for key {" + key + "} was not expected. Expected: " + expected
                        + "  ->  Received: " + actual);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
        return asString(0);
    }

    @Override
    public String toPrettyString() {
        return asPrettyString(0);
    }

    @Override
    public String asString() {
        return asString(Integer.MAX_VALUE);
    }

    @Override
    public String asPrettyString() {
        return asPrettyString(Integer.MAX_VALUE);
    }

    @Override
    public String asPrettyString(int depth) {
        return asPrettyString(depth, DEFAULT_PRETTY_JSON_INDENT_WIDTH);
    }

    @Override
    public String asPrettyString(int depth, int indentWidth) {
        StringBuilder prettyTabSizeBuilder = new StringBuilder();
        do {
            prettyTabSizeBuilder.append(' ');
        } while (indentWidth-- > 0);
        String prettyTabSize = prettyTabSizeBuilder.toString();
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
                        spacing.append(prettyTabSize);
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
                            spacing.delete(0, prettyTabSize.length());
                            ret.append("\n").append(spacing).append(c);
                            continue;
                        }
                        spacing.delete(0, prettyTabSize.length());
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


    //////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public abstract boolean equals(Object otherJSON);

    @Override
    public abstract int hashCode();
}
