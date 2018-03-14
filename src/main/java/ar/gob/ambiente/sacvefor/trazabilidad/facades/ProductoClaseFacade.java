
package ar.gob.ambiente.sacvefor.trazabilidad.facades;

import ar.gob.ambiente.sacvefor.trazabilidad.entities.ProductoClase;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.ProductoUnidad;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Acceso a datos para las Clases de Productos
 * @author rincostante
 */
@Stateless
public class ProductoClaseFacade extends AbstractFacade<ProductoClase> {

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
    public ProductoClaseFacade() {
        super(ProductoClase.class);
    }
    
    /**
     * Método para validar la existencia de una Clase de producto según su nombre y unidad de medida
     * @param nombre String Nombre a validar junto con la unidad de medida
     * @param unidad ProductoUnidad Unidad de medida a validar junto con el nombre
     * @return ProductoClase clase de producto solicitada
     */
    public ProductoClase getExistente(String nombre, ProductoUnidad unidad) {
        List<ProductoClase> lstClases;
        em = getEntityManager();
        
        String queryString = "SELECT clase FROM ProductoClase clase "
                + "WHERE clase.nombre = :nombre "
                + "AND clase.unidad = :unidad";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre)
                .setParameter("unidad", unidad);
        lstClases = q.getResultList();
        if(lstClases.isEmpty()){
            return null;
        }else{
            return lstClases.get(0);
        }
    }       
    
    /**
     * Método sobreescrito que lista las ProductoClase ordenadas por nombre
     * @return List<ProductoClase> listado de las clases de productos ordenadas
     */
    @Override
    public List<ProductoClase> findAll(){
        em = getEntityManager();
        String queryString = "SELECT clase FROM ProductoClase clase "
                + "ORDER BY clase.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }       
    
    /**
     * Mátodo que solo devuelve las ProductoClase habilitadas.
     * Para poblar combos de selección.
     * @return List<ProductoClase> listado de las clases de productos habilitadas
     */
    public List<ProductoClase> getHabilitadas(){
        em = getEntityManager();
        String queryString = "SELECT clase FROM ProductoClase clase "
                + "WHERE clase.habilitado = true "
                + "ORDER BY clase.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }       
}
