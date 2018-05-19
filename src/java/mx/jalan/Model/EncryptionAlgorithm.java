/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.jalan.Model;

import java.util.Map;

/**
 *
 * @author Ck
 */
public class EncryptionAlgorithm {
    
    private String algorithm;
    private int algorithmType;
    private Map<String, String> properties;
    private transient Class<?> keyType;

    public EncryptionAlgorithm(String algorithm, int algorithmType, Map<String, String> properties, Class<?> keyType) {
        this.algorithm = algorithm;
        this.algorithmType = algorithmType;
        this.properties = properties;
        this.keyType = keyType;
    }
    
    public EncryptionAlgorithm(String algorithm, int algorithmType){
        this.algorithm = algorithm;
        this.algorithmType = algorithmType;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public int getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(int algorithmType) {
        this.algorithmType = algorithmType;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Class<?> getKeyType() {
        return keyType;
    }

    public void setKeyType(Class<?> keyType) {
        this.keyType = keyType;
    }

    @Override
    public String toString() {
        return "EncryptionAlgorithm{" + "algorithm=" + algorithm + ", algorithmType=" + algorithmType + ", properties=" + properties + ", keyType=" + keyType + '}';
    }
    
}
