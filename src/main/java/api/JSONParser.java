package api;

import core.JSONTape;
import exceptions.JSONParseException;

import java.util.List;

public final class JSONParser {

    public static IJson parse(String jsonAsString) throws JSONParseException {

        if (jsonAsString.length() == 0) {
            throw new JSONParseException("Empty JSON String, cannot parse");
        }

        return new JSONTape(jsonAsString).parseNextElement();
    }

    public static IJson parse(List<String> jsonAsStringList) throws JSONParseException {

        StringBuilder whole = new StringBuilder();

        for (String s : jsonAsStringList) {
            whole.append(s);
        }

        return parse(whole.toString());
    }

    public static IJson parse(String[] jsonAsStringArray) throws JSONParseException {

        StringBuilder whole = new StringBuilder();

        for (String s : jsonAsStringArray) {
            whole.append(s);
        }

        return parse(whole.toString());
    }

    public static IJson parse(IJSONAble jsonable) throws JSONParseException {
        return jsonable.convertToJSON();
    }

    public static Object getValue(IJson fromObject, String key) {
        return fromObject.getValueAt(key);
    }

    public static List<String> getKeys(IJson fromObject) {
        return fromObject.getKeys();
    }

    public static List<String> getKeys(IJson fromObject, String key) {
        return fromObject.getKeysOf(key);
    }

    public static List<IJson> getList(IJson fromObject, String key) {
        return fromObject.getArrayAt(key);
    }

    public static IJson getJSONObject(IJson fromObject, String key) {
        return fromObject.getJSONObjectAt(key);
    }

    public static boolean getBoolean(IJson fromObject, String key) {
        return fromObject.getBooleanAt(key);
    }

    public static double getDouble(IJson fromObject, String key) {
        return fromObject.getDoubleAt(key);
    }

    public static long getLong(IJson fromObject, String key) {
        return fromObject.getLongAt(key);
    }

    public static String getString(IJson fromObject, String key) {
        return fromObject.getStringAt(key);
    }

    public static IJson getAny(IJson fromObject, String key) {
        return fromObject.getAnyAt(key);
    }

    public static boolean equals(IJson object1, IJson object2) {
        return object1.equals(object2);
    }
}
