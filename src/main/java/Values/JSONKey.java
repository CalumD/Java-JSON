package Values;

import Exceptions.KeyDifferentTypeException;
import Exceptions.KeyInvalidException;
import Exceptions.KeyNotFoundException;

import java.util.List;

class JSONKey {

    private final List<String> callChain;
    private final String fullKey;
    private int currentCallChainIndex = 0;

    JSONKey(String key) throws KeyInvalidException {
        // Sanity Check
        if (key == null) {
            throw new KeyInvalidException("Key cannot be null");
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
        fullKey = key;
        callChain = new KeyTape(fullKey).parseAllElements();
    }

    String getNextKey() {
        return callChain.get(currentCallChainIndex++);
    }

    void createKeyNotFoundException() throws KeyNotFoundException {
        createException(" not found on element: ");
    }

    void createKeyDifferentTypeException() throws KeyDifferentTypeException {
        createException(" is not a valid accessor on element: ");
    }

    private void createException(String message) {
        if (currentCallChainIndex == callChain.size()) {
            currentCallChainIndex--;
        }
        if (currentCallChainIndex == callChain.size()) {
            currentCallChainIndex--;
        }
        throw new KeyDifferentTypeException(
                callChain.get(currentCallChainIndex) + message
                        + fullKey
                        .substring(
                                0,
                                fullKey.indexOf(callChain.get(currentCallChainIndex))
                        )
        );
    }
}
