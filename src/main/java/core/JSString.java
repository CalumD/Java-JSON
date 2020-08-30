package core;

import exceptions.json.JsonParseException;

final class JSString extends Json {

    private final String myValue;

    JSString(JsonTape parsingTape) throws JsonParseException {
        super(parsingTape);

        // Setup tracking variables;
        final char stringDelimiter = parsingTape.checkCurrentChar();
        if (stringDelimiter != '"' && stringDelimiter != '\'' && stringDelimiter != '`') {
            throw parsingTape.createParseError("\" / ' / `", stringDelimiter + " is not a valid string delimiter.");
        }
        final StringBuilder string = new StringBuilder();
        boolean currentlyEscaped = false;
        boolean endFound = false;
        char currentChar;

        // Consume the initial delimiter;
        parsingTape.consumeOne();

        // Parse the rest of the string;
        while (!endFound) {
            try {
                currentChar = parsingTape.consumeOne();
            } catch (IndexOutOfBoundsException e) {
                throw parsingTape.createParseErrorFromOffset(
                        -1,
                        String.valueOf(stringDelimiter),
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
    public String asString(int depth) {
        return "\"" + myValue.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"") + "\"";
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }

        if (other instanceof String) {
            return myValue.equals(other);
        }

        if (getClass() != other.getClass()) {
            return false;
        } else {
            return this.myValue.equals(((JSString) other).myValue);
        }
    }

    @Override
    public int hashCode() {
        return myValue.hashCode();
    }

}
