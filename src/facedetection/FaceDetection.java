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
        
        //NO usar underscore en nombre de galeria
        String nombreGaleria = "TNtest";
        String[] imagenesEnrolamiento = {"http://imageshack.com/a/img908/1719/c6OYjX.jpg", 
                                        "http://imageshack.com/a/img537/1043/ufRqdf.jpg",
                                        "http://imageshack.com/a/img913/5581/zkRpih.jpg",
                                        "http://imageshack.com/a/img913/3559/g324Us.jpg",
                                        "http://imageshack.com/a/img901/6273/1mOE3M.jpg",
                                        "http://imageshack.com/a/img901/205/qN5PB5.jpg",
                                        "http://imageshack.com/a/img633/6061/OIZVGZ.jpg",
                                        "http://imageshack.com/a/img673/302/odcp7C.jpg",
                                        "http://imageshack.com/a/img661/2671/YnldBQ.jpg",
                                        "http://imageshack.com/a/img538/9758/47F48x.jpg",
                                        "http://imageshack.com/a/img661/7230/LujXQ7.jpg",
                                        "http://imageshack.com/a/img633/2036/hqnaVE.jpg",
                                        "http://imageshack.com/a/img909/3884/nHmq0g.jpg",
                                        "http://imageshack.com/a/img913/6017/pqE3TE.jpg",
                                        "http://imageshack.com/a/img673/5097/bR6ToE.jpg",
                                        "http://imageshack.com/a/img540/7770/57UOXV.jpg",
                                        "http://imageshack.com/a/img661/8805/LsPm5J.jpg",
                                        "http://imageshack.com/a/img538/6857/Ok6wNT.jpg",
                                        "http://imageshack.com/a/img538/9196/Vpm1hC.jpg",
                                        "http://imageshack.com/a/img538/5894/UQHZtA.jpg",
                                        "http://imageshack.com/a/img661/849/sGWDpe.jpg",
                                        "http://imageshack.com/a/img538/3516/IUJU50.jpg",
                                        "http://imageshack.com/a/img673/5236/8OrqwS.jpg",
                                        "http://imageshack.com/a/img673/6284/NuICdp.jpg",
                                        "http://imageshack.com/a/img909/7336/ch3AqA.jpg",
                                        "http://imageshack.com/a/img540/3489/v6YnhT.jpg",
                                        "http://imageshack.com/a/img537/6311/3eBDoM.jpg",
                                        "http://imageshack.com/a/img910/1863/thLCmf.jpg",
                                        "http://imageshack.com/a/img673/7641/6VQYrh.jpg",
                                        "http://imageshack.com/a/img538/6342/6zlv0N.jpg",
                                        "http://imageshack.com/a/img661/5995/0GAzij.jpg",
                                        "http://imageshack.com/a/img905/1018/8xiISK.jpg",
                                        "http://imageshack.com/a/img909/580/f8UEmE.jpg",
                                        "http://imageshack.com/a/img538/4590/AN5Z19.jpg",
                                        "http://imageshack.com/a/img907/3318/ooeGhM.jpg"};
        
        
        String appId = "752d01e5";
        String appKey = "f05dd012cb70fc6256357a9aeb17af2d"; 
        
        DeteccionCaras cliente = new DeteccionCaras(appId, appKey);
        
        //YA enrolamos...
        /*for(int i = 0; i < imagenesEnrolamiento.length; i++){
            String idSujeto = "";
            switch(i){
                case 0:
                    idSujeto = "gabriel-intriago";
                    break;
                case 1:
                    idSujeto = "claudia-rojas";
                    break;
                case 2:
                    idSujeto = "gabriel-intriago";
                    break;
                case 3:
                    idSujeto = "claudia-rojas";
                    break;
                case 4:
                    idSujeto = "claudia-rojas";
                    break;
                case 5:
                    idSujeto = "claudia-rojas";
                    break;
                case 6:
                    idSujeto = "duval-medina";
                    break;
                case 7:
                    idSujeto = "duval-medina";
                    break;
                case 8:
                    idSujeto = "claudia-rojas";
                    break;
                case 9:
                    idSujeto = "duval-medina";
                    break;
                case 10:
                    idSujeto = "duval-medina";
                    break;
                case 11:
                    idSujeto = "rosalina-bajana";
                    break;
                case 12:
                    idSujeto = "rosalina-bajana";
                    break;
                case 13:
                    idSujeto = "rosalina-bajana";
                    break;
                case 14:
                    idSujeto = "rosalina-bajana";
                    break;
                case 15:
                    idSujeto = "duval-medina";
                    break;
                case 16:
                    idSujeto = "rosalina-bajana";
                    break;
                case 17:
                    idSujeto = "maria-murillo";
                    break;
                case 18:
                    idSujeto = "maria-murillo";
                    break;
                case 19:
                    idSujeto = "maria-murillo";
                    break;
                case 20:
                    idSujeto = "maria-murillo";
                    break;
                case 21:
                    idSujeto = "maria-murillo";
                    break;
                case 22:
                    idSujeto = "maria-murillo";
                    break;
                case 23:
                    idSujeto = "rosalina-bajana";
                    break;
                case 24:
                    idSujeto = "eduardo-murillo";
                    break;
                case 25:
                    idSujeto = "eduardo-murillo";
                    break;
                case 26:
                    idSujeto = "eduardo-murillo";
                    break;
                case 27:
                    idSujeto = "eduardo-murillo";
                    break;
                case 28:
                    idSujeto = "eduardo-murillo";
                    break;
                case 29:
                    idSujeto = "eduardo-murillo";
                    break;
                case 30:
                    idSujeto = "eduardo-murillo";
                    break;
                case 31:
                    idSujeto = "gabriel-intriago";
                    break;
                case 32:
                    idSujeto = "gabriel-intriago";
                    break;
                case 33:
                    idSujeto = "gabriel-intriago";
                    break;
                case 34:
                    idSujeto = "gabriel-intriago";
                    break;
                default:
                    idSujeto = "";
                    break;
            }
            
            if(idSujeto.equals("")){
                System.exit(-1);
            }
            
            int status = cliente.enrolar(imagenesEnrolamiento[i], idSujeto, nombreGaleria);
            
            if(status == 0){
                System.out.println("OK. Persona i: " + i  + ", URL: " + imagenesEnrolamiento[i] + " enrolada.");
            }
            else{
                System.out.println("ERROR. Persona i: " + i  + ", URL: " + imagenesEnrolamiento[i] + " NO enrolada.");
            }
        }*/
        
        //JSONArray detectar = cliente.detectar("http://imageshack.com/a/img661/1023/0gjNN4.jpg");
        

        //JSONObject ed = detectar.getJSONObject(0);
        
        cliente.reconocer("http://imagizer.imageshack.us/a/img661/912/3iBhgP.jpg", nombreGaleria , 0.7);
        
        int i= 2;
    }
}
