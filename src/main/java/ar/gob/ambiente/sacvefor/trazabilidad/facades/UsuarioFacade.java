
package ar.gob.ambiente.sacvefor.trazabilidad.facades;

import ar.gob.ambiente.sacvefor.trazabilidad.entities.Usuario;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Acceso a datos para la entidad Usuario
 * @author rincostante
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {

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
    public UsuarioFacade() {
        super(Usuario.class);
    }
    
    /**
     * Método para validar la existencia de un Usuario según su CUIT, utilizado como login
     * @param login Long CUIT del Usuario
     * @return Usuario usuario solicitado
     */
    public Usuario getExistente(Long login) {
        List<Usuario> lstUsuarios;
        em = getEntityManager();
        
        String queryString = "SELECT us FROM Usuario us "
                + "WHERE us.login = :login";
        Query q = em.createQuery(queryString)
                .setParameter("login", login);
        lstUsuarios = q.getResultList();
        if(lstUsuarios.isEmpty()){
            return null;
        }else{
            return lstUsuarios.get(0);
        }
    }    
    
    /**
     * Mátodo que solo devuelve los Usuarios habilitados.
     * Para poblar combos de selección.
     * @return List<Usuario> listado de todos los usuarios habilitados
     */
    public List<Usuario> getHabilitados(){
        em = getEntityManager();
        String queryString = "SELECT us FROM Usuario us "
                + "WHERE us.habilitado = true";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }     

    /**
     * Método para validar la existencia de un Usuario con las credenicales recibidas
     * @param login Long DNI del Usuario
     * @param clave String clave encriptada
     * @return Usuario usuario validado
     */
    public Usuario validar(Long login, String clave){
        List<Usuario> lUs;
        em = getEntityManager();
        String queryString = "SELECT us FROM Usuario us "
                + "WHERE us.login = :login "
                + "AND us.clave = :clave";
        Query q = em.createQuery(queryString)
                .setParameter("login", login)
                .setParameter("clave", clave);
        lUs = q.getResultList();
        if(lUs.isEmpty()){
            return null;
        }else{
            return lUs.get(0);
        }
    }  
    
    /**
     * Método que devuelve los Usuarios registrados para una Jurisdicción.
     * @param jurisdiccion String jurisdicción de la cual se solicitan sus usuarios
     * @return List<Usuario> listado de los usuarios correspondientes a la jurisdicción remitida
     */
    public List<Usuario> getByJuris(String jurisdiccion) {
        em = getEntityManager();
        String queryString = "SELECT us FROM Usuario us "
                + "WHERE us.habilitado = true "
                + "AND us.jurisdiccion = :jurisdiccion";
        Query q = em.createQuery(queryString)
                .setParameter("jurisdiccion", jurisdiccion);
        return q.getResultList();
    }
}
