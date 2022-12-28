package com.clumd.projects.javajson.api;

import com.clumd.projects.javajson.exceptions.BuildException;
import com.clumd.projects.javajson.exceptions.json.JsonParseException;

/**
 * Defines the required methods to build a JSON object from Java in this framework.
 * The minimum wrapper for the output is an Object.
 * You cannot return an array, or any primitive JSON datatype without first wrapping them in an object.
 * <p>
 * For each add method, If any element of the key does not already exist it will be created along the way.
 * Additionally, when adding to an object, if your key has spaces, you can use ['spaced key'] notation for this.
 * When adding to an array, you should leave the array accessor anonymous, e.g. someArray[]
 * However, if you are adding to an existing element of an array, you should address the index. e.g. someArray[2]
 */
public interface JsonBuilder extends JsonGenerator {

    /**
     * Used to create an empty JSON shell.
     *
     * @return An empty JSON builder, which will default to an object.
     */
    JsonBuilder builder();

    /**
     * Add a boolean to this JSON object.
     *
     * @param path  The JSON path/key to place the object at.
     * @param value The boolean value to set at the given key.
     * @return The current JSON object, with the new value assigned.
     * @throws BuildException Thrown if the key was malformed or conflicts with values already present along it's path
     */
    JsonBuilder addBoolean(String path, boolean value) throws BuildException;

    /**
     * Add a long to this JSON object.
     *
     * @param path  The JSON path/key to place the object at.
     * @param value The long value to set at the given key.
     * @return The current JSON object, with the new value assigned.
     * @throws BuildException Thrown if the key was malformed or conflicts with values already present along it's path
     */
    JsonBuilder addLong(String path, long value) throws BuildException;

    /**
     * Add a double to this JSON object.
     *
     * @param path  The JSON path/key to place the object at.
     * @param value The double value to set at the given key.
     * @return The current JSON object, with the new value assigned.
     * @throws BuildException Thrown if the key was malformed or conflicts with values already present along it's path
     */
    JsonBuilder addDouble(String path, double value) throws BuildException;

    /**
     * Add a string to this JSON object.
     *
     * @param path  The JSON path/key to place the object at.
     * @param value The string value to set at the given key.
     * @return The current JSON object, with the new value assigned.
     * @throws BuildException Thrown if the key was malformed or conflicts with values already present along it's path
     */
    JsonBuilder addString(String path, String value) throws BuildException;

    /**
     * Add another JsonBuilder (representing an object) with partially filled values to this JSON object.
     *
     * @param path  The JSON path/key to place the object at.
     * @param value The JsonBuilder value to set at the given key.
     * @return The current JSON object, with the new value assigned.
     * @throws BuildException Thrown if the key was malformed or conflicts with values already present along it's path
     */
    JsonBuilder addBuilderBlock(String path, JsonBuilder value) throws BuildException;

    /**
     * Use an existing, compiled piece of JSON, converted to a JsonBuilder, then assigned to the value at the path.
     *
     * @param path  The JSON path/key to place the object at.
     * @param value The JSON value to set at the given key.
     * @return The current JSON object, with the new value assigned.
     * @throws BuildException Thrown if the key was malformed or conflicts with values already present along it's path
     */
    JsonBuilder addBuilderBlock(String path, Json value) throws BuildException;

    /**
     * Use an existing, compiled JSON OBJECT, attempt to retain all information from the current builder, and all new
     * information from the object to merge. This method should treat the data to be merged with higher importance.
     * (e.g. any data in the input Object which has a key already existing in the builder, should overwrite the
     * builder.)
     * <p>
     * The key difference between this and the {@link #addBuilderBlock} methods, is that those are designed to insert a
     * full piece of data at a predefined path, whereas this is designed to dynamically select properties from the input
     * object to use in the output of the builder.
     * <p>
     * N.B.  This method WILL NOT throw if you try to merge an object which contains properties of the same "key"
     * pathing, but different data-types, however the .build() method used elsewhere to transform this
     * {@link JsonBuilder} into a {@link Json} WILL throw with duplicate key warnings.
     *
     * @param value The JSON value to merge into the current builder.
     * @return The current JSON builder, with the additional data merged in.
     * @throws BuildException Thrown if there was something wrong with the input Json.
     */
    JsonBuilder mergeExistingObject(Json value);

    /**
     * Break a full JSON object down into a builder representation of itself to be added to.
     *
     * @param json The JSON object to break into a builder representation.
     * @return The given JSON object, as a JsonBuilder.
     */
    JsonBuilder convertFromJSON(Json json);

    /**
     * Used to finalise the current builder into a fully compiled Java-JSON object.
     *
     * @return The compiled JSON object with all values set through this builder.
     * @throws JsonParseException Thrown in the rare circumstance of building up an object using the merge function
     *                            where the object to merge contains the same key but of a different datatype.
     */
    Json build() throws BuildException;
}
