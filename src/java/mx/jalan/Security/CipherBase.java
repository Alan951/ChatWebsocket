package mx.jalan.Security;

import java.io.Serializable;

public interface CipherBase<T, KT extends Serializable> {
	public String encode(T textToCipher);
	
	public String decode(T textToDecipher);
	
	public String getCipherName();
        
        public boolean isAsyncCipher();
        
        public void setKey(KT key);
    
        public KT getKey();
        
        public void setPrivateKey(KT key);
        
        public KT getPrivateKey();
        
        public void setPublicKey(KT key);
        
        public KT getPublicKey();
}
