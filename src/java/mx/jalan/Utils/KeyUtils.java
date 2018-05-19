/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.jalan.Utils;

import mx.jalan.Model.EncryptionAlgorithm;
import mx.jalan.Security.EncryptionAlgorithms;
import org.apache.commons.lang3.math.NumberUtils;

/**
 *
 * @author Ck
 */
public class KeyUtils {

    public static boolean isValidKey(String key, EncryptionAlgorithm algorithm) {
        return isValidKey(key, algorithm.getAlgorithm());
    }

    public static boolean isValidKey(String key, String algorithmName) {
        Class<?> type = EncryptionAlgorithms.getKeyType(algorithmName);
        
        if (type == Long.class) {
            return isLong(key);
        }else if(type == String.class){
            return true;
        }
        
        return false;
    }
    
    public static boolean isLong(String strLong){
        if (!strLong.matches("(?<=\\s|^)\\d+(?=\\s|$)")) {
            return false;
        }

        if (!NumberUtils.isCreatable(strLong)) {
            return false;
        }

        return true;
    }

    public static Long getLongKey(String key) {
        return Long.parseLong(key);
    }
    
    public static String getStringKey(String key){
        return key;
    }
}
