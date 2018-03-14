
package ar.gob.ambiente.sacvefor.trazabilidad.mb;

import ar.gob.ambiente.sacvefor.servicios.rue.Persona;
import ar.gob.ambiente.sacvefor.servicios.territorial.CentroPoblado;
import ar.gob.ambiente.sacvefor.servicios.territorial.Departamento;
import ar.gob.ambiente.sacvefor.servicios.territorial.Provincia;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.Cuenta;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.Usuario;
import ar.gob.ambiente.sacvefor.trazabilidad.facades.CuentaFacade;
import ar.gob.ambiente.sacvefor.trazabilidad.facades.UsuarioFacade;
import ar.gob.ambiente.sacvefor.trazabilidad.rue.client.PersonaClient;
import ar.gob.ambiente.sacvefor.trazabilidad.territ.client.DepartamentoClient;
import ar.gob.ambiente.sacvefor.trazabilidad.territ.client.ProvinciaClient;
import ar.gob.ambiente.sacvefor.trazabilidad.util.CriptPass;
import ar.gob.ambiente.sacvefor.trazabilidad.util.EntidadServicio;
import ar.gob.ambiente.sacvefor.trazabilidad.util.JsfUtil;
import ar.gob.ambiente.sacvefor.trazabilidad.util.Token;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * Bean para la gestión de la sesión del usuario
 * @author rincostante
 */
public class MbSesion {

    /**
     * Variable privada: cuit que actúa como login del usuario
     */  
    private Long cuit;
    
    /**
     * Variable privada: clave ingresada por el usuario
     */      
    private String clave;
    
    /**
     * Variable privada: clave encriptada para su validación
     */  
    private String claveEncript;
    
    /**
     * Variable privada: nueva clave solicitada al usuario en su primera sesión
     */  
    private String newClave;
    
    /**
     * Variable privada: repetición de la nueva clave
     */  
    private String newClave2;
    
    /**
     * Variable privada: Usuario logueado
     */      
    private Usuario usuario;
    
    /**
     * Variable privada: indica si el usuario tiene o no tiene cuenta asociada
     */      
    private boolean sinCuenta;
    
    /**
     * Variable privada: indica si el usuario está o no logeado
     */      
    private boolean logeado = false;
  
    // campos para la gestión de datos de las API
    /**
     * Variable privada: PersonaClient Cliente para la API REST de Entidades (RUE)
     */
    private PersonaClient personaClient;   
    
    /**
     * Variable privada: persona obtenida del RUE
     */
    private Persona personaRue;
    
    /**
     * Variable privada: UsuarioClient Cliente para la API REST de Entidades (RUE)
     */
    private ar.gob.ambiente.sacvefor.trazabilidad.rue.client.UsuarioClient usuarioClientRue;
    
    /**
     * Variable privada: Token obtenido al validar el usuario de la API RUE
     */  
    private Token tokenRue;
    
    /**
     * Variable privada: Token en formato String del obtenido al validar el usuario de la API RUE
     */ 
    private String strTokenRue;
    
    /**
     * Variable privada: ProvinciaClient Cliente para la API REST de Territorial
     */
    private ProvinciaClient provClient;    
    
    /**
     * Variable privada: DepartamentoClient Cliente para la API REST de Territorial
     */
    private DepartamentoClient deptoClient;
    
    /**
     * Variable privada: Logger para escribir en el log del server
     */  
    private static final Logger logger = Logger.getLogger(MbSesion.class.getName());
    
    /**
     * Variable privada: UsuarioClient Cliente para la API REST de Territorial
     */
    private ar.gob.ambiente.sacvefor.trazabilidad.territ.client.UsuarioClient usuarioClientTerr;
    
    /**
     * Variable privada: Token obtenido al validar el usuario de la API Territorial
     */ 
    private Token tokenTerr;
    
    /**
     * Variable privada: Token en formato String del obtenido al validar el usuario de la API Territorial
     */ 
    private String strTokenTerr;   
    

