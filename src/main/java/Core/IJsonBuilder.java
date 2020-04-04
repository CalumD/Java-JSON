package Core;

import Exceptions.BuildException;

/**
 * This interface defines the methods for dynamically creating a JSON object from contents of a
 * class
 */
public interface IJsonBuilder {

    /**
     * Used to add a boolean to the JSON scaffold
     *
     * @param path The path to add this new element too.
     * @param value The value of the new element to add.
     * @throws BuildException Thrown if there was a syntactic error with the path.
     */
    void addBoolean(String path, boolean value) throws BuildException;

    /**
     * Used to add a boolean to the JSON scaffold
     *
     * @param path The path to add this new element too.
     * @param value The value of the new element to add.
     * @throws BuildException Thrown if there was a syntactic error with the path.
     */
    void addLong(String path, long value) throws BuildException;

    /**
     * Used to add a double to the JSON scaffold
     *
     * @param path The path to add this new element too.
     * @param value The value of the new element to add.
     * @throws BuildException Thrown if there was a syntactic error with the path.
     */
    void addDouble(String path, double value) throws BuildException;

    /**
     * Used to add a String to the JSON scaffold
     *
     * @param path The path to add this new element too.
     * @param value The value of the new element to add.
     * @throws BuildException Thrown if there was a syntactic error with the path.
     */
    void addString(String path, String value) throws BuildException;

    /**
     * Used to add a sub json Object to the JSON scaffold
     *
     * @param path The path to add this new element too.
     * @param value The value of the new element to add.
     * @throws BuildException Thrown if there was a syntactic error with the path.
     */
    void addBuilderBlock(String path, IJsonBuilder value) throws BuildException;

    /**
     * Used to convert the current JSON object from a "scaffold" state to an implemented JSON
     * structure
     *
     * @return The actual JSON representation of what has been built
     * @throws BuildException If there was some error converting this scaffold to a real json
     *                        object.
     */
    IJson convertToJSON() throws BuildException;
}
