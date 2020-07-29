package core;

import exceptions.JsonException;

public abstract class Tape<T, E extends JsonException> {

    private static final int DEFAULT_PARSE_ERROR_CONTEXT_SIZE = 30;
    private static final String DEFAULT_PARSE_ERROR_CONTEXT_SYMBOL = "_";
    private static final String DEFAULT_PARSE_ERROR_MESSAGE = "Unexpected symbol found while parsing.";

    protected final String fullInput;
    protected int currentIndex = 0;

    Tape(String fullInput) {
        this.fullInput = fullInput;
        //Sanity Check
        if (fullInput == null || fullInput.length() == 0) {
            throw newTypedException("You cannot create something from nothing. Input was "
                    + (fullInput == null ? "null." : "empty."));
        }
    }

    abstract T parseNextElement();

    int getCurrentIndex() {
        return currentIndex;
    }

    char checkCurrentChar() {
        return checkCharAtOffsetFromCurrent(0);
    }

    char checkNextChar() {
        return checkCharAtOffsetFromCurrent(1);
    }

    char consumeOne() {
        return checkCharAt(currentIndex++);
    }

    boolean checkNextFragment(String fragment) {
        if (fragment.length() > fullInput.substring(currentIndex).length()) {
            return false;
        }
        boolean matches = fragment.equals(fullInput.substring(currentIndex, currentIndex + fragment.length()));
        if (matches) {
            currentIndex += fragment.length();
        }
        return matches;
    }

    String requestRegion(int fromHere, int toHere) {
        return fullInput.substring(fromHere, toHere);
    }

    E createParseErrorFromOffset(int relativeOffset, String expectedFragment, String customErrorMessage) {
        currentIndex += relativeOffset;
        return createParseError(expectedFragment, customErrorMessage);
    }

    E createParseError(String expectedFragment, String customErrorMessage) {
        String gotFragment = "...";

        // Check if we are far enough into the string to just
        if (currentIndex > DEFAULT_PARSE_ERROR_CONTEXT_SIZE) {
            gotFragment += getNonSpaceSnippetForException();
        } else {
            gotFragment = fullInput.substring(0, currentIndex);
        }
        gotFragment += DEFAULT_PARSE_ERROR_CONTEXT_SYMBOL;

        // Count lines til here:
        int lineCount = 1;
        for (int charIndex = 0; charIndex < currentIndex; charIndex++) {
            if (fullInput.charAt(charIndex) == '\n') {
                lineCount++;
            }
        }

        // Throw the exception
        return newTypedException(customErrorMessage
                + "\nLine: " + lineCount
                + "\nReached: " + gotFragment
                + "\nExpected: " + expectedFragment
        );
    }

    void createParseError(String expectedFragment) {
        throw createParseError(expectedFragment, DEFAULT_PARSE_ERROR_MESSAGE);
    }

    protected abstract E newTypedException(String message);

    protected void consumeWhiteSpace() {
        try {
            while (true) {
                switch (fullInput.charAt(currentIndex)) {
                    case ' ':
                    case '\n':
                    case '\r':
                    case '\t':
                        currentIndex++;
                        break;
                    default:
                        return;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw newTypedException(
                    "Reached end of input before parsing was complete. Are you missing a terminating delimiter?"
            );
        }
    }

    private char checkCharAtOffsetFromCurrent(int relativeOffset) {
        return checkCharAt(currentIndex + relativeOffset);
    }

    private char checkCharAt(int absoluteOffset) {
        return fullInput.charAt(absoluteOffset);
    }

    private String getNonSpaceSnippetForException() {
        // Count back 20 'real' (non-space) characters to show a snippet of "up-to here" code.
        char currentChar;
        int snippetIndex, snippetLength;
        for (snippetIndex = currentIndex - 1, snippetLength = 0;
             ((snippetIndex > 0) && (snippetLength < DEFAULT_PARSE_ERROR_CONTEXT_SIZE));
             snippetIndex--, snippetLength++
        ) {
            currentChar = checkCharAt(snippetIndex);
            switch (currentChar) {
                case ' ':
                case '\n':
                case '\r':
                case '\t':
                    snippetLength--;
                    break;
            }
        }
        return fullInput.substring(snippetIndex, currentIndex);
    }
}
