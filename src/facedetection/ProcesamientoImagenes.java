/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facedetection;

import org.opencv.core.Core;
import org.opencv.core.Mat;
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
    private String directorio;
    private String directorioImagenes;
    private HiloProcesamientoCamara nuestroHilo;
    private String id;
   
    
    public ProcesamientoImagenes(Mat imagen, int indice, ServidorImagenes imgServer, DeteccionCaras faceServer, String galeria, String directorioTrabajo, 
                                    String directorioImagenes, double probabilidad, HiloProcesamientoCamara nuestroHilo, String id){
        this.imagen = imagen;
        this.indice = indice;
        this.imgServer = imgServer;
        this.faceServer = faceServer;
        this.urlImagen = null;
        this.galeria = galeria;
        this.probabilidad = probabilidad;
        this.nuestroHilo = nuestroHilo;
        this.id = id;
        
        //Obtenemos el directorio de trabajo
        /*URL location = ProcesamientoImagenes.class.getProtectionDomain().getCodeSource().getLocation();
        this.directorio = location.getFile();
        this.directorio = this.directorio.substring(1);
        this.directorio = this.directorio.replace("dist/FaceDetection.jar", "");*/
        this.directorio = directorioTrabajo;
        this.directorioImagenes = directorioImagenes;
    }
    
    /*public ProcesamientoImagenes(String urlImagen, int indice, ServidorImagenes imgServer, DeteccionCaras faceServer, String galeria, String directorioTrabajo, double probabilidad){
        this.imagen = null;
        this.indice = indice;
        this.imgServer = imgServer;
        this.faceServer = faceServer;
        this.urlImagen = urlImagen;
        this.persona = null;
        this.galeria = galeria;
        this.directorio = directorioTrabajo;  
        this.probabilidad = probabilidad;
    }*/

    @Override
    public void run() {
        
        try{
            //Usamos try para poder usar finally, y llamar imagen.release() para evitar memory leak
            //Si el hilo principal nos ha dicho que terminemos, salimos.
            if(Thread.interrupted()){
                imagen = null;
                return;
            }

            synchronized(nuestroHilo.mutex){
                if(nuestroHilo.personaDetectada){
                        //Si ya se encontro una cara, no procesamos.
                        //System.out.println("Persona ya fue reconocida, saliendo del hilo...");
                        imagen = null;
                        return;
                }
            }

           //Tomamos la imagen, y la procesamos con OpenCV para encontrar caras...
           System.out.println("Detectando caras para imagen " + this.indice);   

           CascadeClassifier faceDetector;        

           String recurso = null;
           recurso = directorio + "/recursos/haarcascade_frontalface_alt.xml"; //"/recursos/haarcascade_frontalface_alt.xml";

           faceDetector = new CascadeClassifier(recurso);

           if(faceDetector.empty()){
               System.out.println("Clasificador de caras no cargado... No se puede procesar la imagen.");
               return;
           }

           MatOfRect faceDetections = new MatOfRect();



           assert this.imagen != null;

           //Detectamos la cara 
           faceDetector.detectMultiScale(imagen, faceDetections);
           System.out.println(String.format("OpenCV detecto %s caras en la imagen camera%d.jpg", faceDetections.toArray().length, this.indice)); 

           //Detectar...
           if(faceDetections.toArray().length > 0){
                //Escribir imagen para Dbx
                Highgui.imwrite(this.directorioImagenes + "/camara" + indice + ".jpg", imagen);

                //En esta seccion escribimos la imagen a disco con un cuadro señalando la cara..
                /*for (Rect rect : faceDetections.toArray()) {
                Core.rectangle(imagen, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                        new Scalar(0, 255, 0));
                }


               String filename = this.directorioImagenes + "/detected_faces/output" + this.indice + ".jpg";

               Highgui.imwrite(filename, imagen);*/
               //System.out.println(String.format("Escribiendo %s", filename));
               //Fin del dibujo del cuadro de deteccion.

               //Si hay caras, mandamos a Kairos...
               this.imgServer.subirArchivo(this.directorioImagenes + "/camara" + indice + ".jpg", "/" + id +"/camara" + indice + ".jpg");
               String url = this.imgServer.obtenerURLDescarga("/" + id + "/camara" + indice + ".jpg");

               synchronized(nuestroHilo.mutex){        
                    if(nuestroHilo.personaDetectada){
                         imagen = null;
                        System.out.println("Persona ya fue reconocida, saliendo del hilo...");
                        return;
                    }
               }

                //Mandamos fotos a Kairos
                System.out.println("KAIROS: Imagen " + this.indice + " enviada a Kairos...");
                String nombre = this.faceServer.reconocer(url, this.galeria, this.probabilidad);
                //String nombre = "nadie";

                //synchronized(FaceDetection.mutex){
                synchronized(nuestroHilo.mutex){

                    //if(FaceDetection.personaEncontrada){
                    if(nuestroHilo.personaDetectada){
                            //Si ya se encontro una cara, no procesamos.
                            System.out.println("Persona ya fue reconocida, saliendo del hilo...");
                            return;
                    }

                   if(!nombre.equals("nadie")){
                           nuestroHilo.personaDetectada = true;
                           nuestroHilo.persona = nombre;
                           imagen = null;
                           System.out.println("Hola " + nombre + ". (Imagen " + this.indice+ ")");
                           return;                
                    }
                    else{
                        System.out.println("No se reconocio a nadie en la imagen " + indice);
                    }
               }
           }
           else{
               System.out.println("No se detecto cara en la imagen " + indice);
           }
        }
        catch(Exception e){
            
        }
        finally{
            this.imagen.release();
            //System.out.println("Imagen " + indice + " liberada...");
        }
    }
    
}
