package exceptions;

/**
 * The top level exception.
 * Used to encapsulate all possible custom exceptions which can be thrown by the Java-JSON framework.
 *
 * @see BuildException
 * @see SchemaException
 * @see exceptions.json.JsonKeyException
 * @see exceptions.json.JsonParseException
 */
public class JsonException extends RuntimeException {
    public JsonException(String reasonForInvalidity) {
        super(reasonForInvalidity);
    }

    public JsonException(String reasonForInvalidity, Throwable subException) {
        super(reasonForInvalidity, subException);
    }
}
