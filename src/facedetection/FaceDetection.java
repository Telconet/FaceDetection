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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.opencv.core.*;



public class FaceDetection{
    
    //public static boolean personaEncontrada = false;          //solo para pruebas
    public static final Object mutex = new Object();
   

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, DbxException, InterruptedException, IllegalAccessException {
        
        int cores = Runtime.getRuntime().availableProcessors();
        
        //Primero verficamos que exista el archivo de configuracion
        if(args.length == 0){
            System.out.println("No encontramos la ruta del archivo de configuración. ¿Olvido agregarla como argumento?");
            System.exit(-1);
        }
        else{
            File f = new File(args[0]);
            if(!f.exists() || f.isDirectory()){
                System.out.println("¡Ups! El archivo que especificaste no existe.");
                System.exit(-1);
            }
        }
        
        //Obtenemos el archivo de configuracion...
        Configuracion config = new Configuracion(args[0]);
        
        //Cargamos la librerias nativas
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                
        //NO usar underscore en nombre de galeria
        String nombreGaleria = config.obtenerParametro(Configuracion.NOMBRE_GALERIA); //"TNtest";       
        
        String appId = config.obtenerParametro(Configuracion.KAIROS_APP_ID); //"752d01e5";
        String appKey = config.obtenerParametro(Configuracion.KAIROS_APP_KEY); //"f05dd012cb70fc6256357a9aeb17af2d"; 
        
        DeteccionCaras clienteDeteccionCaras = new DeteccionCaras(appId, appKey);
                
        //Directorio de trabajo
        String directorioTrabajo = null;
        directorioTrabajo = config.obtenerParametro(Configuracion.DIRECTORIO_TRABAJO);
        
        File f = new File(directorioTrabajo);
        if(!f.exists() || !f.isDirectory()){
            System.out.println("¡Ups! El diretorio de trabajo (" + directorioTrabajo +") no existe o no es una carpeta.");
            System.exit(-1);
        }
        
        /*URL ubicacion = ProcesamientoImagenes.class.getProtectionDomain().getCodeSource().getLocation();
        directorio = ubicacion.getFile();
        directorio = directorio.substring(1);
        
        if(directorio.contains("build/classes")){
            directorio = directorio.replace("/build/classes", "");
        }
        else{
            directorio = directorio.replace("/dist/FaceDetection.jar", "");
        }*/
        
        System.out.println(directorioTrabajo);
        
        //Servidor Dropbox
        System.out.println(config.obtenerParametro(Configuracion.ARCHIVO_TOKEN_DBX).trim());
        String dbxKey = config.obtenerParametro(Configuracion.KEY_DBX); //"cuiclds3bj72a53";
        String dbxSecret = config.obtenerParametro(Configuracion.SECRETO_DBX); //"m44th47fwrn9d0t";
        ServidorImagenes servidorImagenes = new ServidorImagenes(dbxKey, dbxSecret);
        String rutaArchivoToken = config.obtenerParametro(Configuracion.ARCHIVO_TOKEN_DBX).trim(); 
        
        f = new File(rutaArchivoToken);
        if(!f.exists() || f.isDirectory()){
            System.out.println("¡Ups! El archivo del token de Dropbox (" + rutaArchivoToken +") no existe.");
            System.exit(-1);
        }
        
        boolean conectadoADbx = servidorImagenes.conectarADropbox(rutaArchivoToken, config.obtenerParametro(Configuracion.STRING_APP_DBX)); //"/token.txt", "tnFace/1.0"
        
        if(!conectadoADbx){
            System.out.println("¡Ups! No nos pudimos conectar a Dropbox.");
            System.exit(-1);
        }
        
        //Hasta aqui OK...
                
        //probar los hilos
        /*long millis_before = System.currentTimeMillis();
        ExecutorService ejecutor = Executors.newFixedThreadPool(5);     //4 threads al mismo tiempo...
      
        //Cogemos 200 cuadros
        ArrayList<Mat> arregloImagenes = new ArrayList<>(50);
        Camara camara2 = new Camara("rtsp://172.16.5.12/profile2/media.smp");
        //rtsp://192.168.137.172/profile2/media.smp
         camara2.abrirCamara();      //empieza a enviar datos.
        for(int i = 0; i < 50; i++){
            Mat imagen = camara2.obtenerCuadro();
            ProcesamientoImagenes faceDt = new ProcesamientoImagenes(imagen, i, servidorImagenes, clienteDeteccionCaras, nombreGaleria, directorioTrabajo, 0.6);
            //arregloImagenes.add(imagen);
            ejecutor.execute(faceDt); 
        }
        
        while(true){
            synchronized(mutex){
                if(personaEncontrada){
                    ejecutor.shutdownNow();
                    break;
                }
            }
        }
        
        camara2.cerrarCamara();
        
        
      
        System.out.println("Finalizaron todos los hilos...");
        long millis_after = System.currentTimeMillis();
        
        long tiempo = (millis_after - millis_before ) / 1000;
        System.out.println(String.format("Tiempo de ejecucion %.2f segundos", (float)tiempo));*/
        
        //Ahora en cada cuadro buscamos si hay caras (para no usar llamadas innecesarias), para mandarlas a Kairos...        
        
        //camara2.cerrarCamara();
        
        //Multicamara...
        //Creamos las camaras...
        String[] ipCamaras = config.obtenerParametro(Configuracion.IP_CAMARAS).split(";");
        String[] ubicacionCamaras = config.obtenerParametro(Configuracion.UBICACION_CAMARAS).split(";");
                
        if(ipCamaras.length != ubicacionCamaras.length){
               System.out.println("El numero de IPs y ubicaciones de camaras no son igual. Por favor verifique el archivo de configuracion " + config.obtenerParametro(args[0]));
        }
        
        //Obtenemos la probabilidad de reconocimiento deseada
        double probabilidad = -1.0;
        
        try{
            probabilidad = Double.parseDouble(config.obtenerParametro(Configuracion.PROBABILIDAD_RECON));
        }
        catch(NumberFormatException e){
            System.out.println("Probabilidad incorrecta/no encontrada.");
            System.exit(-1);
        }
        
        //Pool de hilos...
        BlockingQueue<Runnable> colaDeEspera = new LinkedBlockingQueue<>();
        ThreadPoolExecutor ejecutor = new ThreadPoolExecutor(cores, cores, cores, TimeUnit.DAYS, colaDeEspera);
        
        //Hilos de reconocimiento.
        ArrayList<Thread> hilosCamaras = new ArrayList<>();
        
        for(int i = 0; i < ipCamaras.length; i++){
            
            
            HiloProcesamientoCamara hiloCamara = new HiloProcesamientoCamara(ipCamaras[i], directorioTrabajo.trim(),
                    nombreGaleria, probabilidad, servidorImagenes, clienteDeteccionCaras, ubicacionCamaras[i], ejecutor, i);
            
            //Creamos hilo e iniciamos reconocimiento
            Thread hilo = new Thread(hiloCamara);
            hilosCamaras.add(hilo);
            hilo.start();    
            
        }  
        
        for(int i = 0; i < hilosCamaras.size(); i++){
            Thread tmp = hilosCamaras.get(i);
            tmp.join();
            
        }
        
        //ejecutor.awaitTermination(cores, TimeUnit.);
        
        //Usar hilo.join() o ejecutor.shutdown(); NO USAR shutdown ya que cualquier tarea enviada despues del shutdown sera rechazada
        
    }
}
