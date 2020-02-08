package Exceptions.JSON;

public class ParseException extends Exception {

    public ParseException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
