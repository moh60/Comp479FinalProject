package parser;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;

public class parserDriver {

    public static void main(String[] args) {

        // Defines path to the different required directory
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path settingsFilePath = Paths.get(currentPath.toString(), "res", "preprocessSettings.json");
        Path tokenStreamPath = Paths.get(currentPath.toString(), "token_stream");
        Path collecDirPath = Paths.get(currentPath.toString(), "Docs");

        // Read the preprocessing settings
        HashMap<String, Boolean> preProcessSettings = readSettings(settingsFilePath.toString());

        // Parse the collection defined
        CollectionParser collecParser = new CollectionParser(preProcessSettings);
        collecParser.parse_collection(tokenStreamPath, collecDirPath, 334);

    }

    private static HashMap<String, Boolean> readSettings(String settingFilePath) {

        // Extract the preprocessing settings from the JSON file
        HashMap<String, Boolean> settings = new HashMap<String, Boolean>();
        JSONParser parser = new JSONParser();

        try {
            JSONObject json = (JSONObject) parser.parse(new FileReader(settingFilePath));
            Iterator keySet = json.keySet().iterator();

            while (keySet.hasNext()) {
                String key = (String) keySet.next();
                settings.put(key, (Boolean) json.get(key));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        return settings;

    }

}
