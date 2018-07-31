
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

    /**
     * Variable privada: EntityManager al que se le indica la unidad de persistencia mediante la cual accederá a la base de datos
     */
    @PersistenceContext(unitName = "sacvefor-gestionTrazabilidadPU")
    private EntityManager em;

    /**
     * Método que implementa el abstracto para la obtención del EntityManager
     * @return EntityManager para acceder a datos
     */ 
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Constructor
     */
    public SubProductoFacade() {
        super(SubProducto.class);
    }
    
    /**
     * Método para validar la existencia de un Subproducto según el Producto del que deriva y la Clase
     * @param clase ProductoClase Clase cuya existencia se va a validar junto con el Producto
     * @param producto Producto producto del que el Subproducto buscado es un derivado
     * @return SubProducto sub producto solicitado
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
     * @return List<SubProducto> listado de sub productos ordenados por el nombre vulgar de la especie
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
     * @return List<SubProducto> listado de sub productos habilitados ordenados por el nombre vulgar de la especie
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
     * @param idEspecieTax Long Id de la Especie a buscar
     * @return List<SubProducto> listado de sub productos habilitados ordenados por el nombre vulgar pertenecientes a la especie remitida
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
     * @param clase ProductoClase Clase que junto a la Especie definen el Producto cuyas revisiones se busca
     * @param producto Producto Producto que genera al Subproducto como derivado
     * @return List<SubProducto> listado de las revisiones del sub producto requerido 
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
