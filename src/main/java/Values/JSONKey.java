package Values;

import Exceptions.JSONParseException;

import java.util.List;

class JSONKey {

    private final List<String> callChain;

    JSONKey(String key) throws JSONParseException {
        // Sanity Check
        if (key == null) {
            throw new JSONParseException("Key cannot be null");
        }

        // Consume any leading/trailing spaces
        int start = 0, stop = key.length() - 1;
        while (key.charAt(start) == ' ') {
            start++;
        }
        while (key.charAt(stop) == ' ') {
            stop--;
        }
        stop++;
        if (start != 0 || stop != key.length()) {
            key = key.substring(start, stop);
        }

        // Parse out the key
        callChain = new KeyTape(key).parseAllElements();
    }
}
