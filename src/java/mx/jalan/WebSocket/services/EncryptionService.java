/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.jalan.WebSocket.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Singleton;
import mx.jalan.Model.EncryptionAlgorithm;
import mx.jalan.Security.Algorithms.CipherBase;
import mx.jalan.Security.CipherFactory;
import mx.jalan.Security.EncryptionAlgorithms;
import mx.jalan.Utils.KeyUtils;

/**
 *
 * @author Ck
 */

@Singleton
public class EncryptionService {
    private CipherBase cipher;
    
    private EncryptionAlgorithm encryptionAlgorithmEnabled;
    
    private List<EncryptionAlgorithm> encryptionAlgorithmsSupport;
    
    public EncryptionService(){
        System.out.println("EncryptionService constructor initialized!");
        
        Map<String, String> syncProp = new HashMap<String, String>();
        syncProp.put("key", "");
        
        this.encryptionAlgorithmsSupport = new ArrayList<EncryptionAlgorithm>();
        
        this.encryptionAlgorithmsSupport.add(new EncryptionAlgorithm(EncryptionAlgorithms.CAESAR, 
                EncryptionAlgorithms.SYNC_CIPHER, 
                syncProp,
                Long.class
        ));
        this.encryptionAlgorithmsSupport.add(new EncryptionAlgorithm(EncryptionAlgorithms.DES, 
                EncryptionAlgorithms.SYNC_CIPHER,
                syncProp,
                String.class
        ));
    }
    
    /*
        El objeto que le envies por parametro
        verifica si esta en la lista de metodos de cifrados que 
        soporta la aplicacion.
    */
    private EncryptionAlgorithm encryptionExists(EncryptionAlgorithm encryption){
        EncryptionAlgorithm encryptionExists = null;
        
        for(EncryptionAlgorithm encypt : this.encryptionAlgorithmsSupport){
            if(encryption.getAlgorithm().equals(encypt.getAlgorithm())){
                encryptionExists = encypt;
                break;
            }
        }
        
        return encryptionExists;
    }
    
    public boolean enableCipher(EncryptionAlgorithm encryptionReq){
        EncryptionAlgorithm encryption = encryptionExists(encryptionReq);
        
        if(encryption == null) //Encryption no existe
            return false;
        
        System.out.println("[DG] Habilitando metodo criptografico: "+encryption);
        
        if(encryption.getAlgorithmType() == EncryptionAlgorithms.SYNC_CIPHER){
                        
            if(encryption.getKeyType() == Long.class){
                CipherBase<String, Long> cipher = new CipherFactory().getCipher(encryption.getAlgorithm());
                String checkKey = encryptionReq.getProperties().get("key");
                if(KeyUtils.isValidKey(checkKey, encryption)){
                    cipher.setKey(KeyUtils.getLongKey(checkKey));
                    setCipher(cipher);
                }
            }else if(encryption.getKeyType() == String.class){
                CipherBase<String, String> cipher = new CipherFactory().getCipher(encryption.getAlgorithm());
                String checkKey = encryptionReq.getProperties().get("key");
                if(KeyUtils.isValidKey(checkKey, encryption)){
                    cipher.setKey(KeyUtils.getStringKey(checkKey));
                    setCipher(cipher);
                }
            }
            
            
        }else if(encryption.getAlgorithmType() == EncryptionAlgorithms.ASYNC_CIPHER){
            Long publicKey = Long.parseLong(encryptionReq.getProperties().get("publicKey"));
            Long privateKey = Long.parseLong(encryptionReq.getProperties().get("privateKey"));
            
            CipherBase<String, Long> cipher = new CipherFactory().getCipher(encryption.getAlgorithm());
            cipher.setPublicKey(publicKey);
            cipher.setPrivateKey(privateKey);
            
            setCipher(cipher);
        }
        
        this.encryptionAlgorithmEnabled = encryptionReq;
        
        return true;
    }
    
    public void disableCipher(){
        this.cipher = null;
        this.encryptionAlgorithmEnabled = null;
        System.out.println("[DG - OnDisableCipher]: OK");
    }
    
    public void setEncryptionAlgorithmEnabled(EncryptionAlgorithm encryption){
        this.encryptionAlgorithmEnabled = encryption;
    }
    
    public EncryptionAlgorithm getEncryptionAlgorithmEnabled(){
        return this.encryptionAlgorithmEnabled;
    }
    
    public CipherBase getCipher(){
        return this.cipher;
    }
    
    public void setCipher(CipherBase cipher){
        System.out.println("[DG - EncryptionService]: Cipher setted");
        this.cipher = cipher;
    }
    
    public List<EncryptionAlgorithm> getEncryptionAlgorithmSupport(){
        return this.encryptionAlgorithmsSupport;
    }
    
    public boolean cipherActive(){
        return this.cipher != null;
    }
}
