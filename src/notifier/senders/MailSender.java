package notifier.senders;

import java.util.ArrayList;

/**
 * Created by ismaro3 on 21/09/15.
 */
public class MailSender implements Sender {

    /** Las configuraciones tales como usuario, contraseña, etc,
     * se guardaran como parametros de la app para poder modificarlos
     * en la ventana de preferencias.
     * Antes de integrarlo en Aracés, pueden ir los parámetros fijos aquí.
     */

    @Override
    public String metodo_envio() {
        //todo: implementar
        return null;
    }

    @Override
    public void enviar_mensaje(Mensaje mensaje) throws Exception {
        //todo: implementar
    }

    @Override
    public boolean tiene_limite() {
        //todo: implementar
        return false;
    }

    @Override
    public double credito_restante() {
        //todo: implementar
        return 0;
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
}
