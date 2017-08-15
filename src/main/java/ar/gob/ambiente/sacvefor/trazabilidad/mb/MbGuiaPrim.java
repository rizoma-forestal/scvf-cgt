
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

    private Guia guiaSelected;
    private List<Guia> lstGuias;
    private List<Guia> lstGuiaFilters;
    private MbSesion sesion;
    private Usuario usLogueado;
    private boolean view;
    private boolean iniciado;
    static final Logger LOG = Logger.getLogger(MbGuiaPrim.class);
    
    @EJB
    private GuiaFacade guiaFacade;
    
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
    
    public void prepareView(){
        view = true;
    }
    
    public void prepareList(){
        view = false;
        guiaSelected = null;
    }
}
