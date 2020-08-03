package exceptions.json;

public class KeyNotFoundException extends JsonKeyException {

    public KeyNotFoundException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
