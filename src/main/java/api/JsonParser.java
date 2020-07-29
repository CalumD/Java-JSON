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

    public static IJson parse(String jsonAsString) throws JsonException {
        return new JsonTape(jsonAsString).parseNextElement();
    }

    public static IJson parse(Collection<String> jsonAsStringCollection) throws JsonException {

        StringBuilder whole = new StringBuilder();

        for (String s : jsonAsStringCollection) {
            whole.append(s);
        }

        return parse(whole.toString());
    }

    public static IJson parse(String[] jsonAsStringArray) throws JsonException {

        StringBuilder whole = new StringBuilder();

        for (String s : jsonAsStringArray) {
            whole.append(s);
        }

        return parse(whole.toString());
    }

    public static IJson parse(IJsonAble jsonable) throws JsonException {
        return jsonable.convertToJSON();
    }

    public static List<IJson> parseMultipleStrings(Collection<String> multipleJsonAsStrings) throws JsonException {
        List<IJson> jsons = new ArrayList<>(multipleJsonAsStrings.size());
        for (String s : multipleJsonAsStrings) {
            jsons.add(parse(s));
        }
        return jsons;
    }

    public static List<IJson> parseMultipleJSONables(Collection<IJsonAble> multipleJsonAsStrings) throws JsonException {
        List<IJson> jsons = new ArrayList<>(multipleJsonAsStrings.size());
        for (IJsonAble s : multipleJsonAsStrings) {
            jsons.add(s.convertToJSON());
        }
        return jsons;
    }

    public static Set<IJson> parseMultipleStringsForDistinct(Collection<String> multipleJsonAsStrings) throws JsonException {
        Set<IJson> jsons = new HashSet<>(multipleJsonAsStrings.size());
        for (String s : multipleJsonAsStrings) {
            jsons.add(parse(s));
        }
        return jsons;
    }

    public static Set<IJson> parseMultipleJSONablesForDistinct(Collection<IJsonAble> multipleJsonAsStrings) throws JsonException {
        Set<IJson> jsons = new HashSet<>(multipleJsonAsStrings.size());
        for (IJsonAble s : multipleJsonAsStrings) {
            jsons.add(s.convertToJSON());
        }
        return jsons;
    }
}
