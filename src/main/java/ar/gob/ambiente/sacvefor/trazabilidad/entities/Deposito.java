
package ar.gob.ambiente.sacvefor.trazabilidad.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Entidad que encapsula los datos correspondientes a los contenedores de
 * Items derivados, a partir de la transformación de un Producto.
 * Al momento de generar una Guía, desde el Depósito se podrá acceder a los
 * Items derivados con los SubProductos disponibles para su comercialización
 * @author rincostante
 */
@Entity
public class Deposito implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Variable privada: Identificador único
     */  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Variable privada: Nombre completo del Titular de la Cuenta.
     * Cacheado de la Cuenta
     */
    private String nombreTitular;
    
    /**
     * Variable privada: Cuit del Titular de la Cuenta.
     * Cacheado de la Cuenta
     */
    private Long cuit;
    
    /**
     * Variable privada: Items derivados almacenados en el Deósito
     */
    @OneToMany (mappedBy="deposito")
    private List<Item> items;

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public Long getCuit() {
        return cuit;
    }

    public void setCuit(Long cuit) {
        this.cuit = cuit;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Deposito)) {
            return false;
        }
        Deposito other = (Deposito) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.trazabilidad.entities.Deposito[ id=" + id + " ]";
    }
    
}
