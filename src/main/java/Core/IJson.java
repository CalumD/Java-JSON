package Core;

import Exceptions.JSONException;
import Exceptions.JSONParseException;
import Exceptions.KeyDifferentTypeException;
import Exceptions.KeyNotFoundException;
import Values.JSType;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * This interface defines the methods for accessing the elements of a JSON object
 */
public interface IJson extends Serializable {


    /**
     * Used to turn a json fragment as a string into a code JSON object
     *
     * @param jsonFragment The string representation of the JSON object to use in code
     * @return The parsed version of the JSON object in code.
     * @throws JSONParseException Thrown if there is a problem with the input string.
     */
    IJson createFromString(String jsonFragment) throws JSONParseException;

    /**
     * Used to turn a json fragment from a list of strings into a code JSON object
     *
     * @param jsonFragment The list of strings representing the JSON object to use in code
     * @return The parsed version of the JSON object in code.
     * @throws JSONParseException Thrown if there is a problem with the input list.
     */
    IJson createFromMultilineString(List<String> jsonFragment) throws JSONParseException;





    /**
     * Used to check if there is a value at the given key
     *
     * @param key This is the key into the JSON to check for
     * @return True if there is a value at that key, false if not
     */
    boolean contains(String key);

    /**
     * Used to check if there is a value at the given keys
     *
     * @param keys This is the keys into the JSON to check for
     * @return True if all keys have values, false if any do not
     */
    boolean containsAllKeys(Collection<String> keys);


    /**
     * Used to get the value of the IJsonObject
     *
     * @return This has to be a generic object since it can subcontain a number of types, but typing
     * can be cleared up by calling the @getDataType() method.
     */
    Object getValue();

    /**
     * Calls getDataType(key) with an empty key. E.g. Refers to the current object.
     * Used to find out the type of the current object.
     *
     * @return The type of the current object.
     */
    JSType getDataType();

    /**
     * Calls getKeys(key) with an empty key. E.g. Refers to the current object.
     *
     * @return The keys of the current object.
     * @throws KeyNotFoundException      Shouldn't really be thrown by this method since it exists
     *                                   itself.
     * @throws KeyDifferentTypeException Thrown if the type of this object was not what was
     *                                   expected.
     */
    List<String> getKeys();

    /**
     * Calls getValues(key) with an empty key. E.g. Refers to the current object.
     *
     * @return The keys of the current object.
     * @throws KeyNotFoundException      Shouldn't really be thrown by this method since it exists
     *                                   itself.
     * @throws KeyDifferentTypeException Thrown if the type of this object was not what was
     *                                   expected.
     */
    List<IJson> getValues();

    /**
     * Calls getArray(key) with an empty key. E.g. Refers to the current object.
     *
     * @return The list value of the current object.
     * @throws KeyNotFoundException      Shouldn't really be thrown by this method since it exists
     *                                   itself.
     * @throws KeyDifferentTypeException Thrown if the type of this object was not what was
     *                                   expected.
     */
    List<IJson> getArray() throws KeyDifferentTypeException;

    /**
     * Calls getBoolean(key) with an empty key. E.g. Refers to the current object.
     *
     * @return The boolean value of the current object.
     * @throws KeyDifferentTypeException Thrown if the type of this object was not what was
     *                                   expected.
     */
    boolean getBoolean() throws KeyDifferentTypeException;

    /**
     * Calls getDouble(key) with an empty key. E.g. Refers to the current object.
     *
     * @return The double value of the current object.
     * @throws KeyDifferentTypeException Thrown if the type of this object was not what was
     *                                   expected.
     */
    double getDouble() throws KeyDifferentTypeException;

    /**
     * Calls getLong(key) with an empty key. E.g. Refers to the current object.
     *
     * @return The long value of the current object.
     * @throws KeyDifferentTypeException Thrown if the type of this object was not what was
     *                                   expected.
     */
    long getLong() throws KeyDifferentTypeException;

    /**
     * Calls getString(key) with an empty key. E.g. Refers to the current object.
     *
     * @return The string value of the current object.
     * @throws KeyDifferentTypeException Thrown if the type of this object was not what was
     *                                   expected.
     */
    String getString() throws KeyDifferentTypeException;

    /**
     * Calls getJSONObject(key) with an empty key. E.g. Refers to the current object.
     *
     * @return The JSON object value of the current object.
     * @throws KeyNotFoundException      Shouldn't really be thrown by this method since it exists
     *                                   itself.
     * @throws KeyDifferentTypeException Thrown if the type of this object was not what was
     *                                   expected.
     */
    IJson getJSONObject() throws KeyDifferentTypeException;

