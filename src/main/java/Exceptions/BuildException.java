package Exceptions;

public class BuildException extends JSONParseException {

    public BuildException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}