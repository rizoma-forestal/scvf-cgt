
package ar.gob.ambiente.sacvefor.trazabilidad.facades;

import ar.gob.ambiente.sacvefor.trazabilidad.entities.Cuenta;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.EstadoGuia;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.Guia;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.hibernate.Hibernate;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

/**
 * Acceso a datos para la entidad Guía
 * @author rincostante
 */
@Stateless
public class GuiaFacade extends AbstractFacade<Guia> {

    @PersistenceContext(unitName = "sacvefor-gestionTrazabilidadPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GuiaFacade() {
        super(Guia.class);
    }
    
    /**
     * Metodo para validar una Guía existente según el codigo
     * @param codigo
     * @return 
     */
    public Guia getExistente(String codigo) {
        List<Guia> lstGuias;
        em = getEntityManager();
        
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.codigo = :codigo";
        Query q = em.createQuery(queryString)
                .setParameter("codigo", codigo);
        lstGuias = q.getResultList();
        if(lstGuias.isEmpty()){
            return null;
        }else{
            return lstGuias.get(0);
        }
    }   

    /**
     * Método para obetener las Guías cuyo origen es el enviado como parámetro
     * @param origen 
     * @return 
     */
    public List<Guia> getByOrigen(Cuenta origen){
        em = getEntityManager();
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.origen = :origen";
        Query q = em.createQuery(queryString)
                .setParameter("origen", origen);
        return q.getResultList();
    }     
    
    /**
     * Método para obetener las Guías cuyo destino es el enviado como parámetro
     * @param destino
     * @return 
     */
    public List<Guia> getByDestino(Cuenta destino){
        em = getEntityManager();
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.destino = :destino";
        Query q = em.createQuery(queryString)
                .setParameter("destino", destino);
        return q.getResultList();
    }   
    
    /**
     * Método para obtener todas las Guías acreditadas a una Cuenta según su CUIT
     * @param cuit
     * @return 
     */
    public List<Guia> getAcreditadas(Long cuit){
        String estado = ResourceBundle.getBundle("/Config").getString("EstCerrada");
        em = getEntityManager();
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.destino.cuit = :cuit "
                + "AND guia.estado.nombre = :estado";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit)
                .setParameter("estado", estado);
        return q.getResultList();
    }
    
    /**
     * Método para obtener las Guías según el codigo de origen del Producto
     * @param codigoOrigen : Código de origen del producto a buscar entre los Items de las Guía
     * @return 
     */
    public List<Guia> getByIdProd(String codigoOrigen){
        em = getEntityManager();
        String queryString = "SELECT guia FROM Guia guia "
                + "INNER JOIN guia.items item "
                + "WHERE item.codigoOrigen = :codigoOrigen";
        Query q = em.createQuery(queryString)
                .setParameter("codigoOrigen", codigoOrigen);
        return q.getResultList();
    }   
    
    /**
     * Método para obtener las Guías según su estado
     * @param estado : estado en el cual deberán estar las Guías retornadas
     * @return 
     */
    public List<Guia> getByEstado(EstadoGuia estado){
        em = getEntityManager();
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.estado = :estado";
        Query q = em.createQuery(queryString)
                .setParameter("estado", estado);
        return q.getResultList();
    }    
    
    /**
     * Método que devuelve las Guías vinculadas a un vehículo determinado
     * identificado por su matrícula
     * @param matricula : matrícula del Vehículo cuyas Guías se quiere obtener.
     * @return 
     */
    public List<Guia> getByVehiculo(String matricula){
        em = getEntityManager();
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.transporte.vehiculo.matricula = :matricula";
        Query q = em.createQuery(queryString)
                .setParameter("matricula", matricula);
        return q.getResultList();
    }    
    
    /**
     * Devuelve el último id registrado para una Guía
     * @return 
     */
    public int getUltimoId(){    
        em = getEntityManager();    
        String queryString = "SELECT MAX(id) FROM guia"; 
        Query q = em.createNativeQuery(queryString);
        BigInteger result = (BigInteger)q.getSingleResult();
        if(result != null){
            return result.intValue();
        }else{
            return 0;
        }
    }

    /**
     * Método que devuelve las Guías vinculadas a un número de fuente determinado.
     * @param numFuente : Código de la Guía que aportó los Items para la generación de las Guías buscadas
     * @return 
     */
    public List<Guia> findByNumFuente(String numFuente){
        em = getEntityManager();
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.numFuente = :numFuente";
        Query q = em.createQuery(queryString)
                .setParameter("numFuente", numFuente);
        return q.getResultList();
    }  
    
    /**
     * Metodo que devuelve las Guías en condiciones de ser fuentes de productos para una Cuenta a partir del CUIT
     * @param cuit : cuit de la Cuenta cuyas Guías se busca
     * @return 
     */
    public List<Guia> getFuenteByTitular(Long cuit){
        em = getEntityManager();
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.destino.cuit = :cuit "
                + "AND guia.estado.habilitaFuenteProductos = true";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit);
        return q.getResultList();
    }   
    
    /**
     * Método para obtener todas las revisiones de la entidad
     * @param codigo : Código único identificatorio de la Guía
     * @return 
     */
    public List<Guia> findRevisions(String codigo){  
        List<Guia> lstGuias = new ArrayList<>();
        Guia guia = this.getExistente(codigo);
        if(guia != null){
            Long id = this.getExistente(codigo).getId();
            AuditReader reader = AuditReaderFactory.get(getEntityManager());
            List<Number> revisions = reader.getRevisions(Guia.class, id);
            for (Number n : revisions) {
                Guia cli = reader.find(Guia.class, id, n);
                cli.setFechaRevision(reader.getRevisionDate(n));
                Hibernate.initialize(cli.getTipo());
                Hibernate.initialize(cli.getEstado());
                Hibernate.initialize(cli.getItems());
                Hibernate.initialize(cli.getOrigen());
                Hibernate.initialize(cli.getDestino());
                Hibernate.initialize(cli.getTransporte());
                lstGuias.add(cli);
            }
        }
        return lstGuias;
    }      
}
