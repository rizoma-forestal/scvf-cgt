
package ar.gob.ambiente.sacvefor.trazabilidad.service;

import ar.gob.ambiente.sacvefor.trazabilidad.annotation.Secured;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.Parametrica;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.Usuario;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.UsuarioApi;
import ar.gob.ambiente.sacvefor.trazabilidad.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.trazabilidad.facades.UsuarioApiFacade;
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
    @EJB
    private UsuarioApiFacade usApiFacade;

    // campos y recursos para el envío de correos al usuario
    @Resource(mappedName ="java:/mail/ambientePrueba")    
    private Session mailSesion;
    private Message mensaje;    
    
    // campos para la creación del usuario
    private String pass;
    
    @Context
    UriInfo uriInfo;  

    /**
     * @api {post} /usuarios Registrar un Usuario
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X POST -d [PATH_SERVER]/trazabilidad/rest/usuarios -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName PostUsuario
     * @apiGroup Usuario
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {ar.gob.ambiente.sacvefor.servicios.trazabilidad.Usuario} entity Objeto java del paquete trazabilidad.jar con los datos del Usuario a registrar
     * @apiParamExample {java} Ejemplo de Usuario
     *      {"entity": {
     *          "id": "0",
     *          "email": "usuario_1@correo.com",
     *          "jurisdiccion": "CATAMARCA",
     *          "login": "27457740609",
     *          "nombrecompleto": "PEREZ, JUAN",
     *          "rol":{
     *              "id": "1",
     *              "nombre": "TRANSFORMADOR",
     *              "tipo": {
     *                  "id": "1",
     *                  "nombre": "ROL_USUARIOS"
     *                  }
     *              }
     *          }
     *      }
     * @apiDescription Método para registrar un nuevo Usuario. Instancia una entidad a persistir un Usuario local y la crea mediante el método local create(Usuario usuario) 
     * @apiSuccess {String} Location url de acceso mediante GET a la Usuario registrado.
     * @apiSuccessExample Response exitosa:
     *     HTTP/1.1 201 OK
     *     {
     *       {
     *          "Location": "[PATH_SERVER]/trazabilidad/rest/usuarios/:id"
     *       }
     *     }
     *
     * @apiError UsuarioRegistrado No se registró el Usuario.
     * @apiErrorExample Respuesta de Error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "Hubo un error procesando la inserción en el Componente de Trazabilidad"
     *     }
     */
    @POST
    @Secured
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
            // seteo el Rols
            Parametrica rol = paramFacade.find(entity.getRol().getId());
            usuario.setRol(rol);
            // seteo el UsuarioApi
            UsuarioApi usApi = new UsuarioApi();
            usApi = usApiFacade.getByIdProvGt(entity.getIdProvGt());
            // continúo si tengo un usuario API
            if(usApi != null){
                usuario.setUsuarioApi(usApi);
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
            }else{
                return Response.status(400).entity("No se obtuvo cliente alguno para registrar el Usuario").build();
            }

        }catch(IllegalArgumentException | UriBuilderException ex){
            // armo la respuesta de error
            return Response.status(400).entity("Hubo un error procesado la inserción en el Componente de Trazabilidad. " + ex.getMessage()).build();
        }  
    }

    /**
     * @api {get} /usuarios/:id Ver un Usuario
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/trazabilidad/rest/usuarios/2 -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetUsuario
     * @apiGroup Usuario
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {Long} id Identificador único del usuario
     * @apiDescription Método para obtener un usuarios existente según el id remitido.
     * Obtiene el usuario mediante el método local find(Long id)
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.trazabilidad.Usuario} Usuario Detalle del usuario registrado.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *     {
     *          {"id": "2",
     *          "email": "usuario_2@correo.com",
     *          "jurisdiccion": "RIO NEGRO",
     *          "login": "27455506013",
     *          "nombrecompleto": "LOPEZ, JOSE",
     *          "rol":{
     *              "id": "1",
     *              "nombre": "ACOPIADOR",
     *              "tipo": {
     *                  "id": "1",
     *                  "nombre": "ROL_USUARIOS"
     *                  }
     *              }
     *          }
     *     }
     * @apiError UsuarioNotFound No existe usuario registrado con ese id.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay usuario registrado con el id recibido"
     *     }
     */     
    @GET
    @Path("{id}")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Usuario find(@PathParam("id") Long id) {
        return usuarioFacade.find(id);
    }

    /**
     * @api {get} /usuarios Ver todas los usuarios
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/trazabilidad/rest/usuarios -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetUsuarios
     * @apiGroup Usuario
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiDescription Método para obtener un listado de los usuarios existentes.
     * Obtiene los tipos de paramétricas mediante el método local findAll()
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.trazabilidad.Usuario} Usuario Listado con todas los usuarios registrados.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *     {
     *      "Usuarios": [
     *          {"id": "1",
     *          "email": "usuario_1@correo.com",
     *          "jurisdiccion": "CATAMARCA",
     *          "login": "27457740609",
     *          "nombrecompleto": "PEREZ, JUAN",
     *          "rol":{
     *              "id": "1",
     *              "nombre": "TRANSFORMADOR",
     *              "tipo": {
     *                  "id": "1",
     *                  "nombre": "ROL_USUARIOS"
     *                  }
     *              }
     *          },
     *          {"id": "2",
     *          "email": "usuario_2@correo.com",
     *          "jurisdiccion": "RIO NEGRO",
     *          "login": "27455506013",
     *          "nombrecompleto": "LOPEZ, JOSE",
     *          "rol":{
     *              "id": "1",
     *              "nombre": "ACOPIADOR",
     *              "tipo": {
     *                  "id": "1",
     *                  "nombre": "ROL_USUARIOS"
     *                  }
     *              }
     *          }
     *      ]
     *     }
     * @apiError UsuariosNotFound No existen usuarios registrados.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay tipos de usuarios registrados"
     *     }
     */        
    @GET
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usuario> findAll() {
        return usuarioFacade.findAll();
    }

    /**
     * @api {get} /usuarios/query?juris=:juris,cuit=:cuit Ver Usurio según parámetros.
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/trazabilidad/rest/usuarios/query?juris=JUJUY -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetUsuarioQuery
     * @apiGroup Usuario
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     }
     * @apiParam {String} juris Provincia dentro de la cual se desempeña el usuario
     * @apiParam {String} cuit Cuit del usuario
     * @apiDescription Método para obtener un usuario según su jurisdicción o su cuit.
     * Solo uno de los parámetros tendrá un valor y los restantes nulos.
     * Según el caso, obtiene el o los usuarios en cuestión con los métodos locales getByJuris(String juris) o getExistente(Long cuit)
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.trazabilidad.Usuario} Usuario Detalle del usuario registrado.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *     {
     *      "Usuarios": [
     *          {"id": "3",
     *          "email": "usuario_3@correo.com",
     *          "jurisdiccion": "JUJUY",
     *          "login": "27457740000",
     *          "nombrecompleto": "HERNANDES, ERNESTO",
     *          "rol":{
     *              "id": "1",
     *              "nombre": "TRANSFORMADOR",
     *              "tipo": {
     *                  "id": "1",
     *                  "nombre": "ROL_USUARIOS"
     *                  }
     *              }
     *          },
     *          {"id": "7",
     *          "email": "usuario_7@correo.com",
     *          "jurisdiccion": "JUJUY",
     *          "login": "27455501111",
     *          "nombrecompleto": "GUTIERREZ, ARNALDO",
     *          "rol":{
     *              "id": "1",
     *              "nombre": "ACOPIADOR",
     *              "tipo": {
     *                  "id": "1",
     *                  "nombre": "ROL_USUARIOS"
     *                  }
     *              }
     *          }
     *      ]
     *     }
     * @apiError UsuariosNotFound No existen usuarios registrados con esos parámetros.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay usuarios registrados con los parámetros recibidos"
     *     }
     */     
    @GET
    @Path("/query")
    @Secured
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

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usuario> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return usuarioFacade.findRange(new int[]{from, to});
    }
 
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
