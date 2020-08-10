package core;

import api.IJson;
import exceptions.json.JsonParseException;

public final class JsonTape extends Tape<IJson, JsonParseException> {

    static final String VALID_JSON = "{ / [ / \" / <number> / <boolean> ";

    public JsonTape(String fullInput) {
        super(fullInput);
    }

    public IJson parseNextElement() {

        // Reach the first legitimate character.
        consumeWhiteSpace();

        // Figure out the next JSON type
        Json nextElement = null;
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
            case '`':
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
            default:
                createParseError(VALID_JSON);
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
            throw new JsonParseException(
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
