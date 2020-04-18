package Values;

import Exceptions.JSONParseException;

public class JSONParsingTape {

    private static final int DEFAULT_PARSE_ERROR_CONTEXT_SIZE = 30;
    private static final String DEFAULT_PARSE_ERROR_CONTEXT_SYMBOL = "_";
    static final String VALID_JSON = "{ / [ / \" / <number> / <boolean> ";

    private final String fullString;
    private int currentIndex = 0;

    public JSONParsingTape(String fullJSONAsString) {
        this.fullString = fullJSONAsString;
    }

    public JSON parseNextElement() {
        //Sanity Check
        if (fullString == null || fullString.length() == 0) {
            throw new JSONParseException("You cannot create json from nothing. Input was "
                    + (fullString == null ? "null." : "empty."));
        }

        // Reach the first legitimate character.
        consumeWhiteSpace();

        // Figure out the next JSON type
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

    char checkCurrentChar() {
        return checkCharAtOffsetFromCurrent(0);
    }

    char checkNextChar() {
        return checkCharAtOffsetFromCurrent(1);
    }

    char consumeOne() {
        return checkCharAt(currentIndex++);
    }

    boolean checkNextFragment(String fragment) {
        return checkNextFragment(fragment, true);
    }

    boolean checkNextFragment(String fragment, boolean consumeIfMatches) {
        boolean matches = fragment.equals(fullString.substring(currentIndex, currentIndex + fragment.length()));
        if (matches && consumeIfMatches) {
            currentIndex += fragment.length();
        }
        return matches;
    }

    String requestRegion(int fromHere, int toHere) {
        return fullString.substring(fromHere, toHere);
    }

    int getCurrentIndex() {
        return currentIndex;
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
                        break;
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

    void createParseErrorFromOffset(int relativeOffset, String expectedFragment, String customErrorMessage) {
        currentIndex += relativeOffset;
        createParseError(expectedFragment, customErrorMessage);
    }

    void createParseError(String expectedFragment, String customErrorMessage) {
        String gotFragment = "...";

        // Check if we are far enough into the string to just
        if (currentIndex > DEFAULT_PARSE_ERROR_CONTEXT_SIZE) {
            gotFragment += getNonSpaceSnippet();
        } else {
            gotFragment = fullString.substring(0, currentIndex);
        }
        gotFragment += DEFAULT_PARSE_ERROR_CONTEXT_SYMBOL;

        // Count lines til here:
        int lineCount = 1;
        for (int charIndex = 0; charIndex < currentIndex; charIndex++) {
            if (fullString.charAt(charIndex) == '\n') {
                lineCount++;
            }
        }

        // Throw the exception
        throw new JSONParseException(customErrorMessage
                + "\nLine: " + lineCount
                + "\nGot: " + gotFragment
                + "\nExpected: " + expectedFragment
        );
    }

    void createParseError(String expectedFragment) {
        createParseError(
                expectedFragment,
                "Unexpected symbol found in JSON while parsing."
        );
    }

    private void consumeComment() {
        switch (checkCurrentChar()) {
            case '#':
                consumeUntilNewLine();
                break;
            case '/':
                switch (checkNextChar()) {
                    //   <-- this Comment
                    case '/':
                        consumeUntilNewLine();
                        break;

                    /*    This comment    */
                    case '*':
                        consumeUntilEndOfMultilineString();
                        break;

                    // Invalid Comment Type
                    default:
                        createParseError("/ or *");
                }
                break;
            default:
                throw new UnsupportedOperationException("Unsupported comment sequence.");
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

    private char checkCharAtOffsetFromCurrent(int relativeOffset) {
        return checkCharAt(currentIndex + relativeOffset);
    }

    private char checkCharAt(int absoluteOffset) {
        return fullString.charAt(absoluteOffset);
    }

    private String getNonSpaceSnippet() {
        // Count back 20 'real' (non-space) characters to show a snippet of "up-to here" code.
        char currentChar;
        int snippetIndex, snippetLength;
        for (snippetIndex = currentIndex - 1, snippetLength = 0;
             ((snippetIndex > 0) && (snippetLength < DEFAULT_PARSE_ERROR_CONTEXT_SIZE));
             snippetIndex--, snippetLength++
        ) {
            currentChar = checkCharAt(snippetIndex);
            switch (currentChar) {
                case ' ':
                case '\n':
                case '\r':
                case '\t':
                    snippetLength--;
                    break;
            }
        }
        return fullString.substring(snippetIndex, currentIndex);
    }
}
