package com.clumd.projects.javajson.api;

/**
 * This interface defines the method used to unwrap a Java-JSON representation into a 'fully-qualified' Java object,
 * devoid of any control, functionality or influence from this framework.
 *
 * @param <E> The class type which the implementor will convert any provided JSON object into
 */
public interface JsonMapper<E> {

    /**
     * Used to convert the given JSON into a pure, top level Java object.
     *
     * @param json The JSON to convert into a pure Java object.
     * @return The successfully converted Java Object instance.
     * @throws Exception Can be thrown by an implementor if the provided JSON did not satisfy some constraint of
     *                   the conversion process - e.g. missing a required property.
     *                   <p>
     *                   For more advanced implementations, consider writing a JSON schema, and using the methods in
     *                   {@link JsonSchemaEnforceable} to enforce the structure and any additional constraints
     *                   of a JSON object and its properties before attempting to convert it to Java.
     */
    E mapToClass(Json json) throws Exception;
}
