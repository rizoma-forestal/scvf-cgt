
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.envers.Audited;
import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * Entidad que encapsula los datos correspondientes a los Sub Productos generados 
 * como consecuencia de la transformación de Productos primarios.
 * Los SubProductos podrán ser comercializados y tomados como insumos para
 * Items productivos componentes de una Guía.
 * @author rincostante
 */
@Entity
@Audited
public class SubProducto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Clase en la que se comercializa el SubProducto
     * No será auditada
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="clase_id", nullable=false)
    @NotNull(message = "Debe existir una Clase")     
    private ProductoClase clase;
    
    /**
     * Indica la cantidad del SubProducto elaborable por unidad de Producto
     */
    private float cantXProd;
    
    /**
     * Factor de transformación del SubProducto, en caso que pueda ser transformado
     * el valor será menor de 1
     */
    @Column(nullable=true)
    private float factorTransformacion;
    
    /**
     * Equivalencia en Kg. de la Unidad de medida de la Clase del SubProducto
     */
    private float equivalKg;    
    
    /**
     * Producto del que se generará el SubProducto
     */
    @ManyToOne
    @JoinColumn(name="producto_id", nullable=false)
    @NotNull(message = "Debe existir un Producto")    
    private Producto producto;  
    
    private boolean habilitado;    
    
    /**
     * Campo que mostrará la fecha de las revisiones
     * No se persiste
     */
    @Transient
    private Date fechaRevision;          

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

    public float getCantXProd() {
        return cantXProd;
    }

    public void setCantXProd(float cantXProd) {
        this.cantXProd = cantXProd;
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

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
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
        if (!(object instanceof SubProducto)) {
            return false;
        }
        SubProducto other = (SubProducto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.trazabilidad.entities.SubProducto[ id=" + id + " ]";
    }
    
}
