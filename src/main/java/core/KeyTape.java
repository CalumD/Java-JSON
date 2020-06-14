package core;

import exceptions.JSONKeyException;
import exceptions.JSONParseException;
import exceptions.KeyInvalidException;

import java.util.ArrayList;
import java.util.List;

class KeyTape extends Tape<String> {

    private static final String VALID_KEY_ACCESSOR = "[ / <Object Key>";

    public KeyTape(String fullInput) {
        super(fullInput);
    }

    public List<String> parseAllElements() throws JSONKeyException {
        try {
            if (fullInput.endsWith(".")) {
                createParseErrorFromOffset(
                        fullInput.length() - 1,
                        "<object ref> / <end of key>",
                        "Trailing dot separator in key suggests more elements, but end of string was found."
                );
            }
            List<String> allElements = new ArrayList<>();
            while (currentIndex < fullInput.length()) {
                allElements.add(parseNextElement());
            }
            allElements.add("");
            return allElements;
        } catch (JSONParseException e) {
            throw new KeyInvalidException(e.getMessage(), e);
        }
    }

    @Override
    public String parseNextElement() {
        try {
            // Reach the first legitimate character.
            consumeWhiteSpace();

            // Figure out the next JSON type
            String nextElement = null;
            switch (checkCurrentChar()) {
                case '[':
                    currentIndex++;
                    try {
                        consumeWhiteSpace();
                        switch (checkCurrentChar()) {
                            case '\'':
                            case '"':
                            case '`':
                                // special object key accessor E.G. : "foo['bar bar']"
                                nextElement = parseObjectAccess();
                                break;
                            default:
                                nextElement = parseArrayAccess();
                                break;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        createParseErrorFromOffset(
                                -1,
                                "<key reference>",
                                "Reached end of key before resolving all parts. Are you missing a delimiter?"
                        );
                    }
                    break;
                case '.':
                    // In parsing, we should not start the key with a dot, despite it can follow other accessors.
                    createParseError(VALID_KEY_ACCESSOR, "Bad use of '.' separator in key.");
                    break;
                default:
                    nextElement = parseObjectAccess();
                    break;
            }
            return nextElement;
        } catch (JSONParseException e) {
            throw new KeyInvalidException(e.getMessage(), e);
        }
    }

    private String parseArrayAccess() {
        // Go until we find the closing square
        int startingIndex = getCurrentIndex();
        try {
            while (checkCurrentChar() != ']') {
                currentIndex++;
            }
        } catch (StringIndexOutOfBoundsException e) {
            createParseErrorFromOffset(
                    startingIndex - getCurrentIndex(),
                    "<positive integer>",
                    "Failed to parse array accessor in key. Reached end of key before delimiter ']' was found.");
        }

        return validateDeclaredArrayIndex(startingIndex);
    }

    protected String validateDeclaredArrayIndex(int startingIndex) {
        // Validate that the region is a valid integer
        int arrayIndex = -1;
        try {
            arrayIndex = Integer.parseInt(requestRegion(startingIndex, getCurrentIndex()));
        } catch (NumberFormatException e) {
            createParseErrorFromOffset(
                    startingIndex - getCurrentIndex(),
                    "<positive integer>",
                    "Failed to parse array accessor in key. Element was not a valid integer."
            );
        }

        // Validate that the integer is a positive number since you can access a list with negative numbers.
        if (arrayIndex < 0) {
            createParseErrorFromOffset(
                    startingIndex - getCurrentIndex(),
                    "<positive integer>",
                    "Array accessor in key was negative integer. Must be positive."
            );
        }

        // Consume terminating ']'
        consumeOne();
        if (currentIndex < fullInput.length()) {
            validateTrailingArrayElements();
        }

        return '[' + String.valueOf(arrayIndex);
    }

    private String parseObjectAccess() {
        final int startIndex = getCurrentIndex();

        switch (checkCurrentChar()) {
            case '\'':
                consumeUntilMatch("']");
                break;
            case '"':
                consumeUntilMatch("\"]");
                break;
            case '`':
                consumeUntilMatch("`]");
                break;
            default:
                try {
                    while (true) {
                        switch (checkCurrentChar()) {
                            case '.':
                                return '{' + requestRegion(startIndex, currentIndex++);
                            case '[':
                                return '{' + requestRegion(startIndex, currentIndex);
                            case ' ':
                                createParseError(
                                        "<valid key segment>",
                                        "Spaces are invalid in dot separated keys. " +
                                                "Use obj[\"key\"] notation if key contains spaces."
                                );
                                break;
                            default:
                                currentIndex++;
                                break;
                        }
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    return '{' + requestRegion(startIndex, currentIndex);
                }
        }
        final int SKIP_KEY_START = 1;
        int EXCLUDE_KEY_DELIMITERS = 2;
        if (currentIndex < fullInput.length() && consumeTrailingDotDelimiter()) {
            EXCLUDE_KEY_DELIMITERS++;
        }

        return '<' + requestRegion(startIndex + SKIP_KEY_START, getCurrentIndex() - EXCLUDE_KEY_DELIMITERS);
    }

    private void consumeUntilMatch(String delimiter) {
        while (!checkNextFragment(delimiter)) {
            currentIndex++;
        }
    }

    protected void validateTrailingArrayElements() {
        consumeWhiteSpace();
        try {
            switch (checkCurrentChar()) {
                case '.':
                    // Dot separators following arrays are okay, but we should consume.
                    consumeTrailingDotDelimiter();
                    break;
                case '[':
                    // Arrays following arrays are okay and we should leave for next parse.
                    break;
                default:
                    // If we haven't reached the end, and we are not followed by a . or [, then raise exception.
                    createParseError("[ / .", "Invalid continuation from array key.");
            }
        } catch (StringIndexOutOfBoundsException e) {
            // Ignore, won't need to include the end of the string in the next key element anyway.
        }
    }

    private boolean consumeTrailingDotDelimiter() {
        try {
            // If we are followed by a object separator, then consume that too
            if (checkCurrentChar() == '.') {
                consumeOne();
                return true;
            }
        } catch (StringIndexOutOfBoundsException e) {
            // Ignore, won't need to include the end of the string in the next key element anyway.
        }
        return false;
    }
}
