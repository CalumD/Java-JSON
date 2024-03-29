package com.clumd.projects.javajson.core;

import com.clumd.projects.javajson.api.Json;
import com.clumd.projects.javajson.exceptions.json.JsonParseException;
import com.clumd.projects.javajson.exceptions.json.KeyNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class JSObject extends com.clumd.projects.javajson.core.Json {

    private final HashMap<String, Json> json;

    JSObject(JsonTape parsingTape) throws JsonParseException {
        super(parsingTape);
        jsType = JSType.OBJECT;
        char checkingChar;

        // Skip over the object opener
        parsingTape.consumeOne();
        parsingTape.consumeWhiteSpace();
        checkingChar = parsingTape.checkCurrentChar();

        // Initial Object parsing Checks
        json = new HashMap<>();
        if (checkingChar == '}') {
            parsingTape.consumeOne();
            return;
        }
        if (checkingChar != '"' && checkingChar != '\'') {
            throw parsingTape.createParseError("\"", "Missing Key at start of Object.");
        }


        // Parse object
        boolean moreChildren = true;
        while (moreChildren) {
            // Get the Key
            String key;
            try {
                key = ((JSString)parsingTape.parseNextElement()).getValue();
            } catch (ClassCastException e) {
                throw parsingTape.createParseErrorFromOffset(-1, "\"", "Invalid type for object key.");
            }
            assert key != null;
            validateObjectKey(key, parsingTape);

            // Validate Colon
            parsingTape.consumeWhiteSpace();
            if (parsingTape.consumeOne() != ':') {
                throw parsingTape.createParseErrorFromOffset(-1, ":", "Invalid Key:Value separator. Must use a colon(:).");
            }

            // Parse value
            Json nextChild = parsingTape.parseNextElement();
            json.put(key, nextChild);

            // Check delimiters.
            parsingTape.consumeWhiteSpace();
            checkingChar = parsingTape.consumeOne();
            switch (checkingChar) {
                case '}' -> moreChildren = false;
                case ',' -> {
                    // Validate if we see a comma, there are more children to come
                    parsingTape.consumeWhiteSpace();
                    if (parsingTape.checkCurrentChar() == '}') {
                        throw parsingTape.createParseError(JsonTape.VALID_JSON,
                                "Comma suggests more object elements, but object terminates.");
                    }
                }
                default -> throw parsingTape.createParseErrorFromOffset(
                        -1,
                        ", / }",
                        "Invalid object child delimiter."
                );
            }
        }
    }

    private void validateObjectKey(String key, JsonTape parsingTape) {
        if (key.equals("")) {
            throw parsingTape.createParseError("<Valid Key>", "Illegal Object Key (Empty).");
        }
        if (json.containsKey(key)) {
            throw parsingTape.createParseError("<Unique Key>", "Illegal Object key (Duplicate): " + key);
        }
    }

    @Override
    public Json getValue() {
        return this;
    }

    @Override
    public boolean contains(String keys) {
        try {
            getAnyAt(keys);
            return true;
        } catch (KeyNotFoundException e) {
            return false;
        }
    }

    @Override
    protected Json getInternal(JsonKey keySequence) throws KeyNotFoundException {
        String nextKey = keySequence.getNextKey();
        if (nextKey.equals("")) {
            return this;
        }
        if (!nextKey.startsWith("{") && !nextKey.startsWith("<")) {
            throw keySequence.createKeyDifferentTypeException();
        }
        com.clumd.projects.javajson.core.Json childElement = (com.clumd.projects.javajson.core.Json) json.get(nextKey.substring(1));
        if (childElement == null) {
            throw keySequence.createKeyNotFoundException();
        }
        return childElement.getInternal(keySequence);
    }

    @Override
    public List<String> getKeys() {
        return new ArrayList<>(json.keySet());
    }

    @Override
    public List<Json> getValues() {
        return new ArrayList<>(json.values());
    }

    @Override
    public String asString(int depth) {
        StringBuilder ret = new StringBuilder("{");

        //empty object
        if (json.size() == 0) {
            return ret.append('}').toString();
        }
        // just print the boilerplate object stuff
        else if (depth == 0) {
            getKeysAsCompressedForString(ret);
            return ret.append('}').toString();
        }

        //print the full internals based on the next depth though
        for (Map.Entry<String, Json> entry : json.entrySet()) {
            ret
                    .append("\"")
                    .append(entry.getKey().replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\""))
                    .append("\"").append(":")
                    .append(entry.getValue().asString(depth - 1)).append(",");
        }

        if (ret.charAt(ret.length() - 1) == ',') {
            ret.deleteCharAt(ret.length() - 1);
        }

        return ret.append("}").toString();
    }

    private void getKeysAsCompressedForString(StringBuilder stringBuilder) {
        stringBuilder.append('<');

        String[] keys = new String[json.size()];
        json.keySet().toArray(keys);
        for (int i = 0; i < keys.length - 1; i++) {
            stringBuilder.append(keys[i]).append(',');
        }
        stringBuilder.append(keys[keys.length - 1]).append(">");
    }

    @Override
    protected void asPrettyString(StringBuilder indent, String tabSize, StringBuilder result, int depth) {
        if (json.isEmpty()) {
            result.append("{}");
            return;
        }

        indent.append(tabSize);
        result.append('{').append('\n').append(indent);
        if (depth == 0) {
            getKeysAsCompressedForString(result);
        } else {
            json.forEach((key, value) -> {
                result
                        .append('"').append(
                        key.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\""))
                        .append("\": ");
                ((com.clumd.projects.javajson.core.Json) value).asPrettyString(indent, tabSize, result, depth - 1);
                result.append(",\n").append(indent);
            });
            result.delete(result.length() - 2 - indent.length(), result.length() - 1);
        }

        indent.delete(0, tabSize.length());
        result.append("\n").append(indent).append('}');
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || (getClass() != other.getClass())) {
            return false;
        }
        if (other == this) {
            return true;
        }

        JSObject o = (JSObject) other;

        if (this.getKeys().size() != o.getKeys().size()) {
            return false;
        }
        for (String key : this.getKeys()) {
            if (!o.json.containsKey(key)) {
                return false;
            }
            if (!(json.get(key).equals(o.json.get(key)))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return json.hashCode();
    }
}
