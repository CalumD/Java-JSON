package api;

import core.JsonTape;
import exceptions.JsonException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A default static utility class for constructing JSON objects from various forms of String input.
 */
public final class JsonParser {

    private JsonParser() {
    }

    /**
     * Parse a single JSON object from String.
     *
     * @param jsonAsString The JSON object in string form.
     * @return The JSON object in Java-JSON form.
     * @throws JsonException Thrown if there is a problem with the input.
     */
    public static Json parse(String jsonAsString) throws JsonException {
        return new JsonTape(jsonAsString).parseNextElement();
    }

    /**
     * Parse a collection of strings into a single JSON object.
     *
     * @param jsonAsStringCollection The JSON object spread out across a collection of strings.
     * @return The JSON object in Java-JSON form.
     * @throws JsonException Thrown if there is a problem with the input.
     */
    public static Json parse(Collection<String> jsonAsStringCollection) throws JsonException {

        StringBuilder whole = new StringBuilder();

        for (String s : jsonAsStringCollection) {
            whole.append(s);
        }

        return parse(whole.toString());
    }

    /**
     * Parse a static array of strings into a single JSON object.
     *
     * @param jsonAsStringArray The JSON object spread out across a static array of strings.
     * @return The JSON object in Java-JSON form.
     * @throws JsonException Thrown if there is a problem with the input.
     */
    public static Json parse(String[] jsonAsStringArray) throws JsonException {

        StringBuilder whole = new StringBuilder();

        for (String s : jsonAsStringArray) {
            whole.append(s);
        }

        return parse(whole.toString());
    }

    /**
     * A pass-through method to convert any object marked as capable of converting itself to JSON, into the JSON equivalent.
     *
     * @param jsonable The Java object declaring itself as possible to convert to JSON.
     * @return The JSON object in Java-JSON form.
     * @throws JsonException Thrown if there is a problem with the input.
     */
    public static Json parse(JsonGenerator jsonable) throws JsonException {
        return jsonable.convertToJSON();
    }

    /**
     * Parse a collection of strings into a multiple JSON objects.
     * Each string in the collection will be treated as it's own JSON object.
     *
     * @param multipleJsonAsStrings A collection of JSON objects in string form.
     * @return The JSON objects in Java-JSON form.
     * @throws JsonException Thrown if there is a problem with the input.
     */
    public static List<Json> parseMultipleStrings(Collection<String> multipleJsonAsStrings) throws JsonException {
        List<Json> jsons = new ArrayList<>(multipleJsonAsStrings.size());
        for (String s : multipleJsonAsStrings) {
            jsons.add(parse(s));
        }
        return jsons;
    }

    /**
     * Parse a collection of Java Objects marked as capable of converting themselves to JSON into a multiple JSON objects.
     * Each Java object in the collection will be passed through to its own {@link JsonGenerator#convertToJSON()} method.
     *
     * @param multipleJsonAsGenerators The collection of Java objects declaring themselves as supporting a conversion to JSON.
     * @return The JSON objects in Java-JSON form.
     * @throws JsonException Thrown if there is a problem with the input.
     */
    public static List<Json> parseMultipleJSONables(Collection<JsonGenerator> multipleJsonAsGenerators) throws JsonException {
        List<Json> jsons = new ArrayList<>(multipleJsonAsGenerators.size());
        for (JsonGenerator s : multipleJsonAsGenerators) {
            jsons.add(s.convertToJSON());
        }
        return jsons;
    }

    /**
     * The same as {@link #parseMultipleStrings(Collection)}, but any duplicates will be filtered out.
     *
     * @param multipleJsonAsStrings A collection of JSON objects in string form, containing potential duplicates.
     * @return A Set, of unique JSON objects converted from the input collection.
     * @throws JsonException Thrown if there was a problem during the conversion process of the Java to JSON.
     */
    public static Set<Json> parseMultipleStringsForDistinct(Collection<String> multipleJsonAsStrings) throws JsonException {
        Set<Json> jsons = new HashSet<>(multipleJsonAsStrings.size());
        for (String s : multipleJsonAsStrings) {
            jsons.add(parse(s));
        }
        return jsons;
    }

    /**
     * The same as {@link #parseMultipleJSONables(Collection)}, but any duplicates will be filtered out.
     *
     * @param multipleJsonAsGenerators A collection of JSON objects declaring themselves as supporting a conversion to JSON,
     *                                 containing potential duplicates.
     * @return A Set, of unique JSON objects converted from the input collection.
     * @throws JsonException Thrown if there was a problem during the conversion process of the Java to JSON.
     */
    public static Set<Json> parseMultipleJSONablesForDistinct(Collection<JsonGenerator> multipleJsonAsGenerators) throws JsonException {
        Set<Json> jsons = new HashSet<>(multipleJsonAsGenerators.size());
        for (JsonGenerator s : multipleJsonAsGenerators) {
            jsons.add(s.convertToJSON());
        }
        return jsons;
    }
}
