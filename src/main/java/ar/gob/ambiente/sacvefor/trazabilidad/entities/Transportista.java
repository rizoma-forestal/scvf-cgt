
package ar.gob.ambiente.sacvefor.trazabilidad.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.envers.Audited;
import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * Entidad que encapsula los datos correspondientes a un titular de Vehículos,
 * sea persona jurídica o física.
 * @author rincostante
 */
@Entity
@Audited
public class Transportista implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Variable privada: Identificador único
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Variable privada: Identificación de la Persona en el servicio de registro único de entidades (RUE)
     */    
    private Long idRue;
    
    /**
     * Variable privada: Nombre completo o razón social de la persona, cacheado del servicio del RUE
     */
    @Column (nullable=false, length=100)
    @NotNull(message = "El campo nombreCompleto no puede ser nulo")
    @Size(message = "El campo nombreCompleto no puede tener más de 100 caracteres", min = 1, max = 100)      
    private String nombreCompleto;
    
    /**
     * Variable privada: CUIT cacheado el RUE
     */
    private Long cuit;

    /**
     * Variable privada: Tipo de Persona: 
     * Jurídica o Física
     */
    private String tipoPersona;
    
    /**
     * Variable privada: Listado de las Copias a imprimir de la Guía
     */
    @OneToMany (mappedBy="titular")
    private List<Vehiculo> vehiculos;      
    
    /**
     * Variable privada: Fecha de alta de la Persona
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaAlta;

    /**
     * Variable privada: Usuario que gestiona la inserciones o ediciones
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="usuario_id", nullable=false)
    @NotNull(message = "Debe existir un Usuario")   
    private Usuario usuario;
    
    /**
     * Variable privada: Condición de habilitado de la Persona
     */
    private boolean habilitado;
    
    /**
     * Variable privada: Campo que mostrará la fecha de las revisiones
     * No se persiste
     */
    @Transient
    private Date fechaRevision;      
    
    public Transportista(){
        vehiculos = new ArrayList<>();
    }

    public Date getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(Date fechaRevision) {
        this.fechaRevision = fechaRevision;
    }

    public Long getIdRue() {
        return idRue;
    }

    public void setIdRue(Long idRue) {
        this.idRue = idRue;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Long getCuit() {
        return cuit;
    }

    public void setCuit(Long cuit) {
        this.cuit = cuit;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public List<Vehiculo> getVehiculos() {
        return vehiculos;
    }

    public void setVehiculos(List<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
        if (!(object instanceof Transportista)) {
            return false;
        }
        Transportista other = (Transportista) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.trazabilidad.entities.Transportista[ id=" + id + " ]";
    }
    
}
