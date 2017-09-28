
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
import javax.ws.rs.core.Response;

/**
 * Bean para la gestión de la sesión del usuario
 * @author rincostante
 */
public class MbSesion {

    private Long cuit;
    private String clave;
    private String claveEncript;
    private String newClave;
    private String newClave2;
    private Usuario usuario;
    private boolean sinCuenta;
    private boolean logeado = false;
  
    // campos para la gestión de datos de las API
    private PersonaClient personaClient;   
    private Persona personaRue;
    private ProvinciaClient provClient;    
    private DepartamentoClient deptoClient;   
    private static final Logger logger = Logger.getLogger(MbSesion.class.getName());
    /**
     * Campos para la gestión de las Entidades provenientes de la API Territorial en los combos del formulario.
     */  
    private List<EntidadServicio> listProvincias;
    private EntidadServicio provSelected;
    private List<EntidadServicio> listDepartamentos;
    private EntidadServicio deptoSelected;
    private List<EntidadServicio> listLocalidades;
    private EntidadServicio localSelected;    
    
    // campos para la notificación al Usuario
    @Resource(mappedName ="java:/mail/ambientePrueba")    
    private Session mailSesion;
    private Message mensaje;       
    
    @EJB
    private UsuarioFacade usuarioFacade;
    @EJB
    private CuentaFacade cuentaFacade;
    
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
     * Método para validar los datos del usuario
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
                    // obtengo los datos de la persona
                    List<ar.gob.ambiente.sacvefor.servicios.rue.Persona> listPersonas = new ArrayList<>();
                    personaClient = new PersonaClient();
                    GenericType<List<ar.gob.ambiente.sacvefor.servicios.rue.Persona>> gType = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.rue.Persona>>() {};
                    Response response = personaClient.findByQuery_JSON(Response.class, null, String.valueOf(String.valueOf(usuario.getLogin())), null);
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
     * Método para persistir la Cuenta del Usuario
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
     * Método para cambiar la clave del Usuario
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
     * Método para cargar los Departamentos vinculados a la Provincia
     * @param id 
     */
    private void getDepartamentosSrv(Long idProv) {
        EntidadServicio depto;
        List<Departamento> listSrv;
        
        try{
            // instancio el cliente para la selección de los Departamentos
            provClient = new ProvinciaClient();
            // obtngo el listado
            GenericType<List<Departamento>> gType = new GenericType<List<Departamento>>() {};
            Response response = provClient.findByProvincia_JSON(Response.class, String.valueOf(idProv));
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
     * Método para cargar el listado de Provincias para su selección
     */
    private void cargarProvincias() {
        EntidadServicio provincia;
        List<Provincia> listSrv;
        
        try{
            // instancio el cliente para la selección de las provincias
            provClient = new ProvinciaClient();
            // obtengo el listado de provincias 
            GenericType<List<Provincia>> gType = new GenericType<List<Provincia>>() {};
            Response response = provClient.findAll_JSON(Response.class);
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
     * Método que carga el listado de Localidades según el Departamento seleccionado
     * @param id 
     */    
    private void getLocalidadesSrv(Long idDepto) {
        EntidadServicio local;
        List<CentroPoblado> listSrv;
        
        try{
            // instancio el cliente para la selección de las Localidades
            deptoClient = new DepartamentoClient();
            // obtngo el listado
            GenericType<List<CentroPoblado>> gType = new GenericType<List<CentroPoblado>>() {};
            Response response = deptoClient.findByDepto_JSON(Response.class, String.valueOf(idDepto));
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
     * Metodo para validar los datos seleccionados por el Usuario para el registro de la Cuenta
     * @return 
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
     * Método para notificar al usuario la actualización de su clave
     * @return 
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
}
