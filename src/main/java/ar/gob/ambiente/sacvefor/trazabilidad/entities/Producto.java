
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
 * Gestiona los Productos de transformación que un titular de Cuenta pueda generar.
 * Si bien, la Especie será la misma, los atributos de estos productos (Clase, unidad, etc)
 * no serán los mismos que los del Producto de origen que vendrán encapsulados en el campo "códigoOrigen"
 * Será auditada.
 * @author rincostante
 */
@Entity
@Audited
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Variable privada: Identificador único
     */  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Variable privada: Identificación de la Especie del Producto en el servicio de Taxonomía
     * cacheado 
     */
    private Long idEspecieTax;
    
    /**
     * Variable privada: código origen encapsula los atributos del Producto de origen separados por '|' en este orden
     * nombreCientifico: nombre científico de la Especie constituido por 'Género/Especie'
     * nombreVulgar: nombre vulgar de la Especie definido de manera local
     * clase: clase en la que se comercializa el Producto definido de manera local
     * unidad: unidad de medida en la que se comercializa el Producto/Clase definido de manera local
     * resolución: numero de la resolución (campo numero de la entidad Autorización)
     * provincia: nombre de la Provincia dentro de la cual se extraerá el Producto
     * EJ.:"1|Prosopis caldenia|Caldén|Rollo|Unidad|123-DGB-2017|Santiago del Estero"
     * Estos valores serán tomados del Item productivo acreditado que de origen a los nuevos productos
     */
    private String codigoOrigen;
    
    /**
     * Variable privada: Nombre científico de la Especie del Producto obtenido del Servicio de Taxonomía
     */
    @Column (nullable=false, length=100, unique=true)
    @NotNull(message = "El campo nombreCientifico no puede ser nulo")
    @Size(message = "El campo nombreCientifico no puede tener más de 100 caracteres", min = 1, max = 100)
    private String nombreCientifico;
    
    /**
     * Variable privada: Nombre vulgar de la Especie en el ámbito de la Entidad Transformadora
     * ingresado al momento de registrar. Editable
     */
    @Column (nullable=false, length=50, unique=true)
    @NotNull(message = "El campo nombreVulgar no puede ser nulo")
    @Size(message = "El campo nombreVulgar no puede tener más de 50 caracteres", min = 1, max = 50) 
    private String nombreVulgar;     
    
    /**
     * Variable privada: Clase en la que se comercializa el Producto transformado
     * No será auditada
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="clase_id", nullable=false)
    @NotNull(message = "Debe existir una Clase")     
    private ProductoClase clase;
    
    /**
     * Variable privada: Factor de transformación del Producto de su estado primario
     * al estado transformado, será menor de 1
     */
    private float factorTransformacion;
    
    /**
     * Variable privada: Equivalencia en Kg. de la Unidad de medida de la Clase del Producto
     */
    private float equivalKg;
    
    /**
     * Variable privada: Listado de los SubProductos obtenibles mediante a partir de la realización del Producto
     */
    @OneToMany (mappedBy="producto")
    private List<SubProducto> supProductos;     
    
    /**
     * Variable privada: Fecha de registro del Producto
     */      
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaAlta;
    
    /**
     * Variable privada: Usuario que registró el Producto, también registrado en las auditorías.
     * No será auditado
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="usuario_id", nullable=false)
    @NotNull(message = "Debe existir un Usuario")  
    private Usuario usuario;   
    
    /**
     * Variable privada: Estado de habilitación
     */
    private boolean habilitado;
    
    /**
     * Variable privada: Campo que mostrará la fecha de las revisiones
     * No se persiste
     */
    @Transient
    private Date fechaRevision;      
    
    /**
     * Constructor
     */
    public Producto(){
        supProductos = new ArrayList<>();
    }

    public List<SubProducto> getSupProductos() {
        return supProductos;
    }

    public void setSupProductos(List<SubProducto> supProductos) {
        this.supProductos = supProductos;
    }

    public Long getIdEspecieTax() {
        return idEspecieTax;
    }

    public void setIdEspecieTax(Long idEspecieTax) {
        this.idEspecieTax = idEspecieTax;
    }

    public String getCodigoOrigen() {
        return codigoOrigen;
    }

    public void setCodigoOrigen(String codigoOrigen) {
        this.codigoOrigen = codigoOrigen;
    }

    public String getNombreCientifico() {
        return nombreCientifico;
    }

    public void setNombreCientifico(String nombreCientifico) {
        this.nombreCientifico = nombreCientifico;
    }

    public String getNombreVulgar() {
        return nombreVulgar;
    }

    public void setNombreVulgar(String nombreVulgar) {
        this.nombreVulgar = nombreVulgar;
    }

    public float getFactorTransformacion() {
        return factorTransformacion;
    }

    public void setFactorTransformacion(float factorTransformacion) {
        this.factorTransformacion = factorTransformacion;
    }

    public float getEquivalKg() {
        return equivalKg;
    }

    public void setEquivalKg(float equivalKg) {
        this.equivalKg = equivalKg;
    }

    public Date getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(Date fechaRevision) {
        this.fechaRevision = fechaRevision;
    }

    public ProductoClase getClase() {
        return clase;
    }

    public void setClase(ProductoClase clase) {
        this.clase = clase;
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
        if (!(object instanceof Producto)) {
            return false;
        }
        Producto other = (Producto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.trazabilidad.entities.Producto[ id=" + id + " ]";
    }
    
}
