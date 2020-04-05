package Values;

import Exceptions.JSONParseException;

class JSONParsingTape {

    public static final int DEFAULT_PARSE_ERROR_CONTEXT_SIZE = 10;
    public static final String DEFAULT_PARSE_ERROR_CONTEXT_SYMBOL = "_";

    private String fullString;
    private int currentIndex;
    private JSON topLevelJSON;

    JSONParsingTape(String fullJSONString) {
        this.fullString = fullJSONString;
    }

    char currentChar() {
        return charAt(0);
    }
    char nextChar() {
        return charAt(1);
    }
    char previousChar() {
        return charAt(-1);
    }
    char charAt(int offsetToCurrent) {
        return fullString.charAt(currentIndex + offsetToCurrent);
    }

    void moveTapeHead(int byOffset) {
        currentIndex += byOffset;
    }
    char consume() {
        return charAt(currentIndex++);
    }
    String consume(int fragmentSize) {
        String fragment = fullString.substring(currentIndex, currentIndex + fragmentSize);
        moveTapeHead(fragmentSize);
        return fragment;
    }
    boolean checkNext(String fragment) {
        return fragment.equals(fullString.substring(currentIndex, fragment.length()));
    }


    private void consumeWhiteSpace() {
        boolean foundNext = false;
        while (!foundNext) {
            switch (currentChar()) {
                case ' ':
                case '\n':
                case '\r':
                case '\t':
                    currentIndex++;
                default:
                    foundNext = true;
            }
        }
    }
    void createParseError(String expectedFragment, String customErrorMessage) {
        String gotFragment = "...";

        if (currentIndex > DEFAULT_PARSE_ERROR_CONTEXT_SIZE) {
            gotFragment += fullString.substring(currentIndex - DEFAULT_PARSE_ERROR_CONTEXT_SIZE, currentIndex);
        } else {
            gotFragment = fullString.substring(0, currentIndex);
        }
        gotFragment += DEFAULT_PARSE_ERROR_CONTEXT_SYMBOL;

        throw new JSONParseException(customErrorMessage
                + "\nGot: " + gotFragment + ",  Expected: " + expectedFragment
        );
    }
    void createParseError(String expectedFragment) {
        createParseError(
                expectedFragment,
                "Unexpected symbol found in JSON while parsing."
        );
    }

    void parseFragment() {
        // Reach the first legitimate character.
        consumeWhiteSpace();
        if (fullString == null || fullString.length() == 0) {
            throw new JSONParseException("You cannot create json from null.");
        }
        JSON resultTopLevelJSON = null;
        switch (fullString.charAt(0)) {
            case 't':
            case 'T':
            case 'f':
            case 'F':
                resultTopLevelJSON = new JSBoolean(this);
                break;
            case '{':
                resultTopLevelJSON = new JSObject(this);
                break;
            case '[':
                resultTopLevelJSON = new JSArray(this);
                break;
            case '"':
            case '\'':
                resultTopLevelJSON = new JSString(this);
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
                resultTopLevelJSON = new JSNumber(this);
                break;
            default:
                createParseError("'/\"/{/[/<number>/<boolean>",
                        "You cannot start json with a {" + currentChar() + "}.");
        }
        topLevelJSON = resultTopLevelJSON;
    }
    JSON getJson() {
        if (topLevelJSON == null) {
            throw new JSONParseException("Failed to find a parsable fragment.");
        }
        return topLevelJSON;
    }
}
