package exceptions.json;

public class KeyDifferentTypeException extends JsonKeyException {

    public KeyDifferentTypeException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
