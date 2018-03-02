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
public class EncryptionProperties {
    
    private String algorithm;
    private int algorithmType;
    private Map<String, String> properties;

    public EncryptionProperties(String algorithm, int algorithmType, Map<String, String> properties) {
        this.algorithm = algorithm;
        this.algorithmType = algorithmType;
        this.properties = properties;
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

    @Override
    public String toString() {
        return "EncryptionProperties{" + "algorithm=" + algorithm + ", algorithmType=" + algorithmType + ", properties=" + properties + '}';
    }
    
    
    
}
