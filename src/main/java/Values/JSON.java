package Values;

import Core.IJson;
import Exceptions.JSONException;
import Exceptions.JSONParseException;
import Exceptions.KeyDifferentTypeException;

import java.util.List;

public abstract class JSON implements IJson {

    public static final int DEFAULT_PRETTY_JSON_INDENT_WIDTH = 2;

    protected static final long serialVersionUID = 100L;
    protected JSType jsType = null;


    //////////////////////////////////////////////////////////////////////////////////////////////////
    JSON(JSONParsingTape parsingTape) throws JSONParseException {
        // This is only used to enforce each extending class has this matching constructor to execute the parse.
    }

    @Override
    public JSON createFromString(String jsonFragment) throws JSONParseException {
        return parseSelf(jsonFragment);
    }

    @Override
    public JSON createFromMultilineString(List<String> jsonFragment) throws JSONParseException {
        return parseSelf(jsonFragment);
    }

    private JSON parseSelf(String jsonFragment) {
        return new JSONParsingTape(jsonFragment).parseNextElement();
    }

    private JSON parseSelf(List<String> jsonFragment) {
        StringBuilder concater = new StringBuilder();
        jsonFragment.forEach(line -> concater.append(line).append('\n'));
        return parseSelf(concater.toString());
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
        return jsType;
    }

    @Override
    public List<String> getKeys() {
        return getKeys("");
    }

    @Override
    public List<IJson> getValues() {
        return getValues("");
    }

    @Override
    public List<IJson> getList() throws KeyDifferentTypeException {
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
    public IJson getAny() {
        return getAny("");
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object getValue(String key) throws JSONException {
        return getInternal(key, JSType.OBJECT).getValue();
    }

    @Override
    public IJson getJSONByKey(String key) throws JSONException {
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
    public List<IJson> getValues(String key) throws JSONException {
        return getInternal(key).getValues();
    }

    @Override
    public List<IJson> getList(String key) throws JSONException {
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
    public IJson getAny(String key) throws JSONException {
        return getInternal(key);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////
    private IJson getInternal(String key, JSType requiredType) {

        // Acquire accurate typing
        IJson ret = getInternal(key);
        JSType actualTyping = ret.getDataType();

        // check cast to asked typing
        if (!actualTyping.equals(requiredType)) {
            throw getKeyDifferentTypeException(key, requiredType, actualTyping);
        }

        return ret;
    }

    private IJson getInternal(String key) {
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

        StringBuilder result = new StringBuilder();
        asPrettyString(new StringBuilder(), prettyTabSizeBuilder.toString(), result, depth);

        return result.toString();
    }

    abstract void asPrettyString(StringBuilder indent, String tabSize, StringBuilder result, int depth);


    //////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public abstract boolean equals(Object otherJSON);

    @Override
    public abstract int hashCode();
}
