
package ar.gob.ambiente.sacvefor.trazabilidad.facades;

import ar.gob.ambiente.sacvefor.trazabilidad.entities.Vehiculo;
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
 * Acceso a datos para la entidad Vehículo
 * @author rincostante
 */
@Stateless
public class VehiculoFacade extends AbstractFacade<Vehiculo> {
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
    public VehiculoFacade() {
        super(Vehiculo.class);
    }
    
    /**
     * Metodo para validar un Vehículo existente según su matrícula
     * @param matricula String matrícula del vehículo
     * @return Vehiculo vehículo correspondiente a la matrícula remitida
     */
    public Vehiculo getExistente(String matricula) {
        List<Vehiculo> lstPersonas;
        em = getEntityManager();
        
        String queryString = "SELECT veh FROM Vehiculo veh "
                + "WHERE veh.matricula = :matricula";
        Query q = em.createQuery(queryString)
                .setParameter("matricula", matricula);
        lstPersonas = q.getResultList();
        if(lstPersonas.isEmpty()){
            return null;
        }else{
            return lstPersonas.get(0);
        }
    } 

    /**
     * Método sobreescrito que lista las Vehiculos ordenados por su Maraca
     * @return List<Vehiculo> listado de los vehículos ordenados según su marca
     */
    @Override
    public List<Vehiculo> findAll(){
        em = getEntityManager();
        String queryString = "SELECT veh FROM Vehiculo veh "
                + "ORDER BY veh.marca";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }     

    /**
     * Método que devuelve un listado de Vehículos cuya marca contenga la cadena ingresada como parámetro
     * @param param String cadena que la marca del vehículo debe contener
     * @return List<Vehiculo> listado de los vehículos habilitados correspondientes a la marca que incluya en su nombre la cadena ingresada
     */
    public List<Vehiculo> findByMarca(String param){
        em = getEntityManager();
        String queryString = "SELECT veh FROM Vehiculo veh "
                + "WHERE LOWER(veh.marca.nombre) LIKE :param "
                + "AND veh.habilitado = true"
                + "ORDER BY veh.marca";
        Query q = em.createQuery(queryString)
                .setParameter("param", "%" + param + "%".toLowerCase());
        return q.getResultList();
    }

    /**
     * Método que devuelve los Vehículos habilitados
     * Sin distinción del tipo.
     * @return List<Vehiculo> listado de los vehículos habilitados
     */
    public List<Vehiculo> getHabilitadas(){
        List<Vehiculo> lstVehiculos;
        em = getEntityManager();
        
        String queryString = "SELECT veh FROM Vehiculo veh "
                + "WHERE veh.habilitado = true "
                + "ORDER BY veh.marca";
        Query q = em.createQuery(queryString);
        lstVehiculos = q.getResultList();
        return lstVehiculos;
    } 

    /**
     * Método para obtener todas las revisiones de la entidad
     * @param matricula String matrícula del vehículo cuyas revisiones se solicita
     * @return List<Vehiculo> listado de las revisiones del vehículo cuya matrícula se remitió
     */
    public List<Vehiculo> findRevisions(String matricula){
        List<Vehiculo> lstClientes = new ArrayList<>();
        Vehiculo veh = this.getExistente(matricula);
        if(veh != null){
            Long id = this.getExistente(matricula).getId();
            AuditReader reader = AuditReaderFactory.get(getEntityManager());
            List<Number> revisions = reader.getRevisions(Vehiculo.class, id);
            for (Number n : revisions) {
                Vehiculo cli = reader.find(Vehiculo.class, id, n);
                cli.setFechaRevision(reader.getRevisionDate(n));
                Hibernate.initialize(cli.getTitular());
                lstClientes.add(cli);
            }
        }
        return lstClientes;
    }        
}
