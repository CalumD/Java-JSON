package api;

import core.JSType;
import exceptions.JSONException;
import exceptions.JSONParseException;
import exceptions.KeyDifferentTypeException;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface IJson extends Serializable {

    IJson createFromString(String jsonFragment) throws JSONParseException;

    IJson createFromMultilineString(List<String> jsonFragment) throws JSONParseException;

    IJson convertToJSON(IJSONAble jsonable) throws JSONParseException;


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


    Object getValueAt(String key) throws JSONException;

    IJson getJSONObjectAt(String keys) throws JSONException;

    JSType getDataTypeOf(String key) throws JSONException;

    List<String> getKeysOf(String key) throws JSONException;

    List<IJson> getValuesOf(String key) throws JSONException;

    List<IJson> getArrayAt(String key) throws JSONException;

    boolean getBooleanAt(String key) throws JSONException;

    double getDoubleAt(String key) throws JSONException;

    long getLongAt(String key) throws JSONException;

    String getStringAt(String key) throws JSONException;

    IJson getAnyAt(String key) throws JSONException;


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
