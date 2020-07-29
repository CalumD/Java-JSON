package api;

import core.JsonBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonParserTest {

    private static final String jsonObject = "" +
            "{\n" +
            "\"array\": [1, 1.0, 'abc', true],\n" +
            "\"object\": {\n" +
            "\"some key with a space\": 1337\n" +
            "},\n" +
            "\"string\": 'string',\n" +
            "\"long\": 123,\n" +
            "\"double\": 1.23,\n" +
            "\"boolean\": true\n" +
            "}";
    private static final IJsonAble jsonObjectAsJSONAble = JsonBuilder
            .builder()
            .addLong("array[]", 1)
            .addDouble("array[]", 1.0)
            .addString("array[]", "abc")
            .addBoolean("array[]", true)
            .addLong("object['some key with a space']", 1337)
            .addString("string", "string")
            .addLong("long", 123)
            .addDouble("double", 1.23)
            .addBoolean("boolean", true);

    private static final String jsonObjectConvertedAsString = "{\"boolean\":true,\"string\":\"string\",\"array\":" +
            "[1,1.0,\"abc\",true],\"double\":1.23,\"long\":123,\"object\":{\"some key with a space\":1337}}";

    @Test
    public void stringParse() {
        assertEquals(jsonObjectConvertedAsString, JsonParser.parse(jsonObject).asString());
    }

    @Test
    public void stringArrayParse() {
        assertEquals(jsonObjectConvertedAsString, JsonParser.parse(jsonObject.split("\n")).asString());
    }

    @Test
    public void stringCollectionParse() {
        assertEquals(jsonObjectConvertedAsString, JsonParser.parse(new ArrayList<>(Arrays.asList(jsonObject.split("\n")))).asString());
    }

    @Test
    public void jsonableParse() {
        assertEquals(jsonObjectConvertedAsString, JsonParser.parse(jsonObjectAsJSONAble).asString());
    }

    @Test
    public void multipleStringParse() {
        List<String> objects = new ArrayList<>(2);
        objects.add(jsonObject);
        objects.add(jsonObject);
        List<IJson> jsons = JsonParser.parseMultipleStrings(objects);
        assertEquals(2, jsons.size(), 0);
        assertEquals(jsonObjectConvertedAsString, jsons.get(0).asString());
        assertEquals(jsonObjectConvertedAsString, jsons.get(1).asString());
    }

    @Test
    public void multipleJSONablesParse() {
        List<IJsonAble> objects = new ArrayList<>(2);
        objects.add(jsonObjectAsJSONAble);
        objects.add(jsonObjectAsJSONAble);
        List<IJson> jsons = JsonParser.parseMultipleJSONables(objects);
        assertEquals(2, jsons.size(), 0);
        assertEquals(jsonObjectConvertedAsString, jsons.get(0).asString());
        assertEquals(jsonObjectConvertedAsString, jsons.get(1).asString());
    }

    @Test
    public void multipleJSONablesForDistinctParse() {
        List<IJsonAble> objects = new ArrayList<>(2);
        objects.add(jsonObjectAsJSONAble);
        objects.add(jsonObjectAsJSONAble);
        objects.add(jsonObjectAsJSONAble);
        Set<IJson> jsons = JsonParser.parseMultipleJSONablesForDistinct(objects);
        assertEquals(1, jsons.size(), 0);
        jsons.forEach(json -> assertEquals(jsonObjectConvertedAsString, json.asString()));
    }

    @Test
    public void multipleStringsForDistinctParse() {
        List<String> objects = new ArrayList<>(2);
        objects.add(jsonObject);
        objects.add(jsonObject);
        objects.add(jsonObject);
        Set<IJson> jsons = JsonParser.parseMultipleStringsForDistinct(objects);
        assertEquals(1, jsons.size(), 0);
        jsons.forEach(json -> assertEquals(jsonObjectConvertedAsString, json.asString()));
    }
}