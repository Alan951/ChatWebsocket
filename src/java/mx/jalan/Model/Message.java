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
    private String message;
    private User userCreated;
    private User userDestination;
    private EncryptionProperties encryptProps;
    private LocalDateTime timestamp;

    public Message(String message, User userCreated, User userDestination, EncryptionProperties encryptProps, LocalDateTime timestamp) {
        this.message = message;
        this.userCreated = userCreated;
        this.userDestination = userDestination;
        this.encryptProps = encryptProps;
        this.timestamp = timestamp;
    }

    public Message(String message, User userCreated, EncryptionProperties encryptProps, LocalDateTime timestamp) {
        this.message = message;
        this.userCreated = userCreated;
        this.encryptProps = encryptProps;
        this.timestamp = timestamp;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(User userCreated) {
        this.userCreated = userCreated;
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
        return "Message{" + "message=" + message + ", userCreated=" + userCreated + ", userDestination=" + userDestination + ", encryptProps=" + encryptProps + ", timestamp=" + timestamp + '}';
    }
}
