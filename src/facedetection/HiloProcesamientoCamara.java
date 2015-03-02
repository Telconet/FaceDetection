/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facedetection;

import java.util.concurrent.ExecutorService;
import org.opencv.core.Mat;

/**
 * Cada uno de estos objetos maneja una cámara.
 * @author Eduardo
 */
public class HiloProcesamientoCamara implements Runnable {
    
    private String urlCamara;
    private Camara camara;
    private String nombreGaleria;
    private String directorio;
    private double probabilidad;
    private ExecutorService ejecutor;
    private ServidorImagenes servidorImagenes;
    private DeteccionCaras clienteDeteccionCaras;
    private String ubicacion;
    private boolean cerrarCamara;
    protected String persona;
    protected boolean personaDetectada;
    protected Object mutex;
    private int idHiloCamara;
    
    
    public HiloProcesamientoCamara(String urlCamara, String directorio, String nombreGaleria, double probabilidad, ServidorImagenes servidorImagenes, DeteccionCaras clienteDeteccionCaras, 
                                    String ubicacion, ExecutorService ejecutor, int id){
        this.urlCamara = urlCamara; //rtsp://192.168.137.172/profile2/media.smp
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
    }
    
    @Override
    public void run(){
        
        int i = 0;
        this.camara = new Camara(this.urlCamara);
        
        //No se pudo abrir la camara...
        if(camara == null){
           //TODO 
            Bitacora log = new Bitacora();
            log.registarEnBitacora("errores_camara", "errores_camara.txt", "No se pudo abrir la camara con URL " + this.urlCamara + " ubicada en " + this.ubicacion, Bitacora.SEVERE);
        }
        
        //rtsp://192.168.137.172/profile2/media.smp
        camara.abrirCamara();      //empieza a enviar datos.
        while(true){
            Mat imagen = camara.obtenerCuadro();
            ProcesamientoImagenes faceDt = new ProcesamientoImagenes(imagen, i, servidorImagenes, clienteDeteccionCaras, nombreGaleria, directorio, probabilidad, this, idHiloCamara);
            //arregloImagenes.add(imagen);
            ejecutor.execute(faceDt); 
            i++;
            
            //TODO: si se detecto persona,
            synchronized(this.mutex){
                if(personaDetectada){
                    //TODO: realizar accion... Cerrar cámara??
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
