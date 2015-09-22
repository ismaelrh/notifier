package notifier.senders;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by ismaro3 on 21/09/15.
 */
public class MailSender implements Sender {

    /** Las configuraciones tales como usuario, contraseña, etc,
     * se guardaran como parametros de la app para poder modificarlos
     * en la ventana de preferencias.
     * Antes de integrarlo en Aracés, pueden ir los parámetros fijos aquí.
     */

    private static final Logger logger = Logger.getLogger
        (MailSender.class.getName());
    private final String USERNAME = "ssm.fuentes@gmail.com";
    private final String PASSWORD = "";
    private final Properties props = new Properties() {{
        put("mail.smtp.auth", "true");
        put("mail.smtp.starttls.enable", "true");
        put("mail.smtp.host", "smtp.gmail.com");
        put("mail.smtp.port", "587");
    }};    

    @Override
    public String metodo_envio() {
        return "mail";
    }

    @Override
    public void enviar_mensaje(Mensaje mensaje) throws Exception {
        Session session;
        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });
        
        try {
            
             // Crea el mensaje.
            Message message = new MimeMessage(session);

            // Quien lo envia
            message.setFrom(new InternetAddress(USERNAME));

            // Destinatario.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(mensaje.getDestinatario()));

            // Titulo
            message.setSubject(mensaje.getAsunto());

            // Crea el cuerpo del mensaje
            BodyPart messageBodyPart = new MimeBodyPart();

            // add el texto al cuerpo del mensaje
            messageBodyPart.setText(mensaje.getContenido());

            // crea un mensaje multiparte
            Multipart multipart = new MimeMultipart();

            // Le añade la parte de texto al mensaje
            multipart.addBodyPart(messageBodyPart);
            
                       // add al mensaje las partes
            message.setContent(multipart);

            // Envia el mensaje
            Transport.send(message);     
                      
            System.out.printf("enviado a " + mensaje.getDestinatario());
            logger.log(Level.INFO, "Notification sent succesfully to " + mensaje.getDestinatario());
        } catch (Exception e) {
            System.out.printf("fallo enviado a " + mensaje.getDestinatario());
            logger.log(Level.SEVERE, e.toString(), e);
            
        }
        
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
