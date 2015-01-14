/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facedetection;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 *
 * @author Eduardo
 */
public class DeteccionCaras {
    
    public static final String URL_KAIROS = "http://api.kairos.com/";
    
    private String appId, appKey;
    
    public DeteccionCaras(String appId, String appKey){
        this.appId = appId;
        this.appKey = appKey;
    }
            
            
    //Enrola una persona con el id dado.
    public int enrolar(String rutaImagen, String idSujeto, String galeria){
        
        try{
                
            String request = "{\"url\":\"" + rutaImagen + "\",\"gallery_name\":\"" + galeria + "\",\"subject_id\":\"" +idSujeto + "\",\"selector\":\"SETPOSE\", \"symmetricFill\": \"true\"}";

            //Enviar solicitud... .header("X-Mashape-Key", "<required>") https://kairos-face-recognition.p.mashape.com/recognize  
            HttpResponse<JsonNode> respuesta = Unirest.post("http://api.kairos.com/enroll")           
            .header("Content-Type", "application/json")
            .header("app_id", this.appId)
            .header("app_key", this.appKey)
            .header("Accept", "application/json")
            .body(request)
            .asJson();
            
            if(respuesta.getBody().toString().contains("success")){
                return 0;
            }
            else{
                Bitacora log = new Bitacora();
                
                //Obtenemos el arreglo del objeto JSON (Error)          //CHECK si hay JSONObject??
                JSONArray arreglo = respuesta.getBody().getArray();
                JSONObject objetoJSON = arreglo.getJSONObject(0);
                
                //Luego obtenemos el objeto JSON con los errores y mensajes...
                arreglo = objetoJSON.getJSONArray("Errors");
                objetoJSON = arreglo.getJSONObject(0);
                
                //Extraemos los campos.
                StringBuilder str = new StringBuilder();
                Iterator<?> keys = objetoJSON.keys();
                
                str.append(respuesta.getStatus());
                str.append(" ");
                str.append(respuesta.getStatusText());
                str.append(" - ");
                
                int i = 0;
                //Extraemos los mensajes de error.
                while( keys.hasNext() ){
                    String key = (String)keys.next();
                   
                    if(i != 0){
                        str.append(", ");
                    }
                    str.append(key);
                    str.append(": ");
                    str.append(objetoJSON.get(key).toString());
                    i++;
                }
                                
                log.registarEnBitacora("log_errores","errores.txt", "Solicitud ENROLL: " + request, Bitacora.INFO);
                log.registarEnBitacora("log_errores","errores.txt", str.toString(), Bitacora.SEVERE);
                
                //Escribir que paso
                return -1;
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }
    
    //Detecta caras en la foto, y nos devuelve informacion de cada una.
    public JSONArray detectar(String rutaImagen){
        try{
            
            String request = "{\"url\":\"" + rutaImagen + "\",\"selector\":\"SETPOSE\",\"minHeadScale\":\".0625\"}";
            
           
            HttpResponse<JsonNode> respuesta = Unirest.post("http://api.kairos.com/detect")           
            .header("Content-Type", "application/json")
            .header("app_id", appId)
            .header("app_key", appKey)
            .header("Accept", "application/json")
            .body(request)
            .asJson();
            
            if(respuesta.getBody().toString().contains("images")){
              
                JSONArray ret = respuesta.getBody().getArray().getJSONObject(0).getJSONArray("images").getJSONObject(0).getJSONArray("faces");
              
                //TODO: Procesar informacion caras.
                return ret;
            }
            else{
                Bitacora log = new Bitacora();
                
                //Obtenemos el arreglo del objeto JSON (Error)
                JSONArray arreglo = respuesta.getBody().getArray();
                JSONObject objetoJSON = arreglo.getJSONObject(0);
                
                //Luego obtenemos el objeto JSON con los errores y mensajes...
                arreglo = objetoJSON.getJSONArray("Errors");
                objetoJSON = arreglo.getJSONObject(0);
                
                //Extraemos los campos.
                StringBuilder str = new StringBuilder();
                Iterator<?> keys = objetoJSON.keys();
                
                str.append(respuesta.getStatus());
                str.append(" ");
                str.append(respuesta.getStatusText());
                str.append(" - ");
                
                int i = 0;
                //Extraemos los mensajes de error.
                while( keys.hasNext() ){
                    String key = (String)keys.next();
                   
                    if(i != 0){
                        str.append(", ");
                    }
                    str.append(key);
                    str.append(": ");
                    str.append(objetoJSON.get(key).toString());
                    i++;
                }
                                
                log.registarEnBitacora("log_errores","errores.txt", "Solicitud DETECT: " + request, Bitacora.INFO);
                log.registarEnBitacora("log_errores","errores.txt", str.toString(), Bitacora.SEVERE);
                
                //Escribir que paso
                return null;
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    
    //Reconocer...
    public void reconocer(String rutaImagen){
        
    }
    
}
