package api;

import core.JSType;
import exceptions.json.JsonKeyException;
import exceptions.json.JsonParseException;
import exceptions.json.KeyDifferentTypeException;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Defines the required methods callable on a JSON object, to get type-safe properties, check existence,
 * and convert to various degrees of string (pretty print etc).
 */
public interface Json extends JsonGenerator, Serializable {

    /**
     * Convert the contents of a String into the JSON representation.
     *
     * @param jsonFragment A string to be converted.
     * @return A JSON object representing the contents of the string.
     * @throws JsonParseException Thrown if the string was not valid JSON syntax.
     */
    Json createFromString(String jsonFragment) throws JsonParseException;

    /**
     * Convert the contents of consecutive Strings into the JSON representation of them.
     *
     * @param jsonFragment A list of strings (usually originating from each line of a .json file), for a single JSON.
     * @return A JSON object representing the contents of the list of strings.
     * @throws JsonParseException Thrown if any of the input contains invalid JSON syntax.
     */
    Json createFromMultilineString(List<String> jsonFragment) throws JsonParseException;

    /**
     * Used to check if a given key is present in this JSON object (Object in this sentence refers to any JSON data-type).
     *
     * @param key The path within the JSON to check for existence.
     * @return True if a value was found at the given key, false if not.
     */
    boolean contains(String key);

    /**
     * Used to check if every provided key appears in the current JSON object.
     *
     * @param keys The collection of JSON key paths to check for existence.
     * @return True if every key in the collection was found, False if one or more keys was missing from the JSON.
     */
    boolean containsAllKeys(Collection<String> keys);


    /**
     * Used to get the underlying value of each JSON datatype in a non-typesafe manner.
     *
     * @return The Java data value this JSON object wraps.
     */
    Object getValue();

    /**
     * Used to figure out which Datatype this JSON object represents from the JSON standard.
     *
     * @return The datatype of this JSON object.
     */
    JSType getDataType();

    /**
     * Used to get a list of all possible keys available at the current depth/level of json.
     * <p>
     * I.e. if you have an object where each child has its own properties, you will only get a list of the children,
     * NOT of the children's children.
     *
     * @return The list of all possible keys of this JSON object.
     */
    List<String> getKeys();

    /**
     * Used to get a list of all values of the current JSON object.
     *
     * @return The list of all values of this JSON object.
     */
    List<Json> getValues();

    /**
     * Used to get the array value of this JSON object, if it was itself, an array.
     *
     * @return The Array value of the current JSON object.
     * @throws KeyDifferentTypeException Thrown if this JSON object represents something other than an array.
     */
    List<Json> getArray() throws KeyDifferentTypeException;

    /**
     * Used to get the boolean value of this JSON object, if it was itself, a boolean.
     *
     * @return The boolean value of the current JSON object.
     * @throws KeyDifferentTypeException Thrown if this JSON object represents something other than a boolean.
     */
    boolean getBoolean() throws KeyDifferentTypeException;

    /**
     * Used to get the double value of this JSON object, if it was itself, a double.
     *
     * @return The double value of the current JSON object.
     * @throws KeyDifferentTypeException Thrown if this JSON object represents something other than an double.
     */
    double getDouble() throws KeyDifferentTypeException;

    /**
     * Used to get the long value of this JSON object, if it was itself, a long.
     *
     * @return The long value of the current JSON object.
     * @throws KeyDifferentTypeException Thrown if this JSON object represents something other than an long.
     */
    long getLong() throws KeyDifferentTypeException;

    /**
     * Used to get the string value of this JSON object, if it was itself, a string.
     * <p>
     * You should NOT use this method to get the String representation of this object,
     * see {@link #toString() toString} style methods.
     *
     * @return The string value of the current JSON object.
     * @throws KeyDifferentTypeException Thrown if this JSON object represents something other than a string.
     */
    String getString() throws KeyDifferentTypeException;

    /**
     * Used to get the Object value of this JSON object, if it was itself, an Object.
     *
     * @return The Object value of the current JSON object.
     * @throws KeyDifferentTypeException Thrown if this JSON object represents something other than an Object.
     */
    Json getJSONObject() throws KeyDifferentTypeException;

    /**
     * Used to get the JSON datatype of the current object regardless of the underlying datatype it represents.
     *
     * @return The current object as a JSON regardless of the underlying datatype it represents.
     */
    Json getAny();


    /**
     * The same as {@link #getValue() getValue}, but at a path in the JSON given by key.
     *
     * @param key The key / path from the current JSON object to the target you want.
     * @return Any value (non-type safe) present at the given key.
     * @throws JsonKeyException Thrown if they key is malformed, or there was not a property at the provided key.
     */
    Object getValueAt(String key) throws JsonKeyException;

