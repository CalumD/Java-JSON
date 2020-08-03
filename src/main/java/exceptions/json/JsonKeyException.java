package exceptions.json;

import exceptions.JsonException;

public class JsonKeyException extends JsonException {
    public JsonKeyException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
