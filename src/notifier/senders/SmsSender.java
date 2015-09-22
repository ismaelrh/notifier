package notifier.senders;

import notifier.senders.smsup.RefTelefono;
import notifier.senders.smsup.SmsUp;
import java.util.ArrayList;

/**
 * Created by ismaro3 on 21/09/15.
 */
public class SmsSender implements Sender {

    /** Las configuraciones tales como usuario, contraseña, etc,
     * se guardaran como parametros de la app para poder modificarlos
     * en la ventana de preferencias.
     * Antes de integrarlo en Aracés, pueden ir los parámetros fijos aquí.
     */
  
    private SmsUp handler;
    private String clientID = "2eb6a680c8f326c5fc9f6fc53150d2d5";
    private String clientPass ="4ce3c4894ef1fe81e167c140ffa40dc1e010a46c";
    private String username = "ARACES";

    public SmsSender(){
        handler = new SmsUp(clientID,clientPass,username);
    }
    
    @Override
    public String metodo_envio() {
        return "sms";
    }

    @Override
    //Envia el mensaje. Lanza una excepcíon con un mensaje si el servidor
    //ha denegado el envío, es decir, el número no está en la respuesta.
    public void enviar_mensaje(Mensaje mensaje) throws Exception {
        ArrayList<String> dest = new ArrayList<>();
        dest.add(mensaje.getDestinatario());
        ArrayList<RefTelefono> respuesta = handler.nuevoSms(mensaje.getContenido(), dest);
        boolean contains = false;
        //Se comprueba si esta en la respuesta, eso es que se ha enviado
        for(RefTelefono ref: respuesta){
            if(ref.getTelefono().equalsIgnoreCase(mensaje.getDestinatario())){
                contains = true;
            }
        }
        if(!contains){
            throw new Exception("El servidor ha denegado el envío del SMS. Posiblemente el destinatario sea incorrecto.");
        }
    }

    @Override
    public boolean tiene_limite() {
        return true;
    }

    @Override
    public double credito_restante() {
        try{
            return  (double) handler.creditosDisponibles();
        }
        catch(Exception ex){
            return -1;
        }
        
    }

    @Override
    public boolean test_conexion() {
        //Dummy, no se usa de momento
        return true;
    }

    @Override
    public int cuenta_enviados() {
        //Dummy, no se usa de momento
        return 0;
    }

    @Override
    public ArrayList<Mensaje> obtener_enviados() {
        //Dummy, no se usa de momento
        return null;
    }
    
    
    public static void main(String[] args){
        //Pruebas
        SmsSender sender = new SmsSender();
        System.out.println("Restantes: " + sender.credito_restante());
        
        Mensaje mensaje = new Mensaje("669393193","Ya disponible Mesa Inoxidable 4 patas");
        Mensaje mensaje2 = new Mensaje("677017206","Ya disponible Mesa Inoxidable 4 patas");
        
        try{
             System.out.println("Enviando mensajes...");
             sender.enviar_mensaje(mensaje);
             sender.enviar_mensaje(mensaje2);
             System.out.println("Hecho");
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        
       
       
    }
}