    /**
     * The same as {@link #getJSONObject() getJSONObject}, but at a path in the JSON given by key.
     *
     * @param key The key / path from the current JSON object to the target you want.
     * @return Any JSON OBJECT type present at the given key.
     * @throws JsonKeyException Thrown if they key is malformed,
     *                          there was not a property at the provided key,
     *                          or the datatype of the value at key, was not OBJECT.
     */
    Json getJSONObjectAt(String key) throws JsonKeyException;

    /**
     * The same as {@link #getDataType() getDataType}, but at a path in the JSON given by key.
     *
     * @param key The key / path from the current JSON object to the target you want.
     * @return The JSON datatype present at the given key.
     * @throws JsonKeyException Thrown if they key is malformed, or there was not a property at the provided key.
     */
    JSType getDataTypeOf(String key) throws JsonKeyException;

    /**
     * The same as {@link #getKeys() getKeys}, but at a path in the JSON given by key.
     *
     * @param key The key / path from the current JSON object to the target you want.
     * @return A list of all possible keys present from the point the given key.
     * @throws JsonKeyException Thrown if they key is malformed, or there was not a property at the provided key.
     */
    List<String> getKeysOf(String key) throws JsonKeyException;

    /**
     * The same as {@link #getValues() getValues}, but at a path in the JSON given by key.
     *
     * @param key The key / path from the current JSON object to the target you want.
     * @return All values present from the given key.
     * @throws JsonKeyException Thrown if they key is malformed, or there was not a property at the provided key.
     */
    List<Json> getValuesOf(String key) throws JsonKeyException;

    /**
     * The same as {@link #getArray() getArray}, but at a path in the JSON given by key.
     *
     * @param key The key / path from the current JSON object to the target you want.
     * @return Any JSON ARRAY type present at the given key.
     * @throws JsonKeyException Thrown if they key is malformed,
     *                          there was not a property at the provided key,
     *                          or the datatype of the value at key, was not ARRAY.
     */
    List<Json> getArrayAt(String key) throws JsonKeyException;

    /**
     * The same as {@link #getBoolean() getBoolean}, but at a path in the JSON given by key.
     *
     * @param key The key / path from the current JSON object to the target you want.
     * @return Any JSON BOOLEAN type present at the given key.
     * @throws JsonKeyException Thrown if they key is malformed,
     *                          there was not a property at the provided key,
     *                          or the datatype of the value at key, was not BOOLEAN.
     */
    boolean getBooleanAt(String key) throws JsonKeyException;

    /**
     * The same as {@link #getDouble() getDouble}, but at a path in the JSON given by key.
     *
     * @param key The key / path from the current JSON object to the target you want.
     * @return Any JSON DOUBLE type present at the given key.
     * @throws JsonKeyException Thrown if they key is malformed,
     *                          there was not a property at the provided key,
     *                          or the datatype of the value at key, was not DOUBLE.
     */
    double getDoubleAt(String key) throws JsonKeyException;

    /**
     * The same as {@link #getLong() getLong}, but at a path in the JSON given by key.
     *
     * @param key The key / path from the current JSON object to the target you want.
     * @return Any JSON LONG type present at the given key.
     * @throws JsonKeyException Thrown if they key is malformed,
     *                          there was not a property at the provided key,
     *                          or the datatype of the value at key, was not LONG.
     */
    long getLongAt(String key) throws JsonKeyException;

    /**
     * The same as {@link #getString() getString}, but at a path in the JSON given by key.
     * <p>
     * You should NOT use this method to get the String representation of this object,
     * see {@link #toString() toString} style methods.
     *
     * @param key The key / path from the current JSON object to the target you want.
     * @return Any JSON STRING type present at the given key.
     * @throws JsonKeyException Thrown if they key is malformed,
     *                          there was not a property at the provided key,
     *                          or the datatype of the value at key, was not STRING.
     */
    String getStringAt(String key) throws JsonKeyException;

    /**
     * The same as {@link #getAny() getAny}, but at a path in the JSON given by key.
     *
     * @param key The key / path from the current JSON object to the target you want.
     * @return The Java wrapping JSON data present at the given key.
     * @throws JsonKeyException Thrown if they key is malformed,
     *                          there was not a property at the provided key.
     */
    Json getAnyAt(String key) throws JsonKeyException;


    /**
     * Calls as string with the shallowest depth
     *
     * @return The highest level view of the current object
     */
    @Override
    String toString();

    /**
     * Calls asPrettyString with the shallowest depth and a default indent width of 4.
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
     * Calls asPrettyString with the deepest depth and a default indent width of 4.
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
     * Replica of the .equals on a class, but used to enforce that implementors will override.
     *
     * @param other Another instance of a JsonObject.
     * @return The equality state of the two objects in question.
     */
    @Override
    boolean equals(Object other);

    /**
     * Used to enforce the implementation of a Hashcode on Implementers
     *
     * @return The Hashcode of this JsonObject
     */
    @Override
    int hashCode();
}
