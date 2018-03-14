
package ar.gob.ambiente.sacvefor.trazabilidad.facades;

import ar.gob.ambiente.sacvefor.trazabilidad.entities.Transportista;
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
 * Acceso a datos para la entidad Transportista
 * @author rincostante
 */
@Stateless
public class TransportistaFacade extends AbstractFacade<Transportista> {

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
    public TransportistaFacade() {
        super(Transportista.class);
    }
    
    /**
     * Metodo para validar un Transportista existente según su cuit
     * @param cuit Long Cuit a validar
     * @return Transportista transportista con el cuit solicitado
     */
    public Transportista getExistente(Long cuit) {
        List<Transportista> lstTransp;
        em = getEntityManager();
        
        String queryString = "SELECT transp FROM Transportista transp "
                + "WHERE transp.cuit = :cuit";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit);
        lstTransp = q.getResultList();
        if(lstTransp.isEmpty()){
            return null;
        }else{
            return lstTransp.get(0);
        }
    }
    
    /**
     * Método para obtener un Transportista a partir del id del Registro Unico de Entidades del SACVeFor.
     * No debería haber dos Transportistas con el mismo idRue.
     * @param idRue Long id de la Transportista en el RUE
     * @return Transportista correspondientes a la Persona cuyo id se remite
     */
    public Transportista getByIdRue(Long idRue){
        List<Transportista> lstPersonas;
        em = getEntityManager();
        
        String queryString = "SELECT transp FROM Transportista transp "
                + "WHERE transp.idRue = :idRue";
        Query q = em.createQuery(queryString)
                .setParameter("idRue", idRue);
        lstPersonas = q.getResultList();
        if(lstPersonas.isEmpty()){
            return null;
        }else{
            return lstPersonas.get(0);
        }
    }    
    
    /**
     * Método sobreescrito que lista los Transportistas ordenados por nombre completo
     * @return List<Transportista> listado de los transportistas ordenados por su nombre completo
     */
    @Override
    public List<Transportista> findAll(){
        em = getEntityManager();
        String queryString = "SELECT transp FROM Transportista transp "
                + "ORDER BY transp.nombreCompleto";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }     
    
    /**
     * Método que devuelve todos los Transportistas habilitados
     * @return List<Transportista> listado de los transportistas habilitados ordenados por su nombre completo
     */
    public List<Transportista> getHabilitados(){
        em = getEntityManager();
        String queryString = "SELECT transp FROM Transportista transp "
                + "ORDER BY transp.nombreCompleto "
                + "AND transp.habilitado = true";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }
    
    /**
     * Método para obtener un listado de los Transportistas cuyo nombre completo 
     * (o Razón social) contenga la cadena recibida como parámetro
     * @param param String Cadena que deberá contener el nombre completo o razón social de la Persona
     * @return List<Transportista> listado de los transportistas correspondientes al nombre remitido
     */
    public List<Transportista> findByNombreCompeto(String param){
        em = getEntityManager();
        String queryString = "SELECT transp FROM Transportista transp "
                + "WHERE LOWER(transp.nombreCompleto) LIKE :param "
                + "AND transp.habilitado = true "
                + "ORDER BY transp.nombreCompleto";
        Query q = em.createQuery(queryString)
                .setParameter("param", "%" + param + "%".toLowerCase());
        return q.getResultList();
    }   
    
    /**
     * Método para obtener todas las revisiones de la entidad
     * @param cuit Long Cuit del Transportista del que se quieren ver sus revisiones
     * @return List<Transportista> listado de las revisiones del transportista remitido
     */
    public List<Transportista> findRevisions(Long cuit){  
        List<Transportista> lstTransp = new ArrayList<>();
        Transportista per = this.getExistente(cuit);
        if(per != null){
            Long id = per.getId();
            AuditReader reader = AuditReaderFactory.get(getEntityManager());
            List<Number> revisions = reader.getRevisions(Transportista.class, id);
            for (Number n : revisions) {
                Transportista cli = reader.find(Transportista.class, id, n);
                cli.setFechaRevision(reader.getRevisionDate(n));
                Hibernate.initialize(cli.getVehiculos());
                lstTransp.add(cli);
            }
        }
        return lstTransp;
    }      
}
