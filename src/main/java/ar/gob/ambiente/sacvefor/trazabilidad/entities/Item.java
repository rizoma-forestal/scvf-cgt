
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.envers.Audited;
import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * Entidad que encapsula los datos correspondientes a los items
 * que componen las Guías, asimilando a estas últimas al movimiento de las Cuentas.
 * Los Items podrán ser Productivos o Derivados, en los casos que sean generados 
 * para completar una Guía o como consecuencia de la generación de un Producto transformado.
 * El tipo de Item será una Paramétrica.
 * El conjunto de items acreditados mediante Guías constituye el crédito de una Cuenta.
 * El conjunto de items generados al emitir una Guía, constituye el débito de una Cuenta.
 * @author rincostante
 */
@Entity
@Audited
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Id de la Especie de la que se constituye el Producto, 
     * registrado en el Registro de Taxonomía
     */
    @Column
    private Long idEspecieTax;    
    
    /**
     * El código origen encapsula los atributos del Producto de origen separados por '|' en este orden
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
     * Equivalencia en Kg por unidad de medida del Producto.
     * Cacheado del Producto
     */
    @Column
    private float kilosXUnidad;    
    
    /**
     * Nombre Científico cacheado del Producto
     * Se incluye en el código del producto trazable
     */
    @Column (nullable=false, length=100)
    @NotNull(message = "El campo nombreCientifico no puede ser nulo")
    @Size(message = "El campo nombreCientifico no puede tener más de 100 caracteres", min = 1, max = 100)      
    private String nombreCientifico;
    
    /**
     * Nombre vulgar cacheado del Producto
     * Se incluye en el código del producto trazable
     */
    @Column (nullable=false, length=50)
    @NotNull(message = "El campo nombreVulgar no puede ser nulo")
    @Size(message = "El campo nombreVulgar no puede tener más de 50 caracteres", min = 1, max = 50)  
    private String nombreVulgar;
    
    /**
     * Clase cacheada del Producto
     * Se incluye en el código del producto tazable
     */
    @Column (nullable=false, length=30)
    @NotNull(message = "El campo clase no puede ser nulo")
    @Size(message = "El campo clase no puede tener más de 30 caracteres", min = 1, max = 30)  
    private String clase;
    
    /**
     * Unidad de medida cacheada del Producto
     * Se incluye en el código del producto tazable
     */    
    @Column (nullable=false, length=30)
    @NotNull(message = "El campo unidad no puede ser nulo")
    @Size(message = "El campo unidad no puede tener más de 30 caracteres", min = 1, max = 30) 
    private String unidad;
    
    /**
     * Referencia al identificador del Producto.
     * Si la Guía es primaria, el Producto estará registrado en el CGL de la Provincia generadora,
     * en caso contrario, estará registrado en el Componente de Trazabilidad.
     * cacheado al momento de registrar el item
     */
    @Column(nullable=true)
    private Long idProd;
    
    /**
     * Referencia al identificador del SubProducto, en el caso que corresponda,
     * cacheado al momento de registrar el item
     */
    @Column(nullable=true) 
    private Long idSubProd;
    
    /**
     * Para los items derivados, indica dónde fueron depositados.
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="deposito_id")
    private Deposito deposito;
    
    /**
     * Paramétrica cuyo tipo es "Tipos de items" que indica el tipo de ítem actual
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="param_tipoActual_id", nullable=false)
    @NotNull(message = "Debe existir un tipoActual")
    private Parametrica tipoActual;
    
    /**
     * Referencia del ítem del cual se originó el actual
     */
    @Column 
    private Long itemOrigen;
    
    /**
     * Paramétrica cuyo tipo es "Tipos de ïtems" que indica el tipo de ítem que originó al acutal
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="param_tipoOrigen_id")  
    private Parametrica tipoOrigen;
    
    /**
     * Cantidad del Producto incluído en el item
     * Cupo
     */
    @Column
    private float total;
    
    /**
     * Equivalencia del total por unidad del Producto, en Kg.
     */
    @Column
    private float totalKg;
    
    /**
     * Campo temporal que guarda el total asignado al item origen.
     * Empleado durante la operatoria, no se persiste
     */
    @Transient
    private float totalOrigen;     
    
    /**
     * Saldo dispobible del Producto para ser descontado
     * Cupo
     */
    @Column
    private float saldo;  
    
    /**
     * Equivalencia en Kg. del saldo disponible
     */
    @Column
    private float saldoKg;  
    
    /**
     * Campo temporal que guarda el saldo disponible del item origen.
     * Empleado durante la operatoria, no se persiste
     */
    @Transient
    private float saldoOrigen;      
    
    /**
     * Campo temporal que guarda el saldo del item mientras se va realizando la operatoria
     * No se persiste
     */
    @Transient
    private float saldoTemp; 

    /**
     * Observaciones que pudieran corresponder
     */
    @Column (length=500)
    @Size(message = "El campo obs no puede tener más de 500 caracteres", max = 500)     
    private String obs;
    
    /**
     * Fecha de registro de la Autorización
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaAlta;
    
    /**
     * Usuario que gestiona la Autorización
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="usuario_id", nullable=false)
    @NotNull(message = "Debe existir un usuario")       
    private Usuario usuario;

    @Column 
    private boolean habilitado;
    
    /**
     * Campo que mostrará la fecha de las revisiones
     * No se persiste
     */    
    @Transient
    private Date fechaRevision;
    
    /**
     * Flag temporal que indica que el item ya ha sido descontado.
     * Para usar durante la operatoria, no se persiste
     */
    @Transient
    private boolean descontado;    
    
    /**********************
     * Métodos de acceso **
     **********************/ 
    public String getCodigoOrigen() {
        return codigoOrigen;
    }
     
    public void setCodigoOrigen(String codigoOrigen) {
        this.codigoOrigen = codigoOrigen;
    }

    public Long getIdSubProd() {
        return idSubProd;
    }

    public void setIdSubProd(Long idSubProd) {
        this.idSubProd = idSubProd;
    }

    public Deposito getDeposito() {
        return deposito;
    }
   
    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }

    public Long getIdEspecieTax() {
        return idEspecieTax;
    }

    public void setIdEspecieTax(Long idEspecieTax) {
        this.idEspecieTax = idEspecieTax;
    }

    public float getTotalKg() {
        return totalKg;
    }

    public void setTotalKg(float totalKg) {
        this.totalKg = totalKg;
    }

    public float getSaldoKg() {
        return saldoKg;
    }
  
    public float getKilosXUnidad() {
        return kilosXUnidad;
    }
 
    public void setKilosXUnidad(float kilosXUnidad) {
        this.kilosXUnidad = kilosXUnidad;
    }

    public void setSaldoKg(float saldoKg) {
        this.saldoKg = saldoKg;
    }

    public boolean isDescontado() {
        return descontado;
    }
   
    public void setDescontado(boolean descontado) {
        this.descontado = descontado;
    }

    public float getTotalOrigen() {
        return totalOrigen;
    }

    public void setTotalOrigen(float totalOrigen) {
        this.totalOrigen = totalOrigen;
    }

    public float getSaldoOrigen() {
        return saldoOrigen;
    }
 
    public void setSaldoOrigen(float saldoOrigen) {
        this.saldoOrigen = saldoOrigen;
    }

    public float getSaldoTemp() {
        return saldoTemp;
    }
      
    public void setSaldoTemp(float saldoTemp) {
        this.saldoTemp = saldoTemp;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    public String getNombreVulgar() {
        return nombreVulgar;
    }

    public void setNombreVulgar(String nombreVulgar) {
        this.nombreVulgar = nombreVulgar;
    }
    
    public String getNombreCientifico() {
        return nombreCientifico;
    }

    public void setNombreCientifico(String nombreCientifico) {
        this.nombreCientifico = nombreCientifico;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public Long getIdProd() {
        return idProd;
    }

    public void setIdProd(Long idProd) {
        this.idProd = idProd;
    }

    public Parametrica getTipoActual() {
        return tipoActual;
    }

    public void setTipoActual(Parametrica tipoActual) {
        this.tipoActual = tipoActual;
    }

    public Long getItemOrigen() {
        return itemOrigen;
    }

    public void setItemOrigen(Long itemOrigen) {
        this.itemOrigen = itemOrigen;
    }

    public Parametrica getTipoOrigen() {
        return tipoOrigen;
    }

    public void setTipoOrigen(Parametrica tipoOrigen) {
        this.tipoOrigen = tipoOrigen;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
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

    public Date getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(Date fechaRevision) {
        this.fechaRevision = fechaRevision;
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
        if (!(object instanceof Item)) {
            return false;
        }
        Item other = (Item) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.trazabilidad.entities.ItemProductivo[ id=" + id + " ]";
    }
    
}
