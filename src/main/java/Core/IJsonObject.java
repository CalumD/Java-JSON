package Core;

import Exceptions.JSON.KeyDifferentTypeException;
import Exceptions.JSON.KeyNotFoundException;
import Exceptions.JSON.ParseException;
import Values.JSType;

import java.io.Serializable;
import java.util.List;

/**
 * This interface defines the methods for accessing the elements of a JSON object
 */
public interface IJsonObject extends Serializable {

    /**
     * Used to turn a json fragment as a string into a code JSON object
     *
     * @param jsonFragment The string representation of the JSON object to use in code
     * @return The parsed version of the JSON object in code.
     * @throws ParseException Thrown if there is a problem with the input string.
     */
    IJsonObject createFromString(String jsonFragment) throws ParseException;


    /**
     * Used to get the value of the Core.IJsonObject
     *
     * @return This has to be a generic object since it can subcontain a number of types, but typing
     * can be cleared up by calling the @getDataType() method.
     */
    Object getValue();

    /**
     * Used to get the current Core.IJsonObject.
     *
     * @return The Core.IJsonObject.
     */
    IJsonObject get();

    /**
     * Used to check if there is a value at the given key
     *
     * @param keys This is the key into the JSON to check for
     * @return True if there is a value at that key, false if not
     */
    boolean contains(String keys);

    /**
     * This is used to get the object at the given key.
     *
     * @param keys The key to retrieve the object from
     * @return The object at the given key
     * @throws KeyNotFoundException Thrown if there was no object found at that key.
     */
    IJsonObject getByKey(String keys) throws KeyNotFoundException;

    /**
     * Used to find out the type of the current object.
     *
     * @return The type of the current object.
     */
    JSType getDataType();


    /**
     * Calls getObject(key) with an empty key. E.g. Refers to the current object.
     *
     * @return The value of the current object.
     * @throws KeyNotFoundException      Shouldn't really be thrown by this method since it exists
     *                                   itself.
     * @throws KeyDifferentTypeException Thrown if the type of this object was not what was
     *                                   expected.
     */
    Object getObject() throws KeyNotFoundException, KeyDifferentTypeException;

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
    Object getObject(String key) throws KeyNotFoundException, KeyDifferentTypeException;

    /**
     * Calls getKeys(key) with an empty key. E.g. Refers to the current object.
     *
     * @return The keys of the current object.
     * @throws KeyNotFoundException      Shouldn't really be thrown by this method since it exists
     *                                   itself.
     * @throws KeyDifferentTypeException Thrown if the type of this object was not what was
     *                                   expected.
     */
    List<String> getKeys() throws KeyNotFoundException, KeyDifferentTypeException;

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
    List<String> getKeys(String key) throws KeyNotFoundException, KeyDifferentTypeException;

    /**
     * Calls getList(key) with an empty key. E.g. Refers to the current object.
     *
     * @return The list value of the current object.
     * @throws KeyNotFoundException      Shouldn't really be thrown by this method since it exists
     *                                   itself.
     * @throws KeyDifferentTypeException Thrown if the type of this object was not what was
     *                                   expected.
     */
    List<IJsonObject> getList() throws KeyNotFoundException, KeyDifferentTypeException;

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
    List<IJsonObject> getList(String key) throws KeyNotFoundException, KeyDifferentTypeException;

    /**
     * Calls getBoolean(key) with an empty key. E.g. Refers to the current object.
     *
     * @return The boolean value of the current object.
     * @throws KeyNotFoundException      Shouldn't really be thrown by this method since it exists
     *                                   itself.
     * @throws KeyDifferentTypeException Thrown if the type of this object was not what was
     *                                   expected.
     */
    boolean getBoolean() throws KeyNotFoundException, KeyDifferentTypeException;

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
    boolean getBoolean(String key) throws KeyNotFoundException, KeyDifferentTypeException;

    /**
     * Calls getDouble(key) with an empty key. E.g. Refers to the current object.
     *
     * @return The double value of the current object.
     * @throws KeyNotFoundException      Shouldn't really be thrown by this method since it exists
     *                                   itself.
     * @throws KeyDifferentTypeException Thrown if the type of this object was not what was
     *                                   expected.
     */
    double getDouble() throws KeyNotFoundException, KeyDifferentTypeException;

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
    double getDouble(String key) throws KeyNotFoundException, KeyDifferentTypeException;

    /**
     * Calls getLong(key) with an empty key. E.g. Refers to the current object.
     *
     * @return The long value of the current object.
     * @throws KeyNotFoundException      Shouldn't really be thrown by this method since it exists
     *                                   itself.
     * @throws KeyDifferentTypeException Thrown if the type of this object was not what was
     *                                   expected.
     */
    long getLong() throws KeyNotFoundException, KeyDifferentTypeException;

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
    long getLong(String key) throws KeyNotFoundException, KeyDifferentTypeException;

    /**
     * Calls getString(key) with an empty key. E.g. Refers to the current object.
     *
     * @return The string value of the current object.
     * @throws KeyNotFoundException      Shouldn't really be thrown by this method since it exists
     *                                   itself.
     * @throws KeyDifferentTypeException Thrown if the type of this object was not what was
     *                                   expected.
     */
    String getString() throws KeyNotFoundException, KeyDifferentTypeException;

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
    String getString(String key) throws KeyNotFoundException, KeyDifferentTypeException;

    /**
     * Calls getObject(key) with an empty key. E.g. Refers to the current object.
     *
     * @return The 'anything' value of the current object.
     * @throws KeyNotFoundException      Shouldn't really be thrown by this method since it exists
     *                                   itself.
     * @throws KeyDifferentTypeException Thrown if the type of this object was not what was
     *                                   expected.
     */
    IJsonObject getAny() throws KeyNotFoundException, KeyDifferentTypeException;

    /**
     * Gets whatever the object is at the given key.
     *
     * @param key This is the key to search for within the current object.
     * @return The value of the object at the given key.
     * @throws KeyNotFoundException      Thrown if there was not an object at the path (or if the
     *                                   path syntax is bad).
     * @throws KeyDifferentTypeException Thrown if the type of the object found at that path doesn't
     *                                   match the return type of this method.
     */
    IJsonObject getAny(String key) throws KeyNotFoundException, KeyDifferentTypeException;

    /**
     * Replica of the .equals on a class, but used to enforce implementors override.
     *
     * @param other Another instance of an Core.IJsonObject.
     * @return The quality state of the two objects in question.
     */
    @Override
    boolean equals(Object other);

    /**
     * Calls as string with the lowest depth
     *
     * @return The highest level view of the current object
     */
    @Override
    String toString();

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
     * Calls asPrettyString with the least depth
     *
     * @return The highest level view of the current object, with new lines and object indentation
     */
    String toPrettyString();

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
}
