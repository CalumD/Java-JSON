package core;

import api.IJson;
import api.IJsonAble;
import exceptions.JsonException;
import exceptions.JsonParseException;
import exceptions.KeyDifferentTypeException;
import exceptions.KeyNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Json implements IJson {

    public static final int DEFAULT_PRETTY_JSON_INDENT_WIDTH = 2;

    protected static final long serialVersionUID = 100L;
    protected JSType jsType = null;


    //////////////////////////////////////////////////////////////////////////////////////////////////
    Json(JsonTape parsingTape) throws JsonParseException {
        // This is only used to enforce each extending class has this matching constructor to execute the parse.
    }

    @Override
    public IJson createFromString(String jsonFragment) throws JsonParseException {
        return parseSelf(jsonFragment);
    }

    @Override
    public IJson createFromMultilineString(List<String> jsonFragment) throws JsonParseException {
        return parseSelf(jsonFragment);
    }

    @Override
    public IJson convertToJSON(IJsonAble jsonable) throws JsonParseException {
        return jsonable.convertToJSON();
    }

    @Override
    public boolean contains(String key) {
        return "".equals(key);
    }

    private IJson parseSelf(String jsonFragment) {
        return new JsonTape(jsonFragment).parseNextElement();
    }

    private IJson parseSelf(List<String> jsonFragment) {
        StringBuilder concater = new StringBuilder();
        jsonFragment.forEach(line -> {
            concater.append(line);
            if (!line.endsWith("\n")) {
                concater.append('\n');
            }
        });
        return parseSelf(concater.toString());
    }


//////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean containsAllKeys(Collection<String> keys) {
        if (keys == null) return false;
        for (String key : keys) {
            if (key == null) return false;
            if (!contains(key)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public JSType getDataType() {
        return jsType;
    }

    @Override
    public List<String> getKeys() {
        return new ArrayList<>();
    }

    @Override
    public List<IJson> getValues() {
        List<IJson> result = new ArrayList<>(1);
        result.add(this);
        return result;
    }

    @Override
    public List<IJson> getArray() throws KeyDifferentTypeException {
        return getArrayAt("");
    }

    @Override
    public boolean getBoolean() throws KeyDifferentTypeException {
        return getBooleanAt("");
    }

    @Override
    public double getDouble() throws KeyDifferentTypeException {
        return getDoubleAt("");
    }

    @Override
    public long getLong() throws KeyDifferentTypeException {
        return getLongAt("");
    }

    @Override
    public String getString() throws KeyDifferentTypeException {
        return getStringAt("");
    }

    @Override
    public IJson getJSONObject() throws KeyDifferentTypeException {
        return getJSONObjectAt("");
    }

    @Override
    public IJson getAny() {
        return getAnyAt("");
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object getValueAt(String key) throws JsonException {
        return getMatching(key).getValue();
    }

    @Override
    public JSType getDataTypeOf(String key) throws JsonException {
        return getMatching(key).getDataType();
    }

    @Override
    public List<String> getKeysOf(String key) throws JsonException {
        return getMatching(key).getKeys();
    }

    @Override
    public List<IJson> getValuesOf(String key) throws JsonException {
        return getMatching(key).getValues();
    }

    @Override
    public IJson getJSONObjectAt(String key) throws JsonException {
        return ((JSObject) getMatching(key, JSType.OBJECT)).getValue();
    }

    @Override
    public List<IJson> getArrayAt(String key) throws JsonException {
        return ((JSArray) getMatching(key, JSType.ARRAY)).getValue();
    }

    @Override
    public boolean getBooleanAt(String key) throws JsonException {
        return ((JSBoolean) getMatching(key, JSType.BOOLEAN)).getValue();
    }

    @Override
    public double getDoubleAt(String key) throws JsonException {
        return getMatching(key, JSType.DOUBLE).getDouble();
    }

    @Override
    public long getLongAt(String key) throws JsonException {
        return getMatching(key, JSType.LONG).getLong();
    }

    @Override
    public String getStringAt(String key) throws JsonException {
        return ((JSString) getMatching(key, JSType.STRING)).getValue();
    }

    @Override
    public IJson getAnyAt(String key) throws JsonException {
        return getMatching(key);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////
    private IJson getMatching(String key, JSType requiredType) {

        // Acquire accurate typing
        IJson ret = getMatching(key);
        JSType actualTyping = ret.getDataType();

        // check cast to asked typing
        if (!actualTyping.equals(requiredType)) {
            throw new KeyDifferentTypeException(
                    "The Type of Object found for key " + key + " was not expected. "
                            + "Expected: " + requiredType + "  ->  Received: " + actualTyping
            );
        }

        return ret;
    }

    private IJson getMatching(String key) {

        // Sanity Check
        if (key == null) {
            throw new KeyNotFoundException("Key provided was Null");
        }

        // Check for base level keys
        if (key.replaceAll("\\s", "").equals("")) {
            return this;
        }

        // Attempt deeper retrieval of object from structure.
        return getInternal(new JsonKey(key, false));
    }

    protected IJson getInternal(JsonKey keySequence) throws KeyNotFoundException {
        if (keySequence.getNextKey().equals("")) {
            return this;
        }
        throw new KeyNotFoundException("No child elements on a " + jsType);
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
        } while (indentWidth-- > 1);

        StringBuilder result = new StringBuilder();
        asPrettyString(new StringBuilder(), prettyTabSizeBuilder.toString(), result, depth);

        return result.toString();
    }

    protected void asPrettyString(StringBuilder indent, String tabSize, StringBuilder result, int depth) {
        result.append(asString(depth));
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public abstract boolean equals(Object otherJSON);

    @Override
    public abstract int hashCode();
}
