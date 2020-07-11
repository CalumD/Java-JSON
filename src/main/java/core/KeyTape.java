package core;

import exceptions.JSONKeyException;
import exceptions.KeyInvalidException;

import java.util.ArrayList;
import java.util.List;

class KeyTape extends Tape<String, JSONKeyException> {

    private static final String VALID_KEY_ACCESSOR = "[ / <Object Key>";
    private int elementsInKeyWithSpaces = 0, elementsParsed = 0;

    public KeyTape(String fullInput) {
        super(fullInput);
    }

    public List<String> parseAllElements() throws JSONKeyException {
        if (fullInput.endsWith(".")) {
            throw createParseErrorFromOffset(
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
    }

    @Override
    public String parseNextElement() {
        // Since we output keys from Json objects without safety padding round keys with spaces, we should
        // probably be lenient with the first key here.
        if (elementsParsed++ > 0 && elementsInKeyWithSpaces > 0) {
            // Otherwise no spaces allowed in regular object keys.
            throw createParseErrorFromOffset(-1,
                    "<valid key segment>",
                    "Spaces are invalid in dot separated keys. " +
                            "Use obj[\"key\"] notation if key contains spaces."
            );
        }
        // Reach the first legitimate character.
        consumeWhiteSpace();

        // Figure out the next JSON type
        String nextElement;
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
                            nextElement = parseObjectAccess(true);
                            break;
                        default:
                            nextElement = parseArrayAccess();
                            break;
                    }
                    break;
                } catch (IndexOutOfBoundsException e) {
                    throw createParseErrorFromOffset(
                            -1,
                            "<key reference>",
                            "Reached end of key before resolving all parts. Are you missing a delimiter?"
                    );
                }
            case '.':
                // In parsing, we should not start the key with a dot, despite it can follow other accessors.
                throw createParseError(VALID_KEY_ACCESSOR, "Bad use of '.' separator in key");
            default:
                nextElement = parseObjectAccess(false);
                break;
        }
        return nextElement;
    }

    @Override
    protected JSONKeyException newTypedException(String message) {
        return new KeyInvalidException(message);
    }

    private String parseArrayAccess() {
        // Go until we find the closing square
        int startingIndex = getCurrentIndex();
        try {
            while (checkCurrentChar() != ']') {
                currentIndex++;
            }
        } catch (StringIndexOutOfBoundsException e) {
            throw createParseErrorFromOffset(
                    startingIndex - getCurrentIndex(),
                    "<positive integer>",
                    "Failed to parse array accessor in key. Reached end of key before delimiter ']' was found.");
        }

        return validateDeclaredArrayIndex(startingIndex);
    }

    protected String validateDeclaredArrayIndex(int startingIndex) {
        // Validate that the region is a valid integer
        int arrayIndex;
        try {
            arrayIndex = Integer.parseInt(requestRegion(startingIndex, getCurrentIndex()));
        } catch (NumberFormatException e) {
            throw createParseErrorFromOffset(
                    startingIndex - getCurrentIndex(),
                    "<positive integer>",
                    "Failed to parse array accessor in key. Element was not a valid integer."
            );
        }

        // Validate that the integer is a positive number since you can access a list with negative numbers.
        if (arrayIndex < 0) {
            throw createParseErrorFromOffset(
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

    private String parseObjectAccess(boolean enteredAdvancedObjectAccessSafely) {
        final int startIndex = getCurrentIndex();
        int endOfAdvancedObjectAccess;

        switch (checkCurrentChar()) {
            case '\'':
                endOfAdvancedObjectAccess = consumeUntilMatchEndOfAdvancedObjectAccess(enteredAdvancedObjectAccessSafely, "'");
                break;
            case '"':
                endOfAdvancedObjectAccess = consumeUntilMatchEndOfAdvancedObjectAccess(enteredAdvancedObjectAccessSafely, "\"");
                break;
            case '`':
                endOfAdvancedObjectAccess = consumeUntilMatchEndOfAdvancedObjectAccess(enteredAdvancedObjectAccessSafely, "`");
                break;
            default:
                try {
                    while (true) {
                        switch (checkCurrentChar()) {
                            case '.':
                                // Next key is for an object - which can safely follow this object.
                                String nextKey = '{' + requestRegion(startIndex, currentIndex++);
                                if (currentIndex >= fullInput.length()) {
                                    throw createParseErrorFromOffset(
                                            -1,
                                            "<object ref> / <end of key>",
                                            "Trailing dot separator in key suggests more elements, but end of string was found."
                                    );
                                }
                                return nextKey;
                            case '[':
                                // Next key is for an array / advanced object accessor - which can safely follow this object.
                                return '{' + requestRegion(startIndex, currentIndex);
                            case ' ':
                                elementsInKeyWithSpaces++;
                                // Deliberate fallthrough for now as we still want to increase the current index
                            default:
                                // Regular part of a key's name
                                currentIndex++;
                                break;
                        }
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    return '{' + requestRegion(startIndex, currentIndex);
                }
        }
        final int SKIP_KEY_START = 1;
        if (currentIndex < fullInput.length() && consumeTrailingDotDelimiter()) {
            endOfAdvancedObjectAccess++;
        }
        String keyRegion = requestRegion(startIndex + SKIP_KEY_START, getCurrentIndex() - endOfAdvancedObjectAccess);
        if (keyRegion.length() == 0) {
            throw createParseError("<Valid JSON Object Key>", "You cannot address a JSON object with an empty key.");
        }
        return '<' + keyRegion;
    }

    private int consumeUntilMatchEndOfAdvancedObjectAccess(boolean enteredAdvancedObjectAccessSafely, String delimiter) {
        if (!enteredAdvancedObjectAccessSafely) {
            throw createParseErrorFromOffset(0, "<valid object key>",
                    "Complex keys require square bracket delimiters in addition. (e.g. [`key`])");
        }
        boolean consuming = true;
        int closingQuote = 0;
        while (consuming) {
            while (!checkNextFragment(delimiter)) {
                currentIndex++;
            }
            closingQuote = currentIndex - 1;
            consumeWhiteSpace();
            if (checkNextFragment("]")) {
                consuming = false;
            }
        }
        return (currentIndex - closingQuote);
    }

    protected void validateTrailingArrayElements() {
        consumeWhiteSpace();
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
                throw createParseError("[ / .", "Invalid continuation from array key");
        }
    }

    private boolean consumeTrailingDotDelimiter() {
        // If we are followed by a object separator, then consume that too
        if (checkCurrentChar() == '.') {
            consumeOne();
            return true;
        }
        return false;
    }
}
