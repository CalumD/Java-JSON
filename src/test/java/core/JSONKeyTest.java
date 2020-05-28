package core;

import exceptions.KeyInvalidException;
import exceptions.KeyNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class JSONKeyTest {

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
        JSONKey key = new JSONKey("complicated.key[4][5][6].n3sted.objects['thes\\\"e  K\"e'ys']['too'][0].\uD83C\uDF54.last");

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
}