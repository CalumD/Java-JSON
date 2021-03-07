package com.clumd.projects.javajson.api;

import com.clumd.projects.javajson.exceptions.JsonException;

/**
 * This defines all methods required to convert a Java object into a JSON object interperable by this framework.
 */
public interface JsonGenerator {

    /**
     * Convert the current Java Class into a Java-JSON JSON representation of itself.
     *
     * @return The Java-JSON representation of this class as a JSON object.
     * @throws JsonException Thrown if there was a problem during the conversion process from Java into JSON,
     *                       likely due to a violation of the JSON rules from this framework.
     */
    Json convertToJSON() throws JsonException;
}
