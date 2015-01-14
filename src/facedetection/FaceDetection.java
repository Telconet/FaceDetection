/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facedetection;

/**
 *
 * @author Eduardo
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
 
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONObject;

public class FaceDetection {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        
        String rutaImagen = "C:\\Users\\Eduardo\\Documents\\Trabajo\\Telconet\\test\\19.jpg";
        String appId = "752d01e5";
        String appKey = "f05dd012cb70fc6256357a9aeb17af2d"; 
        
        DeteccionCaras cliente = new DeteccionCaras(appId, appKey);
        //cliente.enrolar(rutaImagen, "GabrielIntriago", "test2");//"C:\\Users\\Eduardo\\Documents\\Trabajo\\Telconet\\resized\\1.jpg")
        JSONArray detectar = cliente.detectar("http://imageshack.com/a/img661/1023/0gjNN4.jpg");

    }
}
