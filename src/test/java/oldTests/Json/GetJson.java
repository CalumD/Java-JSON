package oldTests.Json;

import Core.IJson;
import Core.JSONParser;
import Exceptions.JSONParseException;
import Exceptions.KeyDifferentTypeException;
import Exceptions.KeyNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GetJson {

    private static IJson obj, deepNesting;


    @BeforeAll
    public static void setupQueryObject() throws JSONParseException {
        obj = JSONParser.parse(
                "{\"variables\":[10, true, -3.14],\"item\":[{\"item\":[{\"request\":{\"method\":" +
                        "\"POST\",\"isValid\":true,\"header\":[false, {\"description\":\"\",\"value\":\"application/json\",\"key\":"
                        +
                        "\"Content-Type\"},{\"description\":\"\",\"value\":\"Bearer {{access_token}}\",\"key\":\"Authorization\"}]}}]}]}");
        deepNesting = JSONParser
                .parse("{\"variables\":[[123,[123456789,2,{\"nested1?\":[123,[0,1,2,{\"final\":\"Co" +
                        "ngrats It Works\"}]]}]],{}]}");
    }

    @Test
    public void get_elem_0() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "");
    }

    @Test
    public void get_elem_0_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getObject(obj, "");
    }

    @Test
    public void get_elem_1() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "variables");
    }

    @Test
    public void get_elem_1_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getList(obj, "variables");
    }

    @Test
    public void get_elem_1_value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals(3, JSONParser.getList(obj, "variables").size());
    }


    @Test
    public void get_elem_2() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "variables[0]");
    }

    @Test
    public void get_elem_2_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getLong(obj, "variables[0]");
    }

    @Test
    public void get_elem_2_value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals(10, JSONParser.getLong(obj, "variables[0]"));
    }

    @Test
    public void get_elem_3() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "variables[1]");
    }

    @Test
    public void get_elem_3_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getBoolean(obj, "variables[1]");
    }

    @Test
    public void get_elem_3_value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertTrue(JSONParser.getBoolean(obj, "variables[1]"));
    }

    @Test
    public void get_elem_4() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "variables[2]");
    }

    @Test
    public void get_elem_4_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getDouble(obj, "variables[2]");
    }

    @Test
    public void get_elem_4_value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals(-3.14, JSONParser.getDouble(obj, "variables[2]"), 0);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void get_elem_5() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "item");
    }

    @Test
    public void get_elem_5_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getList(obj, "item");
    }

    @Test
    public void get_elem_5_value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals(1, JSONParser.getList(obj, "item").size());
    }


    @Test
    public void get_elem_6() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "item[0]");
    }

    @Test
    public void get_elem_6_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getObject(obj, "item[0]");
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void get_elem_7() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "item[0].item");
    }

    @Test
    public void get_elem_7_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getList(obj, "item[0].item");
    }

    @Test
    public void get_elem_7_value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals(1, JSONParser.getList(obj, "item[0].item").size());
    }


    @Test
    public void get_elem_8() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "item[0].item[0]");
    }

    @Test
    public void get_elem_8_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getObject(obj, "item[0].item[0]");
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void get_elem_9() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "item[0].item[0].request");
    }

    @Test
    public void get_elem_9_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getObject(obj, "item[0].item[0].request");
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void get_elem_10() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "item[0].item[0].request.method");
    }

    @Test
    public void get_elem_10_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getString(obj, "item[0].item[0].request.method");
    }

    @Test
    public void get_elem_10_value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals("POST", JSONParser.getString(obj, "item[0].item[0].request.method"));
    }


    @Test
    public void get_elem_11() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "item[0].item[0].request.isValid");
    }

    @Test
    public void get_elem_11_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getBoolean(obj, "item[0].item[0].request.isValid");
    }

    @Test
    public void get_elem_11_value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertTrue(JSONParser.getBoolean(obj, "item[0].item[0].request.isValid"));
    }


    @Test
    public void get_elem_12() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "item[0].item[0].request.header");
    }

    @Test
    public void get_elem_12_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getList(obj, "item[0].item[0].request.header");
    }

    @Test
    public void get_elem_12_value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals(3, JSONParser.getList(obj, "item[0].item[0].request.header").size());
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void get_elem_13() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "item[0].item[0].request.header[0]");
    }

    @Test
    public void get_elem_13_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getBoolean(obj, "item[0].item[0].request.header[0]");
    }

    @Test
    public void get_elem_13_value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertFalse(JSONParser.getBoolean(obj, "item[0].item[0].request.header[0]"));
    }

    @Test
    public void get_elem_14() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "item[0].item[0].request.header[1]");
    }

    @Test
    public void get_elem_14_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getObject(obj, "item[0].item[0].request.header[1]");
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void get_elem_15() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "item[0].item[0].request.header[1].description");
    }

    @Test
    public void get_elem_15_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getString(obj, "item[0].item[0].request.header[1].description");
    }

    @Test
    public void get_elem_15_Value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals("",
                JSONParser.getString(obj, "item[0].item[0].request.header[1].description"));
    }

    @Test
    public void get_elem_16() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "item[0].item[0].request.header[1].value");
    }

    @Test
    public void get_elem_16_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getString(obj, "item[0].item[0].request.header[1].value");
    }

    @Test
    public void get_elem_16_Value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals("application/json",
                JSONParser.getString(obj, "item[0].item[0].request.header[1].value"));
    }

    @Test
    public void get_elem_17() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "item[0].item[0].request.header[1].key");
    }

    @Test
    public void get_elem_17_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getString(obj, "item[0].item[0].request.header[1].key");
    }

    @Test
    public void get_elem_17_Value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals("Content-Type",
                JSONParser.getString(obj, "item[0].item[0].request.header[1].key"));
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void get_elem_18() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "item[0].item[0].request.header[2].description");
    }

    @Test
    public void get_elem_18_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getString(obj, "item[0].item[0].request.header[2].description");
    }

    @Test
    public void get_elem_18_Value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals("",
                JSONParser.getString(obj, "item[0].item[0].request.header[2].description"));
    }

    @Test
    public void get_elem_19() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "item[0].item[0].request.header[2].value");
    }

    @Test
    public void get_elem_19_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getString(obj, "item[0].item[0].request.header[2].value");
    }

    @Test
    public void get_elem_19_Value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals("Bearer {{access_token}}",
                JSONParser.getString(obj, "item[0].item[0].request.header[2].value"));
    }


    @Test
    public void get_elem_20() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(obj, "item[0].item[0].request.header[2].key");
    }

    @Test
    public void get_elem_20_type() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getString(obj, "item[0].item[0].request.header[2].key");
    }

    @Test
    public void get_elem_20_Value() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals("Authorization",
                JSONParser.getString(obj, "item[0].item[0].request.header[2].key"));
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //missing types
    @Test
    public void boolean_type_mismatch_0() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getBoolean(obj, "variables")
        );
    }

    @Test
    public void boolean_type_mismatch_1() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getBoolean(obj, "variables[0]")
        );
    }

    @Test
    public void boolean_type_mismatch_2() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getBoolean(obj, "variables[2]")
        );
    }

    @Test
    public void boolean_type_mismatch_3() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getBoolean(obj, "")
        );

    }

    @Test
    public void boolean_type_mismatch_4() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getBoolean(obj, "item[0].item[0].request.method")
        );

    }


    @Test
    public void array_type_mismatch_0() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getList(obj, "variables[0]")
        );

    }

    @Test
    public void array_type_mismatch_1() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getList(obj, "variables[1]")
        );

    }

    @Test
    public void array_type_mismatch_2() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getList(obj, "variables[2]")
        );

    }

    @Test
    public void array_type_mismatch_3() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getList(obj, "")
        );

    }

    @Test
    public void array_type_mismatch_4() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getList(obj, "item[0].item[0].request.method")
        );

    }


    @Test
    public void long_type_mismatch_0() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getLong(obj, "variables")
        );

    }

    @Test
    public void long_type_mismatch_1() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getLong(obj, "variables[1]")
        );

    }

    @Test
    public void long_type_mismatch_2() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getLong(obj, "variables[2]")
        );

    }

    @Test
    public void long_type_mismatch_3() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getLong(obj, "")
        );

    }

    @Test
    public void long_type_mismatch_4() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getLong(obj, "item[0].item[0].request.method")
        );

    }


    @Test
    public void double_type_mismatch_0() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getDouble(obj, "variables")
        );

    }

    @Test
    public void double_type_mismatch_1() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getDouble(obj, "variables[1]")
        );

    }

    @Test
    public void double_type_mismatch_2() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getDouble(obj, "variables[0]")
        );

    }

    @Test
    public void double_type_mismatch_3() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getDouble(obj, "")
        );

    }

    @Test
    public void double_type_mismatch_4() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getDouble(obj, "item[0].item[0].request.method")
        );

    }


    @Test
    public void object_type_mismatch_0() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getObject(obj, "variables")
        );

    }

    @Test
    public void object_type_mismatch_1() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getObject(obj, "variables[1]")
        );

    }

    @Test
    public void object_type_mismatch_2() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getObject(obj, "variables[0]")
        );

    }

    @Test
    public void object_type_mismatch_3() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getObject(obj, "item[0].item[0].request.method")
        );

    }

    @Test
    public void object_type_mismatch_4() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getObject(obj, "variables[2]")
        );

    }


    @Test
    public void string_type_mismatch_0() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getString(obj, "variables")
        );

    }

    @Test
    public void string_type_mismatch_1() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getString(obj, "variables[1]")
        );

    }

    @Test
    public void string_type_mismatch_2() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getString(obj, "variables[0]")
        );

    }

    @Test
    public void string_type_mismatch_3() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getString(obj, "")
        );

    }

    @Test
    public void string_type_mismatch_4() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getString(obj, "variables[2]")
        );

    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //missing keys
    @Test
    public void malformed_key_0() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0].item[0].request.header[2].ke")
        );
    }

    @Test
    public void malformed_key_1() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0].item[0].request.heade[2].key")
        );
    }

    @Test
    public void malformed_key_2() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0].item[0].reques.header[2].key")
        );
    }

    @Test
    public void malformed_key_3() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0].ite[0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_4() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "ite[0].item[0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_5() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item0].item[0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_6() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0.item[0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_7() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[].item[0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_8() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0].item0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_9() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0].item[].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_10() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0].item[0.request.header[2].key")
        );
    }

    @Test
    public void malformed_key_11() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0].item[0].request.header2].key")
        );
    }

    @Test
    public void malformed_key_12() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0].item[0].request.header[.key")
        );
    }

    @Test
    public void malformed_key_13() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0].item[0].request.header[2]key")
        );
    }

    @Test
    public void malformed_key_14() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0].item[0].requestheader[2].key")
        );
    }

    @Test
    public void malformed_key_15() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0].item[0]request.header[2].key")
        );
    }

    @Test
    public void malformed_key_16() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0]item[0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_17() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[1].item[0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_18() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[-12].item[0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_19() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0].nope.item[0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_20() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "nope")
        );
    }

    @Test
    public void malformed_key_21() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0].item[0].request.12.header[2].key")
        );
    }

    @Test
    public void malformed_key_22() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0].item[0][0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_23() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0].item{0}.request.header[2].key")
        );
    }

    @Test
    public void malformed_key_24() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0].item[0].request.header[a].key")
        );
    }

    @Test
    public void malformed_key_25() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "[0]")
        );
    }

    @Test
    public void malformed_key_26() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "{}")
        );
    }

    @Test
    public void malformed_key_27() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0]].item[0].request.header[2].key")
        );
    }

    @Test
    public void malformed_key_28() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0].item[0]..request.header[2].key")
        );
    }

    @Test
    public void malformed_key_29() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0].item[0].request.header[2].key.")
        );
    }

    @Test
    public void malformed_key_30() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(obj, "item[0].item[0].requ est.header[2].key")
        );
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Deep nesting Gets
    //Success////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void deep_nest_key_1() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(deepNesting, "variables[0][1]");
    }

    @Test
    public void deep_nest_key_2() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(deepNesting, "variables[0][1][2]");
    }

    @Test
    public void deep_nest_key_3() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(deepNesting, "variables[0][1][2].nested1?");
    }

    @Test
    public void deep_nest_key_4() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(deepNesting, "variables[0][1][2].nested1?[1]");
    }

    @Test
    public void deep_nest_key_5() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getAny(deepNesting, "variables[0][1][2].nested1?[1][3].final");
    }

    @Test
    public void deep_nest_key_6() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getList(deepNesting, "variables[0][1][2].nested1?[1]");
    }

    @Test
    public void deep_nest_key_7() throws KeyDifferentTypeException, KeyNotFoundException {
        JSONParser.getString(deepNesting, "variables[0][1][2].nested1?[1][3].final");
    }

    @Test
    public void deep_nest_key_8() throws KeyDifferentTypeException, KeyNotFoundException {
        assertEquals("Congrats It Works",
                JSONParser.getString(deepNesting, "variables[0][1][2].nested1?[1][3].final"));
    }


    @Test
    public void getAllFromArray()
            throws KeyDifferentTypeException, KeyNotFoundException, JSONParseException {
        IJson obj = JSONParser
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
            throws KeyDifferentTypeException, KeyNotFoundException, JSONParseException {
        IJson obj = JSONParser
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
                JSONParser.getAny(deepNesting, "variables[0][1][2].nested1?[1][3].fial")
        );
    }

    @Test
    public void deep_nest_key_10() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(deepNesting, "variables[0][1].[2].nested1?[1][3].final")
        );
    }

    @Test
    public void deep_nest_key_11() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(deepNesting, "variables[0][1][3].nested1?[1][3].final")
        );
    }

    @Test
    public void deep_nest_key_12() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(deepNesting, "variables[0][1][2].nested1?[1][-1].final")
        );
    }

    @Test
    public void deep_nest_key_13() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(deepNesting, "variables[0][1][2].nested1[1][3].final")
        );
    }

    @Test
    public void deep_nest_key_14() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getObject(deepNesting, "variables[0][1][2].nested1?[1]")
        );
    }

    @Test
    public void deep_nest_key_15() {
        assertThrows(KeyDifferentTypeException.class, () ->
                JSONParser.getLong(deepNesting, "variables[0][1][2].nested1?[1][3].final")
        );
    }

    @Test
    public void deep_nest_key_16() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(deepNesting, "variables[0][")
        );
    }

    @Test
    public void deep_nest_key_17() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(deepNesting, "variables[0][1")
        );
    }

    @Test
    public void deep_nest_key_18() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(deepNesting, "variables[0][1][2")
        );
    }

    @Test
    public void deep_nest_key_19() {
        assertThrows(KeyNotFoundException.class, () ->
                JSONParser.getAny(deepNesting, "variables[]")
        );
    }
}