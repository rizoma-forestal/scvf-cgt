
package ar.gob.ambiente.sacvefor.trazabilidad.facades;

import ar.gob.ambiente.sacvefor.trazabilidad.entities.Producto;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.ProductoClase;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.SubProducto;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.hibernate.Hibernate;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

/**
 * Acceso a datos de los Sub Productos que surjan de la generación de Productos
 * @author rincostante
 */
@Stateless
public class SubProductoFacade extends AbstractFacade<SubProducto> {

    @PersistenceContext(unitName = "sacvefor-gestionTrazabilidadPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SubProductoFacade() {
        super(SubProducto.class);
    }
    
    /**
     * Método para validar la existencia de un Subproducto según el Producto del que deriva y la Clase
     * @param clase : Clase cuya existencia se va a validar junto con el Producto
     * @param producto : Producto del que el Subproducto buscaro de un derivado
     * @return 
     */
    public SubProducto getExistente(ProductoClase clase, Producto producto) {
        List<SubProducto> lstSubProductos;
        em = getEntityManager();
        
        String queryString = "SELECT sub FROM SubProducto sub "
                + "WHERE sub.clase = :clase "
                + "AND sub.producto = :producto";
        Query q = em.createQuery(queryString)
                .setParameter("clase", clase)
                .setParameter("producto", producto);
        lstSubProductos = q.getResultList();
        if(lstSubProductos.isEmpty()){
            return null;
        }else{
            return lstSubProductos.get(0);
        }
    }  
    
    /**
     * Método sobreescrito que lista los SubProductos ordenadas por el nombre vulgar del Producto del que deriva
     * @return 
     */
    @Override
    public List<SubProducto> findAll(){
        em = getEntityManager();
        String queryString = "SELECT sub FROM SubProducto sub "
                + "ORDER BY sub.producto.nombreVulgar";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }     
    
    /**
     * Mátodo que solo devuelve los SubProductos habilitados.
     * Para poblar combos de selección.
     * @return 
     */
    public List<SubProducto> getHabilitados(){
        em = getEntityManager();
        String queryString = "SELECT sub FROM SubProducto sub "
                + "WHERE sub.habilitado = true "
                + "ORDER BY sub.producto.nombreVulgar";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }    
    
    /**
     * Método que devuelve todos los Sub productos para de una misma Especie
     * @param idEspecieTax : Id de la Especie a buscar
     * @return 
     */
    public List<SubProducto> getByEspecieLocal(Long idEspecieTax){
        em = getEntityManager();
        String queryString = "SELECT sub FROM SubProducto sub "
                + "WHERE sub.habilitado = true "
                + "AND sub.producto.idEspecieTax = :idEspecieTax "
                + "ORDER BY sub.producto.nombreVulgar";
        Query q = em.createQuery(queryString)
                .setParameter("idEspecieTax", idEspecieTax);;
        return q.getResultList();
    }    
    
    /**
     * Método para obtener todas las revisiones de la entidad
     * @param clase : Clase que junto a la Especie definen el Producto cuyas revisiones se busca
     * @param producto : Producto que genera al Subproducto como derivado
     * @return 
     */
    public List<SubProducto> findRevisions(ProductoClase clase, Producto producto){  
        List<SubProducto> lstSubProductos = new ArrayList<>();
        SubProducto sub = this.getExistente(clase, producto);
        if(sub != null){
            Long id = this.getExistente(clase, producto).getId();
            AuditReader reader = AuditReaderFactory.get(getEntityManager());
            List<Number> revisions = reader.getRevisions(SubProducto.class, id);
            for (Number n : revisions) {
                SubProducto cli = reader.find(SubProducto.class, id, n);
                cli.setFechaRevision(reader.getRevisionDate(n));
                Hibernate.initialize(cli.getClase());
                Hibernate.initialize(cli.getProducto());
                lstSubProductos.add(cli);
            }
        }
        return lstSubProductos;
    }       
}
