package Values;

import Exceptions.JSONParseException;

public class JSONParsingTape {

    static final int DEFAULT_PARSE_ERROR_CONTEXT_SIZE = 20;
    static final String DEFAULT_PARSE_ERROR_CONTEXT_SYMBOL = "_";
    static final String VALID_JSON = "{ / [ / \" / <number> / <boolean> ";

    private final String fullString;
    private int currentIndex = 0;

    public JSONParsingTape(String fullJSONAsString) {
        this.fullString = fullJSONAsString;
    }

    char checkCurrentChar() {
        return checkCharAtOffsetFromCurrent(0);
    }

    char checkNextChar() {
        return checkCharAtOffsetFromCurrent(1);
    }

    private char checkCharAtOffsetFromCurrent(int offsetToCurrent) {
        return checkCharAt(currentIndex + offsetToCurrent);
    }

    private char checkCharAt(int absoluteOffset) {
        return fullString.charAt(absoluteOffset);
    }

    boolean checkNextFragment(String fragment, boolean consumeIfMatches) {
        boolean matches = fragment.equals(fullString.substring(currentIndex, currentIndex + fragment.length()));
        if (matches && consumeIfMatches) {
            currentIndex += fragment.length();
        }
        return matches;
    }

    char consumeOne() {
        return checkCharAt(currentIndex++);
    }

    String requestRegion(int fromHere, int toHere) {
        return fullString.substring(fromHere, toHere);
    }

    int getCurrentIndex() {
        return currentIndex;
    }

    public JSON parseNextElement() {
        // Reach the first legitimate character.
        consumeWhiteSpace();
        if (fullString == null || fullString.length() == 0) {
            throw new JSONParseException("You cannot create json from nothing.");
        }
        JSON nextElement = null;
        switch (checkCurrentChar()) {
            case 't':
            case 'T':
            case 'f':
            case 'F':
                nextElement = new JSBoolean(this);
                break;
            case '{':
                nextElement = new JSObject(this);
                break;
            case '[':
                nextElement = new JSArray(this);
                break;
            case '"':
            case '\'':
                nextElement = new JSString(this);
                break;
            case '-':
            case '+':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                nextElement = new JSNumber(this);
                break;
            case '/':
            case '#':
                consumeComment();
            default:
                createParseError(VALID_JSON);
        }
        return nextElement;
    }

    void consumeWhiteSpace() {
        try {
            while (true) {
                switch (checkCurrentChar()) {
                    case ' ':
                    case '\n':
                    case '\r':
                    case '\t':
                        currentIndex++;
                        break;
                    case '/':
                    case '#':
                        consumeComment();
                    default:
                        return;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new JSONParseException(
                    "Reached the end of the JSON input before parsing was complete. Are you missing a terminating delimiter?"
            );
        }
    }

    private void consumeComment() {
        switch (checkNextChar()) {

            //   <-- this Comment
            // # <-- or this comment
            case '/':
            case '#':
                while (true) {
                    if (consumeOne() == '\n') {
                        return;
                    }
                }

                /* <-- This comment --> */
            case '*':
                while (true) {
                    if (consumeOne() == '*' && consumeOne() == '/') {
                        return;
                    }
                }

                // Invalid Comment Type
            default:
                createParseError("/ or *");
        }
    }

    void createParseError(String expectedFragment, String customErrorMessage) {
        String gotFragment = "...";
        int reached = currentIndex == 0 ? 0 : currentIndex - 1;

        // Check if we are far enough into the string to just show a snippet of "up-to here" code.
        if (currentIndex > DEFAULT_PARSE_ERROR_CONTEXT_SIZE) {
            gotFragment += fullString.substring(currentIndex - DEFAULT_PARSE_ERROR_CONTEXT_SIZE, reached);
        } else {
            gotFragment = fullString.substring(0, reached);
        }
        gotFragment += DEFAULT_PARSE_ERROR_CONTEXT_SYMBOL;

        // Count lines til here:
        int lineCount = 0;
        for (int charIndex = 0; charIndex < reached; charIndex++) {
            if (fullString.charAt(charIndex) == '\n') {
                lineCount++;
            }
        }

        // Throw the exception
        // TODO remove the sout.
        System.out.println(new JSONParseException(customErrorMessage
                + "\nLine: " + lineCount
                + "\nGot: " + gotFragment + "  Expected: " + expectedFragment
        ).getMessage());
        throw new JSONParseException(customErrorMessage
                + "\nLine: " + lineCount
                + "\nGot: " + gotFragment + "  Expected: " + expectedFragment
        );
    }

    void createParseError(String expectedFragment) {
        createParseError(
                expectedFragment,
                "Unexpected symbol found in JSON while parsing."
        );
    }
}
