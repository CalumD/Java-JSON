package Values;

import Exceptions.JSONParseException;

public class JSONTape extends Tape<JSON> {

    static final String VALID_JSON = "{ / [ / \" / <number> / <boolean> ";

    public JSONTape(String fullInput) {
        super(fullInput);
    }

    public JSON parseNextElement() {
        //Sanity Check
        if (fullInput == null || fullInput.length() == 0) {
            throw new JSONParseException("You cannot create json from nothing. Input was "
                    + (fullInput == null ? "null." : "empty."));
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

    @Override
    protected void consumeWhiteSpace() {
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
}
