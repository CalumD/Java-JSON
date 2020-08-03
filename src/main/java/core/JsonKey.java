package core;

import exceptions.json.KeyDifferentTypeException;
import exceptions.json.KeyInvalidException;
import exceptions.json.KeyNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class JsonKey {

    private final List<String> callChain;
    private int currentCallChainIndex = 0;

    JsonKey(String key, boolean useBuilderKeyTape) throws KeyInvalidException {
        // Sanity Check
        if (key == null) {
            throw new KeyInvalidException("Key cannot be null");
        }

        if (!key.equals("")) {
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
        } else {
            callChain = new ArrayList<>();
            callChain.add("");
            return;
        }

        // Parse out the key
        if (useBuilderKeyTape) {
            callChain = new BuilderKeyTape(key).parseAllElements();
        } else {
            callChain = new KeyTape(key).parseAllElements();
        }
    }

    String getNextKey() {
        try {
            return callChain.get(currentCallChainIndex++);
        } catch (IndexOutOfBoundsException e) {
            throw new KeyNotFoundException("End of Key reached. Have already traversed the whole hierarchy.");
        }
    }

    List<String> getAllKeys() {
        return callChain;
    }

    KeyNotFoundException createKeyNotFoundException() {
        return new KeyNotFoundException(validateKeyStepForErrorMessage(" not found on element: "));
    }

    KeyDifferentTypeException createKeyDifferentTypeException() {
        return new KeyDifferentTypeException(validateKeyStepForErrorMessage(" is not a valid accessor on element: "));
    }

    private String validateKeyStepForErrorMessage(String message) {
        if (currentCallChainIndex <= 0) {
            return "Invalid usage of a JSONKey tape - trying to create error before collecting values.";
        }
        if (callChain.get(currentCallChainIndex - 1).equals("")) {
            return "<Anonymous Key>"
                    + message
                    + createUpToString();
        }
        return (callChain.get(--currentCallChainIndex).equals("[append"))
                ? "[]" + message + createUpToString()
                : callChain.get(currentCallChainIndex).substring(1) + message + createUpToString();
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
                upToString.append(callChainElement.equals("[append") ? "[" : callChainElement).append(']');
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
