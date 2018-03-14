
package ar.gob.ambiente.sacvefor.trazabilidad.facades;

import ar.gob.ambiente.sacvefor.trazabilidad.entities.Producto;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.ProductoClase;
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
 * Acceso a datos de los Productos que puedan ser generados por la Entidad transformadora
 * @author rincostante
 */
@Stateless
public class ProductoFacade extends AbstractFacade<Producto> {

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
    public ProductoFacade() {
        super(Producto.class);
    }
    
    /**
     * Método para validar la existencia de un Producto según su Clase y el id de la Especie en la API-TAX
     * @param clase ProductoClase Clase del producto cuya existencia se va a validar junto con la Especie
     * @param idEspecieTax Long Identificación de la Especie en el servicio de Taxonomía API-TAX
     * @return Producto producto existente
     */
    public Producto getExistente(ProductoClase clase, Long idEspecieTax) {
        List<Producto> lstProductos;
        em = getEntityManager();
        
        String queryString = "SELECT prod FROM Producto prod "
                + "WHERE prod.clase = :clase "
                + "AND prod.idEspecieTax = :idEspecieTax";
        Query q = em.createQuery(queryString)
                .setParameter("clase", clase)
                .setParameter("idEspecieTax", idEspecieTax);
        lstProductos = q.getResultList();
        if(lstProductos.isEmpty()){
            return null;
        }else{
            return lstProductos.get(0);
        }
    }         
    
    /**
     * Método sobreescrito que lista los Productos generable por el nombre vulgar de la Especie
     * @return List<Producto> listado de los productos registrados ordenados por el nombre vulgar de la especie
     */
    @Override
    public List<Producto> findAll(){
        em = getEntityManager();
        String queryString = "SELECT prod FROM Producto prod "
                + "ORDER BY prod.nombreVulgar";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }      
    
    /**
     * Mátodo que solo devuelve los Productos habilitados ordenados por Nombre vulgar
     * Para poblar combos de selección.
     * @return List<Producto> listado de los productos registrados habilitados ordenados por el nombre vulgar de la especie
     */
    public List<Producto> getHabilitados(){
        em = getEntityManager();
        String queryString = "SELECT prod FROM Producto prod "
                + "WHERE prod.habilitado = true "
                + "ORDER BY prod.nombreVulgar";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    } 
    
    /**
     * Método que devuelve todos los productos para de una misma Especie
     * @param idEspecieTax Long Id de la Especie a buscar
     * @return List<Producto> listado de los productos existente de la especie remitida
     */
    public List<Producto> getByEspecieLocal(Long idEspecieTax){
        em = getEntityManager();
        String queryString = "SELECT prod FROM Producto prod "
                + "WHERE prod.habilitado = true "
                + "AND prod.idEspecieTax = :idEspecieTax "
                + "ORDER BY prod.nombreVulgar";
        Query q = em.createQuery(queryString)
                .setParameter("idEspecieTax", idEspecieTax);
        return q.getResultList();
    }    
    
    /**
     * Método para obtener todas las revisiones de la entidad
     * @param clase ProductoClase Clase que junto a la Especie definen el Producto cuyas revisiones se busca
     * @param idEspecieTax Long Identificación de la Especie cuyo Productos se buscan
     * @return List<Producto> listado de las revisiones del producto remitido
     */
    public List<Producto> findRevisions(ProductoClase clase, Long idEspecieTax){  
        List<Producto> lstProductos = new ArrayList<>();
        Producto prod = this.getExistente(clase, idEspecieTax);
        if(prod != null){
            Long id = this.getExistente(clase, idEspecieTax).getId();
            AuditReader reader = AuditReaderFactory.get(getEntityManager());
            List<Number> revisions = reader.getRevisions(Producto.class, id);
            for (Number n : revisions) {
                Producto cli = reader.find(Producto.class, id, n);
                cli.setFechaRevision(reader.getRevisionDate(n));
                Hibernate.initialize(cli.getClase());
                Hibernate.initialize(cli.getSupProductos());
                lstProductos.add(cli);
            }
        }
        return lstProductos;
    }       
}
