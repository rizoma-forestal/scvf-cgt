
package ar.gob.ambiente.sacvefor.trazabilidad.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Entidad que será usada como:
 * TipoAutorizacion
 * TipoIntervencion
 * UsuSuelo
 * TipoItem
 * Rol persona
 * Tipo entidad guía
 * @author rincostante
 */
@Entity
@XmlRootElement
public class Parametrica implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="tipo_id", nullable=false)
    @NotNull(message = "Debe existir un Tipo de parámetro")
    private TipoParam tipo;
    
    @Column (nullable=false, length=50)
    @NotNull(message = "El campo nombre no puede ser nulo")
    @Size(message = "El campo nombre no puede tener más de 50 caracteres", min = 1, max = 50)    
    private String nombre;
    
    private boolean habilitado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoParam getTipo() {
        return tipo;
    }

    public void setTipo(TipoParam tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @XmlTransient
    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
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
        if (!(object instanceof Parametrica)) {
            return false;
        }
        Parametrica other = (Parametrica) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.trazabilidad.entities.Parametrica[ id=" + id + " ]";
    }
    
}
