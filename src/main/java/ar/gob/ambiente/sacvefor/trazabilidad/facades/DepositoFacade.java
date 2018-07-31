
package ar.gob.ambiente.sacvefor.trazabilidad.facades;

import ar.gob.ambiente.sacvefor.trazabilidad.entities.Deposito;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Método de acceso a datos de los Dpósitos
 * @author rincostante
 */
@Stateless
public class DepositoFacade extends AbstractFacade<Deposito> {

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
    public DepositoFacade() {
        super(Deposito.class);
    }
    
    /**
     * Método para obtener un Depósito para una Cuenta determinada según el CUIT de su titular
     * @param cuit Long cuit del titular de la cuenta
     * @return Deposito depósito perteneciente a la cuenta del titular cuyo cuit se remitió
     */
    public Deposito getExistente(Long cuit) {
        List<Deposito> lstDepositos;
        em = getEntityManager();
        
        String queryString = "SELECT dep FROM Deposito dep "
                + "WHERE dep.cuit = :cuit";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit);
        lstDepositos = q.getResultList();
        if(lstDepositos.isEmpty()){
            return null;
        }else{
            return lstDepositos.get(0);
        }
    }
}
