/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.jalan.WebSocket;

/**
 *
 * @author Ck
 */
public class MessageHelper {
//  Actions constants.
    public final static String ERROR_MESSAGE    = "error";
    public final static String SIMPLE_MESSAGE   = "msg";
    public final static String WELCOME_MESSAGE  = "welcome";
    public final static String REQ_CHANGES      = "req_chngs";
    public final static String NEW_USER_MESSAGE = "new_user";
    public final static String USER_LIST        = "user_list";
    public final static String SUPPORT_ENCRYPTION       = "supp_encryption";
    public final static String REQ_ENABLE_ENCRYPTION    = "req_en_encryption";
    public final static String REQ_DISABLE_ENCRYPTION   = "req_de_encryption";
    public final static String ENABLE_ENCRYPTION        = "en_encryption";
    public final static String DISABLE_ENCRYPTION       = "de_encryption";
    public final static String CHECK_ENCRYPTION         = "get_encryption";
//  Codes constants.
    public final static int NOT_FOUND_CODE              = 404;
    public final static int USERNAME_UNAVAILABLE_CODE   = 503;
    public final static int OK_CODE                     = 200;
}
