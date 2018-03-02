/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.jalan.WebSocket;

import javax.websocket.Session;
import mx.jalan.Model.Message;

/**
 *
 * @author Ck
 */
public class MessagesConstructor {
    public static Message constructErrorMessage(String msg, Session session, int code){
        Message msgError = new Message();
        msgError.setAction("error");
        msgError.setMessage(msg);
        msgError.setSessionDestination(session);
        msgError.setCode(code);
        
        return msgError;
    }
}
