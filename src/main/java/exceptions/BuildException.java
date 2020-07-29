package exceptions;

public class BuildException extends JsonException {

    public BuildException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }

    public BuildException(String reasonForInvalidity, Throwable subException) {
        super(reasonForInvalidity, subException);
    }
}
