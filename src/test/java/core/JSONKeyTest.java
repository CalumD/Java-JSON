package core;

import exceptions.KeyDifferentTypeException;
import exceptions.KeyInvalidException;
import exceptions.KeyNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JSONKeyTest {

    private JSONKey key;
    private static final String ALL_VARIATION_KEY = "complicated.key[4][5][6].n3sted.objects['thes\\\"e  K\"e'ys']['too'][0].\uD83C\uDF54.last";

    @BeforeEach
    public void setup() {
        key = new JSONKey(ALL_VARIATION_KEY, false);
    }

    @Test
    public void callingForRegularKeyShouldNotAllowUsAnonArrayKeys() {
        try {
            new JSONKey("key[][][][]", false);
            fail("The previous method call should have thrown an exception.");
        } catch (Exception e) {
            assertEquals("Failed to parse array accessor in key. Element was not a valid integer.\n" +
                    "Line: 1\n" +
                    "Reached: key[_\n" +
                    "Expected: <positive integer>", e.getMessage());
        }
    }

    @Test
    public void callingForBuilderKeyAllowsUsAnonArrayKeys() {
        try {
            new JSONKey("key[][][][]", true);
        } catch (Exception e) {
            fail("The try should not have caused an exception in this instance.", e);
        }
    }

    @Test
    public void canCopeWithLeadingSpaces() {
        try {
            new JSONKey("              key", false);
            new JSONKey("\t\t\t\t\tkey", false);
            new JSONKey("\n\n\n\n\nkey", false);
            new JSONKey("\r\r\r\r\rkey", false);
        } catch (Exception e) {
            fail("The try should not have caused an exception in this instance.", e);
        }
    }

    @Test
    public void canCopeWithTrailingSpaces() {
        try {
            new JSONKey("key              ", false);
            new JSONKey("key\t\t\t\t\t\t", false);
            new JSONKey("key\n\n\n\n\n\n", false);
            new JSONKey("key\r\r\r\r\r\r", false);
        } catch (Exception e) {
            fail("The try should not have caused an exception in this instance.", e);
        }
    }

    @Test
    public void shouldDenyDoubleDotting() {
        try {
            new JSONKey("key1..key2", false);
        } catch (Exception e) {
            assertEquals("Bad use of '.' separator in key.\n" +
                    "Line: 1\n" +
                    "Reached: key1._\n" +
                    "Expected: [ / <Object Key>", e.getMessage());
        }
    }

    @Test
    public void shouldNotAcceptANull() {
        try {
            new JSONKey(null, false);
        } catch (KeyInvalidException e) {
            assertEquals(e.getMessage(), "Key cannot be null");
        } catch (Exception e) {
            fail("This class should only have thrown a KeyInvalid Exception", e);
        }
    }

    @Test
    public void shouldNotAcceptEmptyKey() {
        try {
            new JSONKey("", false);
        } catch (Exception e) {
            fail("The try should not have caused an exception in this instance.", e);
        }
    }

    @Test
    public void checkNextKey() {
        try {
            assertEquals("{complicated", key.getNextKey());
            assertEquals("{key", key.getNextKey());
            assertEquals("[4", key.getNextKey());
            assertEquals("[5", key.getNextKey());
            assertEquals("[6", key.getNextKey());
            assertEquals("{n3sted", key.getNextKey());
            assertEquals("{objects", key.getNextKey());
            assertEquals("<thes\\\"e  K\"e'ys", key.getNextKey());
            assertEquals("<too", key.getNextKey());
            assertEquals("[0", key.getNextKey());
            assertEquals("{\uD83C\uDF54", key.getNextKey());
            assertEquals("{last", key.getNextKey());
            assertEquals("", key.getNextKey());
        } catch (Exception e) {
            fail("The try should not have caused an exception in this instance.", e);
        }

        try {
            key.getNextKey();
            fail("The previous line should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals(e.getMessage(), "End of Key reached. Have already traversed the whole hierarchy.");
        } catch (Exception e) {
            fail("This class should only have thrown a KeyNotFound Exception", e);
        }
    }

    @SuppressWarnings("ThrowableNotThrown")
    @Test
    public void indexOutOfBoundsLower1() {
        assertThrows(IndexOutOfBoundsException.class, () ->
                key.createKeyNotFoundException()
        );
    }

    @Test
    public void keyNotFound1() {
        try {
            key.getNextKey();
            throw key.createKeyNotFoundException();
        } catch (KeyNotFoundException e) {
            assertEquals("complicated not found on element: <base element>", e.getMessage());
        }
    }

    @Test
    public void keyNotFound2() {
        try {
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyNotFoundException();
        } catch (KeyNotFoundException e) {
            assertEquals("key not found on element: complicated", e.getMessage());
        }
    }

    @Test
    public void keyNotFound3() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyNotFoundException();
        } catch (KeyNotFoundException e) {
            assertEquals("4 not found on element: complicated.key", e.getMessage());
        }
    }

    @Test
    public void keyNotFound4() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyNotFoundException();
        } catch (KeyNotFoundException e) {
            assertEquals("5 not found on element: complicated.key[4]", e.getMessage());
        }
    }

    @Test
    public void keyNotFound5() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyNotFoundException();
        } catch (KeyNotFoundException e) {
            assertEquals("6 not found on element: complicated.key[4][5]", e.getMessage());
        }
    }

    @Test
    public void keyNotFound6() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyNotFoundException();
        } catch (KeyNotFoundException e) {
            assertEquals("n3sted not found on element: complicated.key[4][5][6]", e.getMessage());
        }
    }

    @Test
    public void keyNotFound7() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyNotFoundException();
        } catch (KeyNotFoundException e) {
            assertEquals("objects not found on element: complicated.key[4][5][6].n3sted", e.getMessage());
        }
    }

    @Test
    public void keyNotFound8() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyNotFoundException();
        } catch (KeyNotFoundException e) {
            assertEquals("thes\\\"e  K\"e'ys not found on element: complicated.key[4][5][6].n3sted.objects", e.getMessage());
        }
    }

    @Test
    public void keyNotFound9() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyNotFoundException();
        } catch (KeyNotFoundException e) {
            assertEquals("too not found on element: complicated.key[4][5][6].n3sted.objects[\"thes\\\"e  K\\\"e'ys\"]", e.getMessage());
        }
    }

    @Test
    public void keyNotFound10() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyNotFoundException();
        } catch (KeyNotFoundException e) {
            assertEquals("0 not found on element: complicated.key[4][5][6].n3sted.objects[\"thes\\\"e  K\\\"e'ys\"][\"too\"]", e.getMessage());
        }
    }

    @Test
    public void keyNotFound11() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyNotFoundException();
        } catch (KeyNotFoundException e) {
            assertEquals("\uD83C\uDF54 not found on element: complicated.key[4][5][6].n3sted.objects[\"thes\\\"e  K\\\"e'ys\"][\"too\"][0]", e.getMessage());
        }
    }

    @Test
    public void keyNotFound12() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyNotFoundException();
        } catch (KeyNotFoundException e) {
            assertEquals("last not found on element: complicated.key[4][5][6].n3sted.objects[\"thes\\\"e  K\\\"e'ys\"][\"too\"][0].\uD83C\uDF54", e.getMessage());
        }
    }

    @Test
    public void indexOutOfBoundsUpper1() {
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        assertEquals("<Anonymous Key> not found on element: complicated.key[4][5][6].n3sted.objects" +
                "[\"thes\\\"e  K\\\"e'ys\"][\"too\"][0].\uD83C\uDF54.last", key.createKeyNotFoundException().getMessage());
    }

    @SuppressWarnings("ThrowableNotThrown")
    @Test
    public void indexOutOfBoundsLower2() {
        assertThrows(IndexOutOfBoundsException.class, () ->
                key.createKeyNotFoundException()
        );
    }

    @Test
    public void keyDifferentType1() {
        try {
            key.getNextKey();
            throw key.createKeyDifferentTypeException();
        } catch (KeyDifferentTypeException e) {
            assertEquals("complicated is not a valid accessor on element: <base element>", e.getMessage());
        }
    }

    @Test
    public void keyDifferentType2() {
        try {
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyDifferentTypeException();
        } catch (KeyDifferentTypeException e) {
            assertEquals("key is not a valid accessor on element: complicated", e.getMessage());
        }
    }

    @Test
    public void keyDifferentType3() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyDifferentTypeException();
        } catch (KeyDifferentTypeException e) {
            assertEquals("4 is not a valid accessor on element: complicated.key", e.getMessage());
        }
    }

    @Test
    public void keyDifferentType4() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyDifferentTypeException();
        } catch (KeyDifferentTypeException e) {
            assertEquals("5 is not a valid accessor on element: complicated.key[4]", e.getMessage());
        }
    }

    @Test
    public void keyDifferentType5() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyDifferentTypeException();
        } catch (KeyDifferentTypeException e) {
            assertEquals("6 is not a valid accessor on element: complicated.key[4][5]", e.getMessage());
        }
    }

    @Test
    public void keyDifferentType6() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyDifferentTypeException();
        } catch (KeyDifferentTypeException e) {
            assertEquals("n3sted is not a valid accessor on element: complicated.key[4][5][6]", e.getMessage());
        }
    }

    @Test
    public void keyDifferentType7() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyDifferentTypeException();
        } catch (KeyDifferentTypeException e) {
            assertEquals("objects is not a valid accessor on element: complicated.key[4][5][6].n3sted", e.getMessage());
        }
    }

    @Test
    public void keyDifferentType8() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyDifferentTypeException();
        } catch (KeyDifferentTypeException e) {
            assertEquals("thes\\\"e  K\"e'ys is not a valid accessor on element: complicated.key[4][5][6].n3sted.objects", e.getMessage());
        }
    }

    @Test
    public void keyDifferentType9() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyDifferentTypeException();
        } catch (KeyDifferentTypeException e) {
            assertEquals("too is not a valid accessor on element: complicated.key[4][5][6].n3sted.objects[\"thes\\\"e  K\\\"e'ys\"]", e.getMessage());
        }
    }

    @Test
    public void keyDifferentType10() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyDifferentTypeException();
        } catch (KeyDifferentTypeException e) {
            assertEquals("0 is not a valid accessor on element: complicated.key[4][5][6].n3sted.objects[\"thes\\\"e  K\\\"e'ys\"][\"too\"]", e.getMessage());
        }
    }

    @Test
    public void keyDifferentType11() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyDifferentTypeException();
        } catch (KeyDifferentTypeException e) {
            assertEquals("\uD83C\uDF54 is not a valid accessor on element: complicated.key[4][5][6].n3sted.objects[\"thes\\\"e  K\\\"e'ys\"][\"too\"][0]", e.getMessage());
        }
    }

    @Test
    public void keyDifferentType12() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            key.getNextKey();
            throw key.createKeyDifferentTypeException();
        } catch (KeyDifferentTypeException e) {
            assertEquals("last is not a valid accessor on element: complicated.key[4][5][6].n3sted.objects[\"thes\\\"e  K\\\"e'ys\"][\"too\"][0].\uD83C\uDF54", e.getMessage());
        }
    }

    @Test
    public void indexOutOfBoundsUpper2() {
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        key.getNextKey();
        assertEquals("<Anonymous Key> is not a valid accessor on element: complicated.key[4][5][6].n3sted" +
                ".objects[\"thes\\\"e  K\\\"e'ys\"][\"too\"][0].\uD83C\uDF54.last", key.createKeyDifferentTypeException().getMessage());
    }

    @Test
    public void getAllKeys() {
        List<String> keys = new ArrayList<>();
        keys.add("{complicated");
        keys.add("{key");
        keys.add("[4");
        keys.add("[5");
        keys.add("[6");
        keys.add("{n3sted");
        keys.add("{objects");
        keys.add("<thes\\\"e  K\"e'ys");
        keys.add("<too");
        keys.add("[0");
        keys.add("{🍔");
        keys.add("{last");
        keys.add("");

        assertEquals(keys, key.getAllKeys());
    }
}