/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.jalan.websocket;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import mx.jalan.Model.User;

/**
 *
 * @author Jorge
 */
@ApplicationScoped
@ServerEndpoint("/chat")
public class ChatWebSocketServer {
    
    @Inject
    private ChatSessionHandler sessionHandler;
    
    @Inject
    private UserService userService;
    
    Logger log = Logger.getLogger(this.getClass().toString());
    
    @OnOpen
    public void open(Session session){
        log.info("New User");
        System.out.println("New User");
        User usr = new User();
        usr.setSession(session);
        
        userService.addUser(usr, session);
    }
    
    @OnClose
    public void close(Session session){
        log.info("Close connection: "+session);
        //System.out.println("[DG - OnClose]: "+session);
        User usr = userService.existsSession(session);
        if(usr != null) sessionHandler.createMsgFromServer("El usuario: \""+usr.getNombre()+"\" ha salido del chat.");
        userService.removeSession(session);
    }
    
    @OnError
    public void onError(Throwable error){
        Logger.getLogger(ChatWebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }
    
    @OnMessage
    public void handleMessage(String msg, Session session){
        try(JsonReader reader = Json.createReader(new StringReader(msg))){
            JsonObject jsonMsg = reader.readObject();
            
            System.out.println("[DG - OnMessage]: "+jsonMsg);
            
            if("newUsr".equals(jsonMsg.getString("action"))){
                User usr;
                
                String nombre = jsonMsg.getString("nombre");
                
                String avatar = !jsonMsg.isNull("avatarURL") ? jsonMsg.getString("avatarURL") : null;
                
                usr = new User();
                if(avatar != null) usr.setAvatar(avatar);
                usr.setNombre(nombre);
                usr.setSession(session);
                
                userService.addUser(usr, session);
            }
            
            if("msg".equals(jsonMsg.getString("action"))){
                sessionHandler.sendMessage(jsonMsg, session);
            }
            
            if("requestChanges".equals(jsonMsg.getString("action"))){
                sessionHandler.createUpdateMessage(session);
            }
        }
    }
}
