package core;

import exceptions.JSONParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JSONTest {

    /**
     * Can Ignore any implemented method, since they are only there to get an instance of a 'JSON.java'.
     */
    private class AbsractClass extends JSON {

        AbsractClass(JSONTape parsingTape) throws JSONParseException {
            super(parsingTape);
        }

        @Override
        public Object getValue() {
            return null;
        }

        @Override
        public String asString(int depth) {
            return null;
        }

        @Override
        public boolean equals(Object otherJSON) {
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }

    private AbsractClass absractClass;
    private AbsractClass booleanClass;
    private AbsractClass stringClass;
    private AbsractClass longClass;
    private AbsractClass doubleClass;
    private AbsractClass arrayClass;
    private AbsractClass objectClass;

    @BeforeEach
    public void setup() {
        absractClass = new AbsractClass(new JSONTape(""));
        booleanClass = new AbsractClass(new JSONTape("true"));
        stringClass = new AbsractClass(new JSONTape("\"hello world\""));
        longClass = new AbsractClass(new JSONTape("1234"));
        doubleClass = new AbsractClass(new JSONTape("1.23"));
        arrayClass = new AbsractClass(new JSONTape("[]"));
        objectClass = new AbsractClass(new JSONTape("{}"));
    }

    @Test
    public void doNothingYet() {
        System.out.println(absractClass);
    }
}
