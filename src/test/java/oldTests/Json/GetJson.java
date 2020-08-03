package oldTests.Json;

import api.IJson;
import api.JsonParser;
import exceptions.json.JsonParseException;
import exceptions.json.KeyDifferentTypeException;
import exceptions.json.KeyInvalidException;
import exceptions.json.KeyNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetJson {

    private static IJson obj, deepNesting;


    @BeforeAll
    public static void setupQueryObject() throws JsonParseException {
        obj = JsonParser.parse(
                "{\"variables\":[10, true, -3.14],\"item\":[{\"item\":[{\"request\":{\"method\":" +
                        "\"POST\",\"isValid\":true,\"header\":[false, {\"description\":\"\",\"value\":\"application/json\",\"key\":"
                        +
                        "\"Content-Type\"},{\"description\":\"\",\"value\":\"Bearer {{access_token}}\",\"key\":\"Authorization\"}]}}]}]}");
        deepNesting = JsonParser
                .parse("{\"variables\":[[123,[123456789,2,{\"nested1?\":[123,[0,1,2,{\"final\":\"Co" +
                        "ngrats It Works\"}]]}]],{}]}");
    }

    @Test
    public void get_elem_0() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("");
    }

    @Test
    public void get_elem_0_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getValueAt("");
    }

    @Test
    public void get_elem_1() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("variables");
    }

    @Test
    public void get_elem_1_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getArrayAt("variables");
    }

    @Test
    public void get_elem_1_value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals(3, obj.getArrayAt("variables").size());
    }


    @Test
    public void get_elem_2() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("variables[0]");
    }

    @Test
    public void get_elem_2_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getLongAt("variables[0]");
    }

    @Test
    public void get_elem_2_value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals(10, obj.getLongAt("variables[0]"));
    }

    @Test
    public void get_elem_3() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("variables[1]");
    }

    @Test
    public void get_elem_3_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getBooleanAt("variables[1]");
    }

    @Test
    public void get_elem_3_value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertTrue(obj.getBooleanAt("variables[1]"));
    }

    @Test
    public void get_elem_4() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("variables[2]");
    }

    @Test
    public void get_elem_4_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getDoubleAt("variables[2]");
    }

    @Test
    public void get_elem_4_value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals(-3.14, obj.getDoubleAt("variables[2]"), 0);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void get_elem_5() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("item");
    }

    @Test
    public void get_elem_5_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getArrayAt("item");
    }

    @Test
    public void get_elem_5_value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals(1, obj.getArrayAt("item").size());
    }


    @Test
    public void get_elem_6() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("item[0]");
    }

    @Test
    public void get_elem_6_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getValueAt("item[0]");
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void get_elem_7() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("item[0].item");
    }

    @Test
    public void get_elem_7_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getArrayAt("item[0].item");
    }

    @Test
    public void get_elem_7_value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals(1, obj.getArrayAt("item[0].item").size());
    }


    @Test
    public void get_elem_8() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("item[0].item[0]");
    }

    @Test
    public void get_elem_8_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getValueAt("item[0].item[0]");
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void get_elem_9() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("item[0].item[0].request");
    }

    @Test
    public void get_elem_9_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getValueAt("item[0].item[0].request");
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void get_elem_10() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("item[0].item[0].request.method");
    }

    @Test
    public void get_elem_10_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getStringAt("item[0].item[0].request.method");
    }

    @Test
    public void get_elem_10_value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals("POST", obj.getStringAt("item[0].item[0].request.method"));
    }


    @Test
    public void get_elem_11() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("item[0].item[0].request.isValid");
    }

    @Test
    public void get_elem_11_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getBooleanAt("item[0].item[0].request.isValid");
    }

    @Test
    public void get_elem_11_value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertTrue(obj.getBooleanAt("item[0].item[0].request.isValid"));
    }


    @Test
    public void get_elem_12() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("item[0].item[0].request.header");
    }

    @Test
    public void get_elem_12_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getArrayAt("item[0].item[0].request.header");
    }

    @Test
    public void get_elem_12_value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals(3, obj.getArrayAt("item[0].item[0].request.header").size());
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void get_elem_13() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("item[0].item[0].request.header[0]");
    }

    @Test
    public void get_elem_13_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getBooleanAt("item[0].item[0].request.header[0]");
    }

    @Test
    public void get_elem_13_value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertFalse(obj.getBooleanAt("item[0].item[0].request.header[0]"));
    }

    @Test
    public void get_elem_14() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("item[0].item[0].request.header[1]");
    }

    @Test
    public void get_elem_14_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getValueAt("item[0].item[0].request.header[1]");
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void get_elem_15() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("item[0].item[0].request.header[1].description");
    }

    @Test
    public void get_elem_15_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getStringAt("item[0].item[0].request.header[1].description");
    }

    @Test
    public void get_elem_15_Value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals("",
                obj.getStringAt("item[0].item[0].request.header[1].description"));
    }

    @Test
    public void get_elem_16() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("item[0].item[0].request.header[1].value");
    }

    @Test
    public void get_elem_16_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getStringAt("item[0].item[0].request.header[1].value");
    }

    @Test
    public void get_elem_16_Value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals("application/json",
                obj.getStringAt("item[0].item[0].request.header[1].value"));
    }

    @Test
    public void get_elem_17() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("item[0].item[0].request.header[1].key");
    }

    @Test
    public void get_elem_17_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getStringAt("item[0].item[0].request.header[1].key");
    }

    @Test
    public void get_elem_17_Value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals("Content-Type",
                obj.getStringAt("item[0].item[0].request.header[1].key"));
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void get_elem_18() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("item[0].item[0].request.header[2].description");
    }

    @Test
    public void get_elem_18_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getStringAt("item[0].item[0].request.header[2].description");
    }

    @Test
    public void get_elem_18_Value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals("",
                obj.getStringAt("item[0].item[0].request.header[2].description"));
    }

    @Test
    public void get_elem_19() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("item[0].item[0].request.header[2].value");
    }

    @Test
    public void get_elem_19_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getStringAt("item[0].item[0].request.header[2].value");
    }

    @Test
    public void get_elem_19_Value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals("Bearer {{access_token}}",
                obj.getStringAt("item[0].item[0].request.header[2].value"));
    }


    @Test
    public void get_elem_20() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getAnyAt("item[0].item[0].request.header[2].key");
    }

    @Test
    public void get_elem_20_type() throws KeyDifferentTypeException, KeyNotFoundException {
        obj.getStringAt("item[0].item[0].request.header[2].key");
    }

    @Test
    public void get_elem_20_Value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals("Authorization",
                obj.getStringAt("item[0].item[0].request.header[2].key"));
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //missing types
    @Test
    public void boolean_type_mismatch_0() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getBooleanAt("variables")
        );
    }

    @Test
    public void boolean_type_mismatch_1() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getBooleanAt("variables[0]")
        );
    }

    @Test
    public void boolean_type_mismatch_2() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getBooleanAt("variables[2]")
        );
    }

    @Test
    public void boolean_type_mismatch_3() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getBooleanAt("")
        );

    }

    @Test
    public void boolean_type_mismatch_4() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getBooleanAt("item[0].item[0].request.method")
        );

    }


    @Test
    public void array_type_mismatch_0() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getArrayAt("variables[0]")
        );

    }

    @Test
    public void array_type_mismatch_1() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getArrayAt("variables[1]")
        );

    }

    @Test
    public void array_type_mismatch_2() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getArrayAt("variables[2]")
        );

    }

    @Test
    public void array_type_mismatch_3() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getArrayAt("")
        );

    }

    @Test
    public void array_type_mismatch_4() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getArrayAt("item[0].item[0].request.method")
        );

    }


    @Test
    public void long_type_mismatch_0() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getLongAt("variables")
        );

    }

    @Test
    public void long_type_mismatch_1() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getLongAt("variables[1]")
        );

    }

    @Test
    public void long_type_mismatch_2() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getLongAt("variables[2]")
        );

    }

    @Test
    public void long_type_mismatch_3() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getLongAt("")
        );

    }

    @Test
    public void long_type_mismatch_4() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getLongAt("item[0].item[0].request.method")
        );

    }


    @Test
    public void double_type_mismatch_0() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getDoubleAt("variables")
        );

    }

    @Test
    public void double_type_mismatch_1() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getDoubleAt("variables[1]")
        );

    }

    @Test
    public void double_type_mismatch_2() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getDoubleAt("variables[0]")
        );

    }

    @Test
    public void double_type_mismatch_3() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getDoubleAt("")
        );

    }

    @Test
    public void double_type_mismatch_4() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getDoubleAt("item[0].item[0].request.method")
        );

    }


    @Test
    public void object_type_mismatch_0() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getJSONObjectAt("variables")
        );

    }

    @Test
    public void object_type_mismatch_1() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getJSONObjectAt("variables[1]")
        );

    }

    @Test
    public void object_type_mismatch_2() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getJSONObjectAt("variables[0]")
        );

    }

    @Test
    public void object_type_mismatch_3() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getJSONObjectAt("item[0].item[0].request.method")
        );

    }

    @Test
    public void object_type_mismatch_4() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getJSONObjectAt("variables[2]")
        );

    }


    @Test
    public void string_type_mismatch_0() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getStringAt("variables")
        );

    }

    @Test
    public void string_type_mismatch_1() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getStringAt("variables[1]")
        );

    }

    @Test
    public void string_type_mismatch_2() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getStringAt("variables[0]")
        );

    }

    @Test
    public void string_type_mismatch_3() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getStringAt("")
        );

    }

    @Test
    public void string_type_mismatch_4() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getStringAt("variables[2]")
        );

    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //missing keys
    @Test
    public void malformed_key_0() {
        assertThrows(KeyNotFoundException.class, () ->
                obj.getAnyAt("item[0].item[0].request.header[2].ke")
        );
    }

    @Test
    public void malformed_key_1() {
        assertThrows(KeyNotFoundException.class, () ->
                obj.getAnyAt("item[0].item[0].request.heade[2].key")
        );
    }

    @Test
    public void malformed_key_2() {
        assertThrows(KeyNotFoundException.class, () ->
                obj.getAnyAt("item[0].item[0].reques.header[2].key")
        );
    }

    @Test
    public void malformed_key_3() {
        assertThrows(KeyNotFoundException.class, () ->
                obj.getAnyAt("item[0].ite[0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_4() {
        assertThrows(KeyNotFoundException.class, () ->
                obj.getAnyAt("ite[0].item[0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_5() {
        assertThrows(KeyNotFoundException.class, () ->
                obj.getAnyAt("item0].item[0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_6() {
        assertThrows(KeyInvalidException.class, () ->
                obj.getAnyAt("item[0.item[0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_7() {
        assertThrows(KeyInvalidException.class, () ->
                obj.getAnyAt("item[].item[0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_8() {
        assertThrows(KeyNotFoundException.class, () ->
                obj.getAnyAt("item[0].item0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_9() {
        assertThrows(KeyInvalidException.class, () ->
                obj.getAnyAt("item[0].item[].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_10() {
        assertThrows(KeyInvalidException.class, () ->
                obj.getAnyAt("item[0].item[0.request.header[2].key")
        );
    }

    @Test
    public void malformed_key_11() {
        assertThrows(KeyNotFoundException.class, () ->
                obj.getAnyAt("item[0].item[0].request.header2].key")
        );
    }

    @Test
    public void malformed_key_12() {
        assertThrows(KeyInvalidException.class, () ->
                obj.getAnyAt("item[0].item[0].request.header[.key")
        );
    }

    @Test
    public void malformed_key_13() {
        assertThrows(KeyInvalidException.class, () ->
                obj.getAnyAt("item[0].item[0].request.header[2]key")
        );
    }

    @Test
    public void malformed_key_14() {
        assertThrows(KeyNotFoundException.class, () ->
                obj.getAnyAt("item[0].item[0].requestheader[2].key")
        );
    }

    @Test
    public void malformed_key_15() {
        assertThrows(KeyInvalidException.class, () ->
                obj.getAnyAt("item[0].item[0]request.header[2].key")
        );
    }

    @Test
    public void malformed_key_16() {
        assertThrows(KeyInvalidException.class, () ->
                obj.getAnyAt("item[0]item[0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_17() {
        assertThrows(KeyNotFoundException.class, () ->
                obj.getAnyAt("item[1].item[0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_18() {
        assertThrows(KeyInvalidException.class, () ->
                obj.getAnyAt("item[-12].item[0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_19() {
        assertThrows(KeyNotFoundException.class, () ->
                obj.getAnyAt("item[0].nope.item[0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_20() {
        assertThrows(KeyNotFoundException.class, () ->
                obj.getAnyAt("nope")
        );
    }

    @Test
    public void malformed_key_21() {
        assertThrows(KeyNotFoundException.class, () ->
                obj.getAnyAt("item[0].item[0].request.12.header[2].key")
        );
    }

    @Test
    public void malformed_key_22() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getAnyAt("item[0].item[0][0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_23() {
        assertThrows(KeyNotFoundException.class, () ->
                obj.getAnyAt("item[0].item{0}.request.header[2].key")
        );
    }

    @Test
    public void malformed_key_24() {
        assertThrows(KeyInvalidException.class, () ->
                obj.getAnyAt("item[0].item[0].request.header[a].key")
        );
    }

    @Test
    public void malformed_key_25() {
        assertThrows(KeyDifferentTypeException.class, () ->
                obj.getAnyAt("[0]")
        );
    }

    @Test
    public void malformed_key_26() {
        assertThrows(KeyNotFoundException.class, () ->
                obj.getAnyAt("{}")
        );
    }

    @Test
    public void malformed_key_27() {
        assertThrows(KeyInvalidException.class, () ->
                obj.getAnyAt("item[0]].item[0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_28() {
        assertThrows(KeyInvalidException.class, () ->
                obj.getAnyAt("item[0].item[0]..request.header[2].key")
        );
    }

    @Test
    public void malformed_key_29() {
        assertThrows(KeyInvalidException.class, () ->
                obj.getAnyAt("item[0].item[0].request.header[2].key.")
        );
    }

    @Test
    public void malformed_key_30() {
        assertThrows(KeyInvalidException.class, () ->
                obj.getAnyAt("item[0].item[0].requ est.header[2].key")
        );
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Deep nesting Gets
    //Success////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void deep_nest_key_1() throws KeyDifferentTypeException, KeyNotFoundException {
        deepNesting.getAnyAt("variables[0][1]");
    }

    @Test
    public void deep_nest_key_2() throws KeyDifferentTypeException, KeyNotFoundException {
        deepNesting.getAnyAt("variables[0][1][2]");
    }

    @Test
    public void deep_nest_key_3() throws KeyDifferentTypeException, KeyNotFoundException {
        deepNesting.getAnyAt("variables[0][1][2].nested1?");
    }

    @Test
    public void deep_nest_key_4() throws KeyDifferentTypeException, KeyNotFoundException {
        deepNesting.getAnyAt("variables[0][1][2].nested1?[1]");
    }

    @Test
    public void deep_nest_key_5() throws KeyDifferentTypeException, KeyNotFoundException {
        deepNesting.getAnyAt("variables[0][1][2].nested1?[1][3].final");
    }

    @Test
    public void deep_nest_key_6() throws KeyDifferentTypeException, KeyNotFoundException {
        deepNesting.getArrayAt("variables[0][1][2].nested1?[1]");
    }

    @Test
    public void deep_nest_key_7() throws KeyDifferentTypeException, KeyNotFoundException {
        deepNesting.getStringAt("variables[0][1][2].nested1?[1][3].final");
    }

    @Test
    public void deep_nest_key_8() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals("Congrats It Works",
                deepNesting.getStringAt("variables[0][1][2].nested1?[1][3].final"));
    }


    @Test
    public void getAllFromArray()
            throws KeyDifferentTypeException, KeyNotFoundException, JsonParseException {
        IJson obj = JsonParser
                .parse("{\"friends\":[{\"name\":\"Shanna Garrett\",\"id\":0},{\"name\":\"" +
                        "Peggy Brown\",\"id\":1},{\"name\":\"Roach Mcguire\",\"id\":2}]}");

        int count = 3;
        for (String key : obj.getKeysOf("friends")) {
            obj.getValueAt("friends[" + key + "]");
            count--;
        }

        assertEquals(count, 0);
    }

    @Test
    public void getAllFromObject()
            throws KeyDifferentTypeException, KeyNotFoundException, JsonParseException {
        IJson obj = JsonParser
                .parse("{\"friends\":{\"friend1\":{\"name\":\"Shanna Garrett\",\"id\":0}," +
                        "\"friend2\":{\"name\":\"Peggy Brown\",\"id\":1},\"friend3\":{\"name\":\"Roach Mcguire\",\"id\":2}}}");

        int count = 3;
        for (String key : obj.getKeysOf("friends")) {
            obj.getValueAt("friends." + key);
            count--;
        }

        assertEquals(count, 0);
    }


    //Fails//////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void deep_nest_key_9() {
        assertThrows(KeyNotFoundException.class, () ->
                deepNesting.getAnyAt("variables[0][1][2].nested1?[1][3].fial")
        );
    }

    @Test
    public void deep_nest_key_10() {
        assertThrows(KeyInvalidException.class, () ->
                deepNesting.getAnyAt("variables[0][1]..[2].nested1?[1][3].final")
        );
    }

    @Test
    public void deep_nest_key_11() {
        assertThrows(KeyNotFoundException.class, () ->
                deepNesting.getAnyAt("variables[0][1][3].nested1?[1][3].final")
        );
    }

    @Test
    public void deep_nest_key_12() {
        assertThrows(KeyInvalidException.class, () ->
                deepNesting.getAnyAt("variables[0][1][2].nested1?[1][-1].final")
        );
    }

    @Test
    public void deep_nest_key_13() {
        assertThrows(KeyNotFoundException.class, () ->
                deepNesting.getAnyAt("variables[0][1][2].nested1[1][3].final")
        );
    }

    @Test
    public void deep_nest_key_14() {
        assertThrows(KeyDifferentTypeException.class, () ->
                deepNesting.getJSONObjectAt("variables[0][1][2].nested1?[1]")
        );
    }

    @Test
    public void deep_nest_key_15() {
        assertThrows(KeyDifferentTypeException.class, () ->
                deepNesting.getLongAt("variables[0][1][2].nested1?[1][3].final")
        );
    }

    @Test
    public void deep_nest_key_16() {
        assertThrows(KeyInvalidException.class, () ->
                deepNesting.getAnyAt("variables[0][")
        );
    }

    @Test
    public void deep_nest_key_17() {
        assertThrows(KeyInvalidException.class, () ->
                deepNesting.getAnyAt("variables[0][1")
        );
    }

    @Test
    public void deep_nest_key_18() {
        assertThrows(KeyInvalidException.class, () ->
                deepNesting.getAnyAt("variables[0][1][2")
        );
    }

    @Test
    public void deep_nest_key_19() {
        assertThrows(KeyInvalidException.class, () ->
                deepNesting.getAnyAt("variables[]")
        );
    }
}