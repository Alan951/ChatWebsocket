/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package mx.jalan.websocket;
package mx.jalan.WebSocket;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import mx.jalan.WebSocket.services.UserService;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import mx.jalan.Model.EncryptionAlgorithm;
import mx.jalan.Model.User;
import mx.jalan.Model.Message;
import mx.jalan.Security.CipherBase;
import mx.jalan.Security.EncryptionAlgorithms;
import mx.jalan.Utils.JsonUtils;
import mx.jalan.WebSocket.services.EncryptionService;

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
    
    @Inject
    private EncryptionService encryptionService;
    
    private List<EncryptionAlgorithm> encryptionSupport = new ArrayList<EncryptionAlgorithm>();
    private EncryptionAlgorithm encryptionActive;
    private CipherBase cipher;
    
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
    public void handleMessage(String strMessage, Session session)throws IOException{
        //this.sessionHandler.sendUnicastSession(new Message(MessageHelper.SIMPLE_MESSAGE, "Aikabrown, Traes el omnitracks!", this.encryptionActive, MessageHelper.OK_CODE), session);
        
        System.out.println("[DG - handleMessage]: "+strMessage);
        
        Message message = null;
        
        if(JsonUtils.isJsonObject(strMessage)){ //Si strMessage es json
            message = new Gson().fromJson(strMessage, Message.class);
            System.out.println("[DG - OnMessage]: "+message);
        }else{ //Probablemente sea un mensaje cifrado.
            System.out.println("[DG - Verify Encryption with]: " + this.cipher);
            System.out.println("[DG - OnMessage Encrypted?]: "+strMessage);
            
            String msgDecoded = this.cipher.decode(strMessage);
            
            System.out.println("[DG - OnMessage Decrypted?]: "+msgDecoded);
            
            if(JsonUtils.isJsonObject(msgDecoded)){
                message = new Gson().fromJson(msgDecoded, Message.class);
                System.out.println("[DG - OnMessage Decrypted]: "+message);
            }else{
                return;
            }
        }
        
        switch(message.getAction()){
            case MessageHelper.NEW_USER_MESSAGE:                
                //Verificar si existe usuario
                if(userService.existsUser(message.getUserSource().getNombre()) != null){
                    //sessionHandler.createErrorMessage(); //Falta modificar
                    sessionHandler.sendUnicastSession(
                            MessagesConstructor
                                    .constructErrorMessage("El nombre de usuario que escogiste ya esta ocupado.", 
                                            session, 
                                            MessageHelper.USERNAME_UNAVAILABLE_CODE)
                                    .setEncryptProps(this.getEncryptionActive() != null ? this.getEncryptionActive() : null) // SET ENCRYPTION PROPS
                    );
                    
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
            case MessageHelper.USER_LIST:                
                sessionHandler.sendUnicastSession(
                        new Message(
                                MessageHelper.USER_LIST,
                                new Gson().toJson(this.userService.getUsersList()),
                                null,
                                MessageHelper.OK_CODE
                        ).setEncryptProps(this.getEncryptionActive() != null ? this.getEncryptionActive() : null) // SET ENCRYPTION PROPS
                    , session);
                
                break;
            case MessageHelper.SUPPORT_ENCRYPTION:
                Type t = new TypeToken<List<EncryptionAlgorithm>>(){}.getType();
                sessionHandler.sendUnicastSession(
                        new Message(
                                MessageHelper.SUPPORT_ENCRYPTION, 
                                new Gson().toJson(this.encryptionSupport, t), 
                                null, 
                                MessageHelper.OK_CODE),
                        session);
                break;
            case MessageHelper.REQ_ENABLE_ENCRYPTION:
                EncryptionAlgorithm cipher = 
                        new Gson().fromJson(message.getMessage(), 
                                EncryptionAlgorithm.class);
                
                System.out.println("[DG - EnableEncryption]: "+ cipher);
                
                this.sessionHandler.enableEncryption(cipher);
                
                break;
            case MessageHelper.REQ_DISABLE_ENCRYPTION:
                System.out.println("[DG - DisableEncryption]: ");
                
                if(this.encryptionActive == null){
                    //TODO SEND ERROR BECAUSE ENCRYPTION NOT SETTED YET.
                    return;
                }
                
                this.sessionHandler.disableEncryption();
                break;
            case MessageHelper.CHECK_ENCRYPTION:
                sessionHandler.sendUnicastSession(
                        new Message(
                            MessageHelper.CHECK_ENCRYPTION, 
                            this.cipher != null ? this.cipher.getCipherName() : this.cipher.getCipherName(), 
                            null, 
                            this.cipher != null ? MessageHelper.OK_CODE : MessageHelper.NOT_FOUND_CODE),
                        session);
            default:
                //TODO: Respond error action unknown
                System.out.println("[DG - Action unknown]");
        }
    }
    
    /*
        All Algorithm supported is declared here.
    */
    @PostConstruct
    public void initEncryption(){
        System.out.println("postconstruct called");
        Map<String, String> syncProp = new HashMap<String, String>();
        syncProp.put("key", "");
    }
    
    public List<EncryptionAlgorithm> getAlgorithms(){
        return this.encryptionSupport;
    }
    
    public EncryptionAlgorithm getEncryptionActive(){
        return this.encryptionActive;
    }
    
    public void setEncryptionActive(EncryptionAlgorithm cipher){
        this.encryptionActive = cipher;
    }
    
    public CipherBase getCipher(){
        return this.cipher;
    }
    
    public void setCipher(CipherBase cipher){
        this.cipher = cipher;
    }
}
