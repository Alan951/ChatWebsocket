/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.jalan.Security.Algorithms;

import java.io.Serializable;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DESCipher<T, KT extends Serializable> implements CipherBase<T, KT> {

    private KT key;

    public String encode(T textToCipher) {
        String mcifrado = "";
        String part = "";
        byte[] encriptado = null;
        String clave = this.key.toString();
        String mensaje = textToCipher.toString();

        try {
            while (clave.length() < 8) {
                clave = clave + " ";
            }
            Key key = new SecretKeySpec(clave.getBytes("ISO-8859-1"), 0, 8, "DES");
            while (mensaje.length() % 8 != 0) {
                mensaje = mensaje + " ";
            }
            String[] cinta = new String[mensaje.length() / 8];
            for (int i = 0; (i * 8) < mensaje.length(); i++) {
                for (int x = i * 8; x < ((i + 1) * 8); x++) {
                    part = part + mensaje.charAt(x);
                }
                cinta[i] = part;
                part = "";
            }
            for (String a : cinta) {
                Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
                des.init(Cipher.ENCRYPT_MODE, key);
                encriptado = des.doFinal(a.getBytes("ISO-8859-1"));
                mcifrado = mcifrado + new String(encriptado, "ISO-8859-1");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        mcifrado += "a";

        return mcifrado;
    }

    public String decode(T textToDecipher) {
        String mdescifrado = null;
        try {
            String mcifrado = textToDecipher.toString();
            String clave = this.key.toString();
            byte[] decriptado = new byte[mcifrado.length() - 1];;
            byte[] fin = null;
            Key kay;

            byte[] f = mcifrado.getBytes("ISO-8859-1");
            for (int i = 0; i < mcifrado.length() - 1; i++) {
                decriptado[i] = f[i];
            }

            Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
            while (clave.length() < 8) {
                clave = clave + " ";
            }
            kay = new SecretKeySpec(clave.getBytes("ISO-8859-1"), 0, 8, "DES");
            des.init(Cipher.DECRYPT_MODE, kay);
            fin = des.doFinal(decriptado);
            mdescifrado = new String(fin, "ISO-8859-1");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return mdescifrado.trim();
    }

    @Override
    public String getCipherName() {
        return "DES";
    }

    @Override
    public boolean isAsyncCipher() {
        return false;
    }

    @Override
    public void setKey(KT key) {
        this.key = key;
    }

    @Override
    public KT getKey() {
        return this.key;
    }

    @Override
    public void setPrivateKey(KT key) {
        // TODO Auto-generated method stub

    }

    @Override
    public KT getPrivateKey() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPublicKey(KT key) {
        // TODO Auto-generated method stub

    }

    @Override
    public KT getPublicKey() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDebugMode(boolean debug) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean idDebugMode() {
        // TODO Auto-generated method stub
        return false;
    }
}
