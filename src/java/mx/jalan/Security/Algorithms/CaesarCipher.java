package mx.jalan.Security.Algorithms;

import java.io.Serializable;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.math.NumberUtils;

public class CaesarCipher<T, KT extends Serializable> implements CipherBase<T, KT>{

    private KT key;

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    
    private boolean debug = true;

    public CaesarCipher(KT key) {
        this.key = key;
    }

    public CaesarCipher() {}
    
    @Override
    public String getCipherName() {
        return "Caesar Cipher";
    }

    public int getPosLetterInLetters(int letterToSearch) {
        for (int x = 0; x < LETTERS.length(); x++) {
            if (LETTERS.charAt(x) == LETTERS.charAt(letterToSearch)) {
                return x;
            }
        }

        return -1;
    }

    public int searchChar(char letter) {
        for (int x = 0; x < LETTERS.length(); x++) {
            if (LETTERS.charAt(x) == letter) {
                return x;
            }
        }

        return -1;
    }

    public boolean validate(String text) {
        for (char c : text.toCharArray()) {
            if (!LETTERS.contains(Character.toString(c))) {
                return false;
            }
        }

        return true;
    }

    private Long getNumberKey(KT key){ 
    	if(!NumberUtils.isParsable(key.toString())){
    		String numbers = "";
    		char letters[] = key.toString().toCharArray();
    		for(int x = 0 ; x < letters.length ; x++){
    			numbers += (int)letters[x];
    		}
    		
    		return Long.parseLong(numbers);
    	}
    	return Long.parseLong(key.toString());
    }

    @Override
    public String encode(T textToCipher) {    	
        String textString = textToCipher.toString();
        
        if(debug)
            System.out.println("[CIPHER DEBUG (encode) - textString]: "+textString);
        
        String comodinPadding;
        
        String pattern = "(\\=+)";
        String textString64 = new String(Base64.getEncoder().encode(textString.getBytes()));
        
        if(debug)
            System.out.println("[CIPHER DEBUG (encode) - textString64]: "+textString64);
        
        Matcher matcher = Pattern.compile(pattern).matcher(textString64);
        
        comodinPadding = matcher.find() ? matcher.group(1) : null;
        if(comodinPadding != null){
            
            textString64 = textString64.replaceAll(pattern, "");
            if(debug){
                System.out.println("[CIPHER DEBUG (encode) - comodinPadding]: "+comodinPadding);
                System.out.println("[CIPHER DEBUG (encode) - afterDeletePadding]: "+textString64);
            }
        }
        
        char text[] = textString64.toCharArray();
        
        if(debug)
            System.out.println("[CIPHER DEBUG (encode) - charArrayBeforeCipher]: "+text);

        for (int x = 0; x < text.length; x++) { //Recorrer cada letra del texto
            char n = text[x];
            
            int pos = getPosLetterInLetters(searchChar(n));

            if ((pos + getNumberKey(key)) >= LETTERS.length()) {
                Long u = getNumberKey(key) + pos;

                while (u >= LETTERS.length()) {
                    u -= LETTERS.length();
                }
                
                n = LETTERS.charAt(u.intValue());

                
            } else {
                pos += getNumberKey(key);
                n = LETTERS.charAt(pos);
            }

            text[x] = n;
        }
        
        textString64 = new String(text);
        
        if(debug)
            System.out.println("[CIPHER DEBUG (encode) - afterCipher]: "+textString64);
        
        if(comodinPadding != null){
            textString64 += comodinPadding;
            if(debug)
                System.out.println("[CIPHER DEBUG (encode) - afterAddPaddingToCipher]: "+textString64);
        }
        
        if(debug)
            System.out.println("[CIPHER DEBUG (encode) - textCipherEnd]: "+textString64);
        
        return textString64;
    }

    @Override
    public String decode(T textToDecipher) {
    	String textString64 = textToDecipher.toString(); 
        
        if(debug)
            System.out.println("[CIPHER DEBUG (decode) - textString64Cipher]: "+textString64);
        
        
    	String comodinPadding;
    	
    	String pattern = "(\\=+)";
    	Matcher matcher = Pattern.compile(pattern).matcher(textString64);
    	
    	comodinPadding = matcher.find() ? matcher.group(1) : null;
    	if(comodinPadding != null){
            textString64 = textString64.replaceAll(pattern, "");
            if(debug)
                System.out.println("[CIPHER DEBUG (decode) - textAfterRemovePadding]: "+textString64);
    	}
    	
        char text[] = textString64.toString().toCharArray();
        
        if(debug)
            System.out.println("[CIPHER DEBUG (decode) - charArrayBeforeDecrypter]: "+text);

        for (int x = 0; x < text.length; x++) {
            char n = text[x];
            int pos = getPosLetterInLetters(searchChar(n));

            if (getNumberKey(key) - pos > 0) {
                Long u = pos - getNumberKey(key);

                while (u <= 0) {
                    u += LETTERS.length();
                }

                try{
                	n = LETTERS.charAt(u.intValue());
                }catch(StringIndexOutOfBoundsException e){
                	e.printStackTrace();
                }
                //n = LETTERS.charAt(u.intValue());
            } else {
                pos -= getNumberKey(key);
                n = LETTERS.charAt(pos);
            }

            text[x] = n;
        }
        
        String textDecrypted64 = new String(text);
        
        if(debug)
            System.out.println("[CIPHER DEBUG (decode) - textDecrypted64AfterDecrypter]: "+textDecrypted64);
        
        if(comodinPadding != null){
            textDecrypted64 += comodinPadding;
            
            if(debug)
                System.out.println("[CIPHER DEBUG (decode) - textDecryptedAfterAddPadding]: "+textDecrypted64);
        }
        
        String textDecrypted = new String(Base64.getDecoder().decode(textDecrypted64));
        
        if(debug)
            System.out.println("[CIPHER DEBUG (decode) - textDecryptedEnd]: "+textDecrypted);
        
        return textDecrypted;
    }
    
    @Override
    public boolean isAsyncCipher(){
        return false;
    }

    @Override
    public void setKey(KT key) {
        this.key = key;
    }

    @Override
    public KT getKey() {
        return this.key;
    }

    @Override
    public void setPrivateKey(KT key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public KT getPrivateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPublicKey(KT key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public KT getPublicKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return "CaesarCipher{algorithmName="+ this.getCipherName() +" " + "key=" + key + '}';
    }

    @Override
    public void setDebugMode(boolean debug) {
        this.debug = debug;
    }

    @Override
    public boolean idDebugMode() {
        return debug;
    }
    
    

}