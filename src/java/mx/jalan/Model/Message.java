/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.jalan.Model;

import java.time.LocalDateTime;

/**
 *
 * @author Ck
 */
public class Message {
    private int code;
    private String action;
    private String message;
    private User userSource;
    private User userDestination;
    private EncryptionProperties encryptProps;
    private LocalDateTime timestamp;

    public Message(String action, String message, User userSource, User userDestination, EncryptionProperties encryptProps, LocalDateTime timestamp, int code) {
        this.action = action;
        this.message = message;
        this.userSource = userSource;
        this.userDestination = userDestination;
        this.encryptProps = encryptProps;
        this.timestamp = timestamp;
        this.code = code;
    }

    public Message(String action, String message, User userSource, EncryptionProperties encryptProps, LocalDateTime timestamp, int code) {
        this.action = action;
        this.message = message;
        this.userSource = userSource;
        this.encryptProps = encryptProps;
        this.timestamp = timestamp;
        this.code = code;
    }
    
    public void setAction(String action){
        this.action = action;
    }
    
    public String getAction(){
        return action;
    }
    
    public void setCode(int code){
        this.code = code;
    }
    
    public int getCode(){
        return this.code;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUserCreated() {
        return userSource;
    }

    public void setUserCreated(User userSource) {
        this.userSource = userSource;
    }

    public User getUserDestination() {
        return userDestination;
    }

    public void setUserDestination(User userDestination) {
        this.userDestination = userDestination;
    }

    public EncryptionProperties getEncryptProps() {
        return encryptProps;
    }

    public void setEncryptProps(EncryptionProperties encryptProps) {
        this.encryptProps = encryptProps;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" + "code=" + code + ", action=" + action + ", message=" + message + ", userSource=" + userSource + ", userDestination=" + userDestination + ", encryptProps=" + encryptProps + ", timestamp=" + timestamp + '}';
    }
}
