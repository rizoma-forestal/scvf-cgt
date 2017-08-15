
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

    @PersistenceContext(unitName = "sacvefor-gestionTrazabilidadPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TransportistaFacade() {
        super(Transportista.class);
    }
    
    /**
     * Metodo para validar un Transportista existente según su cuit
     * @param cuit : Cuit a validar
     * @return 
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
     * @param idRue : id de la Transportista en el RUE
     * @return 
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
     * @return 
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
     * @return 
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
     * @param param : Cadena que deberá contener el nombr completo o razón social de la Persona
     * @return 
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
     * @param cuit : Cuit del Transportista del que se quieren ver sus revisiones
     * @return 
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
