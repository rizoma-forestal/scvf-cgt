
package ar.gob.ambiente.sacvefor.trazabilidad.mb;

import ar.gob.ambiente.sacvefor.trazabilidad.entities.Guia;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.Usuario;
import ar.gob.ambiente.sacvefor.trazabilidad.facades.GuiaFacade;
import ar.gob.ambiente.sacvefor.trazabilidad.util.JsfUtil;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * Bean para la gestión de las Guías primarias
 * @author rincostante
 */
public class MbGuiaPrim implements Serializable{

    /**
     * Variable privada: Guia Entidad que guarda la Guía seleccionada para su edición o vista.
     */  
    private Guia guiaSelected;
    
    /**
     * Variable privada: List<Guia> listado de las guías primarias acreditadas al usuario
     */
    private List<Guia> lstGuias;
    
    /**
     * Variable privada: List<Guia> listado para el filtrado de las guías primarias acreditadas al usuario
     */
    private List<Guia> lstGuiaFilters;
    
    /**
     * Variable privada: MbSesion para gestionar las variables de sesión del usuario
     */  
    private MbSesion sesion;
    
    /**
     * Variable privada: Usuario de sesión
     */  
    private Usuario usLogueado;
    
    /**
     * Variable privada: indica si el formulario corresponde a una vista detalle de la guía
     */
    private boolean view;
    
    /**
     * Variable privada: indica si el bean está iniciado
     */
    private boolean iniciado;
    
    /**
     * Variable privada: Logger para escribir en el log del server
     */  
    static final Logger LOG = Logger.getLogger(MbGuiaPrim.class);
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Guia
     */  
    @EJB
    private GuiaFacade guiaFacade;
    
    /**
     * Constructor
     */
    public MbGuiaPrim() {
    }

    public Guia getGuiaSelected() {
        return guiaSelected;
    }

    public void setGuiaSelected(Guia guiaSelected) {
        this.guiaSelected = guiaSelected;
    }

    public List<Guia> getLstGuias() {
        return lstGuias;
    }

    public void setLstGuias(List<Guia> lstGuias) {
        this.lstGuias = lstGuias;
    }

    public List<Guia> getLstGuiaFilters() {
        return lstGuiaFilters;
    }

    public void setLstGuiaFilters(List<Guia> lstGuiaFilters) {
        this.lstGuiaFilters = lstGuiaFilters;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }
    
    /******************************
     * Métodos de inicialización **
     ******************************/
    
    /**
     * Método que se ejecuta luego de instanciada la clase e inicializa las entidades a gestionar, 
     * el bean de sesión y el usuario
     */    
    @PostConstruct
    public void init(){
        if(!iniciado){
            // inicializo las propiedades del legger
            BasicConfigurator.configure();
            // obtento el usuario
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            sesion = (MbSesion)ctx.getSessionMap().get("mbSesion");
            usLogueado = sesion.getUsuario();        
            try{
                lstGuias = guiaFacade.getAcreditadas(usLogueado.getLogin());
                LOG.trace("Se ejecutó el logger");
            }catch(Exception ex){
                JsfUtil.addErrorMessage("Hubo un error obteniendo las Guías acreditadas a la Cuenta del Usuario" + ex.getMessage());
                LOG.fatal("Hubo un error obteniendo las Guías acreditadas a la Cuenta del Usuario" + ex.getMessage());
            }
            iniciado = true;
        }
    }
    
    /**
     * Método que prepara la vista detalle de la Guía seleccionada.
     */
    public void prepareView(){
        view = true;
    }
    
    /**
     * Método que prepara el listado de Guías acreditadas
     */
    public void prepareList(){
        view = false;
        guiaSelected = null;
    }
}
