
package ar.gob.ambiente.sacvefor.trazabilidad.facades;

import ar.gob.ambiente.sacvefor.trazabilidad.entities.Parametrica;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.TipoParam;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.Usuario;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Acceso a datos para la entidad Parametrica
 * @author rincostante
 */
@Stateless
public class ParametricaFacade extends AbstractFacade<Parametrica> {

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
    public ParametricaFacade() {
        super(Parametrica.class);
    }
    
    /**
     * Método para validar la existencia de una paramétrica
     * @param nombre String Nombre cuya existencia se quiere validar
     * @param tipo TipoParam tipo de paramétrica correspondiente a la que se quiere validar
     * @return Parametrica paramétrica existente
     */
    public Parametrica getExistente(String nombre, TipoParam tipo) {
        List<Parametrica> lstParam;
        em = getEntityManager();
        
        String queryString = "SELECT param FROM Parametrica param "
                + "WHERE param.nombre = :nombre "
                + "AND param.tipo = :tipo";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre)
                .setParameter("tipo", tipo);
        lstParam = q.getResultList();
        if(lstParam.isEmpty()){
            return null;
        }else{
            return lstParam.get(0);
        }
    }
    
    /**
     * Método sobreescrito que lista las Paramétricas ordenadas por nombre
     * @return List<Parametrica> listado de paramétricas orndenadas
     */
    @Override
    public List<Parametrica> findAll(){
        em = getEntityManager();
        String queryString = "SELECT param FROM Parametrica param "
                + "ORDER BY param.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }    
    
    /**
     * Mátodo que solo devuelve las Parametricas habilitadas, según el tipo.
     * Para poblar combos de selección.
     * @param tipo TipoParam Tipo de Paramétrica buscado
     * @return List<Parametrica> listado de las paramétricas solicitadas
     */
    public List<Parametrica> getHabilitadas(TipoParam tipo){
        em = getEntityManager();
        String queryString = "SELECT param FROM Parametrica param "
                + "WHERE param.habilitado = true "
                + "AND param.tipo = :tipo "
                + "ORDER BY param.nombre";
        Query q = em.createQuery(queryString)
                .setParameter("tipo", tipo);
        return q.getResultList();
    }    
    
    /**
     * Método que devuelve un listado de Usuarios con el rol (Parametrica) cuyo id se recibe.
     * @param idRol Long id de la Parametrica que oficia de rol del Usuario
     * @return List<Usuario> listado de los usuarios solicitados
     */
    public List<Usuario> getUsuariosByRol(Long idRol){
        em = getEntityManager();
        String queryString = "SELECT usuario FROM Usuario usuario "
                + "WHERE usuario.habilitado = true "
                + "AND usuario.rol.id = :idRol "
                + "ORDER BY usuario.nombreCompleto";
        Query q = em.createQuery(queryString)
                .setParameter("idRol", idRol);
        return q.getResultList();
    }
}
