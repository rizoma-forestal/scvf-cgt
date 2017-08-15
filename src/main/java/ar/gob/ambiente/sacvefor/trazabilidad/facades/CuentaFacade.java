
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

    @PersistenceContext(unitName = "sacvefor-gestionTrazabilidadPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CuentaFacade() {
        super(Cuenta.class);
    }
    
    /**
     * Metodo para validar una Cuenta existente según su cuit
     * @param cuit
     * @return 
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
     * @return 
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
     * @param param : Cadena que deberá contener el nombr completo o razón social del Titular
     * @return 
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
     * @param cuit : Cuit a buscar
     * @return 
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
     * Método que retorna verdadero si hay una cuenta para el CUIT recibido
     * o falso si no la hay.
     * @param cuit
     * @return 
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
     * @param cuit : Cuit del Titular de la Cuenta de la que se quieren ver sus revisiones
     * @return 
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
