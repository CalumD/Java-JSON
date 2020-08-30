package api;

import core.JsonTape;
import exceptions.JsonException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class JsonParser {

    private JsonParser() {
    }

    public static Json parse(String jsonAsString) throws JsonException {
        return new JsonTape(jsonAsString).parseNextElement();
    }

    public static Json parse(Collection<String> jsonAsStringCollection) throws JsonException {

        StringBuilder whole = new StringBuilder();

        for (String s : jsonAsStringCollection) {
            whole.append(s);
        }

        return parse(whole.toString());
    }

    public static Json parse(String[] jsonAsStringArray) throws JsonException {

        StringBuilder whole = new StringBuilder();

        for (String s : jsonAsStringArray) {
            whole.append(s);
        }

        return parse(whole.toString());
    }

    public static Json parse(JsonGenerator jsonable) throws JsonException {
        return jsonable.convertToJSON();
    }

    public static List<Json> parseMultipleStrings(Collection<String> multipleJsonAsStrings) throws JsonException {
        List<Json> jsons = new ArrayList<>(multipleJsonAsStrings.size());
        for (String s : multipleJsonAsStrings) {
            jsons.add(parse(s));
        }
        return jsons;
    }

    public static List<Json> parseMultipleJSONables(Collection<JsonGenerator> multipleJsonAsStrings) throws JsonException {
        List<Json> jsons = new ArrayList<>(multipleJsonAsStrings.size());
        for (JsonGenerator s : multipleJsonAsStrings) {
            jsons.add(s.convertToJSON());
        }
        return jsons;
    }

    public static Set<Json> parseMultipleStringsForDistinct(Collection<String> multipleJsonAsStrings) throws JsonException {
        Set<Json> jsons = new HashSet<>(multipleJsonAsStrings.size());
        for (String s : multipleJsonAsStrings) {
            jsons.add(parse(s));
        }
        return jsons;
    }

    public static Set<Json> parseMultipleJSONablesForDistinct(Collection<JsonGenerator> multipleJsonAsStrings) throws JsonException {
        Set<Json> jsons = new HashSet<>(multipleJsonAsStrings.size());
        for (JsonGenerator s : multipleJsonAsStrings) {
            jsons.add(s.convertToJSON());
        }
        return jsons;
    }
}
