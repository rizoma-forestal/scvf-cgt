
package ar.gob.ambiente.sacvefor.trazabilidad.service;

import ar.gob.ambiente.sacvefor.trazabilidad.annotation.Secured;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.Parametrica;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.TipoParam;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.Usuario;
import ar.gob.ambiente.sacvefor.trazabilidad.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.trazabilidad.facades.TipoParamFacade;
import java.net.URI;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;

/**
 * Servicio que implementa los métodos expuestos por la API REST para la entidad Parametrica
 * En este caso específico, se tratarán las paramétricas de tipo Rol de usuarios.
 * De modo que el término "paramétrica" puede interpretarse como "rol de usuarios".
 * @author rincostante
 */
@Stateless
@Path("parametricas")
public class ParametricaFacadeREST {

    @EJB
    private ParametricaFacade paramFacade;
    @EJB
    private TipoParamFacade tipoParamFacade;
    
    @Context
    UriInfo uriInfo;        

    /**
     * @api {post} /parametricas Registrar una Paramétrica
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X POST -d [PATH_SERVER]/trazabilidad/rest/parametricas -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName PostParametrica
     * @apiGroup Parametrica
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {ar.gob.ambiente.sacvefor.servicios.trazabilidad.Parametrica} entity Objeto java del paquete paqTrazabilidad.jar con los datos de la paramétrica a registrar
     * @apiParamExample {java} Ejemplo de Parametrica
     *      {"entity": {
     *          "id": "0",
     *          "nombre": "TRANSFORMADOR",
     *          "tipo": {
     *              "id": "1",
     *              "nombre": "ROL_USUARIOS"
     *          }
     *      }
     * @apiDescription Método para registrar una nueva Parametrica. Instancia una entidad a persistir Parametrica local y la crea mediante el método local create(Parametrica param) 
     * @apiSuccess {String} Location url de acceso mediante GET a la Parametrica registrada.
     * @apiSuccessExample Response exitosa:
     *     HTTP/1.1 201 OK
     *     {
     *       {
     *          "Location": "[PATH_SERVER]/trazabilidad/rest/parametricas/:id"
     *       }
     *     }
     *
     * @apiError ParametricaNoRegistrada No se registró la Parametrica.
     * @apiErrorExample Respuesta de Error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "Hubo un error procesando la inserción en el Registro Unico"
     *     }
     */          
    @POST
    @Secured
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response create(ar.gob.ambiente.sacvefor.servicios.trazabilidad.Parametrica entity) {
        try{
            // instancio la entidad
            Parametrica param = new Parametrica();
            param.setNombre(entity.getNombre());
            // seteo el TipoParam
            TipoParam tipo = tipoParamFacade.find(entity.getTipo().getId());
            // asigno el TipoParam a la Paramétrica
            param.setTipo(tipo);
            // persisto
            paramFacade.create(param);
            // armo la respuesta exitosa
            UriBuilder uriBuilder = uriInfo.getRequestUriBuilder();
            URI uri = uriBuilder.path(tipo.getId().toString()).build();
            return Response.created(uri).build();
        }catch(IllegalArgumentException | UriBuilderException ex){
            // armo la respuesta de error
            return Response.status(400).entity("Hubo un error procesado la inserción en el Componente de Trazabilidad. " + ex.getMessage()).build();
        }        
    }

