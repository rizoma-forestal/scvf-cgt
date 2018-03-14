
package ar.gob.ambiente.sacvefor.trazabilidad.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entidad que encapsula los datos de acceso a los Componentes de Gestión local (CGL)
 * @author rincostante
 */
@Entity
public class ComponenteLocal implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Variable privada: Identificador único
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Variable privada: Nombre de la Provincia a la cual pertenece el Componente local
     */
    @Column (nullable=false, length=50)
    @NotNull(message = "El campo nombreCompleto no puede ser nulo")
    @Size(message = "El campo nombreCompleto no puede tener más de 50 caracteres", min = 1, max = 50)   
    private String provincia;
    
    /**
     * Variable privada: Identificación de la Provincia en el Servicio de Gestión territorial
     */
    private Long idProvGt;    
    
    /**
     * Variable privada: Url de acceso al raíz del Componente local
     */
    @Column (nullable=false, length=100)
    @NotNull(message = "El campo nombreCompleto no puede ser nulo")
    @Size(message = "El campo nombreCompleto no puede tener más de 100 caracteres", min = 1, max = 100)     
    private String url;
    
    /**
     * Variable privada: Estado de habilitación
     */
    private boolean habilitado;

    public Long getIdProvGt() {
        return idProvGt;
    }

    public void setIdProvGt(Long idProvGt) {
        this.idProvGt = idProvGt;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
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
        if (!(object instanceof ComponenteLocal)) {
            return false;
        }
        ComponenteLocal other = (ComponenteLocal) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.trazabilidad.entities.ComponenteLocal[ id=" + id + " ]";
    }
    
}
