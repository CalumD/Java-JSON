package com.clumd.projects.javajson.core;

import com.clumd.projects.javajson.api.Json;
import com.clumd.projects.javajson.exceptions.json.JsonParseException;
import com.clumd.projects.javajson.exceptions.json.KeyNotFoundException;

import java.util.ArrayList;
import java.util.List;

final class JSArray extends com.clumd.projects.javajson.core.Json {

    private final List<Json> myValue;

    JSArray(JsonTape parsingTape) throws JsonParseException {
        super(parsingTape);
        jsType = JSType.ARRAY;
        char checkingChar;

        // Skip over the array opener
        parsingTape.consumeOne();
        parsingTape.consumeWhiteSpace();
        checkingChar = parsingTape.checkCurrentChar();

        // Initial Array parsing Checks
        if (checkingChar == ',') {
            throw parsingTape.createParseError(
                    JsonTape.VALID_JSON,
                    "Missing Valid JSON at start of array."
            );
        }
        myValue = new ArrayList<>();
        if (checkingChar == ']') {
            parsingTape.consumeOne();
            return;
        }


        // Parse array
        boolean moreChildren = true;
        while (moreChildren) {
            myValue.add(parsingTape.parseNextElement());
            parsingTape.consumeWhiteSpace();
            checkingChar = parsingTape.consumeOne();
            switch (checkingChar) {
                case ']' -> moreChildren = false;
                case ',' -> {
                    // Validate if we see a comma, there are more children to come
                    parsingTape.consumeWhiteSpace();
                    if (parsingTape.checkCurrentChar() == ']') {
                        throw parsingTape.createParseError(JsonTape.VALID_JSON,
                                "Comma suggests more array elements, but array terminates.");
                    }
                }
                default -> throw parsingTape.createParseErrorFromOffset(-1, ", / ]",
                        "Invalid array child delimiter.");
            }
        }
    }

    @Override
    public List<Json> getValue() {
        return myValue;
    }

    @Override
    public boolean contains(String keys) {
        try {
            //else get from children
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
        if (!nextKey.startsWith("[")) {
            throw keySequence.createKeyDifferentTypeException();
        }
        com.clumd.projects.javajson.core.Json childElement;
        try {
            childElement = (com.clumd.projects.javajson.core.Json) myValue.get(Integer.parseInt(nextKey.substring(1)));
        } catch (IndexOutOfBoundsException e) {
            throw keySequence.createKeyNotFoundException();
        }
        return childElement.getInternal(keySequence);
    }

    @Override
    public List<String> getKeys() {
        ArrayList<String> ret = new ArrayList<>();
        for (int i = 0; i < myValue.size(); i++) {
            ret.add(String.valueOf(i));
        }
        return ret;
    }

    @Override
    public List<Json> getValues() {
        return getValue();
    }

    @Override
    public String asString(int depth) {
        //if this is an empty array, then be sensible
        if (myValue.isEmpty()) {
            return "[]";
        }

        StringBuilder ret = new StringBuilder("[");

        //if the depth is already too low, display the number of elements we contain
        if (depth == 0) {
            return ret.append("<").append(myValue.size()).append(">]").toString();
        }
        //pass down the next value of depth to all children and get their strings
        else {
            for (Json value : myValue) {
                ret.append(value.asString(depth - 1)).append(",");
            }
            if (ret.charAt(ret.length() - 1) == ',') {
                ret.deleteCharAt(ret.length() - 1);
            }
            return ret.append("]").toString();
        }
    }

    @Override
    protected void asPrettyString(StringBuilder indent, String tabSize, StringBuilder result, int depth) {
        if (myValue.isEmpty()) {
            result.append("[]");
            return;
        }

        indent.append(tabSize);
        result.append('[').append('\n').append(indent);
        if (depth == 0) {
            result.append("<").append(myValue.size()).append(">");
        } else {
            myValue.forEach(value -> {
                ((com.clumd.projects.javajson.core.Json) value).asPrettyString(indent, tabSize, result, depth - 1);
                result.append(",\n").append(indent);
            });
            result.delete(result.length() - 2 - indent.length(), result.length() - 1);
        }

        indent.delete(0, tabSize.length());
        result.append("\n").append(indent).append(']');
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || ((getClass() != other.getClass()) && (!(other instanceof List)))) {
            return false;
        }
        if (other == this) {
            return true;
        }

        if (other instanceof List<?> o) {

            if (this.myValue.size() != o.size()) {
                return false;
            }
            for (int i = 0; i < this.myValue.size(); i++) {
                if (!(this.myValue.get(i).equals(o.get(i)))) {
                    return false;
                }
            }
        } else {
            JSArray o = (JSArray) other;

            if (this.myValue.size() != o.myValue.size()) {
                return false;
            }
            for (int i = 0; i < this.myValue.size(); i++) {
                if (!(this.myValue.get(i).equals(o.myValue.get(i)))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return myValue.hashCode();
    }
}
