package api;

import core.JSONTape;
import exceptions.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class JSONParser {

    private JSONParser() {
    }

    public static IJson parse(String jsonAsString) throws JSONException {
        return new JSONTape(jsonAsString).parseNextElement();
    }

    public static IJson parse(Collection<String> jsonAsStringCollection) throws JSONException {

        StringBuilder whole = new StringBuilder();

        for (String s : jsonAsStringCollection) {
            whole.append(s);
        }

        return parse(whole.toString());
    }

    public static IJson parse(String[] jsonAsStringArray) throws JSONException {

        StringBuilder whole = new StringBuilder();

        for (String s : jsonAsStringArray) {
            whole.append(s);
        }

        return parse(whole.toString());
    }

    public static IJson parse(IJSONAble jsonable) throws JSONException {
        return jsonable.convertToJSON();
    }

    public static List<IJson> parseMultipleStrings(Collection<String> multipleJsonAsStrings) throws JSONException {
        List<IJson> jsons = new ArrayList<>(multipleJsonAsStrings.size());
        for (String s : multipleJsonAsStrings) {
            jsons.add(parse(s));
        }
        return jsons;
    }

    public static List<IJson> parseMultipleJSONables(Collection<IJSONAble> multipleJsonAsStrings) throws JSONException {
        List<IJson> jsons = new ArrayList<>(multipleJsonAsStrings.size());
        for (IJSONAble s : multipleJsonAsStrings) {
            jsons.add(s.convertToJSON());
        }
        return jsons;
    }

    public static Set<IJson> parseMultipleStringsForDistinct(Collection<String> multipleJsonAsStrings) throws JSONException {
        Set<IJson> jsons = new HashSet<>(multipleJsonAsStrings.size());
        for (String s : multipleJsonAsStrings) {
            jsons.add(parse(s));
        }
        return jsons;
    }

    public static Set<IJson> parseMultipleJSONablesForDistinct(Collection<IJSONAble> multipleJsonAsStrings) throws JSONException {
        Set<IJson> jsons = new HashSet<>(multipleJsonAsStrings.size());
        for (IJSONAble s : multipleJsonAsStrings) {
            jsons.add(s.convertToJSON());
        }
        return jsons;
    }
}
