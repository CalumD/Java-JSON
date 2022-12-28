package com.clumd.projects.javajson.core;

import com.clumd.projects.javajson.exceptions.json.JsonParseException;
import com.clumd.projects.javajson.exceptions.json.KeyDifferentTypeException;

final class JSNumber extends Json {

    private Long myLongValue;
    private Double myDoubleValue;

    JSNumber(JsonTape parsingTape) throws JsonParseException {
        super(parsingTape);
        int numberStartIndex = parsingTape.getCurrentIndex();

        boolean foundEnd = false;
        boolean isFloating = false;

        while (!foundEnd) {
            try {
                switch (parsingTape.checkCurrentChar()) {
                    case '.', 'e', 'E':
                        isFloating = true;
                        // Fallthrough to still consume the char we just checked.
                    case '-', '+', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9':
                        parsingTape.consumeOne();
                        break;
                    default:
                        foundEnd = true;
                }
            } catch (IndexOutOfBoundsException e) {
                foundEnd = true;
            }
        }
        String numberString = parsingTape.requestRegion(numberStartIndex, parsingTape.getCurrentIndex());

        try {
            if (isFloating) {
                this.myDoubleValue = Double.parseDouble(numberString);
                jsType = JSType.DOUBLE;
            } else {
                this.myLongValue = Long.parseLong(numberString);
                jsType = JSType.LONG;
            }
        } catch (NumberFormatException e) {
            throw parsingTape.createParseError("<number>",
                    e.getMessage().replaceFirst("For input string", "Invalid number format"));
        }
    }

    @Override
    public Number getValue() {
        if (myLongValue == null) {
            return myDoubleValue;
        } else {
            return myLongValue;
        }
    }

    @Override
    public double getDouble() {
        if (jsType.equals(JSType.LONG)) {
            throw new KeyDifferentTypeException("This number is a long, not a double.");
        }
        return myDoubleValue;
    }

    @Override
    public long getLong() {
        if (jsType.equals(JSType.DOUBLE)) {
            throw new KeyDifferentTypeException("This number is a double, not a long.");
        }
        return myLongValue;
    }


    @Override
    public String asString(int depth) {
        return myLongValue == null
                ? String.valueOf(myDoubleValue)
                : String.valueOf(myLongValue);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }

        if (myLongValue != null) {
            if (other instanceof JSNumber jsNum) {
                return myLongValue.equals(jsNum.myLongValue);
            } else if (other instanceof Long) {
                return myLongValue.equals(other);
            }
        }
        if (myDoubleValue != null) {
            if (other instanceof JSNumber jsNum) {
                return myDoubleValue.equals(jsNum.myDoubleValue);
            } else if (other instanceof Double) {
                return myDoubleValue.equals(other);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return myLongValue == null
                ? Double.hashCode(myDoubleValue)
                : Long.hashCode(myLongValue);
    }
}
