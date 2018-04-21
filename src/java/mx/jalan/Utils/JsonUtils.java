package mx.jalan.Utils;

import com.google.gson.Gson;
import mx.jalan.Model.Message;

public class JsonUtils {
    public static boolean isJsonObject(String text){
        return text.matches("\\{.*\\:\\{.*\\:.*\\}\\}");
    }
}
