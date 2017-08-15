
package ar.gob.ambiente.sacvefor.trazabilidad.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.envers.Audited;
import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * Entidad que encapsula los datos de la Cuenta de la Entidad transformadora.
 * Cada Guía tendrá una Cuenta origen y una Cuenta destino
 * Los datos del Titular de la Cuenta serán cacheados del RUE
 * Las Cuentas se vinculan al Usuario mediante el CUIT
 * @author rincostante
 */
@Entity
@Audited
public class Cuenta implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Identificación de la Persona en el servicio de registro único de entidades (RUE)
     */
    private Long idRue;
    
    /**
     * Nombre completo del Productor si es física o razón social si es jurídica
     */
    @Column (nullable=false, length=100)
    @NotNull(message = "El campo nombreCompleto no puede ser nulo")
    @Size(message = "El campo nombreCompleto no puede tener más de 100 caracteres", min = 1, max = 100)      
    private String nombreCompleto;
    
    /**
     * Tipo de persona:
     * Física
     * Jurídica
     */
    @Column (nullable=false, length=20)
    @NotNull(message = "El campo tipoPersona no puede ser nulo")
    @Size(message = "El campo tipoPersona no puede tener más de 20 caracteres", min = 1, max = 20)   
    private String tipoPersona;
    
    /**
     * Cuit del Productor
     */
    private Long cuit;
    
    /**
     * Indica la Calle del domicilio del Titular de la cuenta
     * Cacheada del RUE.
     */
    @Column (nullable=false, length=50)
    @NotNull(message = "El campo domCalle no puede ser nulo")
    @Size(message = "El campo domCalle no puede tener más de 50 caracteres", min = 1, max = 50)  
    private String domCalle;
    
    /**
     * Indica el número de puerta del domicilio del Titular de la cuenta
     * Cacheada del RUE.
     */
    @Column (nullable=false, length=20)
    @NotNull(message = "El campo domNumero no puede ser nulo")
    @Size(message = "El campo domNumero no puede tener más de 20 caracteres", min = 1, max = 20)  
    private String domNumero;
    
    /**
     * Indica la identificación del piso del domicilio del Titular de la cuenta, si correspondiera
     * Cacheada del RUE.
     */
    @Column (length=10) 
    @Size(message = "El campo domPiso no puede tener más de 10 caracteres", max = 10)  
    private String domPiso;
    
    /**
     * Indica la identificación del departamento del domicilio del Titular de la cuenta, si correspondiera
     * Cacheada del RUE.
     */
    @Column (length=20) 
    @Size(message = "El campo domDepto no puede tener más de 20 caracteres", max = 20)      
    private String domDepto;
    
    /**
     * Identificación de la Localidad en el servicio de gestión territorial
     */
    private Long idLocGT;  
    
    /**
     * Nombre de la Localidad en la que está domiciliado el Titular de la Cuenta
     */
    private String localidad;

    /**
     * Nombre del Departamento en el que está domiciliado el Titular de la Cuenta
     */    
    private String departamento;
    
    /**
     * Nombre de la Provincia en la que está domiciliado el Titular de la Cuenta
     */  
    private String provincia;
    
    /**
     * En los casos en que la Entidad transformadora dispusiera de Depósito
     * Se persiste en casacada
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="deposito_id")
    private Deposito deposito;
    
    /**
     * Campo que muestra los Productos que pudiera generar la Entidad Transformadora titular de la Cuenta
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name = "cuenta_id", referencedColumnName = "id")
    private List<Producto> productos;      
    
    /**
     * Fecha de registro del EntidadGuia
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaAlta;    
    
    /**
     * Usuario que gestiona la inserciones o ediciones
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="usuario_id", nullable=false)
    @NotNull(message = "Debe existir un Usuario")   
    private Usuario usuario;    
      
    private boolean habilitado;
    
    /**
     * Campo que mostrará la fecha de las revisiones
     * No se persiste
     */    
    @Transient
    private Date fechaRevision; 
    
    public Cuenta(){
        productos = new ArrayList<>();
    }

    public String getDomCalle() {
        return domCalle;
    }

    public void setDomCalle(String domCalle) {
        this.domCalle = domCalle;
    }

    public String getDomNumero() {
        return domNumero;
    }

    public void setDomNumero(String domNumero) {
        this.domNumero = domNumero;
    }

    public String getDomPiso() {
        return domPiso;
    }

    public void setDomPiso(String domPiso) {
        this.domPiso = domPiso;
    }

    public String getDomDepto() {
        return domDepto;
    }

    public void setDomDepto(String domDepto) {
        this.domDepto = domDepto;
    }

    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
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

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public Long getCuit() {
        return cuit;
    }

    public void setCuit(Long cuit) {
        this.cuit = cuit;
    }

    public Long getIdLocGT() {
        return idLocGT;
    }

    public void setIdLocGT(Long idLocGT) {
        this.idLocGT = idLocGT;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
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
        if (!(object instanceof Cuenta)) {
            return false;
        }
        Cuenta other = (Cuenta) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.Productor[ id=" + id + " ]";
    }
    
}
