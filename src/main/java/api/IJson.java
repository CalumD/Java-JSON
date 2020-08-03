package api;

import core.JSType;
import exceptions.JsonException;
import exceptions.json.JsonParseException;
import exceptions.json.KeyDifferentTypeException;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface IJson extends Serializable {

    IJson createFromString(String jsonFragment) throws JsonParseException;

    IJson createFromMultilineString(List<String> jsonFragment) throws JsonParseException;

    IJson convertToJSON(IJsonAble jsonable) throws JsonParseException;


    boolean contains(String key);

    boolean containsAllKeys(Collection<String> keys);


    Object getValue();

    JSType getDataType();

    List<String> getKeys();

    List<IJson> getValues();

    List<IJson> getArray() throws KeyDifferentTypeException;

    boolean getBoolean() throws KeyDifferentTypeException;

    double getDouble() throws KeyDifferentTypeException;

    long getLong() throws KeyDifferentTypeException;

    String getString() throws KeyDifferentTypeException;

    IJson getJSONObject() throws KeyDifferentTypeException;

    IJson getAny();


    Object getValueAt(String key) throws JsonException;

    IJson getJSONObjectAt(String keys) throws JsonException;

    JSType getDataTypeOf(String key) throws JsonException;

    List<String> getKeysOf(String key) throws JsonException;

    List<IJson> getValuesOf(String key) throws JsonException;

    List<IJson> getArrayAt(String key) throws JsonException;

    boolean getBooleanAt(String key) throws JsonException;

    double getDoubleAt(String key) throws JsonException;

    long getLongAt(String key) throws JsonException;

    String getStringAt(String key) throws JsonException;

    IJson getAnyAt(String key) throws JsonException;


    /**
     * Calls as string with the shallowest depth
     *
     * @return The highest level view of the current object
     */
    @Override
    String toString();

    /**
     * Calls asPrettyString with the shallowest depth
     *
     * @return The highest level view of the current object, with new lines and object indentation
     */
    String toPrettyString();


    /**
     * Calls as string with the deepest depth
     *
     * @return The most detailed view of the current object
     */
    String asString();

    /**
     * Creates a string representation of the current object with a given depth of object expansion
     *
     * @param depth The depth of objects to show in the output
     * @return The string representation of this object
     */
    String asString(int depth);


    /**
     * Calls asPrettyString with the deepest depth
     *
     * @return The most detailed view of the current object, with new lines and object indentation
     */
    String asPrettyString();

    /**
     * Creates a string representation of the current object with a given depth of object expansion
     *
     * @param depth The depth of objects to show in the output
     * @return The string representation of this object, with new lines and object indentation
     */
    String asPrettyString(int depth);

    /**
     * Creates a string representation of the current object with a given depth of object expansion
     * And a custom level of indentation
     *
     * @param depth The depth of objects to show in the output
     * @return The string representation of this object, with new lines and object indentation
     */
    String asPrettyString(int depth, int indentWidth);


    /**
     * Replica of the .equals on a class, but used to enforce implementors override.
     *
     * @param other Another instance of an IJsonObject.
     * @return The equality state of the two objects in question.
     */
    @Override
    boolean equals(Object other);

    /**
     * Used to enforce the implementation of a Hashcode on Implementers
     *
     * @return The Hashcode of this IJsonObject
     */
    @Override
    int hashCode();
}
