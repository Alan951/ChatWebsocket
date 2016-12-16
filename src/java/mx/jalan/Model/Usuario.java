/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.jalan.Model;

import javax.websocket.Session;

/**
 *
 * @author Jorge
 */
public class Usuario {
    private String nombre;
    private String avatarURL;
    private Session session;

    public Usuario(){}
    
    public Usuario(Session session){
        setSession(session);
    }
    
    public Usuario(String nombre, String avatar) {
        this.nombre = nombre;
        this.avatarURL = avatar;
    }
    
    public Session getSession(){
        return session;
    }
    
    public void setSession(Session session){
        this.session = session;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAvatar() {
        return avatarURL;
    }

    public void setAvatar(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    @Override
    public String toString() {
        return "Usuario{" + "nombre=" + nombre + ", avatar=" + avatarURL + '}';
    }
}
