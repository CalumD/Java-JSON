package core;

import api.IJson;
import api.IJsonBuilder;
import api.JSONParser;
import exceptions.BuildException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class JSONBuilderTest {

    @Test
    public void shouldCreateWithStandardConstructor() {
        JSONBuilder builder = new JSONBuilder();
        assertEquals("{}", builder.toString());
    }

    @Test
    public void addBoolean() {
        assertEquals("{\"value\":true}", new JSONBuilder().addBoolean("value", true).toString());
    }

    @Test
    public void addLong() {
        assertEquals("{\"value\":1}", new JSONBuilder().addLong("value", 1L).toString());
    }

    @Test
    public void addDouble() {
        assertEquals("{\"value\":1.0}", new JSONBuilder().addDouble("value", 1.0).toString());
    }

    @Test
    public void addString() {
        assertEquals("{\"value\":\"str\"}", new JSONBuilder().addString("value", "str").toString());
    }

    @Test
    public void addObject() {
        IJsonBuilder subObject = new JSONBuilder();
        subObject.addLong("subKey", 5L);
        assertEquals("{\"value\":{\"subKey\":5}}", new JSONBuilder().addBuilderBlock("value", subObject).toString());
    }

    @Test
    public void addArray() {
        assertEquals(
                "{\"value\":[1,2]}",
                new JSONBuilder()
                        .addLong("value[]", 1)
                        .addLong("value[]", 2)
                        .toString());
    }

    @Test
    public void stringInAnArrayToStringsCorrectly() {
        assertEquals(
                "{\"key\":[\"value\"]}",
                new JSONBuilder()
                        .addString("key[]", "value")
                        .toString());
    }

    @Test
    public void cannotAddWithEmptyKey() {
        try {
            new JSONBuilder().addBoolean("", false);
            fail("The previous method call should have thrown an exception.");
        } catch (BuildException exception) {
            assertEquals("The minimum wrapper for this IJsonBuilder is a JSON object," +
                    " you must provide at least one valid key for your value.", exception.getMessage());
        }
    }

    @Test
    public void tryToMakeTheBaseValueANonObject() {
        try {
            new JSONBuilder().addBoolean("obj..key2", false);
            fail("The previous method call should have thrown an exception.");
        } catch (BuildException exception) {
            assertEquals("Cannot add new value with invalid key.", exception.getMessage());
        }
    }

    @Test
    public void addingANonJSONBuilderAsABlockShouldFail() {
        try {
            new JSONBuilder().addBuilderBlock("obj", (JSONBuilder) null);
            fail("The previous method call should have thrown an exception.");
        } catch (BuildException exception) {
            assertEquals(
                    "This implementation of an IJSONBuilder only accepts JSONBuilder as a builder block value.",
                    exception.getMessage()
            );
        }
    }


    @Test
    public void addWithBasicName() {
        assertEquals("{\"key\":1}", JSONBuilder.builder().addLong("key", 1).toString());
    }

    @Test
    public void addToArrayWithAnonReference() {
        assertEquals("{\"key\":[1,2]}",
                JSONBuilder.builder()
                        .addLong("key[]", 1)
                        .addLong("key[]", 2).toString());
    }

    @Test
    public void addToRegularObject() {
        assertEquals("{\"key\":{\"obj\":1}}", JSONBuilder.builder().addLong("key.obj", 1).toString());
    }

    @Test
    public void addToNamedEndOfArray() {
        assertEquals("{\"key\":[1,2]}",
                JSONBuilder.builder()
                        .addLong("key[0]", 1)
                        .addLong("key[1]", 2).toString());
    }

    @Test
    public void tryToAddBeyondEndOfNamedArray() {
        try {
            JSONBuilder.builder()
                    .addLong("key[0]", 1)
                    .addLong("key[4]", 1);
            fail("The previous method call should have thrown an exception.");
        } catch (BuildException e) {
            assertEquals("4 not found on element: key", e.getMessage());
        }
    }

    @Test
    public void ensureWhenAddingBeyondTheArrayAndWeHaveAnAnonArrayTheErrorMessageDoesntPrintAppend() {
        try {
            JSONBuilder.builder()
                    .addLong("key[][8]", 1);
            fail("The previous method call should have thrown an exception.");
        } catch (BuildException e) {
            assertEquals("8 not found on element: key[]", e.getMessage());
        }
    }

    @Test
    public void addUsingFancyObjectName() {
        assertEquals("{\"k ..e.. y\":1}", JSONBuilder.builder().addLong("['k ..e.. y']", 1).toString());
    }

    @Test
    public void addUsingAnonThenNamedArray() {
        assertEquals("{\"key\":[[1],[1]]}", JSONBuilder.builder()
                .addLong("key[][0]", 1)
                .addLong("key[][0]", 1)
                .toString());
    }

    @Test
    public void addUsingAnonThenAnonArray() {
        assertEquals("{\"key\":[[1],[2],[3]]}", JSONBuilder.builder()
                .addLong("key[][]", 1)
                .addLong("key[][]", 2)
                .addLong("key[][]", 3)
                .toString());
    }

    @Test
    public void addUsingNamedThenAnonArray() {
        assertEquals("{\"key\":[[1,2,3]]}", JSONBuilder.builder()
                .addLong("key[0][]", 1)
                .addLong("key[0][]", 2)
                .addLong("key[0][]", 3)
                .toString());
    }

    @Test
    public void tryToAddMoreButPartIsAPrimitive() {
        try {
            JSONBuilder.builder()
                    .addLong("key[]", 1)
                    .addLong("key[0].SubObject", 2);
            fail("The previous method call should have thrown an exception.");
        } catch (BuildException e) {
            assertEquals("SubObject is not a valid accessor on element: key[0]", e.getMessage());
        }
    }

    @Test
    public void tryToAppendToANamedArrayElementButThatArrayElementIsAnObject() {
        try {
            JSONBuilder.builder()
                    .addBuilderBlock("key[]", new JSONBuilder().addString("key", "Im an Object"))
                    .addString("key[0]", "hey");
            fail("The previous method call should have thrown an exception.");
        } catch (BuildException e) {
            assertEquals("<Anonymous Key> is not a valid accessor on element: key[0]", e.getMessage());
        }
    }

    @Test
    public void addingToAnExistingKeyWillOverwrite() {
        assertEquals("{\"key\":15}",
                JSONBuilder.builder()
                        .addLong("key", 1)
                        .addLong("key", 15)
                        .toString());
    }

    @Test
    public void tryToAddToDeepObjectPath() {
        assertEquals("{\"would\":{\"you\":{\"look\":[[[{\"at\":{\"this\":{\"w i l d\":{\"path\":{\"though\":1}}}}}]]]}}}",
                JSONBuilder.builder()
                        .addLong("would.you.look[][0][].at.this['w i l d'][\"path\"].though", 1).toString()
        );
    }

    @Test
    public void testBuilderPatternWithSubBuilderObject() {
        IJson builder = JSONBuilder.builder()
                .addBoolean("bool", true)
                .addLong("long", 1L)
                .addDouble("dub", 1.0D)
                .addString("string", "String")
                .addBuilderBlock("subBuilder",
                        JSONBuilder.builder()
                                .addString("string", "I am in a sub Builder")
                                .addDouble("dub2", 1.1)
                )
                .addBuilderBlock("['I used to be Json']", JSONParser.parse("true"))
                .build();
        assertEquals(
                "{" +
                        "\"dub\":1.0," +
                        "\"I used to be Json\":{\"value\":true}," +
                        "\"bool\":true," +
                        "\"string\":\"String\"," +
                        "\"long\":1," +
                        "\"subBuilder\":" +
                        "{" +
                        "\"string\":\"I am in a sub Builder\"," +
                        "\"dub2\":1.1" +
                        "}" +
                        "}", builder.asString());
    }

    @Test
    public void testBuilderPatternWithCorrectKeyMapping() {
        IJson builder = JSONBuilder.builder()
                .addBoolean("bool", true)
                .addLong("long", 1L)
                .addDouble("dub", 1.0D)
                .addString("string", "String")

                .addBuilderBlock("subBuilder",
                        JSONBuilder.builder()
                                .addString("string", "I am in a sub Builder")
                                .addDouble("dub2", 1.1)
                )
                .addBoolean("['I used to be Json']", JSONParser.parse("true").getBoolean())
                .build();
        assertEquals(
                "{" +
                        "\"dub\":1.0," +
                        "\"I used to be Json\":true," +
                        "\"bool\":true," +
                        "\"string\":\"String\"," +
                        "\"long\":1," +
                        "\"subBuilder\":" +
                        "{" +
                        "\"string\":\"I am in a sub Builder\"," +
                        "\"dub2\":1.1" +
                        "}" +
                        "}", builder.asString());
    }

    @Test
    public void convertFromJSONBoolean() {
        assertEquals("{\"value\":true}", new JSONBuilder().convertFromJSON(JSONParser.parse("true")).toString());
    }

    @Test
    public void convertFromJSONLong() {
        assertEquals("{\"value\":1}", new JSONBuilder().convertFromJSON(JSONParser.parse("1")).toString());
    }

    @Test
    public void convertFromJSONDouble() {
        assertEquals("{\"value\":1.0}", new JSONBuilder().convertFromJSON(JSONParser.parse("1.0")).toString());
    }

    @Test
    public void convertFromJSONString() {
        assertEquals("{\"value\":\"str\"}", new JSONBuilder().convertFromJSON(JSONParser.parse("\"str\"")).toString());
    }

    @Test
    public void convertFromJSONArray() {
        assertEquals("{\"value\":[1,2]}", new JSONBuilder().convertFromJSON(JSONParser.parse("[1,2]")).toString());
    }

    @Test
    public void convertFromJSONObject() {
        assertEquals("{\"key\":true}", new JSONBuilder().convertFromJSON(JSONParser.parse("{'key':true}")).toString());
    }

    @Test
    public void convertFromComplexJSONObject() {
        IJsonBuilder builder = new JSONBuilder().convertFromJSON(JSONParser.parse(
                "{\"dub\":1.0," +
                        "\"I used to be Json\":true," +
                        "\"bool\":true," +
                        "\"string\":\"String\"," +
                        "\"long\":1," +
                        "\"subBuilder\":" +
                        "{" +
                        "\"string\":\"I am in a sub Builder\"," +
                        "\"dub2\":1.1" +
                        "}" +
                        "}"));
        assertEquals(
                "{\"I used to be Json\":true," +
                        "\"bool\":true," +
                        "\"dub\":1.0," +
                        "\"long\":1," +
                        "\"string\":\"String\"," +
                        "\"subBuilder\":" +
                        "{\"dub2\":1.1," +
                        "\"string\":\"I am in a sub Builder\"" +
                        "}" +
                        "}", builder.toString());
    }

    @Test
    public void builderConvertFromJSONThenBackAgainResultsInSameOutput() {
        fail("Yet to implement this test (Maybe use the file for schema testing).");
    }
}