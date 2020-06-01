package core;

import exceptions.KeyDifferentTypeException;
import exceptions.KeyInvalidException;
import exceptions.KeyNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JSONKeyTest {

    private JSONKey key;
    private static final String ALL_VARIATION_KEY = "complicated.key[4][5][6].n3sted.objects['thes\\\"e  K\"e'ys']['too'][0].\uD83C\uDF54.last";

    @BeforeEach
    public void setup() {
        key = new JSONKey(ALL_VARIATION_KEY);
    }

    @Test
    public void canCopeWithLeadingSpaces() {
        try {
            new JSONKey("              key");
            new JSONKey("\t\t\t\t\tkey");
            new JSONKey("\n\n\n\n\nkey");
            new JSONKey("\r\r\r\r\rkey");
        } catch (Exception e) {
            fail("The try should not have caused an exception in this instance.");
        }
    }

    @Test
    public void canCopeWithTrailingSpaces() {
        try {
            new JSONKey("key              ");
            new JSONKey("key\t\t\t\t\t\t");
            new JSONKey("key\n\n\n\n\n\n");
            new JSONKey("key\r\r\r\r\r\r");
        } catch (Exception e) {
            fail("The try should not have caused an exception in this instance.");
        }
    }

    @Test
    public void shouldNotAcceptANull() {
        try {
            new JSONKey(null);
        } catch (KeyInvalidException e) {
            assertEquals(e.getMessage(), "Key cannot be null");
        } catch (Exception e) {
            fail("This class should only have thrown a KeyInvalid Exception");
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
            fail("The try should not have caused an exception in this instance.");
        }

        try {
            key.getNextKey();
            fail("The previous line should have thrown an exception.");
        } catch (KeyNotFoundException e) {
            assertEquals(e.getMessage(), "End of Key reached. Have already traversed the whole hierarchy.");
        } catch (Exception e) {
            fail("This class should only have thrown a KeyNotFound Exception");
        }
    }

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
            key.createKeyNotFoundException();
            fail("Previous line should have thrown an exception");
        } catch (KeyNotFoundException e) {
            assertEquals("complicated not found on element: <base element>", e.getMessage());
        }
    }

    @Test
    public void keyNotFound2() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.createKeyNotFoundException();
            fail("Previous line should have thrown an exception");
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
            key.createKeyNotFoundException();
            fail("Previous line should have thrown an exception");
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
            key.createKeyNotFoundException();
            fail("Previous line should have thrown an exception");
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
            key.createKeyNotFoundException();
            fail("Previous line should have thrown an exception");
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
            key.createKeyNotFoundException();
            fail("Previous line should have thrown an exception");
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
            key.createKeyNotFoundException();
            fail("Previous line should have thrown an exception");
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
            key.createKeyNotFoundException();
            fail("Previous line should have thrown an exception");
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
            key.createKeyNotFoundException();
            fail("Previous line should have thrown an exception");
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
            key.createKeyNotFoundException();
            fail("Previous line should have thrown an exception");
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
            key.createKeyNotFoundException();
            fail("Previous line should have thrown an exception");
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
            key.createKeyNotFoundException();
            fail("Previous line should have thrown an exception");
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
        assertThrows(IndexOutOfBoundsException.class, () ->
                key.createKeyNotFoundException()
        );
    }

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
            key.createKeyDifferentTypeException();
            fail("Previous line should have thrown an exception");
        } catch (KeyDifferentTypeException e) {
            assertEquals("complicated is not a valid accessor on element: <base element>", e.getMessage());
        }
    }

    @Test
    public void keyDifferentType2() {
        try {
            key.getNextKey();
            key.getNextKey();
            key.createKeyDifferentTypeException();
            fail("Previous line should have thrown an exception");
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
            key.createKeyDifferentTypeException();
            fail("Previous line should have thrown an exception");
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
            key.createKeyDifferentTypeException();
            fail("Previous line should have thrown an exception");
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
            key.createKeyDifferentTypeException();
            fail("Previous line should have thrown an exception");
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
            key.createKeyDifferentTypeException();
            fail("Previous line should have thrown an exception");
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
            key.createKeyDifferentTypeException();
            fail("Previous line should have thrown an exception");
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
            key.createKeyDifferentTypeException();
            fail("Previous line should have thrown an exception");
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
            key.createKeyDifferentTypeException();
            fail("Previous line should have thrown an exception");
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
            key.createKeyDifferentTypeException();
            fail("Previous line should have thrown an exception");
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
            key.createKeyDifferentTypeException();
            fail("Previous line should have thrown an exception");
        } catch (KeyDifferentTypeException e) {
            assertEquals("\uD83C\uDF54 is not a valid accessor on element: complicated.key[4][5][6].n3sted.objects[\"thes\\\"e  K\\\"e'ys\"][\"too\"][0]", e.getMessage());
        }
    }

    @Test
    public void keyDifferentTyp12() {
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
            key.createKeyDifferentTypeException();
            fail("Previous line should have thrown an exception");
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
        assertThrows(IndexOutOfBoundsException.class, () ->
                key.createKeyDifferentTypeException()
        );
    }
}