/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.jalan.Security;

/**
 *
 * @author Ck
 */
public interface CipherBaseSimple {
    public String encode(String textToCipher);
	
    public String decode(String textToDecipher);

    public String getCipherName();

    public boolean isAsyncCipher();

    public void setKey(Long key);

    public Long getKey();

    public void setPrivateKey(Long key);

    public Long getPrivateKey();

    public void setPublicKey(Long key);

    public Long getPublicKey();
}
