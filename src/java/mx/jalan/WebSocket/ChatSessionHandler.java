/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.jalan.websocket;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import mx.jalan.Model.Usuario;

/**
 *
 * @author Jorge
 */
@ApplicationScoped
public class ChatSessionHandler {
    
    private final Set<Usuario> users = new HashSet<>();
    
    public void addUser(Usuario usuario, Session session){
        if(existsSession(session) == null){
            users.add(usuario);
        }else{
            removeSession(session);
            users.add(usuario);
            if(usuario.getNombre() != null && !usuario.getNombre().trim().isEmpty())
                createMsgFromServer("Bienvenido "+usuario.getNombre()+"!!!");
        }
    }
    
    public void removeUser(Usuario usuario){
        users.remove(usuario);
    }
    
    public void removeSession(Session session){
        users.removeIf(usr -> usr.getSession() == session);
    }

    public void sendMessage(JsonObject msg, Session session) {
        if(msg.getString("destin").equals("all")){
            sendBroadcastSession(msg, session);
        }else{
            Usuario usr = existsUser(msg.getString("destin"));
            
            if(usr != null){
                sendUnicastSession(msg, usr.getSession());
            }else{
                createErrorMessage("El usuario "+msg.getString("destin")+" no existe", session);
            }
        }
    }
    
    public void createUpdateMessage(Session session){
        Usuario sourceUsr = existsSession(session);
        JsonArrayBuilder jarr = JsonProvider.provider().createArrayBuilder();
        
        users.forEach((usr) -> {
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
        users.forEach((usr) -> {
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
            users.removeIf(usr -> usr.getSession() == session);
            e.printStackTrace();
        }
    }
    
    public Usuario existsUser(String nombre){
        for(Usuario usr : users){
            if(usr.getNombre().equalsIgnoreCase(nombre)){
                return usr;
            }
        }
        
        return null;
    }
    
    public Usuario existsSession(Session session){
        for(Usuario usr : users){
            if(usr.getSession().equals(session)){
                return usr;
            }
        }
        
        return null;
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