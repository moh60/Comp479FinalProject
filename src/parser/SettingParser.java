package parser;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

class SettingParser {

    private static final Path currentPath = Paths.get(System.getProperty("usr.dir"));
    private static final Path settingsFilePath = Paths.get(currentPath.toString(), "res", "preprocessSettings.json");

    static HashMap<String, Boolean> readSettings() {

        // Extract the preprocessing settings from the JSON file
        HashMap<String, Boolean> settings = new HashMap<>();
        JSONParser parser = new JSONParser();

        try {
            JSONObject json = (JSONObject) parser.parse(new FileReader(settingsFilePath.toFile()));

            for (Object o : json.keySet()) {
                String key = (String) o;
                settings.put(key, (Boolean) json.get(key));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        return settings;
    }
}