    // Campos para la gestión de las Entidades provenientes de la API Territorial en los combos del formulario.
    /**
     * Variable privada: Listado id y nombre de las provincias para poblar el combo para su selección
     */  
    private List<EntidadServicio> listProvincias;
    
    /**
     * Variable privada: contiene la provincia seleccionada del combo
     */  
    private EntidadServicio provSelected;
    
    /**
     * Variable privada: Listado id y nombre de los departamentos para poblar el combo para su selección
     */  
    private List<EntidadServicio> listDepartamentos;
    
    /**
     * Variable privada: contiene el departamento seleccionada del combo
     */  
    private EntidadServicio deptoSelected;
    
    /**
     * Variable privada: Listado id y nombre de las localidades para poblar el combo para su selección
     */  
    private List<EntidadServicio> listLocalidades;
    
    /**
     * Variable privada: contiene la localidad seleccionada del combo
     */  
    private EntidadServicio localSelected;    
    
    // campos para la notificación al Usuario
    /**
     * Variable privada: sesión de mail del servidor
     */
    @Resource(mappedName ="java:/mail/ambientePrueba")    
    private Session mailSesion;
    
    /**
     * Variable privada: String mensaje a enviar por correo electrónico
     */  
    private Message mensaje;       
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Usuario
     */  
    @EJB
    private UsuarioFacade usuarioFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Cuenta
     */  
    @EJB
    private CuentaFacade cuentaFacade;
    
    /**
     * Constructor
     */
    public MbSesion() {
    }

    public String getNewClave() {
        return newClave;
    }

    public void setNewClave(String newClave) {
        this.newClave = newClave;
    }

    public String getNewClave2() {
        return newClave2;
    }

    public void setNewClave2(String newClave2) {
        this.newClave2 = newClave2;
    }

    public List<EntidadServicio> getListProvincias() {
        if(listProvincias == null) listProvincias = new ArrayList<>();
        return listProvincias;
    }

    public void setListProvincias(List<EntidadServicio> listProvincias) {
        this.listProvincias = listProvincias;
    }

    public EntidadServicio getProvSelected() {
        return provSelected;
    }

    public void setProvSelected(EntidadServicio provSelected) {
        this.provSelected = provSelected;
    }

    public List<EntidadServicio> getListDepartamentos() {
        if(listDepartamentos == null) listDepartamentos = new ArrayList<>();
        return listDepartamentos;
    }

    public void setListDepartamentos(List<EntidadServicio> listDepartamentos) {
        this.listDepartamentos = listDepartamentos;
    }

    public EntidadServicio getDeptoSelected() {
        return deptoSelected;
    }

    public void setDeptoSelected(EntidadServicio deptoSelected) {
        this.deptoSelected = deptoSelected;
    }

    public List<EntidadServicio> getListLocalidades() {
        if(listLocalidades == null) listLocalidades = new ArrayList<>();
        return listLocalidades;
    }

    public void setListLocalidades(List<EntidadServicio> listLocalidades) {
        this.listLocalidades = listLocalidades;
    }

    public EntidadServicio getLocalSelected() {
        return localSelected;
    }

    public void setLocalSelected(EntidadServicio localSelected) {
        this.localSelected = localSelected;
    }

    public Persona getPersonaRue() {
        return personaRue;
    }

