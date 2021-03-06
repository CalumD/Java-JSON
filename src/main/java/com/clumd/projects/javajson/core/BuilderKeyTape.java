package com.clumd.projects.javajson.core;

final class BuilderKeyTape extends KeyTape {

    BuilderKeyTape(String fullInput) {
        super(fullInput);
    }

    @Override
    protected String validateDeclaredArrayIndex(int startingIndex) {
        // Validate that the region is a valid integer
        int arrayIndex;
        try {
            String indexRegion = requestRegion(startingIndex, getCurrentIndex());
            if (indexRegion.equals("")) {
                terminateArrayAccessorInChain();
                return '[' + "append";
            }
            arrayIndex = Integer.parseInt(indexRegion);
        } catch (NumberFormatException e) {
            throw createParseErrorFromOffset(
                    startingIndex - getCurrentIndex(),
                    "<positive integer>",
                    "Failed to parse array accessor in key. Element was not a valid integer."
            );
        }

        // Validate that the integer is a positive number since you can access a list with negative numbers.
        if (arrayIndex < 0) {
            throw createParseErrorFromOffset(
                    startingIndex - getCurrentIndex(),
                    "<positive integer>",
                    "Array accessor in key was negative integer. Must be positive."
            );
        }

        terminateArrayAccessorInChain();
        return '[' + String.valueOf(arrayIndex);
    }

    private void terminateArrayAccessorInChain() {
        // Consume terminating ']'
        consumeOne();
        if (currentIndex < fullInput.length()) {
            validateTrailingArrayElements();
        }
    }
}
