/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facedetection;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import org.opencv.core.Mat;
import org.opencv.objdetect.CascadeClassifier;

/**
 * Cada uno de estos objetos maneja una cámara.
 * @author Eduardo
 */
public class HiloProcesamientoCamara implements Runnable {
    
    private String ipCamara;
    private Camara camara;
    private String nombreGaleria;
    private String directorio;
    private double probabilidad;
    private ThreadPoolExecutor ejecutor;
    private ServidorImagenes servidorImagenes;
    private DeteccionCaras clienteDeteccionCaras;
    private String ubicacion;
    private boolean cerrarCamara;
    protected String persona;
    protected boolean personaDetectada;
    protected final Object mutex;
    private int idHiloCamara;
    private String directorioCamara;
    
    
    public HiloProcesamientoCamara(String ipCamara, String directorio, String nombreGaleria, double probabilidad, ServidorImagenes servidorImagenes, DeteccionCaras clienteDeteccionCaras, 
                                    String ubicacion, ThreadPoolExecutor ejecutor, int id){
        this.ipCamara = ipCamara; //rtsp://192.168.137.172/profile2/media.smp
        this.camara = null;
        this.nombreGaleria = nombreGaleria;
        this.directorio = directorio;
        this.probabilidad = probabilidad;
        this.servidorImagenes = servidorImagenes;
        this.clienteDeteccionCaras = clienteDeteccionCaras;
        this.ubicacion = ubicacion;
        this.ejecutor = ejecutor;
        this.cerrarCamara = false;
        this.mutex = new Object();
        this.idHiloCamara = id;
        this.directorioCamara = directorio.trim() + "\\camara_" + ipCamara;
        this.personaDetectada = false;
        
         //Verificamos que exista el directorio
        File f = new File(directorioCamara);
        if(!f.exists() || !f.isDirectory()){
            //Intentamos crear el directorio
            try{
                f.mkdir();
            }
            catch(Exception e){
                System.out.println("¡Ups! No se pudo crear el directorio para la camara " + ipCamara);
                System.exit(-1);
            }
        }
    }
    
    @Override
    public void run(){
        
        int i = 0;
        
        //Para camaras Samsung SNV-7080R
        this.camara = new Camara("rtsp://" + this.ipCamara + "/profile2/media.smp");
               
        //No se pudo abrir la camara...
        if(camara == null){
           //TODO 
            Bitacora log = new Bitacora();
            log.registarEnBitacora("errores_camara", "errores_camara.txt", "No se pudo abrir la camara con URL " + this.ipCamara + " ubicada en " + this.ubicacion, Bitacora.SEVERE);
        }
        
        //rtsp://192.168.137.172/profile2/media.smp
        camara.abrirCamara();      //empezara recibir datos.
        /*while(true){
            Mat imagen = camara.obtenerCuadro();
            imagen.release();
        }*/
        
        //DEBEMOS LLAMAR imagen.release() para evitar LEAK. Sin embargo, necesitamos la imagen hasta que la procesemos,
        //y ya que el procesamiento toma mas tiempo, empezamos a consumir memoria. 
        //Por lo tanto, debemos mandar un cuadro imagen solo si ha un hilo disponible en el ejecutor.
        int cores = Runtime.getRuntime().availableProcessors();
        while(true){
            int activos = ejecutor.getActiveCount();
            
            //Solo si hay CPU disponible leemos la imagen, caso constrario debemos mantenerla en memoria
            //y si el procesamiento es mas lento que la tasa de lectura de imagenes, eventualmente nos
            //quedaremos sin memoria.
            if( activos < cores){
                System.out.println(String.format("Activos: %s, i: %s", activos, i));
                Mat imagen = camara.obtenerCuadro(); 
                ProcesamientoImagenes faceDt = new ProcesamientoImagenes(imagen, i, servidorImagenes, clienteDeteccionCaras, nombreGaleria, directorio, directorioCamara,
                                                                        probabilidad, this, ipCamara);
                ejecutor.execute(faceDt);
                i++;
            } 
            
            //TODO: si se detecto persona,
            synchronized(this.mutex){
                if(personaDetectada){
                    //TODO: realizar accion... Cerrar cámara??
                    Bitacora log_deteccion = new Bitacora(); 
                    log_deteccion.registarEnBitacora("Log_camara_" + ipCamara,  directorio + "Log_camara_" + ipCamara + ".txt", "Se detecto a " + persona + " en el/la " + ubicacion, Bitacora.INFO);
                    this.personaDetectada = false;
                    this.persona = "";
                }
            }
            
            if(cerrarCamara){
                break;
            }
        }
        this.camara.cerrarCamara();
    }
    
}
