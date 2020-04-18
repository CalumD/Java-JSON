package Values;

import Exceptions.JSONParseException;
import Exceptions.KeyNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class JSString extends JSON {

    private final String myValue;

    JSString(JSONParsingTape parsingTape) throws JSONParseException {
        super(parsingTape);

        // Setup tracking variables;
        final char stringDelimiter = parsingTape.checkCurrentChar();
        final StringBuilder string = new StringBuilder();
        boolean currentlyEscaped = false;
        boolean endFound = false;
        char currentChar = '.';

        // Consume the initial delimiter;
        parsingTape.consumeOne();

        // Parse the rest of the string;
        while (!endFound) {
            try {
                currentChar = parsingTape.consumeOne();
            } catch (IndexOutOfBoundsException e) {
                parsingTape.createParseErrorFromOffset(
                        -1,
                        "\"",
                        "Didn't find matching " + stringDelimiter + ", before end of string."
                );
            }

            // Skip over the current character if it was escaped
            if (currentlyEscaped) {
                currentlyEscaped = false;
                string.append(currentChar);
                continue;
            }

            // If char is backslash, then indicate we are escaped.
            if (currentChar == '\\') {
                currentlyEscaped = true;
                continue;
            }

            // If reached end of string, stop, else add to string.
            if (currentChar == stringDelimiter) {
                endFound = true;
            } else {
                string.append(currentChar);
            }
        }

        // finalise result.
        myValue = string.toString();
        jsType = JSType.STRING;
    }

    @Override
    public String getValue() {
        return myValue;
    }

    @Override
    public boolean contains(String keys) {
        return keys.equals("");
    }

    @Override
    protected void asPrettyString(StringBuilder indent, String tabSize, StringBuilder result, int depth) {
        result.append(asString(depth));
    }

    @Override
    public String asString(int depth) {
        // TODO: Check if I need to re-introduce backslash escapes here.
        return "\"" + myValue + "\"";
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }

        if (other instanceof Character[] || other instanceof String) {
            return myValue.equals(other.toString());
        }
        if (other instanceof JSString) {
            return this.myValue.equals(((JSString) other).myValue);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return myValue.hashCode();
    }

    @Override
    public JSON getJSONByKey(String key) {
        if (contains(key)) {
            return this;
        }
        throw new KeyNotFoundException("Key: " + key + ", not found in JSON.");
    }

    @Override
    public List<String> getKeys() {
        return new ArrayList<>();
    }
}
