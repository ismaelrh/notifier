package notifier.senders;

/**
 * Representa un mensaje. El asunto es opcional.
 */
public class Mensaje {

    private String destinatario;
    private String asunto;
    private String contenido;

    public Mensaje(String destinatario, String asunto, String contenido) {
        this.destinatario = destinatario;
        this.asunto = asunto;
        this.contenido = contenido;
    }

    public Mensaje(String destinatario, String contenido) {
        this.destinatario = destinatario;
        this.contenido = contenido;
        this.asunto = "";
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
