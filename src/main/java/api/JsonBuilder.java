package api;

import exceptions.BuildException;

public interface JsonBuilder extends JsonGenerator {

    JsonBuilder addBoolean(String path, boolean value) throws BuildException;

    JsonBuilder addLong(String path, long value) throws BuildException;

    JsonBuilder addDouble(String path, double value) throws BuildException;

    JsonBuilder addString(String path, String value) throws BuildException;

    JsonBuilder addBuilderBlock(String path, JsonBuilder value) throws BuildException;

    JsonBuilder addBuilderBlock(String path, Json value) throws BuildException;

    JsonBuilder convertFromJSON(Json json);

    Json build();
}
