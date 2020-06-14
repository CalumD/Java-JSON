package exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuildExceptionTest {

    @Test
    public void basicPOJO() {
        JSONException exception = new BuildException("Some error text");
        assertEquals("Some error text", exception.getMessage());
    }
}
