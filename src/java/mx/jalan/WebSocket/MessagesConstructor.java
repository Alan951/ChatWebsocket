/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.jalan.WebSocket;

import javax.inject.Inject;
import javax.websocket.Session;
import mx.jalan.Model.EncryptionAlgorithm;
import mx.jalan.Model.Message;
import mx.jalan.WebSocket.services.UserService;

/**
 *
 * @author Ck
 */
public class MessagesConstructor {
    
    public static Message constructErrorMessage(String msg, Session session, int code){
        Message msgError = new Message();
        msgError.setAction(MessageHelper.ERROR_MESSAGE);
        msgError.setMessage(msg);
        msgError.setSessionDestination(session);
        msgError.setCode(code);
        
        return msgError;
    }
    
    public static Message constructServerMessage(String message){
        Message msg = new Message();
        msg.setAction(MessageHelper.SIMPLE_MESSAGE);
        msg.setMessage(message);
        msg.setCode(200);
        
        return msg;
    }
    
    public static Message constructNotifyEnableEncryptionMessage(EncryptionAlgorithm encryption){
        return new Message(MessageHelper.ENABLE_ENCRYPTION,
                encryption.getAlgorithm(), null, MessageHelper.OK_CODE);
    }
}
