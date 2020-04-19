package oldTests.Json;

import Core.IJson;
import Core.IJsonBuilder;
import Core.JSONBuilder;
import Exceptions.BuildException;
import Exceptions.KeyDifferentTypeException;
import Exceptions.KeyNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class BuildJson {

    private static IJsonBuilder builder;
    private static IJson getter;

    private static void resetGets() {
        IJsonBuilder pre = new JSONBuilder(), last = new JSONBuilder();

        try {
            last.addBoolean("bools.bool1", false);
            last.addDouble("new", 4.5);
            last.addLong("camerlong", 1000000L);
            last.addString("Aloha", "World");

            pre.addBoolean("a[0][].nest1", false);
            pre.addLong("a[]", 1337L);
            pre.addBoolean("a[].canItBe?", false);
            pre.addBoolean("a[]", true);
            pre.addString("b[]", "Secret Internal String");
            pre.addDouble("a[0][0].degrees", 0.45);
            pre.addString("b[]", "Another one");
            pre.addBoolean("a[0][].success", true);
            pre.addLong("a[0][1].final[]", 1L);

            last.addBuilderBlock("last[].old", pre);
            last.addBoolean("last[].new.vars.object.deepArray[].value", false);
            last.addBoolean("last[1].new.arrs[0].nope", true);

            getter = last.convertToJSON();
        } catch (BuildException e) {
            fail("Failed to Build the object to check 'get' ordering");
        }
    }

    @BeforeEach
    public void clear() {
        builder = new JSONBuilder();
    }


    //BASIC BUILD////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void basic_build_1() throws BuildException {
        builder.addBoolean("root", true);
    }

    @Test
    public void basic_build_2() throws BuildException {
        builder.addBoolean("root", false);
    }

    @Test
    public void basic_build_3() throws BuildException {
        builder.addDouble("root", Double.parseDouble("3.1415"));
    }

    @Test
    public void basic_build_4() throws BuildException {
        builder.addDouble("root", Double.parseDouble("-3.1415"));
    }

    @Test
    public void basic_build_5() throws BuildException {
        builder.addLong("root", 10L);
    }

    @Test
    public void basic_build_6() throws BuildException {
        builder.addLong("root", -5L);
    }

    @Test
    public void basic_build_7() throws BuildException {
        builder.addString("root", "Hello World");
    }


    //Simple Array BUILD/////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void basic_build_8() throws BuildException {
        builder.addBoolean("arr[]", true);
    }

    @Test
    public void basic_build_9() throws BuildException {
        builder.addDouble("arr[]", 0.546631);
    }

    @Test
    public void basic_build_10() throws BuildException {
        builder.addLong("arr[]", 9081263L);
    }

    @Test
    public void basic_build_11() throws BuildException {
        builder.addString("arr[]", "New String");
    }

    @Test
    public void basic_build_12() throws BuildException {
        builder.addBoolean("array[0][]", true);
    }

    @Test
    public void basic_build_13() throws BuildException {
        builder.addBoolean("array[0][].isValid", true);
    }

    @Test
    public void basic_build_14() throws BuildException {
        builder.addBoolean("arr[]", true);
        builder.addDouble("arr[]", 0.546631);
        builder.addLong("arr[]", 9081263L);
        builder.addString("arr[]", "New String");
        builder.addBoolean("arr[].isValid", true);
    }

    @Test
    public void basic_build_15() throws BuildException {
        builder.addBoolean("arr[0][]", true);
        builder.addString("arr[0][]", "String");
    }


    //advanced Object Building//////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void advanced_build_1() throws BuildException {
        builder.addString("init[].Name", "Str");
    }

    @Test
    public void advanced_build_2() throws BuildException {
        builder.addBoolean("init[].values.bools.deeplyNested.array[].finalValue", false);
    }

    @Test
    public void advanced_build_3() throws BuildException {
        builder.addBoolean("init[].values.rename[0].nope", true);
    }

    @Test
    public void advanced_build_4() throws BuildException {
        builder.addLong("init[]", 123456789L);
        builder.addBoolean("init[].valid1", true);
        builder.addBoolean("init[1].valid2", false);
        builder.addBoolean("init[1].valid3", true);
        builder.addString("init[]", "Some other value");
    }


    //Expected Building Failures //////////////////////////////////////////////////////////////////////////////////
    @Test
    public void fails_1() throws BuildException {
        assertThrows(BuildException.class, () ->
                builder.addBoolean("init[1]", true)
        );
    }

    @Test
    public void fails_2() {
        assertThrows(BuildException.class, () ->
                builder.addBoolean("init[-1]", true)
        );
    }

    @Test
    public void fails_3() {
        assertThrows(BuildException.class, () ->
                builder.addBoolean("init[][]", true)
        );
    }

    @Test
    public void fails_4() {
        assertThrows(BuildException.class, () ->
                builder.addBoolean("init[1][]", true)
        );
    }

    @Test
    public void fails_5() {
        assertThrows(BuildException.class, () ->
                builder.addBoolean("init[0][1]", true)
        );
    }

    @Test
    public void fails_6() {
        assertThrows(BuildException.class, () -> {
            builder.addBoolean("one[]", true);
            builder.addBoolean("one[0]", true);
        });
    }

    @Test
    public void fails_7() {
        assertThrows(BuildException.class, () ->
                builder.addBoolean("one.two[[", true)
        );
    }

    @Test
    public void fails_8() {
        assertThrows(BuildException.class, () ->
                builder.addBoolean("one.two]]", true)
        );
    }

    @Test
    public void fails_9() {
        assertThrows(BuildException.class, () ->
                builder.addBoolean("", true)
        );
    }


    //GETS This portion checks that the Core.IJsonBuilder creates a json string with the values in the right place//////////
    @Test
    public void get_1() throws KeyDifferentTypeException, KeyNotFoundException {
        resetGets();
        assertFalse(getter.getBooleanAt("bools.bool1"));
    }

    @Test
    public void get_2() throws KeyDifferentTypeException, KeyNotFoundException {
        resetGets();
        assertEquals(4.5, getter.getDoubleAt("new"), 0);
    }

    @Test
    public void get_3() throws KeyDifferentTypeException, KeyNotFoundException {
        resetGets();
        assertEquals(1000000L, getter.getLongAt("camerlong"), 0);
    }

    @Test
    public void get_4() throws KeyDifferentTypeException, KeyNotFoundException {
        resetGets();
        assertEquals("World", getter.getStringAt("Aloha"));
    }

    @Test
    public void get_5() throws KeyDifferentTypeException, KeyNotFoundException {
        resetGets();
        assertFalse(getter.getBooleanAt("last[1].new.vars.object.deepArray[0].value"));
    }

    @Test
    public void get_6() throws KeyDifferentTypeException, KeyNotFoundException {
        resetGets();
        assertTrue(getter.getBooleanAt("last[1].new.arrs[0].nope"));
    }

    @Test
    public void get_7() throws KeyDifferentTypeException, KeyNotFoundException {
        resetGets();
        assertFalse(getter.getBooleanAt("last[0].old.a[0][0].nest1"));
    }

    @Test
    public void get_8() throws KeyDifferentTypeException, KeyNotFoundException {
        resetGets();
        assertEquals(1337L, getter.getLongAt("last[0].old.a[1]"), 0);
    }

    @Test
    public void get_9() throws KeyDifferentTypeException, KeyNotFoundException {
        resetGets();
        assertFalse(getter.getBooleanAt("last[0].old.a[2].canItBe?"));
    }

    @Test
    public void get_10() throws KeyDifferentTypeException, KeyNotFoundException {
        resetGets();
        assertTrue(getter.getBooleanAt("last[0].old.a[3]"));
    }

    @Test
    public void get_11() throws KeyDifferentTypeException, KeyNotFoundException {
        resetGets();
        assertEquals("Secret Internal String", getter.getStringAt("last[0].old.b[0]"));
    }

    @Test
    public void get_12() throws KeyDifferentTypeException, KeyNotFoundException {
        resetGets();
        assertEquals(0.45, getter.getDoubleAt("last[0].old.a[0][0].degrees"), 0);
    }

    @Test
    public void get_13() throws KeyDifferentTypeException, KeyNotFoundException {
        resetGets();
        assertEquals("Another one", getter.getStringAt("last[0].old.b[1]"));
    }

    @Test
    public void get_14() throws KeyDifferentTypeException, KeyNotFoundException {
        resetGets();
        assertTrue(getter.getBooleanAt("last[0].old.a[0][1].success"));
    }

    @Test
    public void get_15() throws KeyDifferentTypeException, KeyNotFoundException {
        resetGets();
        assertEquals(1L, getter.getLongAt("last[0].old.a[0][1].final[0]"), 0);
    }
}

