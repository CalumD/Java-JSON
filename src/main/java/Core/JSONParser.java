package Core;

import Exceptions.JSONParseException;
import Exceptions.KeyDifferentTypeException;
import Exceptions.KeyNotFoundException;
import Values.JSObject;

import java.io.IOException;
import java.util.List;

/**
 * This class provides a wrapper access to the JSON class, and handles passing strings into the
 * parser for an IJsonObject.
 */
public final class JSONParser {


    /**
     * Given JSON as a string, returns a JSON object to be used in code.
     *
     * @param jsonAsString The JSON string.
     * @return A JSON object representation.
     * @throws JSONParseException Thrown if there was an error while parsing the given string.
     */
    public static IJson parse(String jsonAsString) throws JSONParseException {

        if (jsonAsString.length() == 0) {
            throw new JSONParseException("Empty JSON String, cannot parse");
        }

        return new JSObject(jsonAsString);
    }

    /**
     * This function takes a file path as a string and parses the contents inside as JSON.
     *
     * @param filePath The path of the file to try and parse as JSON.
     * @return The object representing the file contents.
     * @throws JSONParseException Thrown if there was an error while parsing the contents of the file.
     * @throws IOException    Thrown if there was an error loading the file from disk
     */
    public static IJson parseFromFile(String filePath) throws JSONParseException, IOException {

        String fileContents = FileManager.getFileAsString(filePath);

        if (fileContents.length() == 0) {
            throw new JSONParseException("Empty JSON String, cannot parse");
        }

        return new JSObject(fileContents);
    }

    /**
     * Given JSON as a list of strings, returns a JSON object to be used in code.
     *
     * @param jsonAsStringList The list of strings representing JSON.
     * @return A JSON object representation.
     * @throws JSONParseException Thrown if there was an error while parsing the given strings.
     */
    public static IJson parse(List<String> jsonAsStringList) throws JSONParseException {

        StringBuilder whole = new StringBuilder();

        for (String s : jsonAsStringList) {
            whole.append(s);
        }

        return parse(whole.toString());
    }

    /**
     * Given JSON as an array of strings, returns a JSON object to be used in code.
     *
     * @param jsonAsStringArray The array of strings representing JSON.
     * @return A JSON object representation.
     * @throws JSONParseException Thrown if there was an error while parsing the given strings.
     */
    public static IJson parse(String[] jsonAsStringArray) throws JSONParseException {

        StringBuilder whole = new StringBuilder();

        for (String s : jsonAsStringArray) {
            whole.append(s);
        }

        return parse(whole.toString());
    }

    /**
     * Calls getObject(key) with an empty key. E.g. Refers to the current object.
     *
     * @param fromObject The object to get from.
     * @param key The key in the object to get the value of.
     * @return The value of the current object.
     * @throws KeyNotFoundException      Shouldn't really be thrown by this method since it exists
     *                                   itself.
     * @throws KeyDifferentTypeException Thrown if the type of this object was not what was
     *                                   expected.
     */
    public static Object getObject(IJson fromObject, String key)
        throws KeyNotFoundException, KeyDifferentTypeException {
        return fromObject.getValue(key);
    }

    /**
     * Calls getKeys(key) with an empty key. E.g. Refers to the current object.
     *
     * @param fromObject The object to get from.
     * @return The keys of the current object.
     * @throws KeyNotFoundException      Shouldn't really be thrown by this method since it exists
     *                                   itself.
     * @throws KeyDifferentTypeException Thrown if the type of this object was not what was
     *                                   expected.
     */
    public static List<String> getKeys(IJson fromObject)
        throws KeyNotFoundException, KeyDifferentTypeException {
        return fromObject.getKeys();
    }

    /**
     * Gets the keys from the object at the given key.
     *
     * @param fromObject The object to get from.
     * @param key The key in the object to get the value of.
     * @return The 'keys' of the object at the given key.
     * @throws KeyNotFoundException      Thrown if there was not an object at the path (or if the
     *                                   path syntax is bad).
     * @throws KeyDifferentTypeException Thrown if the type of the object found at that path doesn't
     *                                   match the return type of this method.
     */
    public static List<String> getKeys(IJson fromObject, String key)
        throws KeyNotFoundException, KeyDifferentTypeException {
        return fromObject.getKeys(key);
    }

