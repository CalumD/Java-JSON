package Exceptions;

public class ConfigParseException extends Exception {

    public ConfigParseException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
