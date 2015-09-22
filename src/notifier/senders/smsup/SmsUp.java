package notifier.senders.smsup;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import javax.net.ssl.HttpsURLConnection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author ismaro3. Clase encargada del envío de SMSs y consulta de creditos
 * usando la API de SmsUp.es
 * 
 * INFO API: https://www.smsup.es/api/
 * Basado en API PHP: https://github.com/smsup/smsuplib_php
 */
public class SmsUp {

    private final static String HOST = "https://www.smsup.es";
    private final static String URL_SMS = "/api/sms/";
    private final static String URL_CREDITOS = "/api/creditos/";
    private final static String URL_ENVIO = "/api/peticion/";

    private String idUsuario;
    private String clave;
    private String username; //Username: 14 numeros u 11 caracteres alfanumericos, sin tilde ni 'ñ'.

    public SmsUp(String idUsuario, String clave, String username) {
        this.idUsuario = idUsuario;
        this.clave = clave;
        this.username = username;
    }

    /**
     * Devuelve los créditos disponibles en la cuenta
     *
     * @return Créditos disponibles en la cuenta
     * @throws Exception en caso de error.
     */
    public int creditosDisponibles() throws Exception {

        JSONParser parser = new JSONParser();
        ArrayList<HttpHeader> cabeceras = this.generarCabeceras("GET", SmsUp.URL_CREDITOS, null);
        //Devolver respuesta
        String json = executeGet(SmsUp.HOST + SmsUp.URL_CREDITOS, cabeceras);
        JSONObject obj = (JSONObject) parser.parse(json);

        return Integer.parseInt(obj.get("creditos").toString());

    }

    /**
     * Envía un SMS con el texto indicado, a los numeros indicados, con el
     * destinatario indicado, que puede ser un nombre o un número. El remitente
     * es el parametro "username" de la clase.
     *
     * @param texto Es el texto a enviar, maximo 160 caracteres
     * @param numeros Array de numeros como String. Si es España, no hace falta
     * prefijo de pais.
     * @throws Exception en caso de error. Ver mensaje para más info.
     * @returns array de objetos RefTelefono, indicando, para cada telefono
     * valida, el numero de ref de la peticioń. Si un numero no está, no se
     * enviará.
     */
    public ArrayList<RefTelefono> nuevoSms(String texto, ArrayList<String> numeros) throws Exception {
        JSONParser parser = new JSONParser();
        String fecha = "NOW"; //Fecha: Enviar ahora

        //Se construye objeto de post
        JSONObject obj = new JSONObject();
        obj.put("texto", texto);
        obj.put("fecha", fecha);
        obj.put("telefonos", numeros);
        obj.put("remitente", this.username);
        String post = obj.toString();
        ArrayList<HttpHeader> cabeceras = generarCabeceras("POST", SmsUp.URL_SMS, post);
        String response = executePost(SmsUp.HOST + SmsUp.URL_SMS, cabeceras, post);

        //Procesar resultado
        Object obj_response = parser.parse(response);
        JSONArray array = (JSONArray) obj_response;
        int size = array.size();
        ArrayList<RefTelefono> resultado = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            JSONObject o = (JSONObject) array.get(i);
            resultado.add(new RefTelefono(o.get("telefono").toString(), o.get("id").toString()));

        }
        return resultado;

    }

    /**
     * Devuelve una lista de cabeceras adecuadas para preguntar a la api.
     *
     * @param verbo cadena "POST" o "GET"
     * @param url La URL completa de la petición
     * @param post Los datos a enviar. Poner a null o cadena vacía en GET.
     * @return Lista de cabeceras (SMS-DATE y Firma)
     * @throws Exception en caso de excepción.
     */
    protected ArrayList<HttpHeader> generarCabeceras(String verbo, String url, String post)
            throws Exception {
        ArrayList<HttpHeader> cabeceras = new ArrayList<>();
        if (post == null) {
            post = "";
        }

        String smsdate = getDate(); //Cabecera Sms-date
        String text = verbo + url + smsdate + post;
        String firma_hash = HmacSha1Signature.calculateRFC2104HMAC(text, this.clave);
        String firma = this.idUsuario + ":" + firma_hash; //Cabecera firma

        cabeceras.add(new HttpHeader("SMS-DATE", smsdate));
        cabeceras.add(new HttpHeader("Firma", firma));

        return cabeceras;

    }

    /**
     * Devuelve hora actual en formato ISO-8601 pedido por la API.
     *
     */
    protected String getDate() {
        TimeZone zone = TimeZone.getTimeZone("GMT+2");

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        format.setTimeZone(zone);
        return format.format(new Date());

    }

    /**
     * Ejecuta un Get y devuelve el resultado.
     *
     * @param https_url La url HTTPS a preguntar.
     * @param cabeceras La lista de cabeceras (pares clave-valor)
     * @return Cadena con el resultado de la petición.
     * @throws Exception si hay algún error. Mirar el mensaje para más info.
     */
    private String executeGet(String https_url, ArrayList<HttpHeader> cabeceras)
            throws Exception {

        URL url;
        String result = "";
        HttpsURLConnection con;

        url = new URL(https_url);
        con = (HttpsURLConnection) url.openConnection();

        //Se añaden cabeceras
        for (HttpHeader cabecera : cabeceras) {
            con.setRequestProperty(cabecera.getName(), cabecera.getValue());
            System.out.println(cabecera.getName() + " -> " + cabecera.getValue());
        }

        if (con.getResponseCode() == 200) {
                //Codigo OK, leemos respuesta

            BufferedReader br
                    = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));

            String input;

            while ((input = br.readLine()) != null) {
                result += input;
            }
            br.close();
        } else {
            //Error, se lanza excepción con mensaje.
            throw new Exception("Codigo HTTP " + con.getResponseCode() + ": "
                    + con.getResponseMessage());
        }

        return result;

    }

    /**
     * Ejecuta un Post con datos de 'post' y devuelve el resultado.
     *
     * @param https_url La url HTTPS a preguntar.
     * @param cabeceras La lista de cabeceras (pares clave-valor)
     * @param post La cadena a enviar como cntenido
     * @return Cadena con el resultado de la petición.
     * @throws Exception si hay algún error. Mirar el mensaje para más info.
     */
    private String executePost(String https_url, ArrayList<HttpHeader> cabeceras, String post) throws Exception {
        URL url;
        String result = "";

        HttpsURLConnection con;
        url = new URL(https_url);

        con = (HttpsURLConnection) url.openConnection();

        //Cabeceras
        for (HttpHeader cabecera : cabeceras) {
            con.setRequestProperty(cabecera.getName(), cabecera.getValue());
        }

        con.setDoOutput(true);

        //Enviamos datos
        DataOutputStream output = new DataOutputStream(con.getOutputStream());
        output.writeBytes(post);
        output.close();

        //Recibimos respuesta
        if (con.getResponseCode() == 200) {
            //Codigo 200 OK
            BufferedReader br
                    = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));

            String input;

            while ((input = br.readLine()) != null) {
                result += input;
            }
            br.close();
        } else {
            //Codigo erroneo, lanzamos excepcion con mensaje
            throw new Exception("Codigo HTTP " + con.getResponseCode() + ": "
                    + con.getResponseMessage());
        }

        return result;

    }


}
