package api;

import core.JSType;
import exceptions.BuildException;
import exceptions.JSONParseException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is an implementation for the Core.IJsonBuilder interface. It is used to dynamically build
 * up a json structure from whatever code may require to save its configuration or state somehow
 * without just dumping the whole class/object to file.
 */
public class JSONBuilder implements IJsonBuilder {

    private JSType type = JSType.OBJECT;

    private final HashMap<String, Boolean> booleans = new HashMap<>();
    private final HashMap<String, String> strings = new HashMap<>();
    private final HashMap<String, Double> doubles = new HashMap<>();
    private final HashMap<String, Long> longs = new HashMap<>();
    private final HashMap<String, JSONBuilder> objects = new HashMap<>();

    private final ArrayList<Object> array = new ArrayList<>();


    /**
     * Simple constructor, required since we specify an internal, non-standard constructor for
     * nested objects.
     */
    public JSONBuilder() {
    }

    /**
     * Constructor for inner, nested objects within this JsonBuilder.
     *
     * @param objectType Should only be an Array or and Object passed here, since it it used to
     * determine content (toString()) design.
     */
    private JSONBuilder(JSType objectType) {
        type = objectType;
    }

    /**
     * Used to convert the current JSON object from a "scaffold" state to an implemented JSON
     * structure
     *
     * @return The actual JSON representation of what has been built
     * @throws BuildException If there was some error converting this scaffold to a 'real' json
     *                        object.
     */
    @Override
    public IJson convertToJSON() throws BuildException {
        try {
            //call our json parser, if there was any problem with that - assume it
            // was our fault and fail with a build exception.
            return JSONParser.parse(this.toString());
        } catch (JSONParseException e) {
            throw new BuildException(
                "An unexpected error occurred when building a JSON object: " + e.getMessage());
        }
    }

    /**
     * Used to validate the syntax and object at location of a given path into this Core.IJsonBuilder
     * Object.
     *
     * @param stringPath The path to check.
     * @return The object that already exists at that location, or a new object at that location if
     * it was valid to do so.
     * @throws BuildException Thrown if there is either a problem with the syntax of the string, or
     *                        if there was an existing object causing a conflict.
     */
    private JSONBuilder validatePath(String stringPath) throws BuildException {

        //ensure that the path is not empty
        if (stringPath.equals("") || stringPath.equals(" ")) {
            buildErr("\"\"");
        }
        //ensure that the path does not start or end with an unfinished object reference
        if (stringPath.startsWith(".") || stringPath.endsWith(".")) {
            buildErr(stringPath);
        }

        //create some structures used in the parsing of the path.
        char[] charPath = stringPath.toCharArray();
        StringBuilder stripped = new StringBuilder();
        boolean inString = false;

        //loop through the whole path
        for (char aCharPath : charPath) {

            //if we see a ", then toggle being in a string
            if (aCharPath == '"') {
                inString = !inString;
            }
            //if we see a space, only add it if we are in a string
            if (aCharPath == ' ') {
                if (inString) {
                    stripped.append(' ');
                }
            }
            //add all other characters as normal
            else {
                stripped.append(aCharPath);
            }
        }
        //split the path into its individual object references
        String[] keys = stripped.toString().split("\\.");
        ArrayList<String> path = new ArrayList<>();

        //for all key references in the path, validate them individually.
        for (String key : keys) {
            path.addAll(validateKey(key));
        }

        //get the json 'scaffold' object at the validated string.
        return getBuilder(path);
    }

