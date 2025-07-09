package utilities;

import base.BasePage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.testng.annotations.DataProvider;

import java.io.FileReader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class DataUtil {
    @DataProvider(name = "userData")
    public static Object[][] getUserData(Method method) {
        // Read @JsonFile annotation on the test method
        JsonFile jsonFile = method.getAnnotation(JsonFile.class);
        if (jsonFile == null) {
            throw new RuntimeException("Missing @JsonFile annotation on test method: " + method.getName());
        }

        String filePath = jsonFile.value();

        // Read JSON as List<HashMap<String, String>>
        List<HashMap<String, String>> data = getJsonDataAsMap(filePath);

        // Convert to Object[][] for TestNG
        Object[][] dataArray = new Object[data.size()][1];
        for (int i = 0; i < data.size(); i++) {
            dataArray[i][0] = data.get(i);
        }

        return dataArray;
    }

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
