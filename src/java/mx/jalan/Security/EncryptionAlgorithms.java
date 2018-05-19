package mx.jalan.Security;

public class EncryptionAlgorithms {
    public static final String DES      = "DES";
    public static final String TDES     = "TDES";
    public static final String RSA      = "RSA";
    public static final String AES      = "AES";
    public static final String CAESAR    = "CAESAR";

    public static final int ASYNC_CIPHER = 2;
    public static final int SYNC_CIPHER = 1;
    
    public static Class<?> getKeyType(String algorithmName){
        switch(algorithmName){
            case CAESAR:
                return Long.class;
            case DES:
                return String.class;
            default:
                return null;
        }
    }
        
        
}

