/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facedetection;

import java.io.InputStream;
import java.net.URL;
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
    private int probabilidad;
    private String urlImagen;
    private String persona;
    private String directorio;
    
    public ProcesamientoImagenes(Mat imagen, int indice, ServidorImagenes imgServer, DeteccionCaras faceServer, String galeria, String directorioTrabajo){
        this.imagen = imagen;
        this.indice = indice;
        this.imgServer = imgServer;
        this.faceServer = faceServer;
        this.urlImagen = null;
        this.persona = null;
        this.galeria = galeria;
        
        //Obtenemos el directorio de trabajo
        /*URL location = ProcesamientoImagenes.class.getProtectionDomain().getCodeSource().getLocation();
        this.directorio = location.getFile();
        this.directorio = this.directorio.substring(1);
        this.directorio = this.directorio.replace("dist/FaceDetection.jar", "");*/
        this.directorio = directorioTrabajo;
    }
    
    public ProcesamientoImagenes(String urlImagen, int indice, ServidorImagenes imgServer, DeteccionCaras faceServer, String galeria, String directorioTrabajo){
        this.imagen = null;
        this.indice = indice;
        this.imgServer = imgServer;
        this.faceServer = faceServer;
        this.urlImagen = urlImagen;
        this.persona = null;
        this.galeria = galeria;
        this.directorio = directorioTrabajo;        
    }

    @Override
    public void run() {
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
           
            for (Rect rect : faceDetections.toArray()) {
            Core.rectangle(imagen, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0));
            }

            String filename = "detected_faces/output" + this.indice + ".jpg";
            System.out.println(String.format("Escribiendo %s", filename));
            Highgui.imwrite(filename, imagen);
           
           
           //Si hay caras, mandamos a Kairos...
           //Subimos fotos a Dropbox
           
           //Highgui.imwrite("camera_images/camera" + indice + ".jpg", imagen);
           //this.imgServer.subirArchivo("camera_images/camera" + indice + ".jpg", "/camera" + indice + ".jpg");
           //String url = this.imgServer.obtenerURLDescarga("/camera" + indice + ".jpg");
           
           //String nombre = this.faceServer.reconocer(url, this.galeria, probabilidad);
           //System.out.println("Imangen camera" + this.indice + "enviada a Kairos");
            
       }
    }
    
}
