package Values;

import Exceptions.JSONParseException;

class JSONParsingTape {

    public static final int DEFAULT_PARSE_ERROR_CONTEXT_SIZE = 10;
    public static final String DEFAULT_PARSE_ERROR_CONTEXT_SYMBOL = "_";

    private String fullString;
    private int currentIndex;


    JSONParsingTape(String fullJSONString) {
        this.fullString = fullJSONString;
    }

    char charAt(int index) {
        return fullString.charAt(index);
    }

    char previousChar(int currentIndex) {
        return fullString.charAt(currentIndex-1);
    }

    void parse() {
//        if (jsonFragment == null || jsonFragment.length() == 0) {
//            throw new JSONParseException("You cannot create json from null.");
//        }
//        if (jsonFragment.equals("true") || jsonFragment.equals("True")
//                || jsonFragment.equals("false") || jsonFragment.equals("False")) {
//            return new JSBoolean(jsonFragment);
//        }
//        switch (jsonFragment.charAt(0)) {
//            case '{':
//                return new JSObject(jsonFragment);
//            case '[':
//                return new JSArray(jsonFragment);
//            case '"':
//            case '\'':
//                return new JSString(jsonFragment);
//            case '-':
//            case '+':
//            case '0':
//            case '1':
//            case '2':
//            case '3':
//            case '4':
//            case '5':
//            case '6':
//            case '7':
//            case '8':
//            case '9':
//                return new JSNumber(jsonFragment);
//            default:
//                throw new JSONParseException("You cannot start json with a {"
//                        + jsonFragment.charAt(0)
//                        + "}. Expected ''', '\"', '{', '[', <number>, <boolean>");
//        }
    }

    JSON getJson() {
        return new JSBoolean();
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
}
