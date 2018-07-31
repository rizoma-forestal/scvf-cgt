
package ar.gob.ambiente.sacvefor.trazabilidad.facades;

import ar.gob.ambiente.sacvefor.trazabilidad.entities.EstadoGuia;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Acceso a datos para la entidad EstadoGuia
 * @author rincostante
 */
@Stateless
public class EstadoGuiaFacade extends AbstractFacade<EstadoGuia> {

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
    public EstadoGuiaFacade() {
        super(EstadoGuia.class);
    }
    
    /**
     * Método para validar la existencia de un EstadoGuia en función de su nombre
     * @param nombre String nombre del estado de guías
     * @return EstadoGuia Estado de guía solicitado
     */
    public EstadoGuia getExistente(String nombre) {
        List<EstadoGuia> lstEstados;
        em = getEntityManager();
        
        String queryString = "SELECT est FROM EstadoGuia est "
                + "WHERE est.nombre = :nombre";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre);
        lstEstados = q.getResultList();
        if(lstEstados.isEmpty()){
            return null;
        }else{
            return lstEstados.get(0);
        }
    }  
    
    /**
     * Método sobreescrito que lista los EstadoGuia ordenadas por nombre
     * @return List<EstadoGuia> listado de los estados de guías ordenados
     */
    @Override
    public List<EstadoGuia> findAll(){
        em = getEntityManager();
        String queryString = "SELECT est FROM EstadoGuia est "
                + "ORDER BY est.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }      
    
    /**
     * Mátodo que solo devuelve las EstadoGuia habilitados, menos el acutal.
     * Para poblar combos de selección de cambios de Estado.
     * @param nombre String nombre del estado que no se incluye
     * @return List<EstadoGuia> lisado de los estados restantes
     */
    public List<EstadoGuia> getHabilitadosSinUno(String nombre){
        em = getEntityManager();
        String queryString = "SELECT est FROM EstadoGuia est "
                + "WHERE est.habilitado = true "
                + "AND est.nombre <> :nombre "
                + "ORDER BY est.nombre";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre);
        return q.getResultList();
    }     
}
