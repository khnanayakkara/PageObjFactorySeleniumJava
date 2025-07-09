package utilities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class JsonReader {

    public static List<HashMap<String, String>> getJsonDataAsMap(String filePath) {
        try {
            FileReader reader = new FileReader(filePath);
            Type type = new TypeToken<List<HashMap<String, String>>>() {}.getType();
            return new Gson().fromJson(reader, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON file: " + filePath, e);
        }
    }
}
