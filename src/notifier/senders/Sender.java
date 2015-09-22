package notifier.senders;

import java.util.ArrayList;

public interface Sender{


    /**
     * Devuelve una cadena en minúsculas indicando el método de envío.
     * Ej: "sms", "mail", "telegram".
     */
    public String metodo_envio();


    /**
     * Envía el mensaje indicado por el método correspondiente,
     * lanzando excepción en caso de error.
     */
    public void enviar_mensaje(Mensaje mensaje) throws Exception;


    /**
     * Devuelve cierto sí y sólo sí el método de envío tiene límite (Saldo, créditos).
     * En caso contrario, devuelve false.
     */
    public boolean tiene_limite();

    /**
     * Devuelve el crédito restante del método de envío.
     * Si el método no tiene límite o se produce un error, devuelve -1.
     */
    public double credito_restante();



    /***************************************************************************
     Estos métodos de abajo podemos dejarlos para más adelante, y que en la
     * implementación actual devuelvan cualquier cosa.
     ****************************************************************************/

    /**
     * Devuelve cierto sí y sólo sí puede conectarse al servicio de envío
     * (Prueba de conexión). En caso contrario, devuelve false.
     */
    public boolean test_conexion();



    /**
     * Devuelve la cuenta de mensajes enviados con el método correspondiente.
     * En caso de error, devuelve -1.
     */
    public int cuenta_enviados();

    /**
     * Devuelve una lista de los mensajes enviados con este método de envío.
     * Devuelve null si se produce un error.
     */
    public ArrayList<Mensaje> obtener_enviados();


}