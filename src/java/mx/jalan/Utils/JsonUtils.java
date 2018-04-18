package mx.jalan.Utils;

import com.google.gson.Gson;

public class JsonUtils {
    public static boolean isJsonObject(String text){
        try{
            new Gson().fromJson(text, Object.class);
            return true;
        }catch(com.google.gson.JsonSyntaxException err){
            return false;
        }
    }
}
