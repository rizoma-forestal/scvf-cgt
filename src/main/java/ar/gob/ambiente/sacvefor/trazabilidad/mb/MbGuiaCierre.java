
package ar.gob.ambiente.sacvefor.trazabilidad.mb;

import ar.gob.ambiente.sacvefor.servicios.cgl.ItemProductivo;
import ar.gob.ambiente.sacvefor.trazabilidad.cgl.client.EstadoGuiaLocalClient;
import ar.gob.ambiente.sacvefor.trazabilidad.cgl.client.GuiaLocalClient;
import ar.gob.ambiente.sacvefor.trazabilidad.ctrl.client.GuiaCtrlClient;
import ar.gob.ambiente.sacvefor.trazabilidad.ctrl.client.ParamCtrlClient;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.ComponenteLocal;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.Cuenta;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.EstadoGuia;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.Guia;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.Item;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.Parametrica;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.TipoGuia;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.TipoParam;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.Usuario;
import ar.gob.ambiente.sacvefor.trazabilidad.facades.ComponenteLocalFacade;
import ar.gob.ambiente.sacvefor.trazabilidad.facades.CuentaFacade;
import ar.gob.ambiente.sacvefor.trazabilidad.facades.EstadoGuiaFacade;
import ar.gob.ambiente.sacvefor.trazabilidad.facades.GuiaFacade;
import ar.gob.ambiente.sacvefor.trazabilidad.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.trazabilidad.facades.TipoGuiaFacade;
import ar.gob.ambiente.sacvefor.trazabilidad.facades.TipoParamFacade;
import ar.gob.ambiente.sacvefor.trazabilidad.util.JsfUtil;
import ar.gob.ambiente.sacvefor.trazabilidad.util.Token;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * Bean para la gestión de la acreditación y cierre de Guías
 * @author rincostante
 */
public class MbGuiaCierre implements Serializable{

    /**
     * Variable privada: ComponenteLocal Entidad que guarda la provincia de la cual se solicitan las guías pendientes de cierre
     */  
    private ComponenteLocal provinciaSelected;
    
    /**
     * Variable privada: List<ComponenteLocal> listado de los componentes locales registrados que compone el combo para su selección
     */
    private List<ComponenteLocal> lstProvincias;
    
    /**
     * Variable privada: List<ar.gob.ambiente.sacvefor.servicios.cgl.ItemProductivo> listado de los ítems correspondientes a la guía seleccionada
     */  
    private List<ar.gob.ambiente.sacvefor.servicios.cgl.ItemProductivo> lstItemLocales;
    
    /**
     * Variable privada: List<ar.gob.ambiente.sacvefor.servicios.cgl.Guia> listado de las guías pendientes de cierre de un componente local
     */  
    private List<ar.gob.ambiente.sacvefor.servicios.cgl.Guia> lstGuiasLocal;
    
    /**
     * Variable privada: List<ar.gob.ambiente.sacvefor.servicios.cgl.Guia> listado para el filtro de las guías
     */  
    private List<ar.gob.ambiente.sacvefor.servicios.cgl.Guia> lstGuiasLocalFilters;
    
    /**
     * Variable privada: ar.gob.ambiente.sacvefor.servicios.cgl.Guia Entidad para guardar la guía seleccionada
     */  
    private ar.gob.ambiente.sacvefor.servicios.cgl.Guia guiaLocalSelected;
    
