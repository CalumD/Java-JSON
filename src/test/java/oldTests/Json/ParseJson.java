package oldTests.Json;

import api.JsonParser;
import exceptions.JsonParseException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ParseJson {

    //Array methods compact into string, so only need basic tests
    @Test
    public void array_parse_1() {
        assertThrows(JsonParseException.class, () -> {

            String[] test = new String[]{
                    "{",
                    "                    ",
                    "\"Invalid This Time\" ", //Missing the colon
                    "1234567890",
                    "}"
            };
            JsonParser.parse(test);
        });
    }

    @Test
    public void array_parse_2() throws JsonParseException {
        String[] test = new String[]{
                "{",
                "                    ",
                "\"Valid This Time\":",
                "1234567890",
                "}"
        };
        JsonParser.parse(test);
    }


    @Test
    public void array_parse_3() {
        assertThrows(JsonParseException.class, () -> {
            ArrayList<String> test = new ArrayList<>();
            test.add("{");
            test.add("                    ");
            test.add("\"Invalid This Time\" "); //Missing the colon
            test.add("1234567890");
            test.add("}");

            JsonParser.parse(test);
        });
    }

    @Test
    public void array_parse_4() throws JsonParseException {
        ArrayList<String> test = new ArrayList<>();
        test.add("{");
        test.add("                    ");
        test.add("\"Valid This Time\":");
        test.add("1234567890");
        test.add("}");

        JsonParser.parse(test);
    }


    //EMPTY
    @Test
    public void empty_parse_1() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("")
        );
    }

    @Test
    public void empty_parse_2() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{")
        );
    }

    @Test
    public void empty_parse_3() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("}")
        );
    }


    /////////////////////////////BASIC PARSE PASS  ------- FAILS ---------/////////////////////////////////////////////
    @Test
    public void basic_parse_1_1() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{[}")
        );
    }

    @Test
    public void basic_parse_1_2() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{]}")
        );
    }

    @Test
    public void basic_parse_1_3() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"a\":}")
        );
    }

    @Test
    public void basic_parse_1_4() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"a\"1}")
        );
    }

    @Test
    public void basic_parse_1_5() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"a:1}")
        );
    }

    @Test
    public void basic_parse_1_6() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"\":1}")
        );
    }

    @Test
    public void basic_parse_1_7() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{a\":1}")
        );
    }

    @Test
    public void basic_parse_1_8() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"a\":[}")
        );
    }

    @Test
    public void basic_parse_1_9() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"a\":]}")
        );
    }

    @Test
    public void basic_parse_1_10() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"a\":{}")
        );
    }

    @Test
    public void basic_parse_1_11() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"a\":}}")
        );
    }

    @Test
    public void basic_parse_1_12() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"a\":\"b\":{}}")
        );
    }

    @Test
    public void basic_parse_1_13() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"a\":tRUE}")
        );
    }

    @Test
    public void basic_parse_1_14() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"a\":TruE}")
        );
    }

    @Test
    public void basic_parse_1_15() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"a\":Tree}")
        );
    }

    @Test
    public void basic_parse_1_16() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"a\":completelyDifferent}")
        );
    }

    @Test
    public void basic_parse_1_17() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"a\":fALSE}")
        );
    }

    @Test
    public void basic_parse_1_18() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"a\":FalsE}")
        );
    }

    @Test
    public void basic_parse_1_19() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"a\":Fals}")
        );
    }

    @Test
    public void basic_parse_1_20() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"a\":[,]}")
        );
    }

    @Test
    public void basic_parse_1_21() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"a\":[1,]}")
        );
    }

    @Test
    public void basic_parse_1_22() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"a\":[,1]}")
        );
    }

    @Test
    public void basic_parse_1_23() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"a\":[:{},1]}")
        );
    }

    @Test
    public void basic_parse_1_24() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"a\":&}")
        );
    }

    @Test
    public void basic_parse_1_25() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{{}}")
        );
    }

    @Test
    public void basic_parse_1_26() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{[]}")
        );
    }

    @Test
    public void basic_parse_1_27() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{1}")
        );
    }

    @Test
    public void basic_parse_1_28() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"blah\"}")
        );
    }

    @Test
    public void basic_parse_1_29() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{true}")
        );
    }

    /////////////////////////////BASIC PARSE PASS  ------- PASSES ---------////////////////////////////////////////////
    @Test
    public void basic_parse_2_1() throws JsonParseException {
        JsonParser.parse("{}");
    }

    @Test
    public void basic_parse_2_2() throws JsonParseException {
        JsonParser.parse("{\"a\":1}");
    }

    @Test
    public void basic_parse_2_3() throws JsonParseException {
        JsonParser.parse("{\"a\":[]}");
    }

    @Test
    public void basic_parse_2_4() throws JsonParseException {
        JsonParser.parse("{\"a\":\"\"}");
    }

    @Test
    public void basic_parse_2_5() throws JsonParseException {
        JsonParser.parse("{\"a\":\"a\"}");
    }

    @Test
    public void basic_parse_2_6() throws JsonParseException {
        JsonParser.parse("{\"a\":true}");
    }

    @Test
    public void basic_parse_2_7() throws JsonParseException {
        JsonParser.parse("{\"a\":True}");
    }

    @Test
    public void basic_parse_2_8() throws JsonParseException {
        JsonParser.parse("{\"a\":TRUE}");
    }

    @Test
    public void basic_parse_2_9() throws JsonParseException {
        JsonParser.parse("{\"a\":false}");
    }

    @Test
    public void basic_parse_2_10() throws JsonParseException {
        JsonParser.parse("{\"a\":False}");
    }

    @Test
    public void basic_parse_2_11() throws JsonParseException {
        JsonParser.parse("{\"a\":FALSE}");
    }

    @Test
    public void basic_parse_2_12() throws JsonParseException {
        JsonParser.parse("{\"SPACES -> \"  :  FALSE }");
    }

    @Test
    public void basic_parse_2_13() throws JsonParseException {
        JsonParser.parse("{\"double\": 0.0}");
    }

    @Test
    public void basic_parse_2_14() throws JsonParseException {
        JsonParser.parse("{\"double\": 3.1415}");
    }

    @Test
    public void basic_parse_2_15() throws JsonParseException {
        JsonParser.parse("{\"Negative double\": -3.0}");
    }

    @Test
    public void basic_parse_2_16() throws JsonParseException {
        JsonParser.parse("{\"max double\": " + Double.MAX_VALUE + "}");
    }

    @Test
    public void basic_parse_2_17() throws JsonParseException {
        JsonParser.parse("{\"min double\": " + Double.MIN_VALUE + "}");
    }

    @Test
    public void basic_parse_2_18() throws JsonParseException {
        JsonParser.parse("{\"long\": 5}");
    }

    @Test
    public void basic_parse_2_19() throws JsonParseException {
        JsonParser.parse("{\"long\": -99}");
    }

    @Test
    public void basic_parse_2_20() throws JsonParseException {
        JsonParser.parse("{\"long\": 0}");
    }

    @Test
    public void basic_parse_2_21() throws JsonParseException {
        JsonParser.parse("{\"min long\": " + Long.MIN_VALUE + "}");
    }

    @Test
    public void basic_parse_2_22() throws JsonParseException {
        JsonParser.parse("{\"max long\": " + Long.MAX_VALUE + "}");
    }

    @Test
    public void basic_parse_2_23() throws JsonParseException {
        JsonParser.parse("{\"a\":{\"b\":1}}");
    }

    @Test
    public void basic_parse_2_24() throws JsonParseException {
        JsonParser.parse("{\"a{b\":0}");
    }

    @Test
    public void basic_parse_2_25() throws JsonParseException {
        JsonParser.parse("{\"a}b\":0}");
    }

    @Test
    public void basic_parse_2_26() {
        JsonParser.parse("{\"a.b\":0}");
    }

    @Test
    public void basic_parse_2_27() {
        JsonParser.parse("{\"a[b\":0}");
    }

    @Test
    public void basic_parse_2_28() {
        JsonParser.parse("{\"a]b\":0}");
    }


    /////////////////////////////ADVANCED PARSE PASS  ------- FAILS ---------//////////////////////////////////////////
    @Test
    public void advanced_parse_1_1() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id:1234,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_2() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":\"id\":1234,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_3() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"guid\":1,2],\"inner\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_4() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,2,\"inner\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_5() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\"\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_6() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_7() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]}}}")
        );
    }

    @Test
    public void advanced_parse_1_8() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_9() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_10() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\"\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_11() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}")
        );
    }

    @Test
    public void advanced_parse_1_12() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_13() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[1\"\"]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_14() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,,2],\"inner\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_15() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[],]}}}")
        );
    }

    @Test
    public void advanced_parse_1_16() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,2]\"inner\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_17() {
        assertThrows(JsonParseException.class, () ->
                JsonParser
                        .parse("{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,2],\"\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_18() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_19() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,[1a1]],\"inner\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_20() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"\"oops\"\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_21() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":}}}")
        );
    }

    @Test
    public void advanced_parse_1_22() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":#}}}")
        );
    }

    @Test
    public void advanced_parse_1_23() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}")
        );
    }

    @Test
    public void advanced_parse_1_24() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse("{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":\"final\":[[]]}}")
        );
    }

    @Test
    public void advanced_parse_1_25() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}")
        );
    }

    @Test
    public void advanced_parse_1_26() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"bools\":Nope,\"inner\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_27() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"bools\":FaLse,\"inner\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_28() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"bools\":FALSe,\"inner\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_29() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"bools\":t,\"inner\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_30() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"bools\":f,\"inner\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_31() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":3.1.1,\"name\":\"Bob\",\"bools\":f,\"inner\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_32() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":3.19_,\"name\":\"Bob\",\"bools\":f,\"inner\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_1_33() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":3.19 ,\"name\":\"Bob\",\"bools\":f,\"inner\":{\"final\":[[]]}}}")
        );
    }

    @Test
    public void advanced_parse_2_34() {
        assertThrows(JsonParseException.class, () ->
                JsonParser.parse(
                        "{\"obj\":{\"id\":123-123,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}")
        );
    }


    /////////////////////////////ADVANCED PARSE PASS  ------- PASSES ---------/////////////////////////////////////////
    @Test
    public void advanced_parse_2_1() throws JsonParseException {
        JsonParser.parse(
                "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void advanced_parse_2_2() throws JsonParseException {
        JsonParser.parse(
                "{\"obj\":{\"id\":\"asString\",\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void advanced_parse_2_3() throws JsonParseException {
        JsonParser.parse(
                "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"guid\":[],\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void advanced_parse_2_4() throws JsonParseException {
        JsonParser.parse(
                "{\"CAPS\":{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void advanced_parse_2_5() throws JsonParseException {
        JsonParser.parse(
                "{\"obj\":{\"id\":1234,\"name\":\"Bob space is fine\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void advanced_parse_2_6() throws JsonParseException {
        JsonParser.parse(
                "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,2,\"\"],\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void advanced_parse_2_7() throws JsonParseException {
        JsonParser.parse(
                "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":{\"moreNest\":1}}}}");
    }

    @Test
    public void advanced_parse_2_8() throws JsonParseException {
        JsonParser.parse(
                "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"bool\":True,\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void advanced_parse_2_9() throws JsonParseException {
        JsonParser.parse(
                "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"bool\":False,\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void advanced_parse_2_10() throws JsonParseException {
        JsonParser.parse(
                "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"bool\":false,\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void advanced_parse_2_11() throws JsonParseException {
        JsonParser.parse(
                "{\"obj\":{\"id\":1234,\"name\":\"Bob\",\"bool\":true,\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void advanced_parse_2_12() throws JsonParseException {
        JsonParser.parse(
                "{\"obj\":{\"id\":[[[[[[]]]]]],\"name\":\"Bob\",\"guid\":[1,2,\"\"],\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void advanced_parse_2_13() throws JsonParseException {
        JsonParser.parse(
                "{\"obj\":{\"id\":[[[[[[1,[],[2]]]]]]],\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void advanced_parse_2_14() throws JsonParseException {
        JsonParser.parse(
                "{\"obj\":{\"id\":3.1415,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void advanced_parse_2_15() throws JsonParseException {
        JsonParser.parse(
                "{\"obj\":{\"id\":   3.1415  ,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void advanced_parse_2_16() throws JsonParseException {
        JsonParser.parse(
                "{\"obj\":{\"id\":1234,\"value1\":true,\"value2\":false,\"value3\":TRUE,\"value4\":FALSE,\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void advanced_parse_2_17() throws JsonParseException {
        JsonParser.parse(
                "{\"obj\":{\"id\":-3.1415,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void advanced_parse_2_18() throws JsonParseException {
        JsonParser.parse(
                "{\"obj\":{\"id\":-3023749823749,\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void advanced_parse_2_19() throws JsonParseException {
        JsonParser.parse("{\"obj\":{\"id\":" + Long.MAX_VALUE
                + ",\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void advanced_parse_2_20() throws JsonParseException {
        JsonParser.parse("{\"obj\":{\"id\":" + Long.MIN_VALUE
                + ",\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void advanced_parse_2_21() throws JsonParseException {
        JsonParser.parse("{\"obj\":{\"id\":" + Double.MAX_VALUE
                + ",\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void advanced_parse_2_22() throws JsonParseException {
        JsonParser.parse("{\"obj\":{\"id\":" + Double.MIN_VALUE
                + ",\"name\":\"Bob\",\"guid\":[1,2],\"inner\":{\"final\":[[]]}}}");
    }

    @Test
    public void ADVANCED_FINAL_parse() throws JsonParseException {
        JsonParser.parse(
                "{\"advancedFull\":[{\"_id\":\"5be0320c9c7399358a2aac32\",\"index\":0,\"guid\":\"" +
                        "b3b156c9-3c62-40b7-b7f4-4428b9a962cb\",\"isActive\":true,\"balance\":\"$2,804.03\",\"picture\":\"http"
                        +
                        "://placehold.it/32x32\",\"age\":26,\"eyeColor\":\"brown\",\"name\":{\"first\":\"Valenzuela\",\"last\":"
                        +
                        "\"Reynolds\"},\"company\":\"ICOLOGY\",\"email\":\"valenzuela.reynolds@icology.io\",\"phone\":\"+1 (819"
                        +
                        ") 415-2268\",\"address\":\"514 Caton Avenue, Leeper, Iowa, 7549\",\"about\":\"Laboris irure nostrud an"
                        +
                        "im ipsum qui. Mollit do elit ex amet excepteur eiusmod adipisicing anim aliquip et. Ut in labore cillu"
                        +
                        "m irure laborum laboris laboris esse mollit culpa veniam elit Lorem. Nulla sit fugiat enim enim aliqua"
                        +
                        " minim sint cillum occaecat ex do eiusmod duis. Mollit reprehenderit ea ex laborum esse labore dolore "
                        +
                        "magna pariatur sint adipisicing cupidatat eiusmod. Tempor do duis aliquip id sit adipisicing laborum d"
                        +
                        "olor Lorem fugiat dolor reprehenderit deserunt.\",\"registered\":\"Thursday, September 14, 2017 1:21 P"
                        +
                        "M\",\"latitude\":\"88.573569\",\"longitude\":\"-101.053405\",\"tags\":[\"est\",\"est\",\"qui\",\"cillu"
                        +
                        "m\",\"do\"],\"range\":[0,1,2,3,4,5,6,7,8,9],\"friends\":[{\"id\":0,\"name\":\"Shanna Garrett\"},{\"id"
                        +
                        "\":1,\"name\":\"Peggy Brown\"},{\"id\":2,\"name\":\"Roach Mcguire\"}],\"greeting\":\"Hello, Valenzuela"
                        +
                        "! You have 7 unread messages.\",\"favoriteFruit\":\"banana\"},{\"_id\":\"5be0320cc5bb384150cf7939\",\""
                        +
                        "index\":1,\"guid\":\"c308e60d-a23c-46e3-b1bf-89b95ff76b72\",\"isActive\":true,\"balance\":\"$3,086.82"
                        +
                        "\",\"picture\":\"http://placehold.it/32x32\",\"age\":27,\"eyeColor\":\"green\",\"name\":{\"first\":\"O"
                        +
                        "rtega\",\"last\":\"Mueller\"},\"company\":\"ZEROLOGY\",\"email\":\"ortega.mueller@zerology.org\",\"pho"
                        +
                        "ne\":\"+1 (969) 401-3184\",\"address\":\"590 Troy Avenue, Jacksonwald, North Dakota, 5185\",\"about\":"
                        +
                        "\"Adipisicing anim eiusmod commodo eiusmod reprehenderit incididunt Lorem. In fugiat proident labore e"
                        +
                        "a. Pariatur voluptate culpa enim id veniam elit. Excepteur enim do sunt dolore officia cupidatat commo"
                        +
                        "do aliqua.\",\"registered\":\"Monday, June 16, 2014 3:55 PM\",\"latitude\":\"42.436971\",\"longitude\""
                        +
                        ":\"167.205829\",\"tags\":[\"mollit\",\"esse\",\"veniam\",\"est\",\"duis\"],\"range\":[0,1,2,3,4,5,6,7,"
                        +
                        "8,9],\"friends\":[{\"id\":0,\"name\":\"Shawn Fischer\"},{\"id\":1,\"name\":\"Melinda Woods\"},{\"id\":"
                        +
                        "2,\"name\":\"Lee Marshall\"}],\"greeting\":\"Hello, Ortega! You have 9 unread messages.\",\"favoriteFr"
                        +
                        "uit\":\"strawberry\"},{\"_id\":\"5be0320cdbb81e12dd175ae0\",\"index\":2,\"guid\":\"c1edcf71-5701-4395-"
                        +
                        "80e7-bf4edf482d61\",\"isActive\":true,\"balance\":\"$1,748.39\",\"picture\":\"http://placehold.it/32x3"
                        +
                        "2\",\"age\":30,\"eyeColor\":\"blue\",\"name\":{\"first\":\"Burks\",\"last\":\"Trujillo\"},\"company\":"
                        +
                        "\"CINCYR\",\"email\":\"burks.trujillo@cincyr.biz\",\"phone\":\"+1 (933) 472-3425\",\"address\":\"615 M"
                        +
                        "onroe Place, Hickory, Alaska, 5046\",\"about\":\"Eu quis aliqua ad dolore est Lorem fugiat fugiat pari"
                        +
                        "atur do non pariatur dolor enim. Deserunt Lorem id laborum ut voluptate anim adipisicing ad sint elit "
                        +
                        "voluptate ex. Excepteur enim irure velit id id adipisicing do elit proident eiusmod dolore fugiat ex a"
                        +
                        "nim.\",\"registered\":\"Friday, July 13, 2018 1:20 PM\",\"latitude\":\"82.00238\",\"longitude\":\"-12"
                        +
                        "6.196654\",\"tags\":[\"amet\",\"mollit\",\"ut\",\"occaecat\",\"laboris\"],\"range\":[0,1,2,3,4,5,6,7,"
                        +
                        "8,9],\"friends\":[{\"id\":0,\"name\":\"Sasha Jones\"},{\"id\":1,\"name\":\"Christian Dunn\"},{\"id\":"
                        +
                        "2,\"name\":\"Abbott Love\"}],\"greeting\":\"Hello, Burks! You have 8 unread messages.\",\"favoriteFru"
                        +
                        "it\":\"apple\"},{\"_id\":\"5be0320c8df53fee7c1ffc5d\",\"index\":3,\"guid\":\"cce98f10-a38e-40ab-a530-"
                        +
                        "eb15673b13d8\",\"isActive\":true,\"balance\":\"$3,324.09\",\"picture\":\"http://placehold.it/32x32\","
                        +
                        "\"age\":38,\"eyeColor\":\"brown\",\"name\":{\"first\":\"Flossie\",\"last\":\"Webster\"},\"company\":\""
                        +
                        "SUREMAX\",\"email\":\"flossie.webster@suremax.com\",\"phone\":\"+1 (907) 487-2831\",\"address\":\"775"
                        +
                        " Colonial Court, Graball, Alabama, 2937\",\"about\":\"Lorem in proident dolor veniam deserunt labore "
                        +
                        "proident aliquip veniam ipsum. Enim sit consequat id non magna anim minim. Ea eiusmod quis esse adipi"
                        +
                        "sicing incididunt anim nisi mollit.\",\"registered\":\"Thursday, September 6, 2018 4:46 PM\",\"latitu"
                        +
                        "de\":\"-16.786818\",\"longitude\":\"-20.319944\",\"tags\":[\"cillum\",\"do\",\"commodo\",\"amet\",\"e"
                        +
                        "iusmod\"],\"range\":[0,1,2,3,4,5,6,7,8,9],\"friends\":[{\"id\":0,\"name\":\"Daisy Tillman\"},{\"id\":"
                        +
                        "1,\"name\":\"Rivers Walters\"},{\"id\":2,\"name\":\"Tamara Cannon\"}],\"greeting\":\"Hello, Flossie! "
                        +
                        "You have 9 unread messages.\",\"favoriteFruit\":\"apple\"},{\"_id\":\"5be0320ca63d45503b1eec34\",\"in"
                        +
                        "dex\":4,\"guid\":\"a171d350-30db-458e-ace1-714311f80cc6\",\"isActive\":true,\"balance\":\"$1,585.76\""
                        +
                        ",\"picture\":\"http://placehold.it/32x32\",\"age\":24,\"eyeColor\":\"brown\",\"name\":{\"first\":\"He"
                        +
                        "bert\",\"last\":\"York\"},\"company\":\"CONJURICA\",\"email\":\"hebert.york@conjurica.info\",\"phone"
                        +
                        "\":\"+1 (814) 522-2569\",\"address\":\"755 Temple Court, Shasta, Ohio, 9758\",\"about\":\"Est id des"
                        +
                        "runt aute exercitation. Proident do est ea et. Voluptate laboris mollit proident do culpa sit. Volupt"
                        +
                        "ate deserunt officia deserunt consequat sunt id pariatur enim eu officia quis. Pariatur deserunt in "
                        +
                        "amet nostrud enim exercitation irure laboris aute adipisicing. Velit deserunt sint velit officia exe"
                        +
                        "rcitation sunt sit magna excepteur dolor dolore.\",\"registered\":\"Thursday, February 18, 2016 2:19"
                        +
                        " AM\",\"latitude\":\"72.524513\",\"longitude\":\"-7.754846\",\"tags\":[\"dolore\",\"veniam\",\"ea\""
                        +
                        ",\"incididunt\",\"quis\"],\"range\":[0,1,2,3,4,5,6,7,8,9],\"friends\":[{\"id\":0,\"name\":\"Winters"
                        +
                        " Mosley\"},{\"id\":1,\"name\":\"Sherrie Harmon\"},{\"id\":2,\"name\":\"Hale Stewart\"}],\"greeting\""
                        +
                        ":\"Hello, Hebert! You have 9 unread messages.\",\"favoriteFruit\":\"apple\"},{\"_id\":\"5be0320cfb9"
                        +
                        "699144add32e9\",\"index\":5,\"guid\":\"1bb15cfe-0e36-4a5c-88b7-8ef0b1d0b226\",\"isActive\":true,\"b"
                        +
                        "alance\":\"$2,746.56\",\"picture\":\"http://placehold.it/32x32\",\"age\":36,\"eyeColor\":\"blue\",\""
                        +
                        "name\":{\"first\":\"Rowe\",\"last\":\"Tyson\"},\"company\":\"KLUGGER\",\"email\":\"rowe.tyson@klugge"
                        +
                        "r.us\",\"phone\":\"+1 (839) 489-2262\",\"address\":\"531 Schroeders Avenue, Allentown, American Samo"
                        +
                        "a, 4815\",\"about\":\"Reprehenderit do duis mollit minim ad magna culpa pariatur aliqua enim. Proide"
                        +
                        "nt amet sunt in nulla ullamco aliqua ad in minim nisi reprehenderit sunt aliquip aute. Dolore eu lab"
                        +
                        "ore laborum officia fugiat ullamco in pariatur laboris voluptate ut id.\",\"registered\":\"Saturday,"
                        +
                        " August 27, 2016 6:37 AM\",\"latitude\":\"-69.215198\",\"longitude\":\"116.228389\",\"tags\":[\"irur"
                        +
                        "e\",\"deserunt\",\"aliquip\",\"cupidatat\",\"voluptate\"],\"range\":[0,1,2,3,4,5,6,7,8,9],\"friends\""
                        +
                        ":[{\"id\":0,\"name\":\"Claudia Burch\"},{\"id\":1,\"name\":\"Lora Fernandez\"},{\"id\":2,\"name\":\""
                        +
                        "Burns Le\"}],\"greeting\":\"Hello, Rowe! You have 7 unread messages.\",\"favoriteFruit\":\"strawberr"
                        +
                        "y\"},{\"_id\":\"5be0320c2e88ee281df05802\",\"index\":6,\"guid\":\"901f9a6c-7c68-4cb8-ac71-f0328dfb07"
                        +
                        "1f\",\"isActive\":true,\"balance\":\"$1,653.81\",\"picture\":\"http://placehold.it/32x32\",\"age\":3"
                        +
                        "0,\"eyeColor\":\"brown\",\"name\":{\"first\":\"Ella\",\"last\":\"Boyle\"},\"company\":\"MARQET\",\"e"
                        +
                        "mail\":\"ella.boyle@marqet.co.uk\",\"phone\":\"+1 (948) 572-3072\",\"address\":\"750 Ridge Court, Si"
                        +
                        "ms, Connecticut, 248\",\"about\":\"Officia proident cillum aliquip velit labore. Mollit deserunt tem"
                        +
                        "por pariatur nulla consectetur nostrud dolor non dolor dolore eu sint aliquip. Eu aliqua amet pariat"
                        +
                        "ur adipisicing anim Lorem magna dolor. Aliqua velit laboris ipsum veniam Lorem Lorem sint eu in proi"
                        +
                        "dent nostrud. Non ea sint non dolor irure proident officia reprehenderit quis. Dolore excepteur exce"
                        +
                        "pteur ex Lorem magna enim voluptate eu.\",\"registered\":\"Tuesday, January 16, 2018 6:54 AM\",\"lat"
                        +
                        "itude\":\"-13.726903\",\"longitude\":\"-66.794313\",\"tags\":[\"deserunt\",\"dolore\",\"minim\",\"ad"
                        +
                        "ipisicing\",\"nostrud\"],\"range\":[0,1,2,3,4,5,6,7,8,9],\"friends\":[{\"id\":0,\"name\":\"Lynette S"
                        +
                        "olomon\"},{\"id\":1,\"name\":\"Emily Meadows\"},{\"id\":2,\"name\":\"Belinda Oliver\"}],\"greeting\""
                        +
                        ":\"Hello, Ella! You have 8 unread messages.\",\"favoriteFruit\":\"banana\"},{\"_id\":\"5be0320d0f895"
                        +
                        "3cbd7a9dd3e\",\"index\":7,\"guid\":\"ee491598-512a-4e5e-ae48-f32a1073ce87\",\"isActive\":false,\"bal"
                        +
                        "ance\":\"$3,214.41\",\"picture\":\"http://placehold.it/32x32\",\"age\":37,\"eyeColor\":\"green\",\"n"
                        +
                        "ame\":{\"first\":\"Adele\",\"last\":\"Shepherd\"},\"company\":\"NURALI\",\"email\":\"adele.shepherd@"
                        +
                        "nurali.me\",\"phone\":\"+1 (924) 543-2428\",\"address\":\"868 Hawthorne Street, Bascom, Minnesota, 3"
                        +
                        "50\",\"about\":\"Veniam culpa esse adipisicing irure incididunt in quis quis quis. Minim ipsum ad al"
                        +
                        "iqua consectetur mollit sunt ipsum id nulla. Laboris nisi officia dolore culpa proident. Nulla commo"
                        +
                        "do enim id elit laboris ex consequat occaecat eu ea. Aliqua labore pariatur et aute in reprehenderit"
                        +
                        " qui reprehenderit duis aute aute magna aute. Mollit labore ut occaecat aliquip culpa dolor laboris."
                        +
                        "\",\"registered\":\"Saturday, July 26, 2014 4:11 PM\",\"latitude\":\"70.603378\",\"longitude\":\"91."
                        +
                        "667562\",\"tags\":[\"nisi\",\"irure\",\"id\",\"cupidatat\",\"irure\"],\"range\":[0,1,2,3,4,5,6,7,8,9"
                        +
                        "],\"friends\":[{\"id\":0,\"name\":\"Della Bright\"},{\"id\":1,\"name\":\"Toni Hebert\"},{\"id\":2,\""
                        +
                        "name\":\"Landry Talley\"}],\"greeting\":\"Hello, Adele! You have 7 unread messages.\",\"favoriteFrui"
                        +
                        "t\":\"banana\"},{\"_id\":\"5be0320dd8b470e525695225\",\"index\":8,\"guid\":\"fda0902a-b2bc-479f-a6e5"
                        +
                        "-16ba6f8ea8c2\",\"isActive\":true,\"balance\":\"$2,057.91\",\"picture\":\"http://placehold.it/32x32"
                        +
                        "\",\"age\":34,\"eyeColor\":\"brown\",\"name\":{\"first\":\"Tina\",\"last\":\"Figueroa\"},\"company\""
                        +
                        ":\"REMOLD\",\"email\":\"tina.figueroa@remold.ca\",\"phone\":\"+1 (985) 568-3189\",\"address\":\"921 "
                        +
                        "Hinckley Place, Longbranch, Kentucky, 8588\",\"about\":\"Cillum voluptate excepteur nostrud laboris "
                        +
                        "est laboris aute ex est excepteur Lorem cillum. Do id qui anim et. Nisi sunt enim irure non occaecat"
                        +
                        " nostrud excepteur sit eu. Esse nostrud mollit sunt commodo occaecat ad commodo anim fugiat voluptat"
                        +
                        "e. Elit duis labore nulla est duis et tempor sit tempor tempor. Exercitation tempor dolor adipisicin"
                        +
                        "g laboris.\",\"registered\":\"Monday, May 26, 2014 11:37 PM\",\"latitude\":\"70.471295\",\"longitude"
                        +
                        "\":\"-47.285138\",\"tags\":[\"velit\",\"incididunt\",\"voluptate\",\"Lorem\",\"velit\"],\"range\":[0"
                        +
                        ",1,2,3,4,[5,9.9],6,7,8.3765,9],\"friends\":[{\"id\":0,\"name\":\"Gloria Johnston\"},{\"id\":1,\"name\""
                        +
                        ":\"Jocelyn Fowler\"},{\"id\":2,\"name\":\"Alberta Leonard\"}],\"greeting\":\"Hello, Tina! You have 7 "
                        +
                        "unread messages.\",\"favoriteFruit\":\"banana\"}]}");
    }
}

