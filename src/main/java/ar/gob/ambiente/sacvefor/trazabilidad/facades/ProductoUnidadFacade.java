
package ar.gob.ambiente.sacvefor.trazabilidad.facades;

import ar.gob.ambiente.sacvefor.trazabilidad.entities.ProductoUnidad;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Acceso a datos para las Unidades de medida de las clases de Productos
 * @author rincostante
 */
@Stateless
public class ProductoUnidadFacade extends AbstractFacade<ProductoUnidad> {

    @PersistenceContext(unitName = "sacvefor-gestionTrazabilidadPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProductoUnidadFacade() {
        super(ProductoUnidad.class);
    }
 
    /**
     * Método para validar la existencia de una Unidad según su nombre
     * @param nombre : nombre a validar
     * @return 
     */
    public ProductoUnidad getExistenteByNombre(String nombre) {
        List<ProductoUnidad> lstUnidad;
        em = getEntityManager();
        
        String queryString = "SELECT unidad FROM ProductoUnidad unidad "
                + "WHERE unidad.nombre = :nombre";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre);
        lstUnidad = q.getResultList();
        if(lstUnidad.isEmpty()){
            return null;
        }else{
            return lstUnidad.get(0);
        }
    }    
    
    /**
     * Método para validar la existencia de una Unidad según su aberviatura
     * @param abrev : aberviatura a validar
     * @return 
     */
    public ProductoUnidad getExistenteByAbrev(String abrev) {
        List<ProductoUnidad> lstUnidad;
        em = getEntityManager();
        
        String queryString = "SELECT unidad FROM ProductoUnidad unidad "
                + "WHERE unidad.abreviatura = :abrev";
        Query q = em.createQuery(queryString)
                .setParameter("abrev", abrev);
        lstUnidad = q.getResultList();
        if(lstUnidad.isEmpty()){
            return null;
        }else{
            return lstUnidad.get(0);
        }
    }  
    
    /**
     * Método sobreescrito que lista las ProductoUnidad ordenadas por nombre
     * @return 
     */
    @Override
    public List<ProductoUnidad> findAll(){
        em = getEntityManager();
        String queryString = "SELECT unidad FROM ProductoUnidad unidad "
                + "ORDER BY unidad.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    } 
    
    /**
     * Mátodo que solo devuelve los ProductoUnidad habilitados.
     * Para poblar combos de selección.
     * @return 
     */
    public List<ProductoUnidad> getHabilitados(){
        em = getEntityManager();
        String queryString = "SELECT unidad FROM ProductoUnidad unidad "
                + "WHERE unidad.habilitado = true "
                + "ORDER BY unidad.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }       
}
