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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        
        userService.removeUser(session);
    }
    
    @OnError
    public void onError(Throwable error){
        Logger.getLogger(ChatWebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }
    
    /*
     *  Todos los mensajes que sean recibidos a traves del websocket
     *      pasaran por este metodo y seran enrutados a su función correspondiente.
     *  @param strMessage Es el string del mensaje debera de ser un JSON, se serializara en objeto Message y
            en caso de que esto no sea posible, solo existen 2 razones
                1) No es un json correctamente formado 
                2) Es un json cifrado.
            Intentara descifrar solo si existe un algoritmo activo y en caso de no ser posible
            el mensaje sera desechado.
        @param session Es un objecto de tipo Session el cual es utilizado para enviar mensajes
            a una session. El modelo Usuario tiene una propiedad de su Session, por lo tanto
            cada usuario del chat tiene su objeto Session el cual lo utiliza el servidor para enviar
            mensajes al usuario..
    
        El json que se utiliza para la comunicación cliente - servidor es el mismo en todos los casos por lo tanto,
            tanto el cliente como el servidor siempre estan esperando un json o un json cifrado.
        Se puede consultar el modelo en mx.jalan.Model.Message.
        Las propiedades "transient" no son serializadas por lo tanto cuando el objeto Message se convierte
            en un json las propiedades transient no son incluidas en el json. Estas propiedades son uso
            de la app para enrutar los mensajes y personalizar su envio (por ejemplo, si se tiene que serializar, bajo que cifrado lo tiene que hacer, etc).
        Para enrutar los mensajes se utiliza la propiedad "action" del objeto Message. Dependiendo de la variable
            action es lo que la aplicación hara con ese mensaje. Existe una clase con las constantes en mx.jalan.WebSocket.MessageHelper.
        TODO: En que casos las notificaciones del servidor al cliente seran cifrados si existe un metodo criptografio activo
            y en que casos aunque exista un metodo criptografico activo seran enviados en texto plano.
     */
    @OnMessage
    public void handleMessage(String strMessage, Session session)throws IOException{
        //this.sessionHandler.sendUnicastSession(new Message(MessageHelper.SIMPLE_MESSAGE, "Aikabrown, Traes el omnitracks!", this.encryptionActive, MessageHelper.OK_CODE), session);
        
        System.out.println("[DG - handleMessage]: "+strMessage);
        
        Message message = null;
        
        if(JsonUtils.isJsonObject(strMessage)){ //Si strMessage es json
            message = new Gson().fromJson(strMessage, Message.class);
            System.out.println("[DG - OnMessage]: "+message);
        }else{ //Probablemente sea un json - message cifrado.
            System.out.println("[DG - Verify Encryption with]: " + this.encryptionService.getCipher());
            System.out.println("[DG - OnMessage Encrypted?]: "+strMessage);
            
            if(this.encryptionService.getCipher() == null) //No existe metodo criptografico configurado
                return; //Desechar mensaje.
            
            String msgDecoded = this.encryptionService.getCipher().decode(strMessage);
            
            System.out.println("[DG - OnMessage Decrypted?]: "+msgDecoded);
            
            if(JsonUtils.isJsonObject(msgDecoded)){ //Json descifrado.
                message = new Gson().fromJson(msgDecoded, Message.class);
                System.out.println("[DG - OnMessage Decrypted]: "+message);
            }else{ //Mensaje extraño desechado.
                return;
            }
        }
        
        switch(message.getAction()){
            case MessageHelper.NEW_USER_MESSAGE:                
                //Verificar si existe usuario
                if(userService.existsUser(message.getUserSource().getNombre()) != null){
                    //Enviar un mensaje al usuario que el username que escogio ya esta siendo usado.
                    sessionHandler.sendUnicastSession(
                            MessagesConstructor
                                    .constructErrorMessage("El nombre de usuario que escogiste ya esta ocupado.", 
                                            session, 
                                            MessageHelper.USERNAME_UNAVAILABLE_CODE)
                                    //
                                    //.setEncryptProps(this.encryptionService.cipherActive() ? this.encryptionService.getEncryptionAlgorithmEnabled() : null) // SET ENCRYPTION PROPS
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
                        ).setEncryptProps(this.encryptionService.cipherActive() ? this.encryptionService.getEncryptionAlgorithmEnabled() : null) // SET ENCRYPTION PROPS
                    , session);
                
                break;
            case MessageHelper.SUPPORT_ENCRYPTION:
                Type t = new TypeToken<List<EncryptionAlgorithm>>(){}.getType();
                sessionHandler.sendUnicastSession(
                        new Message(
                                MessageHelper.SUPPORT_ENCRYPTION, 
                                new Gson().toJson(this.encryptionService.getEncryptionAlgorithmSupport(), t), 
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
                
                if(!this.encryptionService.cipherActive()){
                    //TODO SEND ERROR BECAUSE ENCRYPTION NOT SETTED YET.
                    return;
                }
                
                this.sessionHandler.disableEncryption();
                
                sessionHandler.sendBroadcastSession(MessagesConstructor.constructNotifyDisableEncryptionMessage());
                break;
            case MessageHelper.CHECK_ENCRYPTION:
                sessionHandler.sendUnicastSession(
                        new Message(
                            MessageHelper.CHECK_ENCRYPTION, 
                            this.encryptionService.cipherActive() ? this.encryptionService.getCipher().getCipherName() : null, 
                            null, 
                            this.encryptionService.cipherActive() ? MessageHelper.OK_CODE : MessageHelper.NOT_FOUND_CODE),
                        session);
            default:
                //TODO: Respond error unknown action
                System.out.println("[DG - Action unknown]");
        }
    }
}
