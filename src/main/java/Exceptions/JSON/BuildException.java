package Exceptions.JSON;

public class BuildException extends Exception {

    public BuildException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
