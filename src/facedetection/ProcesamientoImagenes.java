/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facedetection;

import java.io.InputStream;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

/**
 *
 * @author Eduardo
 */
public class ProcesamientoImagenes implements Runnable{
    
    private Mat imagen;
    private int indice;
    private ServidorImagenes imgServer;
    private DeteccionCaras faceServer;
    private String galeria;
    private double probabilidad;
    private String urlImagen;
    private String persona;
    private String directorio;
   
    
    public ProcesamientoImagenes(Mat imagen, int indice, ServidorImagenes imgServer, DeteccionCaras faceServer, String galeria, String directorioTrabajo, double probabilidad){
        this.imagen = imagen;
        this.indice = indice;
        this.imgServer = imgServer;
        this.faceServer = faceServer;
        this.urlImagen = null;
        this.persona = null;
        this.galeria = galeria;
        this.probabilidad = probabilidad;
        
        //Obtenemos el directorio de trabajo
        /*URL location = ProcesamientoImagenes.class.getProtectionDomain().getCodeSource().getLocation();
        this.directorio = location.getFile();
        this.directorio = this.directorio.substring(1);
        this.directorio = this.directorio.replace("dist/FaceDetection.jar", "");*/
        this.directorio = directorioTrabajo;
    }
    
    public ProcesamientoImagenes(String urlImagen, int indice, ServidorImagenes imgServer, DeteccionCaras faceServer, String galeria, String directorioTrabajo, double probabilidad){
        this.imagen = null;
        this.indice = indice;
        this.imgServer = imgServer;
        this.faceServer = faceServer;
        this.urlImagen = urlImagen;
        this.persona = null;
        this.galeria = galeria;
        this.directorio = directorioTrabajo;  
        this.probabilidad = probabilidad;
    }

    @Override
    public void run() {
        
        //Si el hilo principal nos ha dicho que terminemos, salimos.
        /*if(Thread.interrupted()){
            return;
        }*/
        
       //Tomamos la imagen, y la procesamos con OpenCV para encontrar caras...
       System.out.println("Detectando caras para imagen " + this.indice);   
        
       CascadeClassifier faceDetector;        

       String recurso = null;
       recurso = directorio + "/recursos/haarcascade_frontalface_alt.xml";
       //System.out.println(recurso);

       faceDetector = new CascadeClassifier(recurso);
       
       if(faceDetector.empty()){
           System.out.println("Clasificador de caras no cargado... No se puede procesar la imagen.");
           return;
       }
       
       MatOfRect faceDetections = new MatOfRect();
       
       //Abrimos la imagen
       if(this.imagen == null){
           //usampos URL
           this.imagen = Highgui.imread(this.urlImagen);
       }

       assert this.imagen != null;

       //Detectamos la cara 
       faceDetector.detectMultiScale(imagen, faceDetections);
       System.out.println(String.format("OpenCV detecto %s caras en la imagen camera%d.jpg", faceDetections.toArray().length, this.indice)); 

       //Detectar...
       if(faceDetections.toArray().length > 0){
            //En esta seccion escribimos la imagen a disco con un cuadro señalando la cara..
            Highgui.imwrite(this.directorio + "/camera_images/camera" + indice + ".jpg", imagen);
           
            for (Rect rect : faceDetections.toArray()) {
            Core.rectangle(imagen, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0));
            }

           String filename = this.directorio + "/detected_faces/output" + this.indice + ".jpg";
            
           Highgui.imwrite(filename, imagen);
           System.out.println(String.format("Escribiendo %s", filename));
           //Fin del dibujo del cuadro de deteccion.
          
           //Si hay caras, mandamos a Kairos...
           //Subimos fotos a Dropbox (dentro o fuera de mutex)
           this.imgServer.subirArchivo(this.directorio + "/camera_images/camera" + indice + ".jpg", "/camera" + indice + ".jpg");
           String url = this.imgServer.obtenerURLDescarga("/camera" + indice + ".jpg");
           
           synchronized(FaceDetection.mutex){
                System.out.println("Hilo " + indice + " adquirio el mutex");               
                
                if(!FaceDetection.personaEncontrada){
                     
                    
                    //Mandamos fotos a Kairos
                     System.out.println("KAIROS: Imagen " + this.indice + " enviada a Kairos...");
                     String nombre = this.faceServer.reconocer(url, this.galeria, this.probabilidad);
                     
                     if(!nombre.equals("nadie")){

                         //synchronized(FaceDetection.mutex){
                            FaceDetection.personaEncontrada = true;
                         //}

                         System.out.println("Hola " + nombre + ". (Imagen " + this.indice+ ")");
                         System.exit(0);
                         //
                     }
                     else{
                         //System.out.println("No se reconocio a nadie en la imagen " + indice);
                     }
                }
           }
           System.out.println("Hilo " + indice + " libero el mutex");
       }
       else{
           
           System.out.println("No se detecto cara en la imagen " + indice);
       }
    }
    
}
