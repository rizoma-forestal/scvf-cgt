
package ar.gob.ambiente.sacvefor.trazabilidad.facades;

import ar.gob.ambiente.sacvefor.trazabilidad.entities.Cuenta;
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
 * Acceso a datos de la entidad Cuenta
 * @author rincostante
 */
@Stateless
public class CuentaFacade extends AbstractFacade<Cuenta> {

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
    public CuentaFacade() {
        super(Cuenta.class);
    }
    
    /**
     * Metodo para validar una Cuenta existente según su cuit
     * @param cuit Long cuit del titular de la cuenta
     * @return Cuenta cuenta solicitada
     */
    public Cuenta getExistente(Long cuit){
        List<Cuenta> lstCuentas;
        em = getEntityManager();
        
        String queryString = "SELECT cuenta FROM Cuenta cuenta "
                + "WHERE cuenta.cuit = :cuit";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit);
        lstCuentas = q.getResultList();
        if(lstCuentas.isEmpty()){
            return null;
        }else{
            return lstCuentas.get(0);
        }
    }
    
    /**
     * Método sobreescrito que lista las Cuentas ordenadas por nombre completo
     * @return List<Cuenta> listado de las cuentas ordenadas alfabéticamente
     */
    @Override
    public List<Cuenta> findAll(){
        em = getEntityManager();
        String queryString = "SELECT cuenta FROM Cuenta cuenta "
                + "ORDER BY cuenta.nombreCompleto";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }   
    
    /**
     * Método para obtener un listado de Cuentas cuyo nombre completo 
     * (o Razón social) contenga la cadena recibida como parámetro
     * @param param String Cadena que deberá contener el nombr completo o razón social del Titular
     * @return List<Cuenta> listado de las cuentas solicitadas
     */
    public List<Cuenta> findByNombreCompeto(String param){
        em = getEntityManager();
        String queryString = "SELECT cuenta FROM Cuenta cuenta "
                + "WHERE LOWER(cuenta.nombreCompleto) LIKE :param "
                + "AND cuenta.habilitado = true "
                + "ORDER BY cuenta.nombreCompleto";
        Query q = em.createQuery(queryString)
                .setParameter("param", "%" + param + "%".toLowerCase());
        return q.getResultList();
    }      
    
    /**
     * Método para buscar una Cuenta habilitada según su CUIT
     * @param cuit Long Cuit del titular de la cuenta a buscar
     * @return Cuenta cuenta solicitada
     */
    public Cuenta findByCuit(Long cuit){
        List<Cuenta> lstCuentas;
        em = getEntityManager();
        String queryString = "SELECT cuenta FROM Cuenta cuenta "
                + "WHERE cuenta.cuit = :cuit "
                + "AND cuenta.habilitado = true";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit);
        lstCuentas = q.getResultList();
        if(lstCuentas.isEmpty()){
            return null;
        }else{
            return lstCuentas.get(0);
        }
    }     
    
    /**
     * Método que retorna verdadero si hay una cuenta para el titular con el CUIT recibido
     * o falso si no la hay.
     * @param cuit Long cuit del titular de la cuenta
     * @return boolean true o false según el caso
     */
    public boolean existeCuenta(Long cuit){
        List<Cuenta> lstCuentas;
        em = getEntityManager();
        String queryString = "SELECT cuenta FROM Cuenta cuenta "
                + "WHERE cuenta.cuit = :cuit";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit);
        lstCuentas = q.getResultList();
        return !lstCuentas.isEmpty();
    }
    
    /**
     * Método para obtener todas las revisiones de la entidad
     * @param cuit Long Cuit del Titular de la Cuenta de la que se quieren ver sus revisiones
     * @return List<Cuenta> listado de las revisiones de la cuenta cuyo cuit se remitió
     */
    public List<Cuenta> findRevisions(Long cuit){  
        List<Cuenta> lstCuentas = new ArrayList<>();
        Cuenta cuenta = this.getExistente(cuit);
        if(cuenta != null){
            Long id = this.getExistente(cuit).getId();
            AuditReader reader = AuditReaderFactory.get(getEntityManager());
            List<Number> revisions = reader.getRevisions(Cuenta.class, id);
            for (Number n : revisions) {
                Cuenta cli = reader.find(Cuenta.class, id, n);
                cli.setFechaRevision(reader.getRevisionDate(n));
                Hibernate.initialize(cli.getDeposito());
                Hibernate.initialize(cli.getProductos());
                lstCuentas.add(cli);
            }
        }
        return lstCuentas;
    }     
}
