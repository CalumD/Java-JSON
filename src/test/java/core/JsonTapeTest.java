package core;

import exceptions.json.JsonParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class JsonTapeTest {


    @Test
    public void consumeSlashStarNormal() {
        assertEquals("{}", new JsonTape("{/*\n\n\ncomment text\n\n\n*/}").parseNextElement().asString());
    }

    @Test
    public void consumeSlashStarOkayAtStart() {
        assertEquals("{}", new JsonTape("/*comment*/\n{}").parseNextElement().asString());
    }

    @Test
    public void consumeSlashStarOkayAtEnd() {
        assertEquals("{}", new JsonTape("{}\n/*comment*/").parseNextElement().asString());
    }

    @Test
    public void consumeSlashStarDoesntAffectInString() {
        assertEquals("{\"Key\":\"String /*comment*/ Value\"}", new JsonTape("{\"Key\": \"String /*comment*/ Value\"}").parseNextElement().asString());
    }

    @Test
    public void consumeSlashStarMidLine() {
        assertEquals("{\"key\":[1,2,3]}", new JsonTape("{\"key\": [1,/*textInline*/2, 3]}").parseNextElement().asString());
    }

    @Test
    public void consumeMultilineSlashStarDoesntAffectInString() {
        assertEquals("{\"Key\":\"Str\ning /*co\nmment*/ Val\nue\"}", new JsonTape("{\"Key\": \"Str\ning /*co\nmment*/ Val\nue\"}").parseNextElement().asString());
    }

    @Test
    public void consumeSlashStarMustMatchExactlySlashStar() {
        assertEquals("{}", new JsonTape("{/*text*/}").parseNextElement().asString());
        try {
            new JsonTape("{/ *text*/}").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Unexpected symbol found while parsing.\n" +
                    "Line: 1\n" +
                    "Reached: {_\n" +
                    "Expected: / or *", e.getMessage());
        }
    }

    @Test
    public void consumeSlashStarMustMatchExactlyStarSlash() {
        assertEquals("{}", new JsonTape("{/*text*/}").parseNextElement().asString());
        try {
            new JsonTape("{/*text* /}").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Reached the end of the JSON input before parsing was complete. Are you missing a terminating delimiter?", e.getMessage());
        }
    }

    @Test
    public void consumeDoubleSlashOkayAtStart() {
        assertEquals("{}", new JsonTape("//comment\n{}").parseNextElement().asString());
    }

    @Test
    public void consumeDoubleSlashOkayAtEnd() {
        assertEquals("{}", new JsonTape("{}\n//comment").parseNextElement().asString());
    }

    @Test
    public void consumeDoubleSlashNoNewLine() {
        try {
            new JsonTape("{//}").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Reached the end of the JSON input before parsing was complete. Are you missing a terminating delimiter?", e.getMessage());
        }
    }

    @Test
    public void consumeDoubleSlashIsNotInterpretedMidString() {
        assertEquals("{\"Key\":\"String // comment Value\"}", new JsonTape("{\"Key\": \"String // comment Value\"}").parseNextElement().asString());
    }

    @Test
    public void consumeDoubleSlashOkay() {
        assertEquals("{}", new JsonTape("{//Some Comment Text\n}").parseNextElement().asString());
    }

    @Test
    public void consumeDoubleSlashDoesntWorkWithSpace() {
        try {
            new JsonTape("{/ /Some comment text}").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Unexpected symbol found while parsing.\n" +
                    "Line: 1\n" +
                    "Reached: {_\n" +
                    "Expected: / or *", e.getMessage());
        }
    }

    @Test
    public void consumeHashOkayAtStart() {
        assertEquals("{}", new JsonTape("#comment\n{}").parseNextElement().asString());
    }

    @Test
    public void consumeHashOkayAtEnd() {
        assertEquals("{}", new JsonTape("{}\n#comment").parseNextElement().asString());
    }

    @Test
    public void consumeHashNoNewLine() {
        try {
            new JsonTape("{#}").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Reached the end of the JSON input before parsing was complete. Are you missing a terminating delimiter?", e.getMessage());
        }
    }

    @Test
    public void consumeHashOkay() {
        assertEquals("{}", new JsonTape("{#Some Comment text\n}").parseNextElement().asString());
    }

    @Test
    public void consumeHashWorksWithSpace() {
        try {
            new JsonTape("{# Will work with space since only a single character}").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Reached the end of the JSON input before parsing was complete. Are you missing a terminating delimiter?", e.getMessage());
        }
    }

    @Test
    public void consumeHashIsNotInterpretedMidString() {
        assertEquals("{\"Key\":\"String #comment Value\"}", new JsonTape("{\"Key\": \"String #comment Value\"}").parseNextElement().asString());
    }

    @Test
    public void invalidCommentIdentifiersAreNotAllowed() {
        try {
            new JsonTape("/#").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Unexpected symbol found while parsing.\n" +
                    "Line: 1\n" +
                    "Reached: _\n" +
                    "Expected: / or *", e.getMessage());
        }
    }

    @Test
    public void totallyInvalidUnexpectedCommentString() {
        try {
            new JsonTape("--Comment\n{}").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Invalid number format: \"--\"\n" +
                    "Line: 1\n" +
                    "Reached: --_\n" +
                    "Expected: <number>", e.getMessage());
        }
    }

    @Test
    public void emptyInputNotValid() {
        try {
            new JsonTape("").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("You cannot create something from nothing. Input was empty.", e.getMessage());
        }
    }

    @Test
    public void onlyCommentsShouldNotBeAllowed() {
        try {
            new JsonTape("//Comment but no object\n").parseNextElement();
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
            assertEquals("Reached the end of the JSON input before parsing was complete. Are you missing a terminating delimiter?", e.getMessage());
        }
    }

    @Test
    public void consumeWhiteSpace() {
        assertEquals("{\"key \n\r\t\":[1,\"string\"]}", new JsonTape(" \n\r\t{ \n\r\t\"key \n\r\t\" \n\r\t: \n\r\t[ \n\r\t1 \n\r\t, \n\r\t\"string\" \n\r\t] \n\r\t} \n\r\t").parseNextElement().asString());
    }

    @Test
    public void returnsCorrectObjectBoolean() {
        assertEquals(JSType.BOOLEAN, new JsonTape("True").parseNextElement().getDataType());
        assertEquals(JSType.BOOLEAN, new JsonTape("true").parseNextElement().getDataType());
        assertEquals(JSType.BOOLEAN, new JsonTape("false").parseNextElement().getDataType());
        assertEquals(JSType.BOOLEAN, new JsonTape("False").parseNextElement().getDataType());
    }

    @Test
    public void returnsCorrectObjectObject() {
        assertEquals(JSType.OBJECT, new JsonTape("{}").parseNextElement().getDataType());
    }

    @Test
    public void returnsCorrectObjectArray() {
        assertEquals(JSType.ARRAY, new JsonTape("[]").parseNextElement().getDataType());
    }

    @Test
    public void returnsCorrectObjectString() {
        assertEquals(JSType.STRING, new JsonTape("\"string\"").parseNextElement().getDataType());
        assertEquals(JSType.STRING, new JsonTape("'string'").parseNextElement().getDataType());
        assertEquals(JSType.STRING, new JsonTape("`string`").parseNextElement().getDataType());
    }

    @Test
    public void returnsCorrectObjectNumber() {
        assertEquals(JSType.LONG, new JsonTape("-1").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JsonTape("-1.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JsonTape("+1").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JsonTape("+1.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JsonTape("1").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JsonTape("1.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JsonTape("2").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JsonTape("2.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JsonTape("3").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JsonTape("3.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JsonTape("4").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JsonTape("4.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JsonTape("5").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JsonTape("5.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JsonTape("6").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JsonTape("6.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JsonTape("7").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JsonTape("7.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JsonTape("8").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JsonTape("8.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JsonTape("9").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JsonTape("9.0").parseNextElement().getDataType());
        assertEquals(JSType.LONG, new JsonTape("0").parseNextElement().getDataType());
        assertEquals(JSType.DOUBLE, new JsonTape("0.0").parseNextElement().getDataType());
    }

    @Test
    public void handleMultiObjectParsing() {
        JsonTape tape = new JsonTape("{}{\"key2\":2}['object3']");

        assertEquals("{}", tape.parseNextElement().asString());
        assertEquals("{\"key2\":2}", tape.parseNextElement().asString());
        assertEquals("[\"object3\"]", tape.parseNextElement().asString());
    }

    @Test
    public void handleMultiObjectsShouldNotBeCommaSeparated() {
        JsonTape tape = new JsonTape("{}{\"key2\":2},['object3']");

        assertEquals("{}", tape.parseNextElement().asString());
        assertEquals("{\"key2\":2}", tape.parseNextElement().asString());
        try {
            tape.parseNextElement().asString();
            fail("The previous method call should have thrown an exception.");
        } catch (JsonParseException e) {
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
            new JsonTape(jsonContent).parseNextElement();
            fail("The previous line should have thrown an error");
        } catch (JsonParseException e) {
            assertEquals("Comma suggests more object elements, but object terminates.\n" +
                    "Line: 3\n" +
                    "Reached: ...is trailing comma for end of object\", _\n" +
                    "Expected: { / [ / \" / <number> / <boolean> ", e.getMessage());
        }
    }
}