    /**
     * @api {put} /parametricas/:id Actualizar una paramétrica existente
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X PUT -d [PATH_SERVER]/trazabilidad/rest/parametricas/1 -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName PutParametrica
     * @apiGroup Parametrica
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {ar.gob.ambiente.sacvefor.servicios.trazabilidad.Parametrica} entity Objeto java del paquete paqTrazabilidad.jar con los datos de la paramétrica a actualizar
     * @apiParam {Long} Id Identificador único de la Parametrica a actualizar
     * @apiParamExample {java} Ejemplo de Parametrica
     *      {"entity": {
     *          "id": "1",
     *          "nombre": "TRANSFORMADOR",
     *          "tipo": {
     *              "id": "1",
     *              "nombre": "ROL_USUARIOS"
     *          }
     *      }
     * @apiParamExample {json} Emplo de id
     *      {
     *          "id": "1"
     *      }
     * @apiDescription Método para actualizar una Parametrica existente. Obtiene la Parametrica correspondiente al id recibido 
     * mediante el método local find(Long id), actualiza sus datos según los de la entidad recibida y la edita mediante 
     * el método local edit(Parametrica param).
     * @apiSuccessExample Response exitosa:
     *     HTTP/1.1 200 OK
     *     {}
     * @apiError ParametricaNoActualizada No se actualizó la Parametrica.
     * @apiErrorExample Respuesta de Error:
     *     HTTP/1.1 400 Not Modified
     *     {
     *       "error": "Hubo un error procesado la actualización en el Registro Unico."
     *     }
     */      
    @PUT
    @Secured
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response edit(@PathParam("id") Long id, ar.gob.ambiente.sacvefor.servicios.trazabilidad.Parametrica entity) {
        try{
            Parametrica param = paramFacade.find(id);
            param.setNombre(entity.getNombre());
            // obtengo el TipoParam
            TipoParam tipo = tipoParamFacade.find(entity.getTipo().getId());
            // actualizo el tipo
            param.setTipo(tipo);
            // actualizo
            tipoParamFacade.edit(tipo);
            // armo la respuesta exitosa
            return Response.ok().build();
        }catch(IllegalArgumentException | UriBuilderException ex){
            // armo la respuesta de error
            return Response.notModified().entity("Hubo un error procesado la actualización en el Componente de Trazabilidad. " + ex.getMessage()).build();
        }           
    }

    /**
     * @api {get} /parametricas/:id Ver una Parametrica
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/trazabilidad/rest/parametricas/2 -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetParametrica
     * @apiGroup Parametrica
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {Long} id Identificador único de la Parametrica
     * @apiDescription Método para obtener una Parametrica existente según el id remitido.
     * Obtiene la paramétrica mediante el método local find(Long id)
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.trazabilidad.Parametrica} Parametrica Detalle de la paramétrica registrada.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *     {
     *      {
     *          "id": "2",
     *          "nombre": "ACOPIADOR",
     *          "tipo": {
     *              "id": "1",
     *              "nombre": "ROL_USUARIOS"
     *          }
     *      }
     *     }
     * @apiError ParametricaNotFound No existe paramétrica registrada con ese id.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay paramétrica registrada con el id recibido"
     *     }
     */           
    @GET
    @Path("{id}")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Parametrica find(@PathParam("id") Long id) {
        return paramFacade.find(id);
    }

    /**
     * @api {get} /parametricas Ver todas las Parametricas
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/trazabilidad/rest/parametricas -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetParametricas
     * @apiGroup Parametrica
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiDescription Método para obtener un listado de las Parametricas existentes.
     * Obtiene las paramétricas mediante el método local findAll()
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.trazabilidad.Parametrica} Parametricas Listado con todas las Parametricas registradas.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *     {
     *       "parametricas": [
     *          {"id": "1",
     *          "nombre": "TRANSFORMADOR",
     *          "tipo": {
     *              "id": "1",
     *              "nombre": "ROL_USUARIOS"
     *          },
     *          {"id": "2",
     *          "nombre": "ACOPIADOR",
     *          "tipo": {
     *              "id": "1",
     *              "nombre": "ROL_USUARIOS",
     *          }
     *       ]
     *     }
     * @apiError ParametricasNotFound No existen paramétricas registradas.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay paramétricas registradas"
     *     }
     */      
    @GET
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Parametrica> findAll() {
        return paramFacade.findAll();
    }

    /**
     * @api {get} /parametricas/:id/usuarios Ver las paramétricas
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/trazabilidad/rest/parametricas/2/usuarios -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetUsuarios
     * @apiGroup Parametrica
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {Long} id Identificador de la Parametrica cuyos Usuarios se quiere obtener
     * @apiDescription Método para obtener los Usuarios asociados a una Parametrica existente según el id remitido.
     * Obtiene los usuarios mediante el método local getUsuariosByRol(Long id)
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.trazabilidad.Parametrica} Usuario Listado de los Usuarios registrados vinculados a la Parametrica cuyo id se recibió.
     * @apiSuccessExample Respuesta existosa:
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
     *
     * @apiError ModelosNotFound No existen usuarios registrados vinculados a la id de la paramétrica.
     *
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay usuarios registrados vinculados al id de la paramétrica recibida."
     *     }
     */         
    @GET
    @Path("{id}/usuarios")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usuario> findUsuariosByRol(@PathParam("id") Long id){
        return paramFacade.getUsuariosByRol(id);
    }        
    
    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Parametrica> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return paramFacade.findRange(new int[]{from, to});
    }
    
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(paramFacade.count());
    }

}
