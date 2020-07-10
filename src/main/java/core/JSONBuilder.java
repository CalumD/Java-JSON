package core;

import api.IJSONAble;
import api.IJson;
import api.IJsonBuilder;
import api.JSONParser;
import exceptions.BuildException;
import exceptions.KeyDifferentTypeException;
import exceptions.KeyInvalidException;
import exceptions.KeyNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JSONBuilder implements IJsonBuilder, IJSONAble {

    private class NewValueIdentifier {
        final JSONKey keyChain;
        final String finalKey;
        final Object value;

        NewValueIdentifier(String path, Object value) {
            try {
                keyChain = new JSONKey(path, true);
            } catch (KeyInvalidException exception) {
                throw new BuildException("Cannot add new value with invalid key.", exception);
            }
            finalKey = identifyFinalKey(keyChain);
            this.value = value;
        }
    }

    private final HashMap<String, Boolean> booleans = new HashMap<>();
    private final HashMap<String, String> strings = new HashMap<>();
    private final HashMap<String, Double> doubles = new HashMap<>();
    private final HashMap<String, Long> longs = new HashMap<>();
    private final HashMap<String, JSONBuilder> objects = new HashMap<>();
    private final ArrayList<Object> array = new ArrayList<>();

    private JSType type = JSType.OBJECT;


    public JSONBuilder() {
    }

    private JSONBuilder(JSType objectType) {
        type = objectType;
    }

    public static JSONBuilder builder() {
        return new JSONBuilder();
    }

    @Override
    public IJson build() throws BuildException {
        return convertToJSON();
    }

    @Override
    public IJson convertToJSON() throws BuildException {
        return JSONParser.parse(this.toString());
    }

    @Override
    public IJsonBuilder addBoolean(String path, boolean value) throws BuildException {
        NewValueIdentifier valueIdentifier = new NewValueIdentifier(path, value);
        JSONBuilder finalObjectInKeyChain = findObject(valueIdentifier);
        if (finalObjectInKeyChain != null) {
            finalObjectInKeyChain.booleans.put(valueIdentifier.finalKey.substring(1), value);
        }
        return this;
    }

    @Override
    public IJsonBuilder addLong(String path, long value) throws BuildException {
        NewValueIdentifier valueIdentifier = new NewValueIdentifier(path, value);
        JSONBuilder finalObjectInKeyChain = findObject(valueIdentifier);
        if (finalObjectInKeyChain != null) {
            finalObjectInKeyChain.longs.put(valueIdentifier.finalKey.substring(1), value);
        }
        return this;
    }

    @Override
    public IJsonBuilder addDouble(String path, double value) throws BuildException {
        NewValueIdentifier valueIdentifier = new NewValueIdentifier(path, value);
        JSONBuilder finalObjectInKeyChain = findObject(valueIdentifier);
        if (finalObjectInKeyChain != null) {
            finalObjectInKeyChain.doubles.put(valueIdentifier.finalKey.substring(1), value);
        }
        return this;
    }

    @Override
    public IJsonBuilder addString(String path, String value) throws BuildException {
        NewValueIdentifier valueIdentifier = new NewValueIdentifier(path, value);
        JSONBuilder finalObjectInKeyChain = findObject(valueIdentifier);
        if (finalObjectInKeyChain != null) {
            finalObjectInKeyChain.strings.put(valueIdentifier.finalKey.substring(1), value);
        }
        return this;
    }

    @Override
    public IJsonBuilder addBuilderBlock(String path, IJsonBuilder value) throws BuildException {
        if (!(value instanceof JSONBuilder)) {
            throw new BuildException("This implementation of an IJSONBuilder only accepts JSONBuilder as a builder block value.");
        }
        NewValueIdentifier valueIdentifier = new NewValueIdentifier(path, value);
        JSONBuilder finalObjectInKeyChain = findObject(valueIdentifier);
        if (finalObjectInKeyChain != null) {
            finalObjectInKeyChain.objects.put(valueIdentifier.finalKey.substring(1), (JSONBuilder) value);
        }
        return this;
    }

    @Override
    public IJsonBuilder addBuilderBlock(String path, IJson value) throws BuildException {
        return addBuilderBlock(path, convertFromJSON(value));
    }

    private String identifyFinalKey(JSONKey keyChain) {
        List<String> chainAsListOfKeys = keyChain.getAllKeys();
        if (chainAsListOfKeys.size() == 1) {
            KeyInvalidException invalidException = new KeyInvalidException("The minimum wrapper for this IJsonBuilder is a JSON object, " +
                    "you must provide at least one valid key for your value.");
            throw new BuildException(invalidException.getMessage(), invalidException);
        }
        String lastActionableKey = chainAsListOfKeys.get(chainAsListOfKeys.size() - 2);

        return (lastActionableKey.charAt(0) == '[')
                ? ""
                : lastActionableKey;
    }

    private JSONBuilder findObject(NewValueIdentifier valueIdentifier) {

        // Start from the current object, since this is what the method was called on
        JSONBuilder objectStepInKey = this;
        List<String> allKeys = valueIdentifier.keyChain.getAllKeys();

        // Loop through the whole key chain, getting, or creating new objects as required
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

    private JSONBuilder getOrCreateIfNotExists(JSONBuilder currentObject, String currentKey, String nextKey) {

        boolean keyIsForArray = (currentKey.charAt(0) == '[');
        currentKey = currentKey.substring(1);

        if (keyIsForArray) {
            // Note: The integer validation for the array index has already been carried out when parsing the key.

            // Check if we are appending to the current array
            if (currentKey.equals("append") || (Integer.parseInt(currentKey) == currentObject.array.size())) {
                // If we are not at the end of the keychain, add new typed child object we need to create.
                if (!nextKey.equals("")) {
                    JSONBuilder nextBuilder = new JSONBuilder(determineNewChildType(nextKey));
                    currentObject.array.add(nextBuilder);
                    currentObject = nextBuilder;
                }
            } else {
                int index = Integer.parseInt(currentKey);

                if (index >= currentObject.array.size()) {
                    throw new KeyNotFoundException("");
                }
                Object arrayElement = currentObject.array.get(index);
                if (arrayElement instanceof JSONBuilder) {
                    currentObject = (JSONBuilder) arrayElement;
                } else {
                    throw new KeyDifferentTypeException("");
                }
            }
        } else {
            if (currentObject.objects.containsKey(currentKey)) {
                currentObject = currentObject.objects.get(currentKey);
            } else {
                JSONBuilder nextBuilder = new JSONBuilder(determineNewChildType(nextKey));
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
    public IJsonBuilder convertFromJSON(IJson json) {
        switch (json.getDataType()) {
            case BOOLEAN:
                return new JSONBuilder().addBoolean("value", json.getBoolean());
            case DOUBLE:
                return new JSONBuilder().addDouble("value", json.getDouble());
            case LONG:
                return new JSONBuilder().addLong("value", json.getLong());
            case STRING:
                return new JSONBuilder().addString("value", json.getString());
            case ARRAY:
                return JSONBuilder.builder().addBuilderBlock("value", (JSONBuilder) convertNonPrimitive(json));
            default:
                JSONBuilder returnObject = new JSONBuilder(JSType.OBJECT);
                for (String key : json.getKeys()) {
                    Object child = convertNonPrimitive(json.getAnyAt(key));
                    if (child instanceof Boolean) {
                        returnObject.addBoolean(key, (boolean) child);
                    } else if (child instanceof Double) {
                        returnObject.addDouble(key, (double) child);
                    } else if (child instanceof Long) {
                        returnObject.addLong(key, (long) child);
                    } else if (child instanceof String) {
                        returnObject.addString(key, (String) child);
                    } else if (child instanceof JSONBuilder) {
                        returnObject.objects.put(key, (JSONBuilder) child);
                    }
                }
                return returnObject;
        }
    }

    private Object convertNonPrimitive(IJson json) {
        switch (json.getDataType()) {
            case BOOLEAN:
                return json.getBoolean();
            case DOUBLE:
                return json.getDouble();
            case LONG:
                return json.getLong();
            case STRING:
                return json.getString();
            case ARRAY:
                JSONBuilder values = new JSONBuilder(JSType.ARRAY);
                for (IJson element : json.getValues()) {
                    values.array.add(convertNonPrimitive(element));
                }
                return values;
            default:
                return convertFromJSON(json);
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
                if (value instanceof String) {
                    o.append('"').append(value).append('"');
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
            for (String key : booleans.keySet()) {
                o.append('"').append(key).append("\":").append(booleans.get(key)).append(',');
            }
            //print all of our doubles with keys
            for (String key : doubles.keySet()) {
                o.append('"').append(key).append("\":").append(doubles.get(key)).append(',');
            }
            //print all of our longs with keys
            for (String key : longs.keySet()) {
                o.append('"').append(key).append("\":").append(longs.get(key)).append(',');
            }
            //print all of our strings with keys
            for (String key : strings.keySet()) {
                o.append('"').append(key).append("\":\"").append(strings.get(key)).append("\",");
            }

            //print all of our sub objects too (again, with keys)
            for (String key : objects.keySet()) {
                o.append('"').append(key).append("\":");
                o.append(objects.get(key).toString());
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
}
