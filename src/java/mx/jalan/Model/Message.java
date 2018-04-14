/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.jalan.Model;

import java.time.LocalDateTime;
import javax.websocket.Session;

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
    private transient EncryptionAlgorithm encryptProps;
    private transient Session sessionDestination;
    private transient Session sessionSource;
    private LocalDateTime timestamp;

    // Default constructor
    public Message(){}
    
    /*
        Constructor to create message from user to other user by private message
    */
    public Message(String action, String message, User userSource, User userDestination, EncryptionAlgorithm encryptProps, LocalDateTime timestamp, int code) {
        this.action = action;
        this.message = message;
        this.userSource = userSource;
        this.userDestination = userDestination;
        this.encryptProps = encryptProps;
        this.timestamp = timestamp;
        this.code = code;
    }

    /*
        Constructor to create message from user to all
    */
    public Message(String action, String message, User userSource, EncryptionAlgorithm encryptProps, LocalDateTime timestamp, int code) {
        this.action = action;
        this.message = message;
        this.userSource = userSource;
        this.encryptProps = encryptProps;
        this.timestamp = timestamp;
        this.code = code;
    }
    
    /*
        Constructor to create message from server
    */
    public Message(String action, String message, EncryptionAlgorithm encryptProps, int code){
        this.action = action;
        this.message = message;
        this.encryptProps = encryptProps;
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

    public User getUserSource() {
        return userSource;
    }

    public void setUserSource(User userSource) {
        this.userSource = userSource;
    }

    public User getUserDestination() {
        return userDestination;
    }

    public void setUserDestination(User userDestination) {
        this.userDestination = userDestination;
    }

    public EncryptionAlgorithm getEncryptProps() {
        return encryptProps;
    }

    public Message setEncryptProps(EncryptionAlgorithm encryptProps) {
        this.encryptProps = encryptProps;
        
        return this;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Session getSessionDestination() {
        return sessionDestination;
    }

    public void setSessionDestination(Session sessionDestination) {
        this.sessionDestination = sessionDestination;
    }

    public Session getSessionSource() {
        return sessionSource;
    }

    public void setSessionSource(Session sessionSource) {
        this.sessionSource = sessionSource;
    }

    @Override
    public String toString() {
        return "Message{" + "code=" + code + ", action=" + action + ", message=" + message + ", userSource=" + userSource + ", userDestination=" + userDestination + ", encryptProps=" + encryptProps + ", timestamp=" + timestamp + '}';
    }
}
