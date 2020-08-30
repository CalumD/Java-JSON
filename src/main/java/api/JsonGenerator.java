package api;

import exceptions.JsonException;

public interface JsonGenerator {

    Json convertToJSON() throws JsonException;
}