    /**
     * Calls getObject(key) with an empty key. E.g. Refers to the current object.
     *
     * @return The 'anything' value of the current object.
     * @throws KeyDifferentTypeException Thrown if the type of this object was not what was
     *                                   expected.
     */
    IJson getAny();


    /**
     * Gets the object at the given key.
     *
     * @param key This is the key to search for within the current object.
     * @return The value of the object at the given key.
     * @throws KeyNotFoundException      Thrown if there was not an object at the path (or if the
     *                                   path syntax is bad).
     * @throws KeyDifferentTypeException Thrown if the type of the object found at that path doesn't
     *                                   match the return type of this method.
     */
    Object getValueAt(String key) throws JSONException;

    /**
     * This is used to get the object at the given key.
     *
     * @param keys The key to retrieve the object from
     * @return The object at the given key
     * @throws KeyNotFoundException Thrown if there was no object found at that key.
     */
    IJson getJSONObjectAt(String keys) throws JSONException;

    /**
     * Used to find out the type of the current object.
     *
     * @param key the key to look for the type of the object at
     * @return The Type of the object at the given key.
     * @throws JSONException Thrown if the Key was not found in the object.
     */
    JSType getDataTypeOf(String key) throws JSONException;

    /**
     * Gets the keys from the object at the given key.
     *
     * @param key This is the key to search for within the current object.
     * @return The 'keys' of the object at the given key.
     * @throws KeyNotFoundException      Thrown if there was not an object at the path (or if the
     *                                   path syntax is bad).
     * @throws KeyDifferentTypeException Thrown if the type of the object found at that path doesn't
     *                                   match the return type of this method.
     */
    List<String> getKeysOf(String key) throws JSONException;

    /**
     * Gets the values from the object at the given key.
     *
     * @param key This is the key to search for within the current object.
     * @return The 'values' of the object at the given key.
     * @throws KeyNotFoundException      Thrown if there was not an object at the path (or if the
     *                                   path syntax is bad).
     * @throws KeyDifferentTypeException Thrown if the type of the object found at that path doesn't
     *                                   match the return type of this method.
     */
    List<IJson> getValuesOf(String key) throws JSONException;

    /**
     * Gets the values of the array at the given key.
     *
     * @param key This is the key to search for within the current object.
     * @return The values within the array of the object at the given key.
     * @throws KeyNotFoundException      Thrown if there was not an object at the path (or if the
     *                                   path syntax is bad).
     * @throws KeyDifferentTypeException Thrown if the type of the object found at that path doesn't
     *                                   match the return type of this method.
     */
    List<IJson> getArrayAt(String key) throws JSONException;

    /**
     * Gets the boolean at the given key.
     *
     * @param key This is the key to search for within the current object.
     * @return The boolean value of the object at the given key.
     * @throws KeyNotFoundException      Thrown if there was not an object at the path (or if the
     *                                   path syntax is bad).
     * @throws KeyDifferentTypeException Thrown if the type of the object found at that path doesn't
     *                                   match the return type of this method.
     */
    boolean getBooleanAt(String key) throws JSONException;

    /**
     * Gets the double at the given key.
     *
     * @param key This is the key to search for within the current object.
     * @return The double value of the object at the given key.
     * @throws KeyNotFoundException      Thrown if there was not an object at the path (or if the
     *                                   path syntax is bad).
     * @throws KeyDifferentTypeException Thrown if the type of the object found at that path doesn't
     *                                   match the return type of this method.
     */
    double getDoubleAt(String key) throws JSONException;

    /**
     * Gets the long at the given key.
     *
     * @param key This is the key to search for within the current object.
     * @return The long value of the object at the given key.
     * @throws KeyNotFoundException      Thrown if there was not an object at the path (or if the
     *                                   path syntax is bad).
     * @throws KeyDifferentTypeException Thrown if the type of the object found at that path doesn't
     *                                   match the return type of this method.
     */
    long getLongAt(String key) throws JSONException;

    /**
     * Gets the string at the given key.
     *
     * @param key This is the key to search for within the current object.
     * @return The string value of the object at the given key. (E.g. The actual value string, not
     * the object as a string)
     * @throws KeyNotFoundException      Thrown if there was not an object at the path (or if the
     *                                   path syntax is bad).
     * @throws KeyDifferentTypeException Thrown if the type of the object found at that path doesn't
     *                                   match the return type of this method.
     */
    String getStringAt(String key) throws JSONException;

    /**
     * Gets whatever the object is at the given key.
     *
     * @param key This is the key to search for within the current object.
     * @return The value of the object at the given key.
     * @throws KeyNotFoundException Thrown if there was not an object at the path (or if the
     *                              path syntax is bad).
     */
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
