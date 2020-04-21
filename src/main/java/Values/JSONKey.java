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
        currentCallChainIndex--;
        throw new KeyNotFoundException(
                callChain.get(currentCallChainIndex).substring(1)
                        + " not found on element: "
                        + createUpToString()
        );
    }

    void createKeyDifferentTypeException() throws KeyDifferentTypeException {
        currentCallChainIndex--;
        throw new KeyDifferentTypeException(
                callChain.get(currentCallChainIndex).substring(1)
                        + " is not a valid accessor on element: "
                        + createUpToString()
        );
    }

    private String createUpToString() {
        StringBuilder upToString = new StringBuilder();
        for (int index = 0; index < currentCallChainIndex; index++) {
            if (callChain.get(index).startsWith("[")) {
                upToString.append(callChain.get(index)).append(']');
            } else {
                upToString.append('.').append(callChain.get(index).substring(1));
            }
        }
        if (upToString.length() == 0) {
            return "<base element>";
        }

        if (upToString.charAt(0) == '.') {
            upToString.deleteCharAt(0);
        }

        return upToString.toString();
    }
}
