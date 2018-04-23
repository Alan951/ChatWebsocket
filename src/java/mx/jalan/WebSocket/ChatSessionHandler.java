/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.jalan.WebSocket;

import com.google.gson.Gson;
import mx.jalan.WebSocket.services.UserService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.Session;
import mx.jalan.Model.EncryptionAlgorithm;
import mx.jalan.Model.User;
import mx.jalan.Model.Message;
import mx.jalan.Security.CipherBase;
import mx.jalan.Security.CipherFactory;
import mx.jalan.Security.EncryptionAlgorithms;
import mx.jalan.WebSocket.services.EncryptionService;

/**
 *
 * @author Jorge
 */
@ApplicationScoped
public class ChatSessionHandler {

    @Inject
    private UserService userService;
    
    @Inject
    private ChatWebSocketServer chatWS;
    
    @Inject
    private EncryptionService encryptionService;
    
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
                
                /*
                    Cuando un usuario se conecta y existe un
                    algoritmo de cifrado activo en el servidor
                    se lo notificara al usuario recien conectado
                */
                if(this.chatWS.getEncryptionActive() != null){
                    this.sendUnicastSession(
                        MessagesConstructor
                            .constructNotifyEnableEncryptionMessage(this.chatWS.getEncryptionActive()), storageUser.getSession());
                }
                
            }else{
                System.out.println("[*] Error al intentar actualizar los datos del usuario.");
            }
        }
    }
    
    public void sendMessage(Message msg){
        if(this.chatWS.getEncryptionActive() != null) //Si existe un algorithmo de cifrado activo, aplicar la propiedad para cifrar el mensaje
            msg.setEncryptProps(this.chatWS.getEncryptionActive()); 
        
        if(msg.getUserDestination() == null){ //Message to all
            //sendBroadcastSession(msg, msg.getSessionSource());
            sendBroadcastUsers(msg);
        }else{ //Message to user by private message
            User userDest = userService.existsUser(msg.getUserDestination().getNombre());
            
            if(userDest != null){ //Verificar si el usuario existe
                sendUnicastSession(msg, userDest.getSession());
            }else{
                sendUnicastSession(MessagesConstructor
                    .constructErrorMessage("El usuario " + msg.getUserDestination().getNombre() + " no existe", 
                        userService.existsUser(msg.getUserSource().getNombre()).getSession(), 
                        MessageHelper.NOT_FOUND_CODE));
            }
        }
    }
    
    public void createUpdateMessage(Session session){
        String usersArrJson = new Gson().toJson(userService.getUsersList());
        System.out.println(usersArrJson);
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
    
    public void sendBroadcastSession(Message msg) {
        System.out.println("[DG - Send BroadcastSession]: "+msg.toString());
        
        //if(userService.getUsersList().isEmpty())  return;
        if(userService.getSessionList().isEmpty())  return;
        Session session = null;
        
        //Obtener session origen y evitar enviarselo a el
        if(msg.getUserSource() != null)
            session = userService.existsUser(msg.getUserSource().getNombre()).getSession(); 
        
        //for(User usr : userService.getUsersList()){
        for(User usr : userService.getSessionList()){
            if(session == null || usr.getSession() != session){
                //if(usr.getSession().isOpen() && usr.getNombre() != null && !usr.getNombre().isEmpty())
                if(usr.getSession().isOpen())
                    sendMessageSession(msg, usr.getSession());
            }   
        }
    }
    
    public void sendBroadcastUsers(Message msg){
        System.out.println("[DG - Send BroadcastUsers]: "+msg.toString());
        
        if(userService.getUsersList().isEmpty())  return;
        Session session = null;
        
        //Obtener session origen y evitar enviarselo a el
        if(msg.getUserSource() != null)
            session = userService.existsUser(msg.getUserSource().getNombre()).getSession(); 
        
        for(User usr : userService.getUsersList()){
            if(session == null || usr.getSession() != session){
                if(usr.getSession().isOpen() && usr.getNombre() != null && !usr.getNombre().isEmpty())
                    sendMessageSession(msg, usr.getSession());
            }   
        }
    }
    
    public void sendBroadcastUsers(Message msg, Session session){
        System.out.println("[DG - Send BroadcastUsers]: "+msg.toString());
        
        if(userService.getUsersList().size() == 0)  return;
        
        userService.getUsersList().forEach((usr) -> {
            if(session == null || usr.getSession() != session){
                if(usr.getSession().isOpen() && !usr.getNombre().isEmpty())
                    sendMessageSession(msg, usr.getSession());
            }
        });
    }
    
    public void sendUnicastSession(Message msg, Session session){
        sendMessageSession(msg, session);
    }
    
    /*
        Send message to one session when message have the session destination.
    */
    public void sendUnicastSession(Message msg){        
        sendMessageSession(msg, msg.getSessionDestination());
    }
    
    public void sendMessageSession(Message msg, Session session){
        msg.setTimestamp(LocalDateTime.now());
        String jsonMessage = new Gson().toJson(msg, Message.class);
        String message = null;
        
        System.out.println("[DG - onSendMessageToSession JSON]: " + jsonMessage);
        
        if(msg.getEncryptProps() != null){ //Si el mensaje se tiene que enviar cifrado.
            
            message = this.chatWS.getCipher().encode(jsonMessage);
            System.out.println("Encrypted Message");
            System.out.println("Decrypted: " + this.chatWS.getCipher().decode(message));
        }else
            message = jsonMessage;
        
        System.out.println("[DG - onSendMessageToSession PLAIN]: " + message);
        
        
        
        try{
            session.getBasicRemote().sendText(message);
        }catch(IOException e){
            userService.getUsersList().removeIf(usr -> usr.getSession() == session);
            e.printStackTrace();
        }
    }
    
    public void enableEncryption(EncryptionAlgorithm encryptionReq){
        if(this.encryptionService.enableCipher(encryptionReq)){ //Pudo habilitar el cifrado
            this.sendBroadcastSession(MessagesConstructor.
                constructNotifyEnableEncryptionMessage(this.chatWS.getEncryptionActive()));
        }else{ //No pudo habilitar el cifrado.
        
        }
    }
    
    /*public void enableEncryption(EncryptionAlgorithm algorithmReq){
        //check if exists algorithmName
        EncryptionAlgorithm encrypAlg = this.chatWS.getAlgorithms()
                .stream()
                .filter((algorithm) -> algorithm.getAlgorithm()
                        .equals(algorithmReq.getAlgorithm()))
                .findFirst().get();
        
        if(encrypAlg == null){ //El cifrado no existe
            //TODO: respond with error to panel
            System.out.println("[DG] El metodo criptografico no existe");
            return;
        }
        
        System.out.println("[DG] Habilitando metodo criptografico");
        
        if(encrypAlg.getAlgorithmType() == EncryptionAlgorithms.SYNC_CIPHER){
            //validate if is long data type
            Long key = Long.parseLong(algorithmReq.getProperties().get("key"));
            
            CipherBase<String, Long> cipher = new CipherFactory().getCipher(encrypAlg.getAlgorithm());
            cipher.setKey(key);
            
            this.chatWS.setCipher(cipher);
            
        }else if(encrypAlg.getAlgorithmType() == EncryptionAlgorithms.ASYNC_CIPHER){
            Long publicKey = Long.parseLong(algorithmReq.getProperties().get("publicKey"));
            Long privateKey = Long.parseLong(algorithmReq.getProperties().get("privateKey"));
            
            CipherBase<String, Long> cipher = new CipherFactory().getCipher(encrypAlg.getAlgorithm());
            cipher.setPublicKey(publicKey);
            cipher.setPrivateKey(privateKey);
            
            this.chatWS.setCipher(cipher);
        }
        
        this.chatWS.setEncryptionActive(algorithmReq);
        
        //Notify to all the new cipher algorithm established.
        
        //this.sendBroadcastSession(new Message(MessageHelper.ENABLE_ENCRYPTION,
        //        this.chatWS.getCipher().getCipherName(), null, MessageHelper.OK_CODE));
    }*/

    void disableEncryption() {
        this.encryptionService.disableCipher();
        
        
        //Notify to all session that the encryption is disabled.
        this.sendBroadcastSession(new Message(MessageHelper.DISABLE_ENCRYPTION, null, null, MessageHelper.OK_CODE)); 
    }
}
