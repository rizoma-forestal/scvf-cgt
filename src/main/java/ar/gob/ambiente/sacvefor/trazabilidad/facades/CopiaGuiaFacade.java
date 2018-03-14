
package ar.gob.ambiente.sacvefor.trazabilidad.facades;

import ar.gob.ambiente.sacvefor.trazabilidad.entities.CopiaGuia;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.TipoGuia;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Acceso a datos para la entidad CopiaGuia
 * @author rincostante
 */
@Stateless
public class CopiaGuiaFacade extends AbstractFacade<CopiaGuia> {

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
    public CopiaGuiaFacade() {
        super(CopiaGuia.class);
    }
    
    /**
     * Método para validar la existencia de una Copia en función de su nombre y destino
     * @param nombre Sring Nombre de la CopiaGuia a validar
     * @param destino String Destino de la CopiaGuia a validar
     * @return CopiaGuia copia de guía solicitado
     */
    public CopiaGuia getExistente(String nombre, String destino) {
        List<CopiaGuia> lstCopias;
        em = getEntityManager();
        
        String queryString = "SELECT copia FROM CopiaGuia copia "
                + "WHERE copia.nombre = :nombre "
                + "AND copia.destino = :destino";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre)
                .setParameter("destino", destino);
        lstCopias = q.getResultList();
        if(lstCopias.isEmpty()){
            return null;
        }else{
            return lstCopias.get(0);
        }
    }        
    
    /**
     * Método que devuelve todos las Copias habilitadas
     * @param tipo TipoGuia Tipo de Guía de la cual se pide las Copias
     * @return List<CopiaGuia> listado de las copias de guía habilitadas según el tipo 
     */
    public List<CopiaGuia> getHabilitadosByTipo(TipoGuia tipo){
        em = getEntityManager();
        String queryString = "SELECT copia FROM CopiaGuia copia "
                + "WHERE copia.habilitado = true "
                + "AND copia.tipoGuia = :tipo";
        Query q = em.createQuery(queryString)
                .setParameter("tipo", tipo);;
        return q.getResultList();
    }  

    /**
     * Método que devuelve todos las Copias habilitadas
     * @return List<CopiaGuia> listado de las copias de guía habilitadas
     */
    public List<CopiaGuia> getHabilitados(){
        em = getEntityManager();
        String queryString = "SELECT copia FROM CopiaGuia copia "
                + "WHERE copia.habilitado = true";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }       
}
