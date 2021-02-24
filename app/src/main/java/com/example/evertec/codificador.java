package com.example.evertec;

import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONException;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

public class codificador {
    public String nonce ="", dateString ="", base64Str = "", datoAleatorio = "";

    /**
     * @param data to be encrypted
     * @param shaN encrypt method,SHA-1,SHA-224,SHA-256,SHA-384,SHA-512
     * @return 已加密的数据
     * @throws Exception
     */
    public static byte[] encryptSHA(byte[] data, String shaN) throws Exception {

        MessageDigest sha = MessageDigest.getInstance(shaN);
        sha.update(data);
        return sha.digest();
    }

    /**
     * BASE64解密
     *
     * @param key the String to be decrypted
     * @return byte[] the data which is decrypted
     * @throws Exception
     */
    public static byte[] decryptBASE64(String key) throws Exception {
        return Base64.decode(key,Base64.DEFAULT);
    }
    /**
     * BASE64加密
     *
     * @param key the String to be encrypted
     * @return String the data which is encrypted
     * @throws Exception
     */
    public static String encryptBASE64(byte[] key) throws Exception {
        return Base64.encodeToString(key, Base64.DEFAULT);
    }

    //NUMERO ALEATORIO NONCE
    public String aleatorio(){
        int i=0, cantidad=8, rango=10;
        String nonce="";
        int arreglo[] = new int[cantidad];

        arreglo[i] = (int)(Math.random()*rango);
        for(i=0; i<cantidad; i++){
            arreglo[i] = (int)(Math.random()*rango);
            nonce+=arreglo[i];
        }
        return nonce;
    }

    public String tranKey(){

        codificador dato = new codificador();

        datoAleatorio = dato.aleatorio();

        nonce = datoAleatorio;   //nonce
        nonce += dato.fecha();     //seed
        nonce += "024h1IlD";        //Trankey

        //System.out.println("nonce:"+nonce);

        byte [] inputData = nonce.getBytes();
        byte [] outputData = new byte[0];

        try {
            outputData = codificador.encryptSHA(inputData,"SHA-256");
        } catch (Exception e) {
            e.printStackTrace();
        }

        BigInteger shaData = new BigInteger(outputData);
        //System.out.println("Sha: " + shaData.toString(16));

        try {
            base64Str = codificador.encryptBASE64(outputData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return base64Str;

    }

    public String nonce(){
        byte  [] base64nonce = datoAleatorio.getBytes();

        try {
            base64Str = codificador.encryptBASE64(base64nonce);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  base64Str;
    }

    public String fecha(){
        DateFormat x = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Date obj = new Date();
        dateString = x.format(obj);
        return  dateString;
    }

    public String fecha2(String zz){
        DateFormat x = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date obj = new Date();
        dateString = x.format(obj);
        return  dateString;
    }

    public String getIP() {
        String ip1 = "";
        String ip2 = "";
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    ip1 = addr.getHostAddress();
                    ip2 += ip1 + ",";
                    //System.out.println(iface.getDisplayName() + " UNOOOOOOOOO " + ip1);
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        //System.out.println(z);
        //ARREGLO DE DIRECCIONES IP
        String [] vect = ip2.split(",");
        //IP DEL USUARIO
        ip2 = vect[5];
        return ip2;
    }

}
