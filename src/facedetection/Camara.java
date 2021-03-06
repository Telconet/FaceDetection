/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facedetection;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

/**
 *
 * @author Eduardo
 */
public class Camara {
    
    private String urlCamara;
    private int dispositivo;
    private VideoCapture camara;
    private boolean camaraAbierta;
    
    public Camara(String url){
        this.urlCamara = url;
        this.camara = null;
        this.dispositivo = -1;
    }
    
    public Camara(int camara){
        this.urlCamara = null;
        this.camara = null;
        this.dispositivo = camara;
        this.camaraAbierta = false;
    }
    
    public void abrirCamara(){
        
        System.out.println(System.getProperty("java.library.path"));
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        

        this.camara = new VideoCapture();
        if(urlCamara == null){
          
            camara.open(this.dispositivo); //? camar
            //camara.ope
        }
        else{
            
            this.camara.open(this.urlCamara);
        }
        
        if(!camara.isOpened()){
            System.out.println("Error al abrir la camara.");
            this.camaraAbierta = false;
            
        }
        else{
            System.out.println("Camara OK.");
            this.camaraAbierta = true;
        }
        
    }
    
    public void cerrarCamara(){
        if(this.camara != null){
            this.camara.release();
                   
        }
    }
    
    /**
     * Obtiene un cuadro del stream...
     */
    public boolean camaraAbierta(){
        return this.camaraAbierta;
    }
    
    public Mat obtenerCuadro(){
        if(this.camaraAbierta){
            Mat cuadro = new Mat();
            this.camara.read(cuadro); 
            return cuadro;
        }
        return null;
    }
    
}
