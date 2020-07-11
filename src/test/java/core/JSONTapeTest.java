package core;

import exceptions.JSONParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class JSONTapeTest {


    @Test
    public void consumeSlashStarNormal() {
        assertEquals("{}", new JSONTape("{/*\n\n\ncomment text\n\n\n*/}").parseNextElement().asString());
    }

    @Test
    public void consumeSlashStarOkayAtStart() {
        assertEquals("{}", new JSONTape("/*comment*/\n{}").parseNextElement().asString());
    }

    @Test
    public void consumeSlashStarOkayAtEnd() {
        assertEquals("{}", new JSONTape("{}\n/*comment*/").parseNextElement().asString());
    }

    @Test
    public void consumeSlashStarDoesntAffectInString() {
        assertEquals("{\"Key\":\"String /*comment*/ Value\"}", new JSONTape("{\"Key\": \"String /*comment*/ Value\"}").parseNextElement().asString());
    }

    @Test
    public void consumeSlashStarMidLine() {
        assertEquals("{\"key\":[1,2,3]}", new JSONTape("{\"key\": [1,/*textInline*/2, 3]}").parseNextElement().asString());
    }

    @Test
    public void consumeMultilineSlashStarDoesntAffectInString() {
        assertEquals("{\"Key\":\"Str\ning /*co\nmment*/ Val\nue\"}", new JSONTape("{\"Key\": \"Str\ning /*co\nmment*/ Val\nue\"}").parseNextElement().asString());
    }

    @Test
    public void consumeSlashStarMustMatchExactlySlashStar() {
        assertEquals("{}", new JSONTape("{/*text*/}").parseNextElement().asString());
        try {
            new JSONTape("{/ *text*/}").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (JSONParseException e) {
            assertEquals("Unexpected symbol found while parsing.\n" +
                    "Line: 1\n" +
                    "Reached: {_\n" +
                    "Expected: / or *", e.getMessage());
        }
    }

    @Test
    public void consumeSlashStarMustMatchExactlyStarSlash() {
        assertEquals("{}", new JSONTape("{/*text*/}").parseNextElement().asString());
        try {
            new JSONTape("{/*text* /}").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (JSONParseException e) {
            assertEquals("Reached the end of the JSON input before parsing was complete. Are you missing a terminating delimiter?", e.getMessage());
        }
    }

    @Test
    public void consumeDoubleSlashOkayAtStart() {
        assertEquals("{}", new JSONTape("//comment\n{}").parseNextElement().asString());
    }

    @Test
    public void consumeDoubleSlashOkayAtEnd() {
        assertEquals("{}", new JSONTape("{}\n//comment").parseNextElement().asString());
    }

    @Test
    public void consumeDoubleSlashNoNewLine() {
        try {
            new JSONTape("{//}").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (JSONParseException e) {
            assertEquals("Reached the end of the JSON input before parsing was complete. Are you missing a terminating delimiter?", e.getMessage());
        }
    }

    @Test
    public void consumeDoubleSlashIsNotInterpretedMidString() {
        assertEquals("{\"Key\":\"String // comment Value\"}", new JSONTape("{\"Key\": \"String // comment Value\"}").parseNextElement().asString());
    }

    @Test
    public void consumeDoubleSlashOkay() {
        assertEquals("{}", new JSONTape("{//Some Comment Text\n}").parseNextElement().asString());
    }

    @Test
    public void consumeDoubleSlashDoesntWorkWithSpace() {
        try {
            new JSONTape("{/ /Some comment text}").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (JSONParseException e) {
            assertEquals("Unexpected symbol found while parsing.\n" +
                    "Line: 1\n" +
                    "Reached: {_\n" +
                    "Expected: / or *", e.getMessage());
        }
    }

    @Test
    public void consumeHashOkayAtStart() {
        assertEquals("{}", new JSONTape("#comment\n{}").parseNextElement().asString());
    }

    @Test
    public void consumeHashOkayAtEnd() {
        assertEquals("{}", new JSONTape("{}\n#comment").parseNextElement().asString());
    }

    @Test
    public void consumeHashNoNewLine() {
        try {
            new JSONTape("{#}").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (JSONParseException e) {
            assertEquals("Reached the end of the JSON input before parsing was complete. Are you missing a terminating delimiter?", e.getMessage());
        }
    }

    @Test
    public void consumeHashOkay() {
        assertEquals("{}", new JSONTape("{#Some Comment text\n}").parseNextElement().asString());
    }

    @Test
    public void consumeHashWorksWithSpace() {
        try {
            new JSONTape("{# Will work with space since only a single character}").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (JSONParseException e) {
            assertEquals("Reached the end of the JSON input before parsing was complete. Are you missing a terminating delimiter?", e.getMessage());
        }
    }

    @Test
    public void consumeHashIsNotInterpretedMidString() {
        assertEquals("{\"Key\":\"String #comment Value\"}", new JSONTape("{\"Key\": \"String #comment Value\"}").parseNextElement().asString());
    }

    @Test
    public void invalidCommentIdentifiersAreNotAllowed() {
        try {
            new JSONTape("/#").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (JSONParseException e) {
            assertEquals("Unexpected symbol found while parsing.\n" +
                    "Line: 1\n" +
                    "Reached: _\n" +
                    "Expected: / or *", e.getMessage());
        }
    }

    @Test
    public void totallyInvalidUnexpectedCommentString() {
        try {
            new JSONTape("--Comment\n{}").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (JSONParseException e) {
            assertEquals("Invalid number format: \"--\"\n" +
                    "Line: 1\n" +
                    "Reached: --_\n" +
                    "Expected: <number>", e.getMessage());
        }
    }

    @Test
    public void emptyInputNotValid() {
        try {
            new JSONTape("").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (JSONParseException e) {
            assertEquals("You cannot create something from nothing. Input was empty.", e.getMessage());
        }
    }

    @Test
    public void onlyCommentsShouldNotBeAllowed() {
        try {
            new JSONTape("//Comment but no object\n").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (JSONParseException e) {
            assertEquals("Reached the end of the JSON input before parsing was complete. Are you missing a terminating delimiter?", e.getMessage());
        }
    }

    @Test
    public void consumeWhiteSpace() {
        assertEquals("{\"key \n\r\t\":[1,\"string\"]}", new JSONTape(" \n\r\t{ \n\r\t\"key \n\r\t\" \n\r\t: \n\r\t[ \n\r\t1 \n\r\t, \n\r\t\"string\" \n\r\t] \n\r\t} \n\r\t").parseNextElement().asString());
    }

    @Test
    public void returnsCorrectObjectBoolean() {
        assertEquals(JSType.BOOLEAN, new JSONTape("True").parseNextElement().getDataType());
        assertEquals(JSType.BOOLEAN, new JSONTape("true").parseNextElement().getDataType());
        assertEquals(JSType.BOOLEAN, new JSONTape("false").parseNextElement().getDataType());
        assertEquals(JSType.BOOLEAN, new JSONTape("False").parseNextElement().getDataType());
    }

    @Test
    public void returnsCorrectObjectObject() {
        assertEquals(JSType.OBJECT, new JSONTape("{}").parseNextElement().getDataType());
    }

    @Test
    public void returnsCorrectObjectArray() {
        assertEquals(JSType.ARRAY, new JSONTape("[]").parseNextElement().getDataType());
    }

    @Test
    public void returnsCorrectObjectString() {
        assertEquals(JSType.STRING, new JSONTape("\"string\"").parseNextElement().getDataType());
        assertEquals(JSType.STRING, new JSONTape("'string'").parseNextElement().getDataType());
    }

    @Test
    public void returnsCorrectObjectNumber() {
        assertEquals(JSType.LONG, new JSONTape("-1").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JSONTape("-1.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JSONTape("+1").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JSONTape("+1.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JSONTape("1").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JSONTape("1.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JSONTape("2").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JSONTape("2.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JSONTape("3").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JSONTape("3.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JSONTape("4").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JSONTape("4.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JSONTape("5").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JSONTape("5.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JSONTape("6").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JSONTape("6.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JSONTape("7").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JSONTape("7.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JSONTape("8").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JSONTape("8.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JSONTape("9").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JSONTape("9.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JSONTape("0").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JSONTape("0.0").parseNextElement().getDataType());
    }

    @Test
    public void handleMultiObjectParsing() {
        JSONTape tape = new JSONTape("{}{\"key2\":2}['object3']");

        assertEquals("{}", tape.parseNextElement().asString());
        assertEquals("{\"key2\":2}", tape.parseNextElement().asString());
        assertEquals("[\"object3\"]", tape.parseNextElement().asString());
    }

    @Test
    public void handleMultiObjectsShouldNotBeCommaSeparated() {
        JSONTape tape = new JSONTape("{}{\"key2\":2},['object3']");

        assertEquals("{}", tape.parseNextElement().asString());
        assertEquals("{\"key2\":2}", tape.parseNextElement().asString());
        try {
            tape.parseNextElement().asString();
            fail("The previous method call should have thrown an exception.");
        } catch (JSONParseException e) {
            assertEquals("Unexpected symbol found while parsing.\n" +
                    "Line: 1\n" +
                    "Reached: {}{\"key2\":2}_\n" +
                    "Expected: { / [ / \" / <number> / <boolean> ", e.getMessage());
        }
    }

    @Test
    public void longMultilineObjectWithExceptionCausesNiceExplanation() {
        String jsonContent = "{\"key\":\"This is a nice long string which should give enough " +
                "breathing room for a nice error Message" +
                "\nWhy dont we even split it over multiple " +
                "\n lines to make the output more interesting. The error is trailing comma for end of object\", }";
        try {
            new JSONTape(jsonContent).parseNextElement();
            fail("The previous line should have thrown an error");
        } catch (JSONParseException e) {
            assertEquals("Comma suggests more object elements, but object terminates.\n" +
                    "Line: 3\n" +
                    "Reached: ...is trailing comma for end of object\", _\n" +
                    "Expected: { / [ / \" / <number> / <boolean> ", e.getMessage());
        }
    }
}