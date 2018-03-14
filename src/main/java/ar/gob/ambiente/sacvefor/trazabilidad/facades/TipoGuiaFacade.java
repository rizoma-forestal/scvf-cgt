
package ar.gob.ambiente.sacvefor.trazabilidad.facades;

import ar.gob.ambiente.sacvefor.trazabilidad.entities.TipoGuia;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Acceso a datos para la entidad TipoGuia
 * @author rincostante
 */
@Stateless
public class TipoGuiaFacade extends AbstractFacade<TipoGuia> {

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
    public TipoGuiaFacade() {
        super(TipoGuia.class);
    }
    
    /**
     * Método para validar la existencia de un TipoGuia en función de su nombre
     * @param nombre String Nombre del TipoGuia a validar
     * @return TipoGuia Tipo de guía existente
     */
    public TipoGuia getExistente(String nombre) {
        List<TipoGuia> lstTipos;
        em = getEntityManager();
        
        String queryString = "SELECT tipo FROM TipoGuia tipo "
                + "WHERE tipo.nombre = :nombre";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre);
        lstTipos = q.getResultList();
        if(lstTipos.isEmpty()){
            return null;
        }else{
            return lstTipos.get(0);
        }
    }   
    
    /**
     * Método sobreescrito que lista los EstadoGuia ordenadas por nombre
     * @return List<TipoGuia> listado de los tipos de guía ordenados por su nombre
     */
    @Override
    public List<TipoGuia> findAll(){
        em = getEntityManager();
        String queryString = "SELECT tipo FROM TipoGuia tipo "
                + "ORDER BY tipo.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }       
    
    /**
     * Método que devuelve todos los tipos de Guía habilitados
     * @return List<TipoGuia> listado de los tipos de guía habilitados ordenados por su nombre
     */
    public List<TipoGuia> getHabilitados(){
        em = getEntityManager();
        String queryString = "SELECT tipo FROM TipoGuia tipo "
                + "WHERE tipo.habilitado = true "
                + "ORDER BY tipo.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }      
}