    /**
     * Gets the values of the array at the given key.
     *
     * @param fromObject The object to get from.
     * @param key The key in the object to get the value of.
     * @return The values within the array of the object at the given key.
     * @throws KeyNotFoundException      Thrown if there was not an object at the path (or if the
     *                                   path syntax is bad).
     * @throws KeyDifferentTypeException Thrown if the type of the object found at that path doesn't
     *                                   match the return type of this method.
     */
    public static List<IJson> getList(IJson fromObject, String key)
        throws KeyNotFoundException, KeyDifferentTypeException {
        return fromObject.getList(key);
    }

    /**
     * Gets the boolean at the given key.
     *
     * @param fromObject The object to get from.
     * @param key The key in the object to get the value of.
     * @return The boolean value of the object at the given key.
     * @throws KeyNotFoundException      Thrown if there was not an object at the path (or if the
     *                                   path syntax is bad).
     * @throws KeyDifferentTypeException Thrown if the type of the object found at that path doesn't
     *                                   match the return type of this method.
     */
    public static boolean getBoolean(IJson fromObject, String key)
        throws KeyNotFoundException, KeyDifferentTypeException {
        return fromObject.getBoolean(key);
    }

    /**
     * Gets the double at the given key.
     *
     * @param fromObject The object to get from.
     * @param key The key in the object to get the value of.
     * @return The double value of the object at the given key.
     * @throws KeyNotFoundException      Thrown if there was not an object at the path (or if the
     *                                   path syntax is bad).
     * @throws KeyDifferentTypeException Thrown if the type of the object found at that path doesn't
     *                                   match the return type of this method.
     */
    public static double getDouble(IJson fromObject, String key)
        throws KeyNotFoundException, KeyDifferentTypeException {
        return fromObject.getDouble(key);
    }

    /**
     * Gets the long at the given key.
     *
     * @param fromObject The object to get from.
     * @param key The key in the object to get the value of.
     * @return The long value of the object at the given key.
     * @throws KeyNotFoundException      Thrown if there was not an object at the path (or if the
     *                                   path syntax is bad).
     * @throws KeyDifferentTypeException Thrown if the type of the object found at that path doesn't
     *                                   match the return type of this method.
     */
    public static long getLong(IJson fromObject, String key)
        throws KeyNotFoundException, KeyDifferentTypeException {
        return fromObject.getLong(key);
    }

    /**
     * Gets the string at the given key.
     *
     * @param fromObject The object to get from.
     * @param key The key in the object to get the value of.
     * @return The string value of the object at the given key. (E.g. The actual value string, not
     * the object as a string)
     * @throws KeyNotFoundException      Thrown if there was not an object at the path (or if the
     *                                   path syntax is bad).
     * @throws KeyDifferentTypeException Thrown if the type of the object found at that path doesn't
     *                                   match the return type of this method.
     */
    public static String getString(IJson fromObject, String key)
        throws KeyNotFoundException, KeyDifferentTypeException {
        return fromObject.getString(key);
    }

    /**
     * Gets whatever the object is at the given key.
     *
     * @param fromObject The object to get from.
     * @param key The key in the object to get the value of.
     * @return The value of the object at the given key.
     * @throws KeyNotFoundException      Thrown if there was not an object at the path (or if the
     *                                   path syntax is bad).
     * @throws KeyDifferentTypeException Thrown if the type of the object found at that path doesn't
     *                                   match the return type of this method.
     */
    public static IJson getAny(IJson fromObject, String key)
        throws KeyNotFoundException, KeyDifferentTypeException {
        return fromObject.getAny(key);
    }

    /**
     * Simple equality method to check the contents of two JSON objects.
     *
     * @param object1 The first JSON object to compare with.
     * @param object2 The second JSON object to compare with.
     * @return The equality of the two objects.
     */
    public static boolean equals(IJson object1, IJson object2) {
        return object1.equals(object2);
    }
}
