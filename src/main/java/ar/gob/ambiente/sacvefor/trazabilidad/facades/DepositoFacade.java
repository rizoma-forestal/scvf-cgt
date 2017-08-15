
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

    @PersistenceContext(unitName = "sacvefor-gestionTrazabilidadPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DepositoFacade() {
        super(Deposito.class);
    }
    
    /**
     * Método para obtener un Depósito para una Cuenta determinada según su CUIT
     * @param cuit
     * @return 
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
