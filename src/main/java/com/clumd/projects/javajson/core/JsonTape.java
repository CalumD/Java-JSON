package com.clumd.projects.javajson.core;

import com.clumd.projects.javajson.exceptions.json.JsonParseException;

public final class JsonTape extends Tape<Json, JsonParseException> {

    static final String VALID_JSON = "{ / [ / \" / <number> / <boolean> ";

    public JsonTape(String fullInput) {
        super(fullInput);
    }

    public Json parseNextElement() {

        // Reach the first legitimate character.
        consumeWhiteSpace();

        // Figure out the next JSON type
        Json nextElement = null;
        switch (checkCurrentChar()) {
            case 't', 'T', 'f', 'F' -> nextElement = new JSBoolean(this);
            case '{' -> nextElement = new JSObject(this);
            case '[' -> nextElement = new JSArray(this);
            case '"', '\'', '`' -> nextElement = new JSString(this);
            case '-', '+', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> nextElement = new JSNumber(this);
            default -> createParseError(VALID_JSON);
        }
        return nextElement;
    }

    @Override
    protected JsonParseException newTypedException(String message) {
        return new JsonParseException(message);
    }

    @Override
    protected void consumeWhiteSpace() {
        try {
            while (true) {
                switch (checkCurrentChar()) {
                    case ' ', '\n', '\r', '\t' -> currentIndex++;
                    case '/', '#' -> consumeComment();
                    default -> {
                        return;
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new JsonParseException(
                    "Reached the end of the JSON input before parsing was complete. Are you missing a terminating delimiter?"
            );
        }
    }

    private void consumeComment() {
        switch (checkCurrentChar()) {
            case '#' -> consumeUntilNewLine();
            case '/' -> {
                switch (checkNextChar()) {
                    //   <-- this Comment
                    case '/' -> consumeUntilNewLine();

                    /*    This comment    */
                    case '*' -> consumeUntilEndOfMultilineString();

                    // Invalid Comment Type
                    default -> createParseError("/ or *");
                }
            }
            default -> {
                // do nothing, was not actually a comment
            }
        }
    }

    private void consumeUntilNewLine() {
        while (checkCurrentChar() != '\n') {
            currentIndex++;
        }
        currentIndex++;
    }

    private void consumeUntilEndOfMultilineString() {
        while (!checkNextFragment("*/")) {
            currentIndex++;
        }
    }
}
