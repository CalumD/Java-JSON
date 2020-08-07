import api.IJson;
import api.JsonParser;
import api.JsonSchemaEnforcer;
import exceptions.JsonException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class OfficialSchemaTests {

    @Disabled
    @Test
    public void testAll() {
        File currentDir = new File("src/test/resources/tests/"); // current directory
        HashMap<String, List<File>> allFiles = new HashMap<>();
        getDirContents(currentDir, "", allFiles);

        StringBuilder builder = new StringBuilder();

        allFiles.forEach((folderName, files) -> {
            System.out.println("TESTING FOLDER: " + folderName);
            files.forEach(file -> {
                System.out.println("    FILE: " + file.getName());
                IJson testContent;
                try {
                    testContent = JsonParser.parse(getFileContents(file, builder));
                } catch (JsonException | IOException e) {
                    System.err.println("        File failed to parse: " + file.getName());
                    return;
                }
                for (IJson scenario : testContent.getArray()) {
                    System.out.println("            SCENARIO: " + scenario.getStringAt("description"));
                    IJson schemaForScenario = scenario.getJSONObjectAt("schema");
                    for (IJson individualTest : scenario.getArrayAt("tests")) {
                        boolean passed = JsonSchemaEnforcer.validateStrict(individualTest.getAnyAt("data"), schemaForScenario);
                        if (passed == individualTest.getBooleanAt("valid")) {
                            System.out.println("                TEST: " + individualTest.getStringAt("description") + " PASSED.");
                        } else {
                            System.err.println("                TEST: " + individualTest.getStringAt("description") + " FAILED.");
                        }
                    }
                }
            });
        });
    }

    private void getDirContents(File dir, String directoryPathSoFar, HashMap<String, List<File>> listToAddTo) {
        listToAddTo.put(directoryPathSoFar, new ArrayList<>());

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                getDirContents(file, directoryPathSoFar + "/" + file.getName(), listToAddTo);
            } else {
                listToAddTo.get(directoryPathSoFar).add(file);
            }
        }
    }

    private String getFileContents(File file, StringBuilder ret) throws IOException {
        ret.setLength(0);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                ret.append(reader.readLine()).append('\n');
            }
        }
        return ret.toString();
    }
}
