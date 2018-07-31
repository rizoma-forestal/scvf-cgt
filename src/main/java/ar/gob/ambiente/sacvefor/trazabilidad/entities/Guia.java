
package ar.gob.ambiente.sacvefor.trazabilidad.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
 * Entidad que encapsula los datos correspondientes a una Guía.
 * Podrá ser de tipo "Primaria" o "Trazabilidad".
 * Una Guía, además, oficia como registro de movimiento entre cuentas
 * @author rincostante
 */
@Entity
@Audited
public class Guia implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Variable privada: Identificador único
     */  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Variable privada: Cadena que constituye el código único de la Guía,
     * el formato se configurará en el archivo de propiedades Config.
     */
    @Column (nullable=false, length=20, unique=true)
    @NotNull(message = "El campo codigo no puede ser nulo")
    @Size(message = "El campo codigo no puede tener más de 20 caracteres", min = 1, max = 20)    
    private String codigo;
    
    /**
     * Variable privada: Tipo de Guía:
     * Primaria,
     * Trazabilidad
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="tipo_id", nullable=false)
    @NotNull(message = "Debe existir una tipo de guía")    
    private TipoGuia tipo;
    
    /**
     * Variable privada: Tipo de Guía que sirvió como fuente de productos
     * Solo para Guías primarias
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="tipofuente_id", nullable=false)
    @NotNull(message = "Debe existir una tipoOrigen de guía")    
    private TipoGuia tipoFuente;
    
    /**
     * Variable privada: Cadena que constituye el número identificatorio de la Guía fuente de Productos,
     * solo para Guías primarias
     */
    @Column (nullable=false, length=30)
    @NotNull(message = "El campo numFuente no puede ser nulo")
    @Size(message = "El campo numFuente no puede tener más de 30 caracteres", min = 1, max = 30)  
    private String numFuente;
    
    /**
     * Variable privada: Jurisdicción de gestión local de la Guía de origen, para el caso de las Guías Primarias
     */
    @Column (length=50)
    @Size(message = "El campo numFuente no puede tener más de 30 caracteres", max = 50)  
    private String jurOrigen;    
    
    /**
     * Variable privada: items que constituyen el detalle de la Guía
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name = "guia_id", referencedColumnName = "id")
    private List<Item> items;      
    
    /**
     * Variable privada: Cuenta destino de la Guía
     */
    @ManyToOne
    @JoinColumn(name="cuentadestino_id")
    private Cuenta destino;
    
    /**
     * Variable privada: transporte que llevará los productos a destino
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @OneToOne(cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name="transporte_id")
    private Transporte transporte;
    
    /**
     * Variable privada: origen de la Guía, solo para las Guías de Trazabilidad
     */
    @ManyToOne
    @JoinColumn(name="cuentaorigen_id")
    private Cuenta origen;

    /**
     * Variable privada: Fecha de registro de la Guía
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaAlta;  
    
    /**
     * Variable privada: Fecha de emisión de la Guía.
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaEmisionGuia;    
    
    /**
     * Variable privada: Fecha de emisión de vencimiento de la Guía.
     * Si correspondiera
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaVencimiento; 
    
    /**
     * Variable privada: Fecha de cierre de la Guía, para los casos de Guías de transporte,
     * cuando el destinatario la acepta mediante la interface de intermediación
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaCierre;    

    /**
     * Variable privada: Usuario que gestiona la inserciones o ediciones
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="usuario_id", nullable=false)
    @NotNull(message = "Debe existir un Usuario")   
    private Usuario usuario;    
    
    /**
     * Variable privada: Estado que puede tomar una Guía:
     * Carga inicial
     * Cerrada
     * En tránsito
     * Intervenida
     * Extraviada
     * etc.
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="estado_id", nullable=false)
    @NotNull(message = "Debe existir una estado de guía")        
    private EstadoGuia estado;
    
    /**
     * Variable privada no persistida: Muestra la fecha de la revisión 
     * para cada item del listado de revisiones de una Guía.
     */    
    @Transient
    private Date fechaRevision;    
    
    /**
     * Variable privada no persistida: indicará el destino de la copia de la Guía, al generar el pdf.
     * Dicho valor será obtenido del listado de Copias vinculadas al Tipo de Guía
     */
    @Transient
    private String destinoCopia;  

    public Guia(){
        items = new ArrayList<>();
    }

    public TipoGuia getTipoFuente() {
        return tipoFuente;
    }

    public void setTipoFuente(TipoGuia tipoFuente) {
        this.tipoFuente = tipoFuente;
    }

    public String getJurOrigen() {
        return jurOrigen;
    }

    public void setJurOrigen(String jurOrigen) {
        this.jurOrigen = jurOrigen;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Cuenta getDestino() {
        return destino;
    }

    public void setDestino(Cuenta destino) {
        this.destino = destino;
    }
    
    public String getDestinoCopia() {
        return destinoCopia;
    }

    public void setDestinoCopia(String destinoCopia) {
        this.destinoCopia = destinoCopia;
    }

    public Date getFechaEmisionGuia() {
        return fechaEmisionGuia;
    }

    public void setFechaEmisionGuia(Date fechaEmisionGuia) {
        this.fechaEmisionGuia = fechaEmisionGuia;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Date getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(Date fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getNumFuente() {
        return numFuente;
    }

    public void setNumFuente(String numFuente) {
        this.numFuente = numFuente;
    }

    public EstadoGuia getEstado() {
        return estado;
    }

    public void setEstado(EstadoGuia estado) {
        this.estado = estado;
    }

    public Date getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(Date fechaRevision) {
        this.fechaRevision = fechaRevision;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoGuia getTipo() {
        return tipo;
    }

    public void setTipo(TipoGuia tipo) {
        this.tipo = tipo;
    }

    public Transporte getTransporte() {
        return transporte;
    }

    public void setTransporte(Transporte transporte) {
        this.transporte = transporte;
    }

    public Cuenta getOrigen() {
        return origen;
    }

    public void setOrigen(Cuenta origen) {
        this.origen = origen;
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
        if (!(object instanceof Guia)) {
            return false;
        }
        Guia other = (Guia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.trazabilidad.entities.Guia[ id=" + id + " ]";
    }
    
}
