/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.jalan.WebSocket;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import mx.jalan.Model.User;

/**
 *
 * @author Ck
 */
@ApplicationScoped
public class UserService {
    
    private final Set<User> users = new HashSet<>();
    
    public void addUser(User user){
        users.add(user);
    }
    
    public boolean removeUser(User user){
        return users.remove(user);
    }
    
    public boolean removeUser(Session session){
        for(User user : users){
            if(user.getSession().equals(session)){
                return users.remove(user);
            }
        }
        
        return false;
    }
    
    public User existsUser(User user){
        for(User u : this.users){
            if(u.equals(user)){
                return user;
            }
        }
        
        return null;
    }
    
    public User existsUser(String userName){
        for(User user : this.users){
            if(user.getNombre() != null && user.getNombre().equals(userName)){
                return user;
            }
        }
        
        return null;
    }
    
    public User existsSession(Session session){
        for(User user : this.users){
            if(user.getSession().equals(session)){
                return user;
            }
        }
        
        return null;
    }
    
    public Set<User> getUsersList(){
        return this.users;
    }
    
}