    /**
     * Used to check each individual 'dot' reference in a path to a json object
     *
     * @param key The individual key to check
     * @return The return array contains each step within the key (when nested arrays are involved)
     * @throws BuildException Thrown if there is a syntactic error with the specific key, not picked
     *                        up by the whole path.
     */
    private ArrayList<String> validateKey(String key) throws BuildException {

        //ensure the string does not contain reserved characters
        if (key.contains("{")) {
            throw new BuildException("You have used a reserved character in a JSON key: {");
        }
        if (key.contains("}")) {
            throw new BuildException("You have used a reserved character in a JSON key: }");
        }

        //figure out if we need to deal with array references
        boolean containsOSB = key.contains("[");
        boolean containsCSB = key.contains("]");

        //check for mismatched brackets
        if ((containsOSB && !containsCSB) || (!containsOSB && containsCSB)) {
            throw new BuildException(
                "The input has mismatched square brackets, wont attempt to parse.");
        }

        ArrayList<String> ret = new ArrayList<>();

        //if the string does not contain brackets, we can return with a simple, 1-step path.
        if (!containsOSB) {
            ret.add(key);
            return ret;
        }

        //else further checks and splitting is required
        char[] keyAsChars = key.toCharArray();
        StringBuilder arrayAccessIndex = new StringBuilder();
        StringBuilder objectRef = new StringBuilder();
        boolean addingNumber = false, wasLastNest = false;

        //check the whole key
        for (char keyAsChar : keyAsChars) {

            if (keyAsChar == '[') {
                //Ensure that we are not on the first character as this would be an invalid key
                if (ret.size() == 0) {
                    ret.add(objectRef.toString());
                }
                //check that we are not already inside an open square bracket.
                if (addingNumber) {
                    throw new BuildException(
                        "An array index in your string is malformed or out of bounds: " + key);
                }
                //set that we are now adding to a number and continue
                addingNumber = true;
                continue;
            }

            if (keyAsChar == ']') {
                //ensure that we had been adding to an array index before finding closing square
                if (!addingNumber) {
                    throw new BuildException(
                        "An array index in your string is malformed or out of bounds: " + key);
                }
                //ensure that we were not on the last array reference, as only the final reference can be anonymous
                if (wasLastNest) {
                    throw new BuildException(
                        "Only the final nested array reference can be anonymous: " + key);
                }

                String accessIndex = arrayAccessIndex.toString();
                if (accessIndex.equals("")) {
                    //if the array index value is empty, then we have reached the final/anonymous array, so mark it
                    ret.add("[");
                    wasLastNest = true;
                } else {
                    //there was an index fragment found, so check that it was a valid number,
                    validateArrayIndex(accessIndex);

                    //add it as a step in the path with the '[' marker
                    ret.add('[' + accessIndex);
                    //reset the key index
                    arrayAccessIndex.delete(0, arrayAccessIndex.length());
                }

                //set that we are no longer adding to the index
                addingNumber = false;
                continue;
            }

            //decide which subset to add the current key to.
            if (addingNumber) {
                arrayAccessIndex.append(keyAsChar);
            } else {
                objectRef.append(keyAsChar);
            }
        }

        //return the steps for this key.
        return ret;
    }

