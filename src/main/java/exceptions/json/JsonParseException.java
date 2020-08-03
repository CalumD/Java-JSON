package exceptions.json;

import exceptions.JsonException;

public class JsonParseException extends JsonException {

    public JsonParseException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }
}
