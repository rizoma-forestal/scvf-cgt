
package ar.gob.ambiente.sacvefor.trazabilidad.service;

import ar.gob.ambiente.sacvefor.trazabilidad.entities.Parametrica;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.Usuario;
import ar.gob.ambiente.sacvefor.trazabilidad.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.trazabilidad.facades.UsuarioFacade;
import ar.gob.ambiente.sacvefor.trazabilidad.util.CriptPass;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;

/**
 * Servicio que implementa los métodos expuestos por la API REST para la entidad Usuario
 * @author rincostante
 */
@Stateless
@Path("usuarios")
public class UsuarioFacadeREST {

    @EJB
    private UsuarioFacade usuarioFacade;
    @EJB
    private ParametricaFacade paramFacade;

    // campos y recursos para el envío de correos al usuario
    @Resource(mappedName ="java:/mail/ambientePrueba")    
    private Session mailSesion;
    private Message mensaje;    
    
    // campos para la creación del usuario
    private String pass;
    
    @Context
    UriInfo uriInfo;  
    
    /**
     * Método para crear un Usuario.
     * El registro dependerá del envío del corre electrónico al usuario 
     * comunicándole la novedad y invitándolo a acceder al sistema
     * @param entity: El Usuario a persistir
     * @return : Un código de respuesta (201) con la uri de acceso a la Entidad creada o un código de error (400)
     */     
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response create(ar.gob.ambiente.sacvefor.servicios.trazabilidad.Usuario entity) {
        boolean correoEnviado = false;
        String passEncrpitpado;
        try{
            // instancio la entidad
            Usuario usuario = new Usuario();
            // creo la clave
            pass = CriptPass.generar();
            passEncrpitpado = CriptPass.encriptar(pass);
            // seteo la clave y el resto de los campos
            usuario.setClave(passEncrpitpado);
            usuario.setEmail(entity.getEmail());
            usuario.setJurisdiccion(entity.getJurisdiccion());
            usuario.setLogin(entity.getLogin());
            usuario.setNombreCompleto(entity.getNombreCompleto());
            // genero el Rol
            Parametrica rol = paramFacade.find(entity.getRol().getId());
            usuario.setRol(rol);
            // si envío el correo correctamente, persisto
            correoEnviado = enviarCorreo(usuario, "Se ha creado una cuenta de acceso al Componente de Trazabilidad del SACVeFor.");
            if(correoEnviado){
                // seteo los datos de alta
                Date fechaAlta = new Date(System.currentTimeMillis());
                usuario.setFechaAlta(fechaAlta);
                usuario.setHabilitado(true);
                // persisto
                usuarioFacade.create(usuario);
                // armo la respuesta exitosa
                UriBuilder uriBuilder = uriInfo.getRequestUriBuilder();
                URI uri = uriBuilder.path(usuario.getId().toString()).build();
                return Response.created(uri).build();
            }else{
                // si no se envió el correo comunico el error
                return Response.status(400).entity("Hubo un error enviando el correo al usuario. No se ha podido generar el Usuario").build();
            }
        }catch(IllegalArgumentException | UriBuilderException ex){
            // armo la respuesta de error
            return Response.status(400).entity("Hubo un error procesado la inserción en el Componente de Trazabilidad. " + ex.getMessage()).build();
        }  
    }

    /**
     * Método para obtener el Usuario correspondiente al id recibido
     * Ej: [PATH]/usuarios/1
     * @param id: id del Usuario a obtener
     * @return
     */      
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Usuario find(@PathParam("id") Long id) {
        return usuarioFacade.find(id);
    }

    /**
     * Método que retorna todos los Usuarios registrados
     * Ej: [PATH]/usuarios
     * @return 
     */        
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usuario> findAll() {
        return usuarioFacade.findAll();
    }
    
    /**
     * Método que, según los parámetros recibidos ejecuta uno u otro método
     * @param juris: con la Jurisdicción, devuelve todos los Usuarios registrados con esa Jurisdicción:
     * Ej: [PATH]/usuarios/query?juris=JUJUY
     * @param cuit: con el parámetro cuit, devuelve el Usuario cuyo login correspondiente al CUIT ingresado:
     * Ej: [PATH]/vehiculos/query?cuit=20339210315
     * @return 
     */        
    @GET
    @Path("/query")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usuario> findByQuery(@QueryParam("juris") String juris, @QueryParam("cuit") Long cuit) {
        List<Usuario> result = new ArrayList<>();
        if(juris != null){
            return usuarioFacade.getByJuris(juris);
        }else if(cuit != null){
            Usuario us = usuarioFacade.getExistente(cuit);
            if(us != null){
                result.add(us);
            }
        }
        return result;
    }     

    /**
     * Método que obtiene un listado de Usuarios cuyos id se encuentran entre los parámetros de inicio y fin recibidos
     * Ej: [PATH]/usuarios/1/10
     * @param from: parámetro 'desde' el cual se inicia el listado
     * @param to: parámetro 'hasta' el cual se completa el listado
     * @return 
     */    
    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usuario> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return usuarioFacade.findRange(new int[]{from, to});
    }

    /**
     * Método que devuelve un entero con la totalidad de los Usuarios registrados
     * Ej: [PATH]/usuarios/count
     * @return 
     */        
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(usuarioFacade.count());
    }
    
    /*********************
     * Métodos privados **
     *********************/
    
    /**
     * Método para enviar un correo electrónico al usuario
     * @param us
     * @param motivo
     * @return 
     */
    private boolean enviarCorreo(Usuario us, String motivo){  
        boolean result;
        String bodyMessage;
        mensaje = new MimeMessage(mailSesion);
        bodyMessage = "<p>Estimado/a</p> "
                + "<p>" + motivo + ".</p> "
                + "<p>Esta cuenta ha sido creada debido a que se ha remitido una Guía de Productos Forestales a su nombre.</p>"
                + "<p>Deberá iniciar sesión en en el siguiente link: " + ResourceBundle.getBundle("/Config").getString("Server") + ResourceBundle.getBundle("/Bundle").getString("RutaAplicacion") + " </p>"
                + "<p>Las credenciales de acceso son las siguientes:</p> "
                + "<p><strong>usuario:</strong> " + us.getLogin() + "<br/> "
                + "<strong>contraseña:</strong> " + pass + "</p> "
                + "<p>Una vez iniciada la sesión, el sistema le indicará cómo completar la configuración de su Cuenta "
                + "para aceptar la Guía remitida.</p>"
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
            mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(us.getEmail()));
            mensaje.setSubject("SACVeFor - Trazabilidad: Credenciales de acceso" );
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
