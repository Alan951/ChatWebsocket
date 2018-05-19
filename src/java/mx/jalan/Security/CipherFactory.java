package mx.jalan.Security;

import mx.jalan.Security.Algorithms.CipherBase;
import java.io.Serializable;
import mx.jalan.Security.Algorithms.CaesarCipher;
import mx.jalan.Security.Algorithms.DESCipher;

public class CipherFactory<T, KT extends Serializable> {
    
    public  CipherBase<T, KT> getCipher(String cipherName){
        switch(cipherName){
            case EncryptionAlgorithms.CAESAR:
                return new CaesarCipher<T, KT>();
            case EncryptionAlgorithms.DES:
            	return new DESCipher<T, KT>();
        }
        
        return null;
    }
}