    /**
     * Used to get the JsonBuilder object at the given key, or create it where possible and return
     * it.
     *
     * @param path The path to look for/create the object at.
     * @return The object at the given path, whether already existing, or freshly created as we go.
     * @throws BuildException Thrown if there was a clash between existing objects at that path, or
     *                        indexes were out of bounds etc.
     */
    private JSONBuilder getBuilder(ArrayList<String> path) throws BuildException {

        JSONBuilder ret = this, test;
        String currentKey, arrayIndex;

        //If the path is only one long, then we dont need to look any further, it is for an object on this node..
        if (path.size() == 1 && !path.get(0).startsWith("[")) {
            return ret;
        }

        //for all steps along this path
        for (int i = 0; i < path.size(); i++) {
            currentKey = path.get(i);

            //check if the current step is an array reference or not.
            if (currentKey.startsWith("[")) {

                if (i == 0) {
                    //if is the first element, then this will be invalid
                    throw new BuildException(
                        "There is a value missing in the JSON string." + currentKey);
                } else {
                    if (ret.type != JSType.ARRAY) {
                        //if the current ret is not an array - then we shouldn't have a [  starting the line as invalid key
                        throw new BuildException(
                            "You cannot add an object of that type to that path. " + currentKey);
                    } else {
                        //get the actual index from the step
                        arrayIndex = currentKey.substring(1);
                        if (arrayIndex.equals("") && ret.array.size() != 0) {
                            //if the reference is empty, and we have objects, then it is an anonymous reference so skip
                            continue;
                        }

                        try {
                            //try to get the object at the given array index
                            ret = (JSONBuilder) ret.array.get(Integer.parseInt(arrayIndex));
                        } catch (ClassCastException e) {
                            //The object pulled from the array at that index was not a JSType.Array or JSType.Object
                            throw new BuildException(
                                "You cannot add an object of that type to that path. "
                                    + currentKey);
                        } catch (IndexOutOfBoundsException e) {
                            //the desired array exists, but reference is out of bounds
                            throw new BuildException(
                                "An array index in your string is malformed or out of bounds: "
                                    + arrayIndex);
                        } catch (NumberFormatException e) {

                            //check if need to create:
                            if (arrayIndex.equals("") || arrayIndex.equals("0")) {
                                ret = ret.addNew(new JSONBuilder(JSType.ARRAY), null);
                            } else {
                                //unless the key is 0 or anonymous, then cannot create on an array index that hasn't been created yet.
                                //or the reference was bad
                                throw new BuildException(
                                    "You cannot add an object of that type to that path. "
                                        + currentKey);
                            }
                        }
                    }
                }
            }

            //Current key does not start with array syntax
            else {
                //if the current step was an object reference, check that it exists
                if ((test = ret.objects.get(currentKey)) != null) {
                    //it does exist so continue
                    ret = test;
                }

                //if the next key in path starts with an array modifier (and we are not at the last step yet) then:
                else if (i + 1 < path.size() && path.get(i + 1).startsWith("[")) {
                    //get the index from the step
                    arrayIndex = path.get(i + 1).substring(1);

                    //if the index is not anonymous, then fail as we are not on the last step.
                    if (!arrayIndex.equals("") && !arrayIndex.equals("0")) {
                        throw new BuildException(
                            "Only the final nested array reference can be anonymous: ");
                    }

                    //create a new array based on the current object reference
                    ret = ret.addNew(new JSONBuilder(JSType.ARRAY), currentKey);
                    //skip the next step (array ref)
                    i++;
                }

                //Next key in path is also an object
                else if (i + 1 < path.size()) {

                    //so long as we are not on the first step, AND the previous step was an array reference
                    if (i != 0 && path.get(i - 1).equals("[")) {
                        //we need to create a duplicate object on the current key, so as to have the correct JSON placement
                        ret = ret.addNew(new JSONBuilder(JSType.OBJECT), currentKey);
                    }

                    //if that key does't exist, create it
                    ret = ret.addNew(new JSONBuilder(JSType.OBJECT), currentKey);
                }

                //check if the last element was an array for final indexing
                else if (path.get(i - 1).startsWith("[") && ret.type != JSType.OBJECT) {
                    ret = ret.addNew(new JSONBuilder(JSType.OBJECT), currentKey);
                }
                //we should continue on the last object since that's what we want the key to the value to be;
            }
        }

        //If the ret type is an object, but the final step in the path is an array reference, then invalidate
        if (ret.type == JSType.OBJECT && path.get(path.size() - 1).startsWith("[")) {
            throw new BuildException("You cannot add an object of that type to that path. ");
        }

        //return the object we have reached
        return ret;
    }

    /**
     * Used to add a new sub-structure to the current JSON 'scaffold' object
     *
     * @param value This is the the new object to add
     * @param key This is the key to put the value against if we are an OBJECT (since arrays don't
     * use explicit keying)
     * @return The new object created at the location within the current object.
     * @throws BuildException Thrown if you try to add to a different type of object than OBJECT or
     *                        ARRAY.
     */
    private JSONBuilder addNew(JSONBuilder value, String key) throws BuildException {

        //check which type of object THIS is.
        switch (type) {

            //if we are an object, place the new one in the objects list at the given key
            case OBJECT:
                objects.put(key, value);
                return objects.get(key);

            //if we are an array, then simply add it to the list of objects.
            case ARRAY:
                array.add(value);
                return value;

            //alert of bad add.
            default:
                throw new BuildException("You cannot add an object of that type to that path. ");
        }
    }


    /**
     * Used to add a boolean to the JSON scaffold
     *
     * @param path The path to add this new element too.
     * @param value The value of the new element to add.
     * @throws BuildException Thrown if there was a syntactic error with the path.
     */
    @Override
    public void addBoolean(String path, boolean value) throws BuildException {
        JSONBuilder objectAtPath = validatePath(path);

        if (objectAtPath.type == JSType.ARRAY) {
            objectAtPath.array.add(value);
        } else {
            objectAtPath.booleans
                .put(path.substring(path.lastIndexOf(".") + 1).split("\\[")[0], value);
        }
    }

    /**
     * Used to add a boolean to the JSON scaffold
     *
     * @param path The path to add this new element too.
     * @param value The value of the new element to add.
     * @throws BuildException Thrown if there was a syntactic error with the path.
     */
    @Override
    public void addLong(String path, long value) throws BuildException {
        JSONBuilder objectAtPath = validatePath(path);

        if (objectAtPath.type == JSType.ARRAY) {
            objectAtPath.array.add(value);
        } else {
            objectAtPath.longs
                .put(path.substring(path.lastIndexOf(".") + 1).split("\\[")[0], value);
        }
    }