    public void setPersonaRue(Persona personaRue) {
        this.personaRue = personaRue;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isSinCuenta() {
        return sinCuenta;
    }

    public void setSinCuenta(boolean sinCuenta) {
        this.sinCuenta = sinCuenta;
    }

    public Long getCuit() {
        return cuit;
    }

    public void setCuit(Long cuit) {
        this.cuit = cuit;
    }

    public String getClaveEncript() {
        return claveEncript;
    }

    public void setClaveEncript(String claveEncript) {
        this.claveEncript = claveEncript;
    }

    public boolean isLogeado() {
        return logeado;
    }

    public void setLogeado(boolean logeado) {
        this.logeado = logeado;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
    
    /******************************
     * Métodos de inicialización **
     ******************************/

    /**
     * Método que se ejecuta luego de instanciada la clase e invoca al método privado para setear la estructura territorial
     */       
    @PostConstruct
    public void init(){
        cargarProvincias();
    }   
    
    /**
     * Método para actualizar el listado de Departamentos al seleccionar una Provincia
     * para la inserción de Personas en el RUP
     */
    public void provinciaChangeListener(){
        localSelected = new EntidadServicio();
        getDepartamentosSrv(provSelected.getId());
    }     

    /**
     * Método para actualizar el listado de Localidades al seleccionar un Departamento
     */    
    public void deptoChangeListener(){
        getLocalidadesSrv(deptoSelected.getId());
    }     
    
    /***********************
     * Métodos operativos **
     ***********************/ 
    
    /**
     * Método para validar los datos del usuario.
     * Verifica si el usuario tiene cuenta, si no la tiene, obtiene los datos personales mediante la API RUE
     * y redirecciona a la vista que solicita la validación de los datos domiciliarios y actualiza la contraseña.
     * Si ya tiene cuenta, redirecciona a la vista principal.
     */
    public void login(){
        ExternalContext contextoExterno = FacesContext.getCurrentInstance().getExternalContext();
        // encripto la contraseña recibida
        claveEncript = "";
        claveEncript = CriptPass.encriptar(clave);
        
        try{
            // valdo el usuario
            usuario = usuarioFacade.validar(cuit, claveEncript);
            if(usuario != null){
                logeado = true;

                // redirecciono según el usuario haya o no dado de alta su cuenta
                String ctxPath = ((ServletContext) contextoExterno.getContext()).getContextPath();
                if(cuentaFacade.existeCuenta(cuit)){
                    sinCuenta = false;
                    contextoExterno.redirect(ctxPath);
                    JsfUtil.addSuccessMessage("Bienvenid@ " + usuario.getNombreCompleto());
                }else{
                    sinCuenta = true;
                    // instancio el cliente para la selección de la Persona RUE, obtengo el token si no está seteado o está vencido
                    if(tokenRue == null){
                        getTokenRue();
                    }else try {
                        if(!tokenRue.isVigente()){
                            getTokenRue();
                        }
                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token RUE", ex.getMessage()});
                    }
                    // obtengo los datos de la persona
                    List<ar.gob.ambiente.sacvefor.servicios.rue.Persona> listPersonas = new ArrayList<>();
                    personaClient = new PersonaClient();
                    GenericType<List<ar.gob.ambiente.sacvefor.servicios.rue.Persona>> gType = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.rue.Persona>>() {};
                    Response response = personaClient.findByQuery_JSON(Response.class, null, String.valueOf(String.valueOf(usuario.getLogin())), null, tokenRue.getStrToken());
                    listPersonas = response.readEntity(gType);
                    if(!listPersonas.isEmpty()){
                        personaRue = listPersonas.get(0);
                        JsfUtil.addSuccessMessage("Bienvenid@ " + usuario.getNombreCompleto() + ". Para poder completar el registro deberá validar su Cuenta.");
                    }else{
                        JsfUtil.addErrorMessage("No se encontró la Persona en el Registro Unico del SACVeFor.");
                    }
                }

            }else{
                logeado = false;
                JsfUtil.addErrorMessage("No se han validado los datos ingresados, alguno de los dos o ambos son incorrectos.");
            }
        }catch(IOException ex){
            System.out.println("Hubo un error iniciando la sesión. " + ex.getMessage());
        }
    }   
    
    /**
     * Método para persistir la Cuenta del Usuario.
     * Notifica la novedad al usuario por correo electrónico.
     * Si hubo un error, cierra la sesión para volver a repetir la operación.
     */
    public void saveCuenta(){
        if(validarCuenta()){
            ExternalContext contextoExterno = FacesContext.getCurrentInstance().getExternalContext();
            Cuenta cuenta = new Cuenta();
            Date fechaAlta = new Date(System.currentTimeMillis());
            cuenta.setCuit(personaRue.getCuit());
            cuenta.setDepartamento(deptoSelected.getNombre());
            cuenta.setDomCalle(personaRue.getDomicilio().getCalle());
            cuenta.setDomDepto(personaRue.getDomicilio().getDepto());
            cuenta.setDomNumero(personaRue.getDomicilio().getNumero());
            cuenta.setDomPiso(personaRue.getDomicilio().getPiso());
            cuenta.setFechaAlta(fechaAlta);
            cuenta.setHabilitado(true);
            cuenta.setIdLocGT(personaRue.getDomicilio().getIdLocalidadGt());
            cuenta.setIdRue(personaRue.getId());
            cuenta.setLocalidad(localSelected.getNombre());
            if(personaRue.getNombreCompleto() != null){
                cuenta.setNombreCompleto(personaRue.getNombreCompleto());
            }else{
                cuenta.setNombreCompleto(personaRue.getRazonSocial());
            }
            cuenta.setProvincia(provSelected.getNombre());
            cuenta.setTipoPersona(personaRue.getTipo().getNombre());
            cuenta.setUsuario(usuario);
            // actualizo el Usuario y persisto la Cuenta
            try{
                // actualizo la clave
                String nuevaClaveEncrip = CriptPass.encriptar(newClave);
                usuario.setClave(nuevaClaveEncrip);
                if(notificarUsuario()){
                    // actualizo el Usuario
                    usuarioFacade.edit(usuario);
                    // actualizo la cuenta
                    cuentaFacade.create(cuenta);
                    sinCuenta = false;
                    // si todo anduvo bien, elimino campos del bean
                    personaRue = null;
                    personaClient = null;
                    provClient = null;
                    deptoClient = null;
                    limpiarFormCuenta();
                    JsfUtil.addSuccessMessage("La Cuenta fue validada con éxito, ya puede operar la aplicación.");
                    String ctxPath = ((ServletContext) contextoExterno.getContext()).getContextPath();
                    contextoExterno.redirect(ctxPath);
                }else{
                    // si no pudo mandar la notificación, cierro la sesión
                    HttpSession session = (HttpSession) contextoExterno.getSession(false);
                    session.invalidate();
                    logeado = false;
                    JsfUtil.addErrorMessage("No se ha podido enviar la notificación al Usuario.");
                }
            }catch(IOException ex){
                JsfUtil.addErrorMessage("Hubo un error procesando los datos de la Cuenta. " + ex.getMessage());
            }
        }
    }
    
    /**
     * Método para limpiar el formulario de logueo
     */
    public void limpiarFormLogin(){
        cuit = null;
        clave = null;
    }   
    
    /**
     * Método para limpiar los combos para la validación de los datos de la Persona.
     */
    public void limpiarFormCuenta(){
        listProvincias = new ArrayList<>();
        listDepartamentos = new ArrayList<>();
        listLocalidades = new ArrayList<>();
        provSelected = new EntidadServicio();
        deptoSelected = new EntidadServicio();
        localSelected = new EntidadServicio();
        newClave = null;
        newClave2 = null;
    }
    
    /**
     * Método para cerrar la sesión del usuario
     */
    public void logout(){
        ExternalContext contextoExterno = FacesContext.getCurrentInstance().getExternalContext();
        try {
            String ctxPath = ((ServletContext) contextoExterno.getContext()).getContextPath();
            contextoExterno.redirect(ctxPath);
            HttpSession session = (HttpSession) contextoExterno.getSession(false);
            session.invalidate();
            logeado = false;
        } catch (IOException ex) {
            JsfUtil.addErrorMessage("Hubo un error cerrando la sesión. " + ex.getMessage());
            logger.log(Level.SEVERE, null, ex);
        }
    }         
    
    /**
     * Método para cambiar la clave del Usuario.
     * Puede hacerlo en cualquier momento. Como en el caso de la validación de la cuenta,
     * notifica al usuario el cambio por correo electrónico y si hay un error, cierra la
     * sesión para volver a intentar la operación.
     */
    public void saveNewPass(){
        ExternalContext contextoExterno = FacesContext.getCurrentInstance().getExternalContext();
        if(!newClave.equals(newClave2)){
            JsfUtil.addErrorMessage("La nueva clave y su repetición no se corresponden, por favor vuelva a intentarlo.");
        }else{
            try{
                String nuevaClaveEncrip = CriptPass.encriptar(newClave);
                usuario.setClave(nuevaClaveEncrip);
                if(notificarUsuario()){
                    // actualizo el Usuario
                    usuarioFacade.edit(usuario);
                    JsfUtil.addSuccessMessage("La clave ha sido actualizada con éxito.");
                    // redirecciono al inicio
                    String ctxPath = ((ServletContext) contextoExterno.getContext()).getContextPath();
                    contextoExterno.redirect(ctxPath);
                }
                // en cualquier caso cierro la sesión
                HttpSession session = (HttpSession) contextoExterno.getSession(false);
                session.invalidate();
                logeado = false;
            }catch(IOException ex){
                JsfUtil.addErrorMessage("Hubo un error actualizando la contraseña. " + ex.getMessage());
            }
        }
    }
    
    /**
     * Método para limpiar el formulario de cambio de clave
     */
    public void limpiarFormNewPass(){
        newClave = null;
        newClave2 = null;
    }

    /*********************
     * Métodos privados **
     *********************/

    /**
     * Método privado para cargar los Departamentos vinculados a la Provincia.
     * Utilizado por provinciaChangeListener().
     * Una vez seleccionada la Provincia, puebla el listado con los Departamentos pertenecientes a ella
     * consultando la API Territorial.
     * @param id Long identificación de la Provincia en el RUE
     */
    private void getDepartamentosSrv(Long idProv) {
        EntidadServicio depto;
        List<Departamento> listSrv;
        
        try{
            // instancio el cliente para la selección de la Provincia TERR, obtengo el token si no está seteado o está vencido
            if(tokenTerr == null){
                getTokenTerr();
            }else try {
                if(!tokenTerr.isVigente()){
                    getTokenTerr();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token RUE", ex.getMessage()});
            }
            // instancio el cliente para la selección de los Departamentos
            provClient = new ProvinciaClient();
            // obtngo el listado
            GenericType<List<Departamento>> gType = new GenericType<List<Departamento>>() {};
            Response response = provClient.findByProvincia_JSON(Response.class, String.valueOf(idProv), tokenTerr.getStrToken());
            listSrv = response.readEntity(gType);
            // lleno el listado de los combos
            listDepartamentos = new ArrayList<>();
            for(Departamento dpt : listSrv){
                depto = new EntidadServicio(dpt.getId(), dpt.getNombre());
                listDepartamentos.add(depto);
            }
            
            provClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Departamentos de la Provincia seleccionada. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo los Departamentos por Provincia "
                    + "del servicio REST de centros poblados", ex.getMessage()});
        }
    }
    
    /**
     * Método privado para cargar el listado de Provincias para su selección mediante la API Territorial
     * Utilizado por el método init()
     */
    private void cargarProvincias() {
        EntidadServicio provincia;
        List<Provincia> listSrv;
        
        try{
            // instancio el cliente para la selección de la Provincia TERR, obtengo el token si no está seteado o está vencido
            if(tokenTerr == null){
                getTokenTerr();
            }else try {
                if(!tokenTerr.isVigente()){
                    getTokenTerr();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token RUE", ex.getMessage()});
            }
            // instancio el cliente para la selección de las provincias
            provClient = new ProvinciaClient();
            // obtengo el listado de provincias 
            GenericType<List<Provincia>> gType = new GenericType<List<Provincia>>() {};
            Response response = provClient.findAll_JSON(Response.class, tokenTerr.getStrToken());
            listSrv = response.readEntity(gType);
            // lleno el list con las provincias como un objeto Entidad Servicio
            listProvincias = new ArrayList<>();
            for(Provincia prov : listSrv){
                provincia = new EntidadServicio(prov.getId(), prov.getNombre());
                listProvincias.add(provincia);
                //provincia = null;
            }
            // cierro el cliente
            provClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error cargando el listado de Provincias para su selección.");
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error cargando las Provincias desde "
                    + "el servicio REST de Centros poblados.", ex.getMessage()});
        }
    }      

