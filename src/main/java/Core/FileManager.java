package Core;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * This class is used to interact with various files on disk.
 */
public class FileManager {

    /**
     * This method takes a file path, and concats all the content into a single string.
     *
     * @param file The file path for the file to turn into a string
     * @return The file as a single string.
     * @throws IOException Thrown if it could not find, or you don't have permissions for that
     *                     file.
     */
    public static String getFileAsString(String file) throws IOException {
        StringBuilder ret = new StringBuilder();

        for (String s : getFileAsStrings(file)) {
            ret.append(s);
        }

        return ret.toString();
    }

    /**
     * This method takes a file path, and returns an array of strings for each line in the file.
     *
     * @param file The file path for the file to turn into string array
     * @return An element for each line in the file.
     * @throws IOException Thrown if it could not find, or you don't have permissions for that
     *                     file.
     */
    public static ArrayList<String> getFileAsStrings(String file) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(file));
        ArrayList<String> ret = new ArrayList<>();

        while (reader.ready()) {
            ret.add(reader.readLine() + '\n');
        }

        reader.close();

        return ret;
    }

    /**
     * This is used to get a file part of the source as a string.
     *
     * @param resourceName The name of the resource to get as a string
     * @return The string value of the resource.
     * @throws IOException Thrown if there was a problem accessing the requested file
     */
    static String getLocalResourceAsString(String resourceName) throws IOException {
        StringBuilder ret = new StringBuilder();

        for (String s : getLocalResourceAsStrings(resourceName)) {
            ret.append(s);
        }

        return ret.toString();
    }

    /**
     * This is used to get the contents of a file line by line.
     *
     * @param resourceName The name of the resource to get as a String array.
     * @return The line by line values of the resource.
     * @throws IOException Thrown if there was a problem accessing the requested file
     */
    static ArrayList<String> getLocalResourceAsStrings(String resourceName) throws IOException {

        //open stream to resource
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(FileManager.class.getResourceAsStream(resourceName)));
        ArrayList<String> ret = new ArrayList<>();

        //read all the contents in
        while (reader.ready()) {
            ret.add(reader.readLine() + '\n');
        }
        //be a good person
        reader.close();

        //return complete result.
        return ret;
    }

    /**
     * This is used to return the raw bytes of a given file.
     *
     * @param path The full path to the file to get the bytes from.
     * @return The byte[] of the entire file contents.
     * @throws IOException Thrown if there was a problem accessing the requested file.
     */
    static byte[] getComponentAsBytes(File path) throws IOException {
        if (path == null) {
            return new byte[]{};
        }
        return Files.readAllBytes(path.toPath());
    }

    /**
     * This is used to validate the existence (as a directory) and permission to read from the given
     * path.
     *
     * @param path The path to validate.
     * @return The String of the resolved path to that directory (removal of . and .. if necessary)
     * @throws IOException Thrown if there was a problem accessing that path as a directory.
     */
    public static String validateDirectory(String path) throws IOException {
        File activeDir;

        //resolve canonical
        try {
            activeDir = new File(path).getCanonicalFile();
        } catch (IOException e) {
            throw new IOException(
                "The path provided could not be resolved to a file system location.");
        }

        //check attributes
        if (!activeDir.exists()) {
            throw new IOException("The path provided for active components does not exist.");
        }
        if (!activeDir.isDirectory()) {
            throw new IOException("The path provided for active components is not a directory.");
        }
        if (!activeDir.canRead()) {
            throw new IOException(
                "The directory provided for active components cannot be read from.");
        }

        //return valid path
        return activeDir.getPath();
    }
}
