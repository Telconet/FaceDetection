/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facedetection;

/**
 *
 * @author Eduardo
 */
import com.dropbox.core.DbxException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.opencv.core.*;



public class FaceDetection {
    
    public static boolean personaEncontrada = false;          //solo para pruebas
    public static final Object mutex = new Object();
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, DbxException, InterruptedException, IllegalAccessException {
        // TODO code application logic here
        
        //Cargamos la librerias nativas
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                
        //NO usar underscore en nombre de galeria
        String nombreGaleria = "TNtest";       
        
        String appId = "752d01e5";
        String appKey = "f05dd012cb70fc6256357a9aeb17af2d"; 
        
        DeteccionCaras clienteDeteccionCaras = new DeteccionCaras(appId, appKey);
        
        //clienteDeteccionCaras.enrolar("https://www.dropbox.com/s/89s7ne23f7y18ta/tomi1.jpg?dl=1", "tomislav-topic", nombreGaleria);
        //clienteDeteccionCaras.enrolar("https://www.dropbox.com/s/h4jr1zz98hwbjjx/tomi2.jpg?dl=1", "tomislav-topic", nombreGaleria);
            
        
        //Directorio de trabajo
        String directorio = null;
        URL ubicacion = ProcesamientoImagenes.class.getProtectionDomain().getCodeSource().getLocation();
        directorio = ubicacion.getFile();
        directorio = directorio.substring(1);
        
        if(directorio.contains("build/classes")){
            directorio = directorio.replace("/build/classes", "");
        }
        else{
            directorio = directorio.replace("/dist/FaceDetection.jar", "");
        }
        
        System.out.println(directorio);
        
        //Servidor Dropbox
        System.out.println("Ruta de archivo token: " + directorio + "/token.txt");
        String dbxKey = "cuiclds3bj72a53";
        String dbxSecret = "m44th47fwrn9d0t";
        ServidorImagenes servidorImagenes = new ServidorImagenes(dbxKey, dbxSecret);
        servidorImagenes.conectarADropbox(directorio + "/token.txt","tnFace/1.0");
                
        //probar los hilos
        long millis_before = System.currentTimeMillis();
        ExecutorService ejecutor = Executors.newFixedThreadPool(5);     //4 threads al mismo tiempo...
      
        //Cogemos 200 cuadros
        ArrayList<Mat> arregloImagenes = new ArrayList<>(50);
        Camara camara2 = new Camara("rtsp://192.168.137.172/profile2/media.smp");
        camara2.abrirCamara();      //empieza a enviar datos.
        for(int i = 0; i < 50; i++){
            Mat imagen = camara2.obtenerCuadro();
            ProcesamientoImagenes faceDt = new ProcesamientoImagenes(imagen, i, servidorImagenes, clienteDeteccionCaras, nombreGaleria, directorio, 0.6);
            //arregloImagenes.add(imagen);
            ejecutor.execute(faceDt); 
        }
        
        /*synchronized(mutex){
            if(personaEncontrada){
                ejecutor.shutdownNow();
            }
        }*/
        
        camara2.cerrarCamara();
        
        
        /*for(int i = 0; i < 50; i++){
            ProcesamientoImagenes faceDt = new ProcesamientoImagenes(arregloImagenes.get(i), i, servidorImagenes, clienteDeteccionCaras, nombreGaleria, directorio, 0.7);
            //ProcesamientoImagenes faceDt = new ProcesamientoImagenes("camera_images/camera" + i + ".jpg", i, servidorImagenes, clienteDeteccionCaras, nombreGaleria, directorio, 0.7);
            ejecutor.execute(faceDt);            
        }*/
        
        ejecutor.shutdown();
        
        while (!ejecutor.isTerminated()) {
        }
        
        System.out.println("Finalizaron todos los hilos...");
        long millis_after = System.currentTimeMillis();
        
        long tiempo = (millis_after - millis_before ) / 1000;
        System.out.println(String.format("Tiempo de ejecucion %.2f segundos", (float)tiempo));
        
        //Ahora en cada cuadro buscamos si hay caras (para no usar llamadas innecesarias), para mandarlas a Kairos...        
        
        //camara2.cerrarCamara();
        return;
    }
}
