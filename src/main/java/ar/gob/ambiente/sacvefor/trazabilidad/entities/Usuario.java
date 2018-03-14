
package ar.gob.ambiente.sacvefor.trazabilidad.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Entidad para gestionar los Usuarios de la aplicación
 * @author rincostante
 */
@Entity
@XmlRootElement
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Variable privada: Identificador único
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Variable privada: Guarda el rol al que pertenece el usuario
     */
    @ManyToOne
    @JoinColumn(name="rol_id", nullable=false)
    @NotNull(message = "Debe existir un Rol")    
    private Parametrica rol;
    
    /**
     * Variable privada: Guarda el nombre de la Provincia del domicilio de la Persona en el RUE
     */
    private String jurisdiccion;    
    
    /**
     * Variable privada: CUIT del usuario que oficiará como nombre de usuario
     */
    private Long login;
    
    /**
     * Variable privada: Nombre y apellido del usuario
     */
    @Column (nullable=false, length=50)
    @NotNull(message = "El campo nombreCompleto no puede ser nulo")
    @Size(message = "El campo nombreCompleto no puede tener más de 50 caracteres", min = 1, max = 50)       
    private String nombreCompleto;
    
    /**
     * Variable privada: Correo electrónico válido del usuario al que se le remitrirán las credenciales de acceso
     */
    @Column (nullable=false, length=50)
    @NotNull(message = "El campo email no puede ser nulo")
    @Size(message = "El campo email no puede tener más de 50 caracteres", min = 1, max = 50)    
    private String email;    
    
    /**
     * Variable privada: Clave encriptada que generará el sistema automáticamente la primera vez y 
     * solicitará al usuario su cambio cuando realice la primera sesión.
     */
    @Column (length=100)
    @Size(message = "el campo clave no puede tener más de 100 caracteres", max = 100)   
    private String clave;
    
    /**
     * Variable privada: Fecha de alta del usuario
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaAlta;
    
    /**
     * Variable privada: Fecha de la última modificación de los datos del usuario
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaModif;    
    
    /**
     * Variable privada: Fecha de la última vez que el usuario registra una sesión en la aplicación
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaUltimoLogin;
    
    /**
     * Variable privada: Condición de habilitado de la Persona
     */
    private boolean habilitado;

    public String getJurisdiccion() {
        return jurisdiccion;
    }

    public void setJurisdiccion(String jurisdiccion) {
        this.jurisdiccion = jurisdiccion;
    }

    public Parametrica getRol() {
        return rol;
    }

    public void setRol(Parametrica rol) {
        this.rol = rol;
    }

    public Long getLogin() {
        return login;
    }

    public void setLogin(Long login) {
        this.login = login;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    @XmlTransient
    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @XmlTransient
    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    @XmlTransient
    public Date getFechaModif() {
        return fechaModif;
    }

    public void setFechaModif(Date fechaModif) {
        this.fechaModif = fechaModif;
    }

    @XmlTransient
    public Date getFechaUltimoLogin() {
        return fechaUltimoLogin;
    }

    public void setFechaUltimoLogin(Date fechaUltimoLogin) {
        this.fechaUltimoLogin = fechaUltimoLogin;
    }

    @XmlTransient
    public boolean isHabilitado() {
        return habilitado;
    }

    @ManyToOne
    @JoinColumn(name="usuarioapi_id", nullable=true)
    private UsuarioApi usuarioApi;   
    
    @XmlTransient
    public UsuarioApi getUsuarioApi() {
        return usuarioApi;
    }

    public void setUsuarioApi(UsuarioApi usuarioApi) {
        this.usuarioApi = usuarioApi;
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
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.trazabilidad.entities.Usuario[ id=" + id + " ]";
    }
    
}
