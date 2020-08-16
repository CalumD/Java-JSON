package core;

import api.IJson;
import api.IJsonBuilder;
import api.JsonParser;
import exceptions.BuildException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class JsonBuilderTest {

    @Test
    public void shouldCreateWithStandardConstructor() {
        JsonBuilder builder = new JsonBuilder();
        assertEquals("{}", builder.toString());
    }

    @Test
    public void addBoolean() {
        assertEquals("{\"value\":true}", new JsonBuilder().addBoolean("value", true).toString());
    }

    @Test
    public void addLong() {
        assertEquals("{\"value\":1}", new JsonBuilder().addLong("value", 1L).toString());
    }

    @Test
    public void addDouble() {
        assertEquals("{\"value\":1.0}", new JsonBuilder().addDouble("value", 1.0).toString());
    }

    @Test
    public void addString() {
        assertEquals("{\"value\":\"str\"}", new JsonBuilder().addString("value", "str").toString());
    }

    @Test
    public void addObject() {
        IJsonBuilder subObject = new JsonBuilder();
        subObject.addLong("subKey", 5L);
        assertEquals("{\"value\":{\"subKey\":5}}", new JsonBuilder().addBuilderBlock("value", subObject).toString());
    }

    @Test
    public void addArray() {
        assertEquals(
                "{\"value\":[1,2]}",
                new JsonBuilder()
                        .addLong("value[]", 1)
                        .addLong("value[]", 2)
                        .toString());
    }

    @Test
    public void stringInAnArrayToStringsCorrectly() {
        assertEquals(
                "{\"key\":[\"value\"]}",
                new JsonBuilder()
                        .addString("key[]", "value")
                        .toString());
    }

    @Test
    public void cannotAddWithEmptyKey() {
        try {
            new JsonBuilder().addBoolean("", false);
            fail("The previous method call should have thrown an exception.");
        } catch (BuildException exception) {
            assertEquals("The minimum wrapper for this IJsonBuilder is a JSON object," +
                    " you must provide at least one valid key for your value.", exception.getMessage());
        }
    }

    @Test
    public void tryToMakeTheBaseValueANonObject() {
        try {
            new JsonBuilder().addBoolean("obj..key2", false);
            fail("The previous method call should have thrown an exception.");
        } catch (BuildException exception) {
            assertEquals("Cannot add new value with invalid key.", exception.getMessage());
        }
    }

    @Test
    public void addingANonJSONBuilderAsABlockShouldFail() {
        try {
            new JsonBuilder().addBuilderBlock("obj", (JsonBuilder) null);
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
        assertEquals("{\"key\":1}", JsonBuilder.builder().addLong("key", 1).toString());
    }

    @Test
    public void addToArrayWithAnonReference() {
        assertEquals("{\"key\":[1,2]}",
                JsonBuilder.builder()
                        .addLong("key[]", 1)
                        .addLong("key[]", 2).toString());
    }

    @Test
    public void addToRegularObject() {
        assertEquals("{\"key\":{\"obj\":1}}", JsonBuilder.builder().addLong("key.obj", 1).toString());
    }

    @Test
    public void addToNamedEndOfArray() {
        assertEquals("{\"key\":[1,2]}",
                JsonBuilder.builder()
                        .addLong("key[0]", 1)
                        .addLong("key[1]", 2).toString());
    }

    @Test
    public void tryToAddBeyondEndOfNamedArray() {
        try {
            JsonBuilder.builder()
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
            JsonBuilder.builder()
                    .addLong("key[][8]", 1);
            fail("The previous method call should have thrown an exception.");
        } catch (BuildException e) {
            assertEquals("8 not found on element: key[]", e.getMessage());
        }
    }

    @Test
    public void addUsingFancyObjectName() {
        assertEquals("{\"k ..e.. y\":1}", JsonBuilder.builder().addLong("['k ..e.. y']", 1).toString());
    }

    @Test
    public void addUsingAnonThenNamedArray() {
        assertEquals("{\"key\":[[1],[1]]}", JsonBuilder.builder()
                .addLong("key[][0]", 1)
                .addLong("key[][0]", 1)
                .toString());
    }

    @Test
    public void addUsingAnonThenAnonArray() {
        assertEquals("{\"key\":[[1],[2],[3]]}", JsonBuilder.builder()
                .addLong("key[][]", 1)
                .addLong("key[][]", 2)
                .addLong("key[][]", 3)
                .toString());
    }

    @Test
    public void addUsingNamedThenAnonArray() {
        assertEquals("{\"key\":[[1,2,3]]}", JsonBuilder.builder()
                .addLong("key[0][]", 1)
                .addLong("key[0][]", 2)
                .addLong("key[0][]", 3)
                .toString());
    }

    @Test
    public void tryToAddMoreButPartIsAPrimitive() {
        try {
            JsonBuilder.builder()
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
            JsonBuilder.builder()
                    .addBuilderBlock("key[]", new JsonBuilder().addString("key", "Im an Object"))
                    .addString("key[0]", "hey");
            fail("The previous method call should have thrown an exception.");
        } catch (BuildException e) {
            assertEquals("<Anonymous Key> is not a valid accessor on element: key[0]", e.getMessage());
        }
    }

    @Test
    public void addingToAnExistingKeyWillOverwrite() {
        assertEquals("{\"key\":15}",
                JsonBuilder.builder()
                        .addLong("key", 1)
                        .addLong("key", 15)
                        .toString());
    }

    @Test
    public void tryToAddToDeepObjectPath() {
        assertEquals("{\"would\":{\"you\":{\"look\":[[[{\"at\":{\"this\":{\"w i l d\":{\"path\":{\"though\":1}}}}}]]]}}}",
                JsonBuilder.builder()
                        .addLong("would.you.look[][0][].at.this['w i l d'][\"path\"].though", 1).toString()
        );
    }

    @Test
    public void testBuilderPatternWithSubBuilderObject() {
        IJson builder = JsonBuilder.builder()
                .addBoolean("bool", true)
                .addLong("long", 1L)
                .addDouble("dub", 1.0D)
                .addString("string", "String")
                .addBuilderBlock("subBuilder",
                        JsonBuilder.builder()
                                .addString("string", "I am in a sub Builder")
                                .addDouble("dub2", 1.1)
                )
                .addBuilderBlock("['I used to be Json']", JsonParser.parse("true"))
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
        IJson builder = JsonBuilder.builder()
                .addBoolean("bool", true)
                .addLong("long", 1L)
                .addDouble("dub", 1.0D)
                .addString("string", "String")

                .addBuilderBlock("subBuilder",
                        JsonBuilder.builder()
                                .addString("string", "I am in a sub Builder")
                                .addDouble("dub2", 1.1)
                )
                .addBoolean("['I used to be Json']", JsonParser.parse("true").getBoolean())
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
        assertEquals("{\"value\":true}", new JsonBuilder().convertFromJSON(JsonParser.parse("true")).toString());
    }

    @Test
    public void convertFromJSONLong() {
        assertEquals("{\"value\":1}", new JsonBuilder().convertFromJSON(JsonParser.parse("1")).toString());
    }

    @Test
    public void convertFromJSONDouble() {
        assertEquals("{\"value\":1.0}", new JsonBuilder().convertFromJSON(JsonParser.parse("1.0")).toString());
    }

    @Test
    public void convertFromJSONString() {
        assertEquals("{\"value\":\"str\"}", new JsonBuilder().convertFromJSON(JsonParser.parse("\"str\"")).toString());
    }

    @Test
    public void convertFromJSONArray() {
        assertEquals("{\"value\":[1,2]}", new JsonBuilder().convertFromJSON(JsonParser.parse("[1,2]")).toString());
    }

    @Test
    public void convertFromJSONObject() {
        assertEquals("{\"key\":true}", new JsonBuilder().convertFromJSON(JsonParser.parse("{'key':true}")).toString());
    }

    @Test
    public void convertFromComplexJSONObject() {
        IJsonBuilder builder = new JsonBuilder().convertFromJSON(JsonParser.parse(
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
        String jsonContent = "{\n" +
                "  \"$schema\": \"http://json-schema.org/draft-07/schema\",\n" +
                "  \"$id\": \"\",\n" +
                "  \"title\": \"CS Schema\",\n" +
                "  \"description\": \"Th\",\n" +
                "  \"type\": \"object\",\n" +
                "  \"escape\\\"key\": \"escape\\\\object\",\n" +
                "  \"properties\": {\n" +
                "    \"system\": {\n" +
                "      \"description\": \"r advanced/future work/development.\",\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"name\": {\n" +
                "          \"description\": \" 'name' config.\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"maxLength\": 100\n" +
                "        },\n" +
                "        \"description\": {\n" +
                "          \"description\": \"This.\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"maxLength\": 10000\n" +
                "        },\n" +
                "        \"rolesDirectory\": {\n" +
                "          \"description\": \"This '/'.\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"maxLength\": 10000\n" +
                "        },\n" +
                "        \"roles\": {\n" +
                "          \"description\": \"This.\",\n" +
                "          \"type\": \"array\",\n" +
                "          \"items\": {\n" +
                "            \"$ref\": \"#/definitions/roleDeclaration\",\n" +
                "            \"v N e s t e d!\": [[0,1], {}, \"\", 1.0, true]\n" +
                "          },\n" +
                "          \"uniqueItems\": true,\n" +
                "          \"additionalItems\": false\n" +
                "        },\n" +
                "        \"machines\": {\n" +
                "          \"description\": \"Them.\",\n" +
                "          \"type\": \"array\",\n" +
                "          \"items\": {\n" +
                "            \"$ref\": \"#/definitions/machine\"\n" +
                "          },\n" +
                "          \"additionalItems\": false\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\n" +
                "        \"rolesDirectory\",\n" +
                "        \"roles\",\n" +
                "        \"machines\"\n" +
                "      ],\n" +
                "      \"additionalProperties\": false\n" +
                "    }\n" +
                "  },\n" +
                "  \"required\": [\n" +
                "    \"system\"\n" +
                "  ],\n" +
                "  \"definitions\": {\n" +
                "    \"roleDeclaration\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"name\": {\n" +
                "          \"description\": \"This\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"maxLength\": 100\n" +
                "        },\n" +
                "        \"description\": {\n" +
                "          \"description\": \"This.\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"maxLength\": 5000\n" +
                "        },\n" +
                "        \"id\": {\n" +
                "          \"description\": \"This.\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"minLength\": 1,\n" +
                "          \"maxLength\": 1000\n" +
                "        },\n" +
                "        \"path\": {\n" +
                "          \"description\": \"This 'system.rolesDirectory' property.\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"minLength\": 1,\n" +
                "          \"maxLength\": 10000\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\n" +
                "        \"id\",\n" +
                "        \"path\"\n" +
                "      ],\n" +
                "      \"additionalProperties\": false\n" +
                "    },\n" +
                "    \"machine\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"name\": {\n" +
                "          \"description\": \"This.\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"minLength\": 1,\n" +
                "          \"maxLength\": 1000\n" +
                "        },\n" +
                "        \"address\": {\n" +
                "          \"description\": \"This\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"anyOf\": [\n" +
                "            {\n" +
                "              \"format\": \"hostname\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"format\": \"ipv4\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"format\": \"ipv6\"\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"arp\": {\n" +
                "          \"description\": \"This\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"anyOf\": [\n" +
                "            {\n" +
                "              \"format\": \"hostname\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"format\": \"ipv4\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"format\": \"ipv6\"\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"components\": {\n" +
                "          \"description\": \"This\",\n" +
                "          \"type\": \"array\",\n" +
                "          \"items\": {\n" +
                "            \"$ref\": \"#/definitions/roleUse\"\n" +
                "          },\n" +
                "          \"additionalItems\": false\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\n" +
                "        \"name\",\n" +
                "        \"address\",\n" +
                "        \"components\"\n" +
                "      ],\n" +
                "      \"additionalProperties\": false\n" +
                "    },\n" +
                "    \"roleUse\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"role\": {\n" +
                "          \"description\": \"This.\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"oneOf\": [\n" +
                "            {\n" +
                "              \"$ref\": \"#/definitions/roleDeclaration/properties/id\"\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"custom\": {\n" +
                "          \"description\": \"This.\",\n" +
                "          \"type\": \"object\",\n" +
                "          \"$schema\": \"http://json-schema.org/draft-07/schema\",\n" +
                "          \"properties\": {}\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\n" +
                "        \"role\"\n" +
                "      ],\n" +
                "      \"additionalProperties\": false\n" +
                "    }\n" +
                "  }\n" +
                "}";

        IJson parsedJSON = JsonParser.parse(jsonContent);
        assertEquals(
                JsonBuilder.builder().convertFromJSON(
                        JsonBuilder.builder().convertFromJSON(parsedJSON).convertToJSON()
                ).convertToJSON(),
                parsedJSON
        );
    }

    @Test
    public void checkWeCantAddToAnonymousBaseArray() {
        try {
            JsonBuilder.builder().addLong("[]", 1L).build();
            fail("The previous method should have thrown an exception.");
        } catch (BuildException e) {
            assertEquals("<Anonymous Key> is not a valid accessor on element: []", e.getMessage());
        }
    }

    @Test
    public void addAnonymousKeyNameButProperlyEscaped() {
        assertEquals("{\"[]\":1}", JsonBuilder.builder().addLong("['[]']", 1L).toString());
    }
}