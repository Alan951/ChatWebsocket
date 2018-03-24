/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package mx.jalan.websocket;
package mx.jalan.WebSocket;

import com.google.gson.Gson;
import mx.jalan.WebSocket.services.UserService;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import mx.jalan.Model.EncryptionAlgorithms;
import mx.jalan.Model.User;
import mx.jalan.Model.Message;

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
    
    @OnOpen
    public void open(Session session){
        System.out.println("OnOpen - New Session");
        User usr = new User();
        usr.setSession(session);
        
        sessionHandler.addUser(usr);
    }
    
    @OnClose
    public void close(Session session, CloseReason reason){
        System.out.println("Close connection: "+session);
        User usr = userService.existsSession(session);
        if(usr != null && usr.getNombre() != null){
            Message msg = MessagesConstructor.constructServerMessage("El usuario: \""+usr.getNombre()+"\" ha salido del chat.");
            sessionHandler.sendBroadcastSession(msg);
        }
        //if(usr != null && usr.getNombre() != null) sessionHandler.createMsgFromServer("El usuario: \""+usr.getNombre()+"\" ha salido del chat.");
        userService.removeUser(session);
    }
    
    @OnError
    public void onError(Throwable error){
        Logger.getLogger(ChatWebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }
    
    @OnMessage
    public void handleMessage(String jsonMessage, Session session)throws IOException{
        System.out.println("handleMessage: "+jsonMessage);
        Message message = new Gson().fromJson(jsonMessage, Message.class);
        
        System.out.println("[DG - OnMessage]: "+message);
        
        switch(message.getAction()){
            case MessageHelper.NEW_USER_MESSAGE:                
                //Verificar si existe usuario
                if(userService.existsUser(message.getUserSource().getNombre()) != null){
                    //sessionHandler.createErrorMessage(); //Falta modificar
                    sessionHandler.sendUnicastSession(
                            MessagesConstructor
                                    .constructErrorMessage("El nombre de usuario que escogiste ya esta ocupado.", 
                                            session, 
                                            MessageHelper.USERNAME_UNAVAILABLE));
                    session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "USER EXISTS"));
                    return;
                }
                
                //Actualizar los datos de la sesion   
                message.getUserSource().setSession(session);
                sessionHandler.addUser(message.getUserSource());
                break;
            case MessageHelper.SIMPLE_MESSAGE:
                sessionHandler.sendMessage(message);
                break;
            case MessageHelper.REQ_CHANGES:
                sessionHandler.createUpdateMessage(session);
                break;
        }
    }
}
