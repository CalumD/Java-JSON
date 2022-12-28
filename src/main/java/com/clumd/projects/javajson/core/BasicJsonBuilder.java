package com.clumd.projects.javajson.core;

import com.clumd.projects.javajson.api.Json;
import com.clumd.projects.javajson.api.JsonBuilder;
import com.clumd.projects.javajson.api.JsonGenerator;
import com.clumd.projects.javajson.api.JsonParser;
import com.clumd.projects.javajson.exceptions.BuildException;
import com.clumd.projects.javajson.exceptions.json.JsonParseException;
import com.clumd.projects.javajson.exceptions.json.KeyDifferentTypeException;
import com.clumd.projects.javajson.exceptions.json.KeyInvalidException;
import com.clumd.projects.javajson.exceptions.json.KeyNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicJsonBuilder implements JsonBuilder, JsonGenerator {

    private static class NewValueIdentifier {
        final JsonKey keyChain;
        final String finalKey;
        final Object value;

        NewValueIdentifier(String path, Object value) {
            try {
                keyChain = new JsonKey(path, true);
            } catch (KeyInvalidException exception) {
                throw new BuildException("Cannot add new value with invalid key.", exception);
            }
            finalKey = identifyFinalKey(keyChain);
            ensureFirstStepIsNotForArray();
            this.value = value;
        }

        private String identifyFinalKey(JsonKey keyChain) {
            List<String> chainAsListOfKeys = keyChain.getAllKeys();
            if (chainAsListOfKeys.size() == 1) {
                KeyInvalidException invalidException = new KeyInvalidException("The minimum wrapper for this JsonBuilder is a JSON object, " +
                        "you must provide at least one valid key for your value.");
                throw new BuildException(invalidException.getMessage(), invalidException);
            }
            String lastActionableKey = chainAsListOfKeys.get(chainAsListOfKeys.size() - 2);

            return (lastActionableKey.charAt(0) == '[')
                    ? ""
                    : lastActionableKey;
        }

        private void ensureFirstStepIsNotForArray() {
            if (keyChain.getAllKeys().get(0).charAt(0) == '[') {
                throw new BuildException("The base of a JsonBuilder must always be an object, " +
                        "you are trying to assign a value as if it was an array.");
            }
        }
    }

    private final HashMap<String, Boolean> booleans = new HashMap<>();
    private final HashMap<String, String> strings = new HashMap<>();
    private final HashMap<String, Double> doubles = new HashMap<>();
    private final HashMap<String, Long> longs = new HashMap<>();
    private final HashMap<String, BasicJsonBuilder> objects = new HashMap<>();
    private final ArrayList<Object> array = new ArrayList<>();
    private static final String VALUE = "value";

    private JSType type = JSType.OBJECT;


    public BasicJsonBuilder() {
    }

    private BasicJsonBuilder(JSType objectType) {
        type = objectType;
    }

    public static BasicJsonBuilder getBuilder() {
        return new BasicJsonBuilder();
    }

    public BasicJsonBuilder builder() {
        return new BasicJsonBuilder();
    }

    @Override
    public Json build() throws BuildException {
        return convertToJSON();
    }

    @Override
    public Json convertToJSON() throws BuildException {
        try {
            return JsonParser.parse(this.toString());
        } catch (JsonParseException e) {
            throw new BuildException("Failed to convert JsonBuilder to Json.", e);
        }
    }

    @Override
    public JsonBuilder addBoolean(String path, boolean value) throws BuildException {
        NewValueIdentifier valueIdentifier = new NewValueIdentifier(path, value);
        BasicJsonBuilder finalObjectInKeyChain = findObject(valueIdentifier);
        if (finalObjectInKeyChain != null) {
            finalObjectInKeyChain.booleans.put(valueIdentifier.finalKey.substring(1), value);
        }
        return this;
    }

    @Override
    public JsonBuilder addLong(String path, long value) throws BuildException {
        NewValueIdentifier valueIdentifier = new NewValueIdentifier(path, value);
        BasicJsonBuilder finalObjectInKeyChain = findObject(valueIdentifier);
        if (finalObjectInKeyChain != null) {
            finalObjectInKeyChain.longs.put(valueIdentifier.finalKey.substring(1), value);
        }
        return this;
    }

    @Override
    public JsonBuilder addDouble(String path, double value) throws BuildException {
        NewValueIdentifier valueIdentifier = new NewValueIdentifier(path, value);
        BasicJsonBuilder finalObjectInKeyChain = findObject(valueIdentifier);
        if (finalObjectInKeyChain != null) {
            finalObjectInKeyChain.doubles.put(valueIdentifier.finalKey.substring(1), value);
        }
        return this;
    }

    @Override
    public JsonBuilder addString(String path, String value) throws BuildException {
        if (value == null) {
            throw new BuildException("This implementation of a JSONBuilder does not accept null values for strings.");
        }
        NewValueIdentifier valueIdentifier = new NewValueIdentifier(path, value);
        BasicJsonBuilder finalObjectInKeyChain = findObject(valueIdentifier);
        if (finalObjectInKeyChain != null) {
            finalObjectInKeyChain.strings.put(valueIdentifier.finalKey.substring(1), value);
        }
        return this;
    }

    @Override
    public JsonBuilder addBuilderBlock(String path, JsonBuilder value) throws BuildException {
        if (!(value instanceof BasicJsonBuilder)) {
            throw new BuildException("This implementation of a JSONBuilder only accepts JSONBuilder as a builder block value.");
        }
        NewValueIdentifier valueIdentifier = new NewValueIdentifier(path, value);
        BasicJsonBuilder finalObjectInKeyChain = findObject(valueIdentifier);
        if (finalObjectInKeyChain != null) {
            finalObjectInKeyChain.objects.put(valueIdentifier.finalKey.substring(1), (BasicJsonBuilder) value);
        }
        return this;
    }

    @Override
    public JsonBuilder addBuilderBlock(String path, Json value) throws BuildException {
        if (value == null) {
            throw new BuildException("This implementation of a JSONBuilder does not accept null values for Json builder blocks.");
        }
        return addBuilderBlock(path, convertFromJSON(value));
    }

    @Override
    public JsonBuilder mergeExistingObject(Json objectToMerge) throws BuildException {
        if (objectToMerge == null) {
            throw new BuildException("Input object was Null.");
        }
        if (objectToMerge.getDataType() != JSType.OBJECT) {
            throw new BuildException("Invalid Input", new IllegalArgumentException("This method can only " +
                    "be used with Objects, you provided {" + objectToMerge.getDataType() + "}, " +
                    "for other data types, please try method addBuilderBlock()"));
        }

        for (String key : objectToMerge.getKeys()) {
            overridePrimitives(key, objectToMerge.getAnyAt(key));
        }

        return this;
    }

    private void overridePrimitives(String path, Json data) throws BuildException {
        switch (data.getDataType()) {
            case BOOLEAN -> this.addBoolean(path, data.getBoolean());
            case DOUBLE -> this.addDouble(path, data.getDouble());
            case LONG -> this.addLong(path, data.getLong());
            case STRING -> this.addString(path, data.getString());
            case ARRAY -> {
                truncateArray(path);
                for (String index : data.getKeys()) {
                    overridePrimitives(path + '[' + ']', data.getAnyAt('[' + index + ']'));
                }
            }
            case OBJECT -> {
                for (String index : data.getKeys()) {
                    overridePrimitives(path + "[`" + index + "`]", data.getAnyAt(index));
                }
            }
        }
    }

    private void truncateArray(String path) {
        NewValueIdentifier valueIdentifier = new NewValueIdentifier(path, null);
        BasicJsonBuilder finalObjectInKeyChain = findObject(valueIdentifier);
        if (finalObjectInKeyChain != null) {
            String indexingKey = valueIdentifier.finalKey.substring(1);
            finalObjectInKeyChain.objects.remove(indexingKey);
            finalObjectInKeyChain.objects.put(indexingKey, new BasicJsonBuilder(JSType.ARRAY));
        }
    }

    private BasicJsonBuilder findObject(NewValueIdentifier valueIdentifier) {

        // Start from the current object, since this is what the method was called on
        BasicJsonBuilder objectStepInKey = this;
        List<String> allKeys = valueIdentifier.keyChain.getAllKeys();

        // Loop through the whole chain of keys, getting, or creating new objects as required
        try {
            for (int index = 0; index < allKeys.size(); index++) {
                if (allKeys.get(index).equals(valueIdentifier.finalKey)) {
                    break;
                }
                valueIdentifier.keyChain.getNextKey();
                objectStepInKey = getOrCreateIfNotExists(objectStepInKey, allKeys.get(index), allKeys.get(index + 1));
            }
        } catch (KeyNotFoundException notFoundException) {
            throw new BuildException(
                    valueIdentifier.keyChain.createKeyNotFoundException().getMessage().replaceAll("\\[append]", "[]")
                    , notFoundException);
        } catch (KeyDifferentTypeException differentTypeException) {
            valueIdentifier.keyChain.getNextKey();
            throw new BuildException(valueIdentifier.keyChain.createKeyDifferentTypeException().getMessage(), differentTypeException);
        }

        // If the final object is an array, it doesn't care about a key, so add here and return no final object
        if (objectStepInKey.type == JSType.ARRAY) {
            objectStepInKey.array.add(valueIdentifier.value);
            return null;
        } else if (valueIdentifier.finalKey.equals("")) {
            valueIdentifier.keyChain.getNextKey();
            throw new BuildException(valueIdentifier.keyChain.createKeyDifferentTypeException().getMessage());
        }

        // Else, let the caller carry out any further actions.
        return objectStepInKey;
    }

    private BasicJsonBuilder getOrCreateIfNotExists(BasicJsonBuilder currentObject, String currentKey, String nextKey) {

        boolean keyIsForArray = (currentKey.charAt(0) == '[');
        currentKey = currentKey.substring(1);

        if (keyIsForArray) {
            // Note: The integer validation for the array index has already been carried out when parsing the key.

            // Check if we are appending to the current array
            if (currentKey.equals("append") || (Integer.parseInt(currentKey) == currentObject.array.size())) {
                // If we are not at the end of the keychain, add new typed child object we need to create.
                if (!nextKey.equals("")) {
                    BasicJsonBuilder nextBuilder = new BasicJsonBuilder(determineNewChildType(nextKey));
                    currentObject.array.add(nextBuilder);
                    currentObject = nextBuilder;
                }
            } else {
                int index = Integer.parseInt(currentKey);

                if (index >= currentObject.array.size()) {
                    throw new KeyNotFoundException("");
                }
                Object arrayElement = currentObject.array.get(index);
                if (arrayElement instanceof BasicJsonBuilder jsonBuilder) {
                    currentObject = jsonBuilder;
                } else {
                    throw new KeyDifferentTypeException("");
                }
            }
        } else {
            if (currentObject.objects.containsKey(currentKey)) {
                currentObject = currentObject.objects.get(currentKey);
            } else {
                BasicJsonBuilder nextBuilder = new BasicJsonBuilder(determineNewChildType(nextKey));
                currentObject.objects.put(currentKey, nextBuilder);
                currentObject = nextBuilder;
            }
        }

        return currentObject;
    }

    private JSType determineNewChildType(String nextKey) {
        return nextKey.charAt(0) == '[' ? JSType.ARRAY : JSType.OBJECT;
    }

    @Override
    public JsonBuilder convertFromJSON(Json json) {
        switch (json.getDataType()) {
            case BOOLEAN -> {
                return new BasicJsonBuilder().addBoolean(VALUE, json.getBoolean());
            }
            case DOUBLE -> {
                return new BasicJsonBuilder().addDouble(VALUE, json.getDouble());
            }
            case LONG -> {
                return new BasicJsonBuilder().addLong(VALUE, json.getLong());
            }
            case STRING -> {
                return new BasicJsonBuilder().addString(VALUE, json.getString());
            }
            case ARRAY -> {
                return BasicJsonBuilder.getBuilder().addBuilderBlock(VALUE, (BasicJsonBuilder) convertNonPrimitive(json));
            }
            default -> {
                BasicJsonBuilder returnObject = new BasicJsonBuilder(JSType.OBJECT);
                for (String key : json.getKeys()) {
                    Object child = convertNonPrimitive(json.getAnyAt(key));
                    if (child instanceof Boolean) {
                        returnObject.addBoolean(key, (boolean) child);
                    } else if (child instanceof Double) {
                        returnObject.addDouble(key, (double) child);
                    } else if (child instanceof Long) {
                        returnObject.addLong(key, (long) child);
                    } else if (child instanceof String childString) {
                        returnObject.addString(key, childString);
                    } else if (child instanceof BasicJsonBuilder childJsonBuilder) {
                        returnObject.objects.put(key, childJsonBuilder);
                    }
                }
                return returnObject;
            }
        }
    }

    private Object convertNonPrimitive(Json json) {
        switch (json.getDataType()) {
            case BOOLEAN -> {
                return json.getBoolean();
            }
            case DOUBLE -> {
                return json.getDouble();
            }
            case LONG -> {
                return json.getLong();
            }
            case STRING -> {
                return json.getString();
            }
            case ARRAY -> {
                BasicJsonBuilder values = new BasicJsonBuilder(JSType.ARRAY);
                for (Json element : json.getValues()) {
                    values.array.add(convertNonPrimitive(element));
                }
                return values;
            }
            default -> {
                return convertFromJSON(json);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder o = new StringBuilder();

        if (type == JSType.ARRAY) {
            //if we are an array, use square syntax
            o.append("[");

            //for each value in our array object
            for (Object value : array) {

                //if it is a string, be sure to bookend with quotes else call the default toString
                if (value instanceof String s) {
                    o.append('"').append(escapeString(s)).append('"');
                } else {
                    o.append(value.toString());
                }
                //append comma between elements
                o.append(',');
            }
            //delete the last comma, before the closing square
            if (o.charAt(o.length() - 1) == ',') {
                o.deleteCharAt(o.length() - 1);
            }

            //finish up
            o.append("]");

        } else {
            //if we are an object then use the curly syntax
            o.append("{");

            //print all of our booleans with keys
            for (Map.Entry<String, Boolean> entry : booleans.entrySet()) {
                o.append('"').append(escapeString(entry.getKey())).append("\":").append(entry.getValue()).append(',');
            }
            //print all of our doubles with keys
            for (Map.Entry<String, Double> entry : doubles.entrySet()) {
                o.append('"').append(escapeString(entry.getKey())).append("\":").append(entry.getValue()).append(',');
            }
            //print all of our longs with keys
            for (Map.Entry<String, Long> entry : longs.entrySet()) {
                o.append('"').append(escapeString(entry.getKey())).append("\":").append(entry.getValue()).append(',');
            }
            //print all of our strings with keys
            for (Map.Entry<String, String> entry : strings.entrySet()) {
                o.append('"').append(escapeString(entry.getKey())).append("\":\"").append(escapeString(entry.getValue())).append("\",");
            }

            //print all of our sub objects too (again, with keys)
            for (Map.Entry<String, BasicJsonBuilder> entry : objects.entrySet()) {
                o.append('"').append(escapeString(entry.getKey())).append("\":");
                o.append(objects.get(entry.getKey()).toString());
                o.append(',');
            }

            //also remove the final comma from this list too
            if (o.charAt(o.length() - 1) == ',') {
                o.deleteCharAt(o.length() - 1);
            }

            //finish up
            o.append('}');
        }
        return o.toString();
    }

    private String escapeString(String stringToEscape) {
        return stringToEscape
                .replaceAll("\\\\", "\\\\\\\\")
                .replaceAll("\"", "\\\\\"");
    }
}
