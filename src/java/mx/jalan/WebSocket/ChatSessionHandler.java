/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.jalan.WebSocket;

import mx.jalan.WebSocket.services.UserService;
import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import mx.jalan.Model.User;
import mx.jalan.Model.Message;

/**
 *
 * @author Jorge
 */
@ApplicationScoped
public class ChatSessionHandler {

    @Inject
    private UserService userService;
    
    public void addUser(User usuario){
        if(userService.existsSession(usuario.getSession()) == null){ 
            userService.addUser(usuario); //Agrega la session
        }else{
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
                createErrorMessage("El usuario "+msg.getString("destin")+" no existe", session, 404);
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
        
        if(userService.getUsersList().size() == 0)  return;
        
        userService.getUsersList().forEach((usr) -> {
            if(session == null || usr.getSession() != session){
                if(usr.getSession().isOpen() && !usr.getNombre().isEmpty())
                    sendMessageSession(msg, usr.getSession());
            }
        });
    }
    
    public void sendBroadcastSession(Message msg, Session session){
        System.out.println("[DG - Send Broadcast]: "+msg.toString());
        
        if(userService.getUsersList().size() == 0)  return;
        
        userService.getUsersList().forEach((usr) -> {
            if(session == null || usr.getSession() != session){
                if(usr.getSession().isOpen() && !usr.getNombre().isEmpty())
                    sendMessageSession(msg, usr.getSession());
            }
        });
    }
    
    public void sendUnicastSession(JsonObject msg, Session session){
        System.out.println("[DG - Send Unicast]: "+msg.toString());
        sendMessageSession(msg, session);
    }
    
    public void sendUnicastSession(Message msg, Session session){
        System.out.println("[DG - Send Unicast]: "+msg.toString());
        sendMessageSession(msg, session);
    }
    
    /*
        Send message to one session when message have the session destination.
    */
    public void sendUnicastSession(Message msg){
        sendMessageSession(msg, msg.getSessionDestination());
    }
    
    private void sendMessageSession(JsonObject msg, Session session){
        try{
            session.getBasicRemote().sendText(msg.toString());
        }catch(IOException e){
            userService.getUsersList().removeIf(usr -> usr.getSession() == session);
            e.printStackTrace();
        }
    }
    
    public void sendMessageSession(Message msg, Session session){
        //TODO: Convert msg to json string
        
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
    
    public void createMsgFromServer(Message message){
        
    }
    
    public void createErrorMessage(String msg, Session session, int code){
        JsonProvider provider = JsonProvider.provider();
        JsonObject msgError = provider.createObjectBuilder()
                .add("action", "msgError")
                .add("content", msg)
                .add("code", code).build();
        
        sendUnicastSession(msgError, session);
    }
    
    public void createErrorMessageN(String msg, Session session, int code){
        
    }
}