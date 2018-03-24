/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.jalan.WebSocket;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import mx.jalan.WebSocket.services.UserService;
import java.io.IOException;
import java.time.LocalDateTime;
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
        User storageUser = userService.existsSession(usuario.getSession());
        
        if(storageUser == null){  //Agregar nueva sesion
            userService.addUser(usuario); //Agrega la session sin otros daots
        }else{ //Actualiza los datos del usuario.
            if(usuario.getNombre() != null && !usuario.getNombre().trim().isEmpty()){
                storageUser.setNombre(usuario.getNombre());
                
                Message msg = MessagesConstructor
                        .constructServerMessage("Bienvenido: "+ usuario.getNombre()+"!!!");
                sendBroadcastSession(msg);
                
            }else{
                System.out.println("[*] Error al intentar actualizar los datos del usuario.");
            }
        }
    }

    /*public void sendMessage(JsonObject msg, Session session) {
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
    }*/
    
    public void sendMessage(Message msg){
        if(msg.getUserDestination() == null){ //Message to all
            //sendBroadcastSession(msg, msg.getSessionSource());
            sendBroadcastSession(msg);
        }else{ //Message to user by private message
            User userDest = userService.existsUser(msg.getUserDestination().getNombre());
            
            if(userDest != null){ //Verificar si el usuario existe
                sendUnicastSession(msg, userDest.getSession());
            }else{
                sendUnicastSession(MessagesConstructor
                    .constructErrorMessage("El usuario " + msg.getUserDestination().getNombre() + " no existe", 
                        userService.existsUser(msg.getUserSource().getNombre()).getSession(), 
                        MessageHelper.USER_NOT_FOUND_CODE));
            }
        }
    }
    
    /*public void createUpdateMessage(Session session){
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
    }*/
    
    public void createUpdateMessage(Session session){
        String usersArrJson = new Gson().toJson(userService.getUsersList());
        System.out.println(usersArrJson);
    }
    
    /*public void sendBroadcastSession(JsonObject msg, Session session){
        System.out.println("[DG - Send Broadcast]: "+msg.toString());
        
        if(userService.getUsersList().size() == 0)  return;
        
        userService.getUsersList().forEach((usr) -> {
            if(session == null || usr.getSession() != session){
                if(usr.getSession().isOpen() && !usr.getNombre().isEmpty())
                    sendMessageSession(msg, usr.getSession());
            }
        });
    }*/
    
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
    
    public void sendBroadcastSession(Message msg) {
        System.out.println("[DG - Send Broadcast]: "+msg.toString());
        
        if(userService.getUsersList().isEmpty())  return;
        Session session = null;
        
        //Obtener session origen y evitar enviarselo a el
        if(msg.getUserSource() != null)
            session = userService.existsUser(msg.getUserSource().getNombre()).getSession(); 
        
        for(User usr : userService.getUsersList()){
            if(session == null || usr.getSession() != session){
                if(usr.getSession().isOpen() && !usr.getNombre().isEmpty())
                    sendMessageSession(msg, usr.getSession());
            }   
        }
    }
    
    /*public void sendUnicastSession(JsonObject msg, Session session){
        System.out.println("[DG - Send Unicast]: "+msg.toString());
        sendMessageSession(msg, session);
    }*/
    
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
    
    /*private void sendMessageSession(JsonObject msg, Session session){
        try{
            session.getBasicRemote().sendText(msg.toString());
        }catch(IOException e){
            userService.getUsersList().removeIf(usr -> usr.getSession() == session);
            e.printStackTrace();
        }
    }*/
    
    public void sendMessageSession(Message msg, Session session){
        //TODO: Convert msg to json string and before send set timestamp now
        msg.setTimestamp(LocalDateTime.now());
        String jsonMessage = new Gson().toJson(msg, Message.class);
        
        try{
            session.getBasicRemote().sendText(jsonMessage);
        }catch(IOException e){
            userService.getUsersList().removeIf(usr -> usr.getSession() == session);
            e.printStackTrace();
        }
    }
}