/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notifier.senders.smsup;

/**
 *
 * @author ismaro3
 * Representa una referencia sobre un telefono.
 */
public class RefTelefono {
    
    private String telefono;
    private String id;

    public RefTelefono(String telefono, String id) {
        this.telefono = telefono;
        this.id = id;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    
}
