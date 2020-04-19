package Values;

import Exceptions.JSONParseException;
import Exceptions.KeyInvalidException;

import java.util.ArrayList;
import java.util.List;

class KeyTape extends Tape<String> {

    private static final String VALID_KEY_ACCESSOR = "[ / <Object Key>";

    public KeyTape(String fullInput) {
        super(fullInput);
    }

    public List<String> parseAllElements() {
        try {
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
        return null;
    }

    public List<String> parseAllElements() {
        return new ArrayList<>();
    }
}
