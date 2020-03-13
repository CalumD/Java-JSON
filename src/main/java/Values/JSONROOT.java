package Values;

import Core.IJsonObject;
import Exceptions.JSONParseException;

public abstract class JSONROOT implements IJsonObject {

    long fragmentSize = -1;
    int currentFragmentIndex = 0;

    JSONROOT(String jsonFragment) throws JSONParseException {
        parse(jsonFragment);
    }

    //CREATION/////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public JSON createFromString(String jsonFragment) throws JSONParseException {
        return new JSObject("", jsonFragment, true);
    }

    abstract void parse(String jsonFragment) throws JSONParseException;


    //PARSING NEW//////////////////////////////////////////////////////////////////////////////////////////////////////////
    JSON parseNextElement(char idChar, String keyAtElement, String nextKey,
                          int currentFragmentIndex, String jsonFragment) throws JSONParseException {
        JSON nextElement;

        //find out the next element based on the json fragment
        switch (idChar) {
            //we are seeing a new object
            case '{':
                nextElement = new JSObject(
                        keyAtElement + (nextKey.startsWith("[") ? "" : ".") + nextKey,
                        jsonFragment.substring(currentFragmentIndex), false);
                break;
            //we are seeing a new array
            case '[':
                //adjust for object references
                if (myType == JSType.ARRAY) {
                    nextElement = new JSArray(keyAtElement + nextKey,
                            jsonFragment.substring(currentFragmentIndex), false);
                } else {
                    nextElement = new JSArray(keyAtElement + "." + nextKey,
                            jsonFragment.substring(currentFragmentIndex), false);
                }
                break;
            //new strings
            case '"':
            case '\'':
                nextElement = new JSString(keyAtElement,
                        jsonFragment.substring(currentFragmentIndex), false);
                break;
            //must be something else basic
            default:
                idChar = jsonFragment.substring(currentFragmentIndex, currentFragmentIndex + 1)
                        .charAt(0);

                if ((idChar >= '0' && idChar <= '9') || idChar == '-') {
                    nextElement = new JSNumber(keyAtElement,
                            jsonFragment.substring(currentFragmentIndex), false);
                } else {
                    nextElement = new JSBoolean(keyAtElement,
                            jsonFragment.substring(currentFragmentIndex), false);
                }
                break;
        }

        return nextElement;
    }

    String sanitiseFragment(String jsonFragment) throws JSONParseException {

        StringBuilder strippedJSON = new StringBuilder();

        boolean
                inDoubleQuote = false,
                inSingleQuote = false,
                inComment = false,
                inComment2 = false,
                exitComment2 = false;
        int
                curlyCount = 0,
                squareCount = 0,
                slashcount = 0;

        //check that all brackets and quotes match up
        for (char c : jsonFragment.toCharArray()) {

            //ignore content in comments
            if (inComment) {
                if (c == '\n') {
                    inComment = false;
                    slashcount = 0;
                }
            } else if (inComment2) {
                if (c == '*') {
                    exitComment2 = true;
                } else if (c == '/' && exitComment2) {
                    inComment2 = false;
                } else {
                    exitComment2 = false;
                }
            } else {
                switch (c) {
                    case '/':
                        //check entry into single line comment
                        if (!inSingleQuote && !inDoubleQuote) {
                            slashcount++;
                            if (slashcount == 2) {
                                inComment = true;
                            }
                            continue;
                        }
                        break;
                    case '*':
                        //check entry into multiple line comment
                        if (!inSingleQuote && !inDoubleQuote) {
                            if (slashcount > 0) {
                                inComment2 = true;
                            }
                            continue;
                        }
                        break;
                    case '"':
                        inDoubleQuote = !inDoubleQuote;
                        break;
                    case '\'':
                        inSingleQuote = !inSingleQuote;
                        break;
                    case '{':
                        curlyCount++;
                        break;
                    case '}':
                        curlyCount--;
                        break;
                    case '[':
                        squareCount++;
                        break;
                    case ']':
                        squareCount--;
                        break;
                    case ' ':
                    case '\n':
                        if (inSingleQuote || inDoubleQuote) {
                            strippedJSON.append(c);
                        }
                        slashcount = 0;
                        continue;
                }
                slashcount = 0;
                strippedJSON.append(c);
            }
        }
        if (inDoubleQuote) {
            throw new JSONParseException("JSON Has mismatched <\"> opening quote");
        }
        if (inSingleQuote) {
            throw new JSONParseException("JSON Has mismatched <'> opening quote");
        }
        if (curlyCount != 0) {
            throw new JSONParseException(
                    "The input has mismatched curly brackets, wont attempt to parse.");
        }
        if (squareCount != 0) {
            throw new JSONParseException(
                    "The input has mismatched square brackets, wont attempt to parse.");
        }

        return strippedJSON.toString();
    }

}
