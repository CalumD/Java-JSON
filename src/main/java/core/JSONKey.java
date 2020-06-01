package core;

import exceptions.KeyDifferentTypeException;
import exceptions.KeyInvalidException;
import exceptions.KeyNotFoundException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class JSONKey {

    private final List<String> callChain;
    private int currentCallChainIndex = 0;

    JSONKey(String key) throws KeyInvalidException {
        // Sanity Check
        if (key == null) {
            throw new KeyInvalidException("Key cannot be null");
        }

        final Set<Character> whitespaces = new HashSet<>(Arrays.asList(' ', '\n', '\r', '\t'));

        // Consume any leading/trailing spaces
        int start = 0, stop = key.length() - 1;
        while (whitespaces.contains(key.charAt(start))) {
            start++;
        }
        while (whitespaces.contains(key.charAt(stop))) {
            stop--;
        }
        stop++;
        if (start != 0 || stop != key.length()) {
            key = key.substring(start, stop);
        }

        // Parse out the key
        callChain = new KeyTape(key).parseAllElements();
    }

    String getNextKey() {
        try {
            return callChain.get(currentCallChainIndex++);
        } catch (IndexOutOfBoundsException e) {
            throw new KeyNotFoundException("End of Key reached. Have already traversed the whole hierarchy.");
        }
    }

    void createKeyNotFoundException() throws KeyNotFoundException {
        throw new KeyNotFoundException(
                callChain.get(--currentCallChainIndex).substring(1)
                        + " not found on element: "
                        + createUpToString()
        );
    }

    void createKeyDifferentTypeException() throws KeyDifferentTypeException {
        throw new KeyDifferentTypeException(
                callChain.get(--currentCallChainIndex).substring(1)
                        + " is not a valid accessor on element: "
                        + createUpToString()
        );
    }

    private String createUpToString() {
        if (currentCallChainIndex <= 0) {
            return "<base element>";
        }

        StringBuilder upToString = new StringBuilder();
        String callChainElement;
        for (int index = 0; index < currentCallChainIndex; index++) {
            callChainElement = callChain.get(index);
            if (callChainElement.startsWith("[")) {
                upToString.append(callChainElement).append(']');
            } else if (callChainElement.startsWith("{")) {
                upToString.append('.').append(callChainElement.substring(1));
            } else if (callChainElement.startsWith("<")) {
                upToString.append("[\"").append(callChainElement.substring(1).replaceAll("(?<!\\\\)\"", "\\\\\"")).append("\"]");
            }
        }

        if (upToString.charAt(0) == '.') {
            upToString.deleteCharAt(0);
        }

        return upToString.toString();
    }
}
