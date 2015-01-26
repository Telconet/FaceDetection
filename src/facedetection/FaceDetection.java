/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facedetection;

/**
 *
 * @author Eduardo
 */
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.DbxWriteMode;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.core.*;



public class FaceDetection {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, DbxException {
        // TODO code application logic here
        
        //Cargamos la librerias nativas
        System.out.println(System.getProperty("java.library.path"));
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
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
        
        cliente.reconocer("https://www.dropbox.com/s/f0lftfsmaq63sza/35.jpg?dl=1", nombreGaleria, 0.6);
        
        //Autorizacion dropbox...
        String accessToken = null;
        File archivoToken = new File("token.txt");
        
        String dbxKey = "cuiclds3bj72a53";
        String dbxSecret = "m44th47fwrn9d0t";
        DbxAppInfo appInfo = new DbxAppInfo(dbxKey, dbxSecret);
        DbxRequestConfig config = new DbxRequestConfig("tnFace/1.0", Locale.getDefault().toString());
            
        if(!archivoToken.exists() ){
            //Inicializamos la cuenta dropbo           
            DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

            String authorizeUrl = webAuth.start();
            System.out.println("1. Go to: " + authorizeUrl);
            System.out.println("2. Click \"Allow\" (you might have to log in first)");
            System.out.println("3. Copy the authorization code.");
            String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

            DbxAuthFinish authFinish = webAuth.finish(code);
            accessToken = authFinish.accessToken;

            //Guardamos el token...
            BufferedWriter escritor = new BufferedWriter(new FileWriter(archivoToken));
            escritor.write(accessToken);
            escritor.close();
            
        }
        else{
            try{
                //Leemos el string de autorizacion...
                FileReader lectorArchivo = new FileReader(archivoToken);
                BufferedReader lector = new BufferedReader(lectorArchivo);

                //solo hay una linea en el archivo...
                 accessToken = lector.readLine().trim();

                 lector.close();
            }
            catch(IOException e){
                Bitacora log = new Bitacora();
                log.registarEnBitacora("errores.txt", "errores.txt", e.getMessage() + ": No se puedo leer el token de autorización Dropbox", Bitacora.SEVERE);
                System.exit(-1);
            }
        }
        
        
        //Creamos el cliente Dropbox, si ya tenemos el token de autorización
        DbxClient clienteDbx = new DbxClient(config, accessToken);
        //System.out.println("Linked account: " + clienteDbx.getAccountInfo().displayName);
                
        //Ahora intentamos subir un archivos...
        File archivoASubir = new File("camera_images/35.jpg");
        
        if(!archivoASubir.exists()){
                Bitacora log = new Bitacora();
                log.registarEnBitacora("errores.txt", "errores.txt", "El archivo no existe", Bitacora.WARNING);
        }
        
        FileInputStream inputStream = new FileInputStream(archivoASubir);
        String url = "";
        try {
            DbxEntry.File uploadedFile = clienteDbx.uploadFile("/35.jpg", DbxWriteMode.add(), archivoASubir.length(), inputStream);
            System.out.println("Uploaded: " + uploadedFile.toString());
            
             url = clienteDbx.createShareableUrl("/35.jpg");
            String url2 = url;
        } finally {
            inputStream.close();
        }
        
        //Mandamos foto a 
        
        
        
        
        //Ahora en cada cuadro buscamos si hay caras (para no usar llamadas innecesarias), para mandarlas a Kairos...
        //Debemos haber cargado las librerias nativas antes de usar estos metodos..
        CascadeClassifier faceDetector;        

        String recurso = FaceDetection.class.getResource("recursos/haarcascade_frontalface_alt.xml").getPath();
        faceDetector = new CascadeClassifier(recurso);
        
        //Cogemos 200 cuadros
        ArrayList<Mat> arregloImagenes = new ArrayList<>(200);
        Camara camara2 = new Camara("rtsp://192.168.137.172/profile2/media.smp");
        camara2.abrirCamara();      //empieza a enviar datos.
        for(int i = 0; i < 200; i++){
            Mat imagen = camara2.obtenerCuadro();
            arregloImagenes.add(imagen);
        }
        
        //Ahora en cada cuadro buscamos si hay caras (para no usar llamadas innecesarias), para mandarlas a Kairos...        
        Mat imagen;
        for(int i = 0; i < 200; i++ ){
             imagen = arregloImagenes.get(i);

             Highgui.imwrite("camera_images/camera" + i + ".jpg", imagen);
             
             //debemos subir fotos a servidor publico para que Kairos pueda acceder...
             
             MatOfRect faceDetections = new MatOfRect();
             faceDetector.detectMultiScale(imagen, faceDetections);
             
             //Detectamos la cara
            faceDetector.detectMultiScale(imagen, faceDetections);
 
            System.out.println(String.format(i + ": Se detectaron %s caras", faceDetections.toArray().length));
             
        }
        camara2.cerrarCamara();
        return;
    }
}
