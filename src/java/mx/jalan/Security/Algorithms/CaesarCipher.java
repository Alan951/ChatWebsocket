package mx.jalan.Security.Algorithms;

import java.io.Serializable;
import java.util.Base64;
import mx.jalan.Security.CipherBase;
import org.apache.commons.lang3.math.NumberUtils;

public class CaesarCipher<T, KT extends Serializable> implements CipherBase<T, KT>{

    private KT key;

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

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
        String textString64 = new String(Base64.getEncoder().encode(textString.getBytes()));
        char text[] = textString64.toCharArray();

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
        
        return new String(text);
    }

    @Override
    public String decode(T textToDecipher) {
        char text[] = textToDecipher.toString().toCharArray();

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
        
        return new String(Base64.getDecoder().decode(new String(text)));

        //return new String(text);
    }
    
    @Override
    public boolean isAsyncCipher(){
        return true;
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

}
