
package ar.gob.ambiente.sacvefor.trazabilidad.facades;

import ar.gob.ambiente.sacvefor.trazabilidad.entities.Guia;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.Item;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.Parametrica;
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
 * Acceso a datos para la entidad Item
 * @author rincostante
 */
@Stateless
public class ItemFacade extends AbstractFacade<Item> {

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
    public ItemFacade() {
        super(Item.class);
    }
    
    /**
     * Método que devuelve los items según la Guía enviada, todos los estados
     * @param guia Guia guía cuyos ítems se solicitan
     * @return List<Item> listado de los ítems solicitados
     */
    public List<Item> getByGuia(Guia guia){
        em = getEntityManager();
        String queryString = "SELECT item FROM Itemitem "
                + "WHERE item.guia = :guia";
        Query q = em.createQuery(queryString)
                .setParameter("guia", guia);
        return q.getResultList();
    }      

    /**
     * Método que devuelve los items habiliados según la Guía enviada
     * @param guia Guia guía cuyos ítems habilitados se solicitan
     * @return List<Item> listado de los ítems solicitados
     */
    public List<Item> getByGuiaHabilitados(Guia guia){
        em = getEntityManager();
        String queryString = "SELECT item FROM Item item "
                + "WHERE item.guia = :guia "
                + "AND item.habilitado = true";
        Query q = em.createQuery(queryString)
                .setParameter("guia", guia);
        return q.getResultList();
    }    
    
    /**
     * Método que devuelve los Items habiliados según el Tipo
     * @param tipo Parametrica tipo de los ítems solicitados
     * @return List<Item> listado de los ítems solicitados
     */
    public List<Item> getByTipoHabilitados(Parametrica tipo){
        em = getEntityManager();
        String queryString = "SELECT item FROM Item item "
                + "WHERE item.tipoActual = :tipo "
                + "AND item.habilitado = true";
        Query q = em.createQuery(queryString)
                .setParameter("tipo", tipo);
        return q.getResultList();
    }
    
    /**
     * Método para obtener todas las revisiones de la entidad
     * @param idItem Long id del Item
     * @return List<Item> listado de las revisiones del ítem remitido
     */
    public List<Item> findRevisions(Long idItem){  
        List<Item> lstItems = new ArrayList<>();
        Item item = this.find(idItem);
        if(item != null){
            Long id = this.find(idItem).getId();
            AuditReader reader = AuditReaderFactory.get(getEntityManager());
            List<Number> revisions = reader.getRevisions(Item.class, id);
            for (Number n : revisions) {
                Item cli = reader.find(Item.class, id, n);
                cli.setFechaRevision(reader.getRevisionDate(n));
                Hibernate.initialize(cli.getTipoActual());
                Hibernate.initialize(cli.getTipoOrigen());
                Hibernate.initialize(cli.getDeposito());
                lstItems.add(cli);
            }
        }
        return lstItems;
    }    
}