    /**
     * Método privado que carga el listado de Localidades según el Departamento seleccionado.
     * Mediante la API correspondiente del servicio Territorial.
     * Utilizado por deptoChangeListener()
     * @param id Long identificación del Departamento en el RUE
     */    
    private void getLocalidadesSrv(Long idDepto) {
        EntidadServicio local;
        List<CentroPoblado> listSrv;
        
        try{
            // instancio el cliente para la selección del Departamento TERR, obtengo el token si no está seteado o está vencido
            if(tokenTerr == null){
                getTokenTerr();
            }else try {
                if(!tokenTerr.isVigente()){
                    getTokenTerr();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token RUE", ex.getMessage()});
            }
            // instancio el cliente para la selección de las Localidades
            deptoClient = new DepartamentoClient();
            // obtngo el listado
            GenericType<List<CentroPoblado>> gType = new GenericType<List<CentroPoblado>>() {};
            Response response = deptoClient.findByDepto_JSON(Response.class, String.valueOf(idDepto), tokenTerr.getStrToken());
            listSrv = response.readEntity(gType);
            // lleno el listado de los combos
            listLocalidades = new ArrayList<>();
            for(CentroPoblado loc : listSrv){
                local = new EntidadServicio(loc.getId(), loc.getNombre() + " - " + loc.getCentroPobladoTipo().getNombre());
                listLocalidades.add(local);
            }
            
            deptoClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Centros poblados del Departamento seleccionado. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo los Centros poblados por Departamento "
                    + "del servicio REST de centros poblados", ex.getMessage()});
        }
    }

    /**
     * Metodo privado para validar los datos seleccionados por el Usuario para el registro de la Cuenta.
     * Compara la localidad seleccionada con la correspondiente a la Persona en el RUE y las nueva clave y su repetición.
     * Utilizado por saveCuenta()
     * @return boolean true o false según valide o no.
     */
    private boolean validarCuenta() {
        if(!Objects.equals(localSelected.getId(), personaRue.getDomicilio().getIdLocalidadGt())){
            JsfUtil.addErrorMessage("Los datos de Provincia/Departamento/Localidad no corresponden con los registrados. Por favor, contacte al Administrador.");
            return false;
        }else if(!newClave.equals(newClave2)){
            JsfUtil.addErrorMessage("La nueva clave y su repetición no se corresponden, por favor vuelva a intentarlo.");
            return false;
        }else{
            return true;
        }
    }

    /**
     * Método privado para notificar al usuario la actualización de su clave.
     * Envía un correo electrónico al usuario con la nueva clave ingresada
     * mediante el objeto de sesión para el envío de mails "mailSesion".
     * Utilizado en los método saveNewPass() saveCuenta()
     * @return boolean true o false según el correo se haya enviado correctamente o no
     */
    private boolean notificarUsuario() {
        boolean result;
        String bodyMessage;
        mensaje = new MimeMessage(mailSesion);
        bodyMessage = "<p>Estimado/a</p> "
                + "<p>Se ha modificado su clave de acceso al Componente de Trazabilidad del SACVeFor</p> "
                + "<p>Le recordamos el link de acceso: " + ResourceBundle.getBundle("/Config").getString("Server") + ResourceBundle.getBundle("/Bundle").getString("RutaAplicacion") + " </p>"
                + "<p>Las nuevas credenciales de acceso son las siguientes:</p> "
                + "<p><strong>usuario:</strong> " + usuario.getLogin() + "<br/> "
                + "<strong>contraseña:</strong> " + newClave + "</p> "
                + "<p>Por favor, no responda este correo. No divulgue ni comparta las credenciales de acceso.</p> "
                + "<p>Saludos cordiales</p> "
                
                + "<p>" + ResourceBundle.getBundle("/Config").getString("EntidadBosques") + "</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("EntidadBosqesSup_1") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("EntidadBosqesSup_2") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("EntidadBosqesSup_3") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("EntidadBosqesSup_4") + "<br/> "
                + "Presidencia de la Nación<br/> "
                + ResourceBundle.getBundle("/Config").getString("DomicilioBosques") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("TelBosques") + "<br /> "
                + "Correo electrónico: <a href=\"mailto:" + ResourceBundle.getBundle("/Config").getString("emailBosques") + "\">" + ResourceBundle.getBundle("/Config").getString("emailBosques") + "</a></p>";    
        
        try{
            mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(usuario.getEmail()));
            mensaje.setSubject("SACVeFor - Trazabilidad: Cambio de clave de acceso." );
            mensaje.setContent(bodyMessage, "text/html; charset=utf-8");
            
            Date timeStamp = new Date();
            mensaje.setSentDate(timeStamp);
            
            Transport.send(mensaje);
            
            result = true;
            
        }catch(MessagingException ex){
            result = false;
            System.out.println("Hubo un error enviando el correo de registro de usuario" + ex.getMessage());
        }
        
        return result;
    }
    
    /**
     * Método privado que obtiene y setea el tokenRue para autentificarse ante la API rest del RUE
     * Crea el campo de tipo Token con la clave recibida y el momento de la obtención.
     * Utilizado por login()
     */
    private void getTokenRue(){
        try{
            usuarioClientRue = new ar.gob.ambiente.sacvefor.trazabilidad.rue.client.UsuarioClient();
            Response responseUs = usuarioClientRue.authenticateUser_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("UsRestRue"));
            MultivaluedMap<String, Object> headers = responseUs.getHeaders();
            List<Object> lstHeaders = headers.get("Authorization");
            strTokenRue = (String)lstHeaders.get(0); 
            tokenRue = new Token(strTokenRue, System.currentTimeMillis());
            usuarioClientRue.close();
        }catch(ClientErrorException ex){
            System.out.println("Hubo un error obteniendo el token para la API Territorial: " + ex.getMessage());
        }
    } 
    
    /**
     * Método privado que obtiene y setea el token para autentificarse ante la API rest de Territorial
     * Crea el campo de tipo Token con la clave recibida y el momento de la obtención.
     * Utilizado por los método getLocalidadesSrv(), cargarProvincias() y getDepartamentosSrv()
     */
    private void getTokenTerr(){
        try{
            usuarioClientTerr = new ar.gob.ambiente.sacvefor.trazabilidad.territ.client.UsuarioClient();
            Response responseUs = usuarioClientTerr.authenticateUser_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("UsRestTerr"));
            MultivaluedMap<String, Object> headers = responseUs.getHeaders();
            List<Object> lstHeaders = headers.get("Authorization");
            strTokenTerr = (String)lstHeaders.get(0); 
            tokenTerr = new Token(strTokenTerr, System.currentTimeMillis());
            usuarioClientTerr.close();
        }catch(ClientErrorException ex){
            System.out.println("Hubo un error obteniendo el token para la API RUE: " + ex.getMessage());
        }
    }       
}
