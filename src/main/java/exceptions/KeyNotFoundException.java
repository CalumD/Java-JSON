package exceptions;

public class KeyNotFoundException extends JSONKeyException {

    public KeyNotFoundException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
