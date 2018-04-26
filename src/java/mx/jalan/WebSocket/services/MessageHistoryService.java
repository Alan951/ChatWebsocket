/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.jalan.WebSocket.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import mx.jalan.Model.Message;
import mx.jalan.Model.User;
import javax.inject.Singleton;

/**
 *
 * @author Ck
 * 
 * *Aun sin implementar*
 * 
 */
@Singleton
public class MessageHistoryService {
    
    private Set<Message> messages = new HashSet<>();
    
    public Set<Message> getAllMessages(){
        return this.messages;
    }
    
    public Set<Message> getServerMessages(){
        return this.messages
                .stream()
                .filter(msg -> msg.getUserSource() == null)
                .collect(Collectors.toSet());
    }
    
    public Set<Message> getUserMessages(User user){
        return this.messages
                .stream()
                .filter(msg -> msg.getUserSource().equals(user))
                .collect(Collectors.toSet());
    }
    
    public List<Message> searchMessage(String q){
        return null;
    }
    
    public void addMessage(Message message){
        messages.add(message);
    }
}