    /**
     * Used to add a double to the JSON scaffold
     *
     * @param path The path to add this new element too.
     * @param value The value of the new element to add.
     * @throws BuildException Thrown if there was a syntactic error with the path.
     */
    @Override
    public void addDouble(String path, double value) throws BuildException {
        JSONBuilder objectAtPath = validatePath(path);

        if (objectAtPath.type == JSType.ARRAY) {
            objectAtPath.array.add(value);
        } else {
            objectAtPath.doubles
                .put(path.substring(path.lastIndexOf(".") + 1).split("\\[")[0], value);
        }
    }

    /**
     * Used to add a String to the JSON scaffold
     *
     * @param path The path to add this new element too.
     * @param value The value of the new element to add.
     * @throws BuildException Thrown if there was a syntactic error with the path.
     */
    @Override
    public void addString(String path, String value) throws BuildException {
        JSONBuilder objectAtPath = validatePath(path);

        if (objectAtPath.type == JSType.ARRAY) {
            objectAtPath.array.add(value);
        } else {
            objectAtPath.strings
                .put(path.substring(path.lastIndexOf(".") + 1).split("\\[")[0], value);
        }
    }

    /**
     * Used to add a sub json Object to the JSON scaffold
     *
     * @param path The path to add this new element too.
     * @param value The value of the new element to add.
     * @throws BuildException Thrown if there was a syntactic error with the path.
     */
    @Override
    public void addBuilderBlock(String path, IJsonBuilder value) throws BuildException {
        JSONBuilder objectAtPath = validatePath(path);

        if (objectAtPath.type == JSType.ARRAY) {
            objectAtPath.array.add(value);
        } else {
            try {
                objectAtPath.objects.put(path.substring(path.lastIndexOf(".") + 1).split("\\[")[0],
                    (JSONBuilder) value);
            } catch (ClassCastException e) {
                throw new BuildException(
                    "An unexpected error occurred when building a JSON object: ");
            }
        }
    }


    @Override
    public String toString() {
        StringBuilder o = new StringBuilder();

        if (type == JSType.ARRAY) {
            //if we are an array, use square syntax
            o.append("[");

            //for each value in our array object
            for (Object jb : array) {

                //if it is a string, be sure to bookend with quotes else call the default toString
                if (jb instanceof String) {
                    o.append('"').append(jb).append('"');
                } else {
                    o.append(jb.toString());
                }
                //append comma between elements
                o.append(',');
            }
            //delete the last comma, before the closing square
            if (o.charAt(o.length() - 1) == ',') {
                o.deleteCharAt(o.length() - 1);
            }

            //finish up
            o.append("]");

        } else {
            //if we are an object then use the curly syntax
            o.append("{");

            //print all of our booleans with keys
            for (String k : booleans.keySet()) {
                o.append('"').append(k).append("\":").append(booleans.get(k)).append(',');
            }
            //print all of our doubles with keys
            for (String k : doubles.keySet()) {
                o.append('"').append(k).append("\":").append(doubles.get(k)).append(',');
            }
            //print all of our longs with keys
            for (String k : longs.keySet()) {
                o.append('"').append(k).append("\":").append(longs.get(k)).append(',');
            }
            //print all of our strings with keys
            for (String k : strings.keySet()) {
                o.append('"').append(k).append("\":\"").append(strings.get(k)).append("\",");
            }

            //print all of our sub objects too (again, with keys)
            for (String k : objects.keySet()) {
                o.append('"').append(k).append("\":");
                o.append(objects.get(k));
                o.append(',');
            }

            //also remove the final comma from this list too
            if (o.charAt(o.length() - 1) == ',') {
                o.deleteCharAt(o.length() - 1);
            }

            //finish up
            o.append('}');
        }
        return o.toString();
    }

    /**
     * Used to validate that a string is a valid array index
     *
     * @param arrayIndex The string value of the array index to check.
     * @throws BuildException Thrown if the string was not valid
     */
    private void validateArrayIndex(String arrayIndex) throws BuildException {
        try {
            //check that we are a number, and are >= 0;
            if (Integer.parseInt(arrayIndex) < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            //invalid number throw exception
            throw new BuildException(
                "An array index in your string is malformed or out of bounds: " + arrayIndex);
        }
    }

    /**
     * Used to remove the throw new, and message writing each time this is needed. (simple helper
     * function)
     *
     * @param path The path that caused the error
     * @throws BuildException Thrown as the function of this method (with message)
     */
    private void buildErr(String path) throws BuildException {
        throw new BuildException("That Key is not a valid key to place an element at:  " + path);
    }
}
