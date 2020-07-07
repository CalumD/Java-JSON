package core;

import api.JSONParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

class JSONBuilderTest {

    @Test
    public void doTests() {
        fail("Yet to implement these tests.");
    }

    @Test
    public void testBuilderPattern() {
        fail("Yet to implement these tests.");
    }

    @Test
    public void testConvertToJSON() {
        fail("Yet to implement these tests.");
    }

    @Test
    public void convertFromJSON() {
        new JSONBuilder().convertFromJSON(JSONParser.parse("true"));
        fail("Yet to implement these tests.");
    }
}