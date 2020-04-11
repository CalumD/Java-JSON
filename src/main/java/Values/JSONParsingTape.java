package Values;

import Exceptions.JSONParseException;

class JSONParsingTape {

    public static final int DEFAULT_PARSE_ERROR_CONTEXT_SIZE = 10;
    public static final String DEFAULT_PARSE_ERROR_CONTEXT_SYMBOL = "_";

    private final String fullString;
    private int currentIndex = 0;

    JSONParsingTape(String fullJSONString) {
        this.fullString = fullJSONString;
    }


    char checkCurrentChar() {
        return checkCharAt(0);
    }

    char checkNextChar() {
        return checkCharAt(1);
    }

    char checkPreviousChar() {
        return checkCharAt(-1);
    }

    private char checkCharAt(int offsetToCurrent) {
        return fullString.charAt(currentIndex + offsetToCurrent);
    }

    boolean checkNextFragment(String fragment, boolean consumeIfMatches) {
        boolean matches = fragment.equals(fullString.substring(currentIndex, fragment.length()));
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

    JSON parseNextElement() {
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
                createParseError("{/[/<number>/<boolean>/\"");
        }
        return nextElement;
    }

    private void consumeWhiteSpace() {
        while (true) {
            switch (checkCurrentChar()) {
                case ' ':
                case '\n':
                case '\r':
                case '\t':
                    currentIndex++;
                    break;
                default:
                    return;
            }
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

        // Check if we are far enough into the string to just show a snippet of "up-to here" code.
        if (currentIndex > DEFAULT_PARSE_ERROR_CONTEXT_SIZE) {
            gotFragment += fullString.substring(currentIndex - DEFAULT_PARSE_ERROR_CONTEXT_SIZE, currentIndex);
        } else {
            gotFragment = fullString.substring(0, currentIndex);
        }
        gotFragment += DEFAULT_PARSE_ERROR_CONTEXT_SYMBOL;

        // Count lines til here:
        int lineCount = 0;
        for (int charIndex = 0; charIndex <= currentIndex; charIndex++) {
            if (fullString.charAt(charIndex) == '\n') {
                lineCount++;
            }
        }

        // Throw the exception
        throw new JSONParseException(customErrorMessage
                + "\nLine: " + lineCount
                + "\nGot: " + gotFragment + ",  Expected: " + expectedFragment
        );
    }

    void createParseError(String expectedFragment) {
        createParseError(
                expectedFragment,
                "Unexpected symbol found in JSON while parsing."
        );
    }
}