    /**
     * Variable privada: Logger para escribir en el log del server
     */  
    private static final Logger logger = Logger.getLogger(MbGuiaCierre.class.getName());
    
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
     * Variable privada: Logger para escribir en el log del server
     */  
    static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MbGuiaCierre.class);
    
    // campos para la API CGL
    /**
     * Variable privada: GuiaLocalClient Cliente para la API REST de Componente local
     */
    private GuiaLocalClient guiaLocalClient;
    
    /**
     * Variable privada: EstadoGuiaLocalClient Cliente para la API REST de Componente local
     */
    private EstadoGuiaLocalClient estadoGuiaLocClient;
    
    /**
     * Variable privada: UsuarioApiClient Cliente para la API REST de Gestión local
     */    
    private ar.gob.ambiente.sacvefor.trazabilidad.cgl.client.UsuarioApiClient usuarioClientCgl;
    
    /**
     * Variable privada: Token obtenido al validar el usuario de la API de Gestión local
     */     
    private Token tokenCgl;
    
    /**
     * Variable privada: Token en formato String del obtenido al validar el usuario de la API de Gestión local
     */ 
    private String strTokenCgl;
    
    // campos para la API CCV
    /**
     * Variable privada: GuiaCtrlClient Cliente para la API REST de Control y Verificación
     */
    private GuiaCtrlClient guiaCtrlClient;
    
    /**
     * Variable privada: Cliente para la API de UsuarioAPI de control y verificación
     */
    private ar.gob.ambiente.sacvefor.trazabilidad.ctrl.client.UsuarioApiClient usClientCtrl;
    
    /**
     * Variable privada: token recibido al autenticar el usuario de la API
     */
    private String strTokenCtrl;
    
    /**
     * Variable privada: Objeto para gestionar el token
     */
    private Token tokenCtrl;    
    
    /**
     * Variable privada: ParamCtrlClient Cliente para la API REST de Control y Verificación
     */
    private ParamCtrlClient paramCtrlClient;
    
    // campos y recursos para el envío de correos al usuario
    /**
     * Variable privada: sesión de mail del servidor
     */
    @Resource(mappedName ="java:/mail/ambientePrueba") 
    private Session mailSesion;
    
    /**
     * Variable privada: String mensaje a enviar por correo electrónico
     */  
    private Message mensaje; 
    
    // inyección de recursos
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Componente local
     */  
    @EJB
    private ComponenteLocalFacade componenteLocFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de EstadoGuia
     */  
    @EJB
    private EstadoGuiaFacade estadoFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de TipoGuia
     */  
    @EJB
    private TipoGuiaFacade tipoGuiaFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Cuenta
     */  
    @EJB
    private CuentaFacade cuentaFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de TipoParam
     */  
    @EJB
    private TipoParamFacade tipoParamFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Parametrica
     */  
    @EJB
    private ParametricaFacade paramFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Guia
     */  
    @EJB
    private GuiaFacade guiaFacade;
    
    /**
     * Constructor
     */
    public MbGuiaCierre() {
    }

    public List<ItemProductivo> getLstItemLocales() {
        return lstItemLocales;
    }

    public void setLstItemLocales(List<ItemProductivo> lstItemLocales) {
        this.lstItemLocales = lstItemLocales;
    }

    /**
     * Método que puebla el listado de las Provincias emisoras de guías
     * @return List<ComponenteLocal> listado de los componentes locales de las provincias
     */
    public List<ComponenteLocal> getLstProvincias() {
        lstProvincias = componenteLocFacade.getHabilitados();
        return lstProvincias;
    }

    public void setLstProvincias(List<ComponenteLocal> lstProvincias) {
        this.lstProvincias = lstProvincias;
    }

    public ComponenteLocal getProvinciaSelected() {
        return provinciaSelected;
    }

    public void setProvinciaSelected(ComponenteLocal provinciaSelected) {
        this.provinciaSelected = provinciaSelected;
    }

    public List<ar.gob.ambiente.sacvefor.servicios.cgl.Guia> getLstGuiasLocal() {
        return lstGuiasLocal;
    }

    public void setLstGuiasLocal(List<ar.gob.ambiente.sacvefor.servicios.cgl.Guia> lstGuiasLocal) {
        this.lstGuiasLocal = lstGuiasLocal;
    }

    public List<ar.gob.ambiente.sacvefor.servicios.cgl.Guia> getLstGuiasLocalFilters() {
        return lstGuiasLocalFilters;
    }

    public void setLstGuiasLocalFilters(List<ar.gob.ambiente.sacvefor.servicios.cgl.Guia> lstGuiasLocalFilters) {
        this.lstGuiasLocalFilters = lstGuiasLocalFilters;
    }

    public ar.gob.ambiente.sacvefor.servicios.cgl.Guia getGuiaLocalSelected() {
        return guiaLocalSelected;
    }

    public void setGuiaLocalSelected(ar.gob.ambiente.sacvefor.servicios.cgl.Guia guiaLocalSelected) {
        this.guiaLocalSelected = guiaLocalSelected;
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
        // obtento el usuario
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        sesion = (MbSesion)ctx.getSessionMap().get("mbSesion");
        usLogueado = sesion.getUsuario();
        provinciaSelected = new ComponenteLocal();
    } 
    
    /**
     * Método que prepara el listado de Guías a confirmar
     */
    public void prepareList(){
        view = false;
        lstItemLocales = null;
        guiaLocalSelected = null;
    }
    
    /**
     * Método para limpiar el listado de Guías y redirecciónar a la selección de una nueva Provincia de orígen
     */
    public void limpiarListado(){
        provinciaSelected = new ComponenteLocal();
        lstGuiasLocal = new ArrayList<>();
    }    
    
    /***********************
     * Métodos operativos **
     ***********************/     
    /**
     * Método para cargar las Guías locales a nombre del Usuario, pendientes de aceptación.
     * Las obtinene mediante una consulta a la API del componente local seleccionado oportunamente
     */
    public void cargarGuiasLocales() {
        if(provinciaSelected.getId() != null){
            try{
                // instancio el cliente para la selección de la Guía, obtengo el token si no está seteado o está vencido
                if(tokenCgl == null){
                    getTokenCgl();
                }else try {
                    if(!tokenCgl.isVigente()){
                        getTokenCgl();
                    }
                } catch (IOException ex) {
                    LOG.fatal("Hubo un error obteniendo la vigencia del token de Gestión local. " + ex.getMessage());
                }                
                // instancio el cliente
                guiaLocalClient = new GuiaLocalClient(provinciaSelected.getUrl());
                // obtngo el listado
                GenericType<List<ar.gob.ambiente.sacvefor.servicios.cgl.Guia>> gType = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.cgl.Guia>>() {};
                Response response = guiaLocalClient.findByQuery_JSON(Response.class, null, null, String.valueOf(usLogueado.getLogin()), tokenCgl.getStrToken());
                List<ar.gob.ambiente.sacvefor.servicios.cgl.Guia> lstGuiasTemp = new ArrayList<>();
                lstGuiasTemp = response.readEntity(gType);
                lstGuiasLocal = new ArrayList<>();
                Date hoy = new Date(System.currentTimeMillis());
                for(ar.gob.ambiente.sacvefor.servicios.cgl.Guia g : lstGuiasTemp){
                    if(!g.getFechaVencimiento().before(hoy)){
                        lstGuiasLocal.add(g);
                    }
                }
                guiaLocalClient.close();
            }catch(ClientErrorException ex){
                // muestro un mensaje al usuario
                JsfUtil.addErrorMessage("Hubo un error obteniendo las Guías pendientes de aceptación. " + ex.getMessage());
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo los Departamentos por Provincia "
                        + "del servicio REST de centros poblados", ex.getMessage()});
            }      
        }else{
            JsfUtil.addErrorMessage("Debe seleccionar una Provincia emisora de la Guía.");
        }     
    }    
    
    /**
     * Método que prepara la vista detalle de la Guía seleccionada.
     * Obtiene los items de la Guía mediante una consulta a la API del componente local emisor
     */
    public void prepareView(){
        // instancio el cliente para la selección de la Guía, obtengo el token si no está seteado o está vencido
        if(tokenCgl == null){
            getTokenCgl();
        }else try {
            if(!tokenCgl.isVigente()){
                getTokenCgl();
            }
        } catch (IOException ex) {
            LOG.fatal("Hubo un error obteniendo la vigencia del token de Gestión local. " + ex.getMessage());
        }   
        guiaLocalClient = new GuiaLocalClient(provinciaSelected.getUrl());
        lstItemLocales = new ArrayList<>();
        // obtengo el listado
        GenericType<List<ar.gob.ambiente.sacvefor.servicios.cgl.ItemProductivo>> gType = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.cgl.ItemProductivo>>() {};
        Response response = guiaLocalClient.findItemsByGuia_JSON(Response.class, String.valueOf(guiaLocalSelected.getId()), tokenCgl.getStrToken());
        lstItemLocales = response.readEntity(gType);
        guiaLocalClient.close();
        view = true;
    }
    
    /**
     * Método para aceptar una Guía remitida a la cuenta del usuario.
     * Si la Guía no está persistida en el Componente de Control y Verificación (CCV)
     * Se asume que es de tipo acopio interno (sin transporte, no controlada)
     */
    public void aceptarGuia(){
        try{
            // obtengo el estado de la Guía a persisitir
            EstadoGuia estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("EstCerrada"));
            // obtego el Tipo fuente y el Tipo Actual
            TipoGuia tipoFuente = tipoGuiaFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoLocal"));
            TipoGuia tipoActual = tipoGuiaFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoPrimaira"));
            // obtengo la Cuenta del Usuario
            Cuenta cuenta = cuentaFacade.getExistente(usLogueado.getLogin());
            // seteo la fecha
            Date fechaAlta = new Date(System.currentTimeMillis());
            Guia guia = new Guia();
            // genero el código de la Guía
            guia.setCodigo(setCodigoGuia());
            // obtengo los tipos de items
            Parametrica tipoItemActual = obtenerParametro(ResourceBundle.getBundle("/Config").getString("TipoItem"), ResourceBundle.getBundle("/Config").getString("TipoItemPrim"));
            Parametrica tipoItemOrigen = obtenerParametro(ResourceBundle.getBundle("/Config").getString("TipoItem"), ResourceBundle.getBundle("/Config").getString("TipoItemGestLocal"));
            // seteo los items
            List<Item> lstItems = new ArrayList<>();
            for(ar.gob.ambiente.sacvefor.servicios.cgl.ItemProductivo it : lstItemLocales){
                // gero un Item por cada uno de la API
                Item item = new Item();
                item.setClase(it.getClase());
                item.setCodigoOrigen(it.getCodigoProducto());
                item.setFechaAlta(fechaAlta);
                item.setHabilitado(true);
                item.setIdEspecieTax(it.getIdEspecieTax());
                item.setItemOrigen(it.getId());
                item.setKilosXUnidad(it.getKilosXUnidad());
                item.setNombreCientifico(it.getNombreCientifico());
                item.setNombreVulgar(it.getNombreVulgar());
                item.setSaldo(it.getTotal());
                item.setSaldoKg(it.getSaldoKg());
                item.setTipoActual(tipoItemActual);
                item.setTipoOrigen(tipoItemOrigen);
                item.setTotal(it.getTotal());
                item.setTotalKg(it.getTotalKg());
                item.setUnidad(it.getUnidad());
                item.setUsuario(usLogueado);
                lstItems.add(item);
            }
            guia.setItems(lstItems);
            // seteo el resto de los datos
            guia.setDestino(cuenta);
            guia.setEstado(estado);
            guia.setFechaAlta(fechaAlta);
            guia.setFechaCierre(fechaAlta);
            guia.setFechaEmisionGuia(guiaLocalSelected.getFechaEmisionGuia());
            guia.setFechaVencimiento(guiaLocalSelected.getFechaVencimiento());
            guia.setJurOrigen(provinciaSelected.getProvincia());
            guia.setNumFuente(guiaLocalSelected.getCodigo());
            guia.setTipo(tipoActual);
            guia.setTipoFuente(tipoFuente);
            guia.setUsuario(usLogueado);
            // persisto
            guiaFacade.create(guia);

            // actualizo la Guía de Producción local
            List<ar.gob.ambiente.sacvefor.servicios.cgl.EstadoGuia> lstEstados;
            // instancio el cliente para la selección del estado de Guía, obtengo el token si no está seteado o está vencido
            if(tokenCgl == null){
                getTokenCgl();
            }else try {
                if(!tokenCgl.isVigente()){
                    getTokenCgl();
                }
            } catch (IOException ex) {
                LOG.fatal("Hubo un error obteniendo la vigencia del token de Gestión local. " + ex.getMessage());
            }   
            estadoGuiaLocClient = new EstadoGuiaLocalClient(provinciaSelected.getUrl());
            // obtengo el estado de cerrada 
            GenericType<List<ar.gob.ambiente.sacvefor.servicios.cgl.EstadoGuia>> gTypeEstado = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.cgl.EstadoGuia>>() {};
            Response responseCgl = estadoGuiaLocClient.findByQuery_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("EstCerrada"), tokenCgl.getStrToken());
            lstEstados = responseCgl.readEntity(gTypeEstado);
            estadoGuiaLocClient.close();
            if(!lstEstados.isEmpty()){
                guiaLocalSelected.setEstado(lstEstados.get(0));
                // seteo la fecha de cierre
                guiaLocalSelected.setFechaCierre(fechaAlta);
                // actualizo
                // instancio el cliente para la selección del estado de Guía, obtengo el token si no está seteado o está vencido
                if(tokenCgl == null){
                    getTokenCgl();
                }else try {
                    if(!tokenCgl.isVigente()){
                        getTokenCgl();
                    }
                } catch (IOException ex) {
                    LOG.fatal("Hubo un error obteniendo la vigencia del token de Gestión local. " + ex.getMessage());
                }   
                guiaLocalClient = new GuiaLocalClient(provinciaSelected.getUrl());
                responseCgl = guiaLocalClient.edit_JSON(guiaLocalSelected, String.valueOf(guiaLocalSelected.getId()), tokenCgl.getStrToken());
                guiaLocalClient.close();
                // respondo según el mensaje recibido
                if(responseCgl.getStatus() == 200){
                    // si el origen tiene configurado correo, comunico el cierre de la Guía
                    if(guiaLocalSelected.getOrigen().getEmail() != null){
                        if(!enviarCorreo()){
                            JsfUtil.addErrorMessage("No se pudo enviar el mensaje de confirmación al Titular de la Guía emitida.");
                        }
                    }
                    // obtengo el token si no está seteado o está vencido
                    if(tokenCtrl == null){
                        getTokenCtrl();
                    }else try {
                        if(!tokenCtrl.isVigente()){
                            getTokenCtrl();
                        }
                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token", ex.getMessage()});
                    }
                    // busco la Guía en el CCV
                    guiaCtrlClient = new GuiaCtrlClient();
                    List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia> lstGuias;
                    GenericType<List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia>> gTypeG = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia>>() {};
                    Response response = guiaCtrlClient.findByQuery_JSON(Response.class, guiaLocalSelected.getCodigo(), null, null, tokenCtrl.getStrToken());
                    lstGuias = response.readEntity(gTypeG);
                    if(lstGuias.get(0) != null){
                        // si está registrada en el CCV, actualizo su estado
                        Long idGuiaCtrl = lstGuias.get(0).getId();   
                        // instancio la Guía a editar en el CCV
                        ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia guiaCtrol = new ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia();
                        // obtengo el estado "CERRADA" del CCV
                        List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Parametrica> lstParmEstados;
                        paramCtrlClient = new ParamCtrlClient();
                        GenericType<List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Parametrica>> gTypeParam = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Parametrica>>() {};
                        responseCgl = paramCtrlClient.findByQuery_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("CtrlTipoParamEstGuia"), ResourceBundle.getBundle("/Config").getString("CtrlGuiaCerrada"), tokenCtrl.getStrToken());
                        lstParmEstados = responseCgl.readEntity(gTypeParam);
                        // solo continúo si encontré el Estado correspondiente
                        if(!lstParmEstados.isEmpty()){
                            // seteo la Guía solo con los valores que necesito para editarla
                            guiaCtrol.setId(idGuiaCtrl);
                            guiaCtrol.setEstado(lstParmEstados.get(0));
                            
                            // obtengo el token si no está seteado o está vencido
                            if(tokenCtrl == null){
                                getTokenCtrl();
                            }else try {
                                if(!tokenCtrl.isVigente()){
                                    getTokenCtrl();
                                }
                            } catch (IOException ex) {
                                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token", ex.getMessage()});
                            }
                            responseCgl = guiaCtrlClient.edit_JSON(guiaCtrol, String.valueOf(guiaCtrol.getId()), tokenCtrl.getStrToken());
                            if(responseCgl.getStatus() == 200){
                                // se completaron todas las operaciones
                                JsfUtil.addSuccessMessage("La Guía se cerró correctamente y se actualizaron los Componentes de Gestión local y Cotrol y Verificación.");
                            }else{
                                // se cerró en CGL pero no actualizó en CCV
                                JsfUtil.addErrorMessage("La Guía se cerró correctamente y se actualizó el Componente de Gestión local, pero hubo un error actualizando el Componente de Control y Verificación. Deberá contactar al Administrador.");
                            }
                        }else{
                            // se cerró en CGL pero no hubo estado "CERRADA", por lo tanto, no actualizó en CCV
                            JsfUtil.addErrorMessage("La Guía se cerró correctamente y se actualizó el Componente de Gestión local, pero no se obtuvo el estado 'CERRADA' de la Guía en el Componente de Control y Verificación. Deberá contactar al Administrador.");
                        }
                    }
                    guiaCtrlClient.close();
                    
                    // si no se encontró la Guía en el CCV, se asume que es de tipo acopio interno y limpio todo
                    prepareList();
                    // recargo las Guías pendientes
                    cargarGuiasLocales();                    
                    
                }else{
                    // no actualizó la Guía en CGL
                    JsfUtil.addErrorMessage("La Guía se cerró correctamente pero no se pudo actualizar el Componente de Gestión local. Deberá contactar al Administrador.");
                }
            }else{
                // no obtuvo el estado, no actualizo en ninguno de los componentes, comunico el error
                JsfUtil.addErrorMessage("La Guía se cerró pero no se pudo obtener el Estado de la Guía correspondiente para su cierre en el Sistema de Gestión local ni se actualizó el Componente de Control y Verificación. Deberá contactar al Administrador.");
            }
        }catch(ClientErrorException ex){
            JsfUtil.addErrorMessage("Hubo un error aceptando la Guía. " + ex.getMessage());
        }
    }    

    
    /*********************
     * Métodos privados **
     *********************/
    
    /**
     * Método privado que crea el código de la Guía.
     * Consumido por aceptarGuia()
     * @return String código de la guía
     */
    private String setCodigoGuia() {
        String codigo;
        Calendar fecha = Calendar.getInstance();
        String sAnio = String.valueOf(fecha.get(Calendar.YEAR));
        int id = guiaFacade.getUltimoId();
        
        // genero el código según sea o no la primera
        if(id == 0){
            // si inicia
            codigo = "PRI-00001";
        }else{
            String sId = String.valueOf(id + 1);
            int ceros = 5 - sId.length();
            // agrego los ceros a la izquierda
            int j = ceros;
            codigo = "PRI-";
            while(j > 0){
                codigo = codigo + "0";
                j -= 1;
            }
            codigo = codigo + sId;
        }
        return codigo + "-" + sAnio;
    }
    
    /**
     * Método privado para obtener una Paramétrica según su nombre y nombre del Tipo.
     * Consumido por varios métodos públicos.
     * @param nomTipo String nombre del Tipo de Paramétrica
     * @param nomParam String nombre de la Paramétrica
     * @return Parametrica paramétrica solicitada
     */
    private Parametrica obtenerParametro(String nomTipo, String nomParam) {
        TipoParam tipo = tipoParamFacade.getExistente(nomTipo);
        return paramFacade.getExistente(nomParam, tipo);
    }    

    /**
     * Método privado para enviar un correo al Origen de la Guía comunicando su cierre.
     * Consumido por aceptarGuia()
     * @return boolean true o false según el correo se haya emitido o no
     */    
    private boolean enviarCorreo() {
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        boolean result;
        String bodyMessage;
        mensaje = new MimeMessage(mailSesion);
        bodyMessage = "<p>Estimado/a</p> "
                + "<p>La Guía " + guiaLocalSelected.getCodigo() + " emitida el " + formateador.format(guiaLocalSelected.getFechaEmisionGuia()) + " "
                + "remitida a " + guiaLocalSelected.getDestino().getNombreCompleto() + " ha sido aceptada por el destinatario "
                + "con fecha " + formateador.format(guiaLocalSelected.getFechaCierre()) + ".</p>"
                + "<p>Por favor, no responda este correo.</p> "
                + "<p>Saludos cordiales</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("EntidadBosques") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("EntidadBosqesSup_1") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("EntidadBosqesSup_2") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("EntidadBosqesSup_3") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("EntidadBosqesSup_4") + "<br/> "
                + "Presidencia de la Nación<br/> "
                + ResourceBundle.getBundle("/Config").getString("DomicilioBosques") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("TelBosques") + "<br /> "
                + "Correo electrónico: <a href=\"mailto:" + ResourceBundle.getBundle("/Config").getString("emailBosques") + "\">" + ResourceBundle.getBundle("/Config").getString("emailBosques") + "</a></p>";     
        
        try{
            mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(guiaLocalSelected.getOrigen().getEmail()));
            mensaje.setSubject("SACVeFor - Trazabilidad - Confirmación de cierre de Guía" );
            mensaje.setContent(bodyMessage, "text/html; charset=utf-8");
            
            Date timeStamp = new Date();
            mensaje.setSentDate(timeStamp);
            
            Transport.send(mensaje);
            
            result = true;
            
        }catch(MessagingException ex){
            result = false;
            LOG.fatal("Hubo un error enviando el correo de notificación de cierre de Guía al Titular. " + ex.getMessage());
        }
        
        return result;
    }
    
    /**
     * Método privado que obtiene y setea el tokenTraz para autentificarse ante la API rest de control y verificación.
     * Crea el campo de tipo Token con la clave recibida y el momento de la obtención.
     * Utilizado en aceptarGuia()
     */    
    private void getTokenCtrl(){
        try{
            usClientCtrl = new ar.gob.ambiente.sacvefor.trazabilidad.ctrl.client.UsuarioApiClient();
            Response responseUs = usClientCtrl.authenticateUser_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("UsRestCtrl"));
            MultivaluedMap<String, Object> headers = responseUs.getHeaders();
            List<Object> lstHeaders = headers.get("Authorization");
            strTokenCtrl = (String)lstHeaders.get(0); 
            tokenCtrl = new Token(strTokenCtrl, System.currentTimeMillis());
            usClientCtrl.close();
        }catch(ClientErrorException ex){
            System.out.println("Hubo un error obteniendo el token: " + ex.getMessage());
        }
    }     
    
    /**
     * Método privado que obtiene y setea el token para autentificarse ante la API rest de Gestión local para la provincia que corresponda
     * Crea el campo de tipo Token con la clave recibida y el momento de la obtención.
     * Utilizado por cargarGuiasLocales(), prepareView() y aceptarGuia()
     */
    private void getTokenCgl(){
        try{
            usuarioClientCgl = new ar.gob.ambiente.sacvefor.trazabilidad.cgl.client.UsuarioApiClient(provinciaSelected.getUrl());
            Response responseUs = usuarioClientCgl.authenticateUser_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("UsRestCgl"));
            MultivaluedMap<String, Object> headers = responseUs.getHeaders();
            List<Object> lstHeaders = headers.get("Authorization");
            strTokenCgl = (String)lstHeaders.get(0); 
            tokenCgl = new Token(strTokenCgl, System.currentTimeMillis());
            usuarioClientCgl.close();
        }catch(ClientErrorException ex){
            System.out.println("Hubo un error obteniendo el token para la API Gestión local: " + ex.getMessage());
        }
    }    
}
