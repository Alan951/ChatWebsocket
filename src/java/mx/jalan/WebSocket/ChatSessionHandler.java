/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.jalan.websocket;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import mx.jalan.Model.User;
import mx.jalan.WebSocket.UserService;

/**
 *
 * @author Jorge
 */
@ApplicationScoped
public class ChatSessionHandler {
    
    //private final Set<User> users = new HashSet<>();
    
    @Inject
    private UserService userService;
    
    public void addUser(User usuario, Session session){
        if(userService.existsSession(session) == null){
            userService.addUser(usuario);
        }else{
            userService.removeUser(session);
            userService.addUser(usuario);
            if(usuario.getNombre() != null && !usuario.getNombre().trim().isEmpty())
                createMsgFromServer("Bienvenido "+usuario.getNombre()+"!!!");
        }
    }

    public void sendMessage(JsonObject msg, Session session) {
        if(msg.getString("destin").equals("all")){
            sendBroadcastSession(msg, session);
        }else{
            User usr = userService.existsUser(msg.getString("destin"));
            
            if(usr != null){
                sendUnicastSession(msg, usr.getSession());
            }else{
                createErrorMessage("El usuario "+msg.getString("destin")+" no existe", session);
            }
        }
    }
    
    public void createUpdateMessage(Session session){
        User sourceUsr = userService.existsSession(session);
        JsonArrayBuilder jarr = JsonProvider.provider().createArrayBuilder();
        
        userService.getUsersList().forEach((usr) -> {
            if(sourceUsr != usr){
                JsonObject jsO = JsonProvider.provider().createObjectBuilder()
                        .add("usuario", usr.getNombre()).add("avatar", JsonObject.NULL).build();
                jarr.add(jsO);
            }
        });
        
        JsonProvider provider = JsonProvider.provider();
        JsonObject message = provider.createObjectBuilder().add("action", "updateData").add("data", jarr.build()).build();
        
        sendUnicastSession(message, session);
    }
    
    public void sendBroadcastSession(JsonObject msg, Session session){
        System.out.println("[DG - Send Broadcast]: "+msg.toString());
        userService.getUsersList().forEach((usr) -> {
            if(session == null || usr.getSession() != session){
                if(usr.getSession().isOpen())
                    sendMessageSession(msg, usr.getSession());
            }
        });
    }
    
    public void sendUnicastSession(JsonObject msg, Session session){
        System.out.println("[DG - Send Unicast]: "+msg.toString());
        sendMessageSession(msg, session);
    }
    
    public void sendMessageSession(JsonObject msg, Session session){
        try{
            session.getBasicRemote().sendText(msg.toString());
        }catch(IOException e){
            userService.getUsersList().removeIf(usr -> usr.getSession() == session);
            e.printStackTrace();
        }
    }
    
    public void createMsgFromServer(String msg){
        JsonProvider provider = JsonProvider.provider();
        JsonObject msgjs = provider.createObjectBuilder()
                .add("action", "msg")
                .add("content", msg)
                .add("rem", "server").build();
        
        sendBroadcastSession(msgjs, null);
    }
    
    public void createErrorMessage(String msg, Session session){
        JsonProvider provider = JsonProvider.provider();
        JsonObject msgError = provider.createObjectBuilder()
                .add("action", "msgError")
                .add("content", msg).build();
        
        sendUnicastSession(msgError, session);
    }
}