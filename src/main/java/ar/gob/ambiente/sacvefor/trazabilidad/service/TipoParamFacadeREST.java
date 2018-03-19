
package ar.gob.ambiente.sacvefor.trazabilidad.service;

import ar.gob.ambiente.sacvefor.trazabilidad.annotation.Secured;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.Parametrica;
import ar.gob.ambiente.sacvefor.trazabilidad.entities.TipoParam;
import ar.gob.ambiente.sacvefor.trazabilidad.facades.TipoParamFacade;
import java.net.URI;
import java.util.ArrayList;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;

/**
 * Servicio que implementa los métodos expuestos por la API REST para la entidad TipoParam
 * @author rincostante
 */
@Stateless
@Path("tipoparams")
public class TipoParamFacadeREST {
    
    @EJB
    private TipoParamFacade tipoParamFacade;
    
    @Context
    UriInfo uriInfo;          

    /**
     * @api {post} /tipoparams Registrar un TipoParam
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X POST -d [PATH_SERVER]/trazabilidad/rest/tipoparams -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName PostTipoParam
     * @apiGroup TipoParam
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {ar.gob.ambiente.sacvefor.servicios.trazabilidad.TipoParam} entity Objeto java del paquete paqTrazabilidad.jar con los datos de la tipo de paramétrica a registrar
     * @apiParamExample {java} Ejemplo de TipoParam
     *      {"entity": {
     *          "id": "0",
     *          "nombre": "ROL_USUARIOS"
     *      }
     * @apiDescription Método para registrar un nuevo Tipo de paramétrica. Instancia una entidad a persistir un TipoParam local y la crea mediante el método local create(TipoParam tipoParam) 
     * @apiSuccess {String} Location url de acceso mediante GET a la TipoParam registrado.
     * @apiSuccessExample Response exitosa:
     *     HTTP/1.1 201 OK
     *     {
     *       {
     *          "Location": "[PATH_SERVER]/trazabilidad/rest/tipoparams/:id"
     *       }
     *     }
     *
     * @apiError TipoParamNoRegistrada No se registró el Tipo de paramétrica.
     * @apiErrorExample Respuesta de Error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "Hubo un error procesando la inserción en el Registro Unico"
     *     }
     */
    @POST
    @Secured
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response create(ar.gob.ambiente.sacvefor.servicios.trazabilidad.TipoParam entity) {
        // instancio la entidad
        TipoParam tipo = new TipoParam();
        tipo.setNombre(entity.getNombre());
        // persisto
        try{
            tipoParamFacade.create(tipo);
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
     * @api {put} /tipoparams/:id Actualizar un tipo de paramétruica existente
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X PUT -d [PATH_SERVER]/trazabilidad/rest/tipoparams/1 -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName PutTipoParam
     * @apiGroup TipoParam
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {ar.gob.ambiente.sacvefor.servicios.trazabilidad.TipoParam} entity Objeto java del paquete paqTrazabilidad.jar con los datos del tipo de paramétrica a actualizar
     * @apiParam {Long} Id Identificador único del tipo de paramétrica a actualizar
     * @apiParamExample {java} Ejemplo de TipoParam
     *      {"entity": {
     *          "id": "1",
     *          "nombre": "ROL_USUARIOS"}
     *      }
     * @apiParamExample {json} Emplo de id
     *      {
     *          "id": "1"
     *      }
     * @apiDescription Método para actualizar un tipo de paramétrica existente. Obtiene el TipoParam correspondiente al id recibido 
     * mediante el método local find(Long id), actualiza sus datos según los de la entidad recibida y la edita mediante 
     * el método local edit(TipoParam tipoParam).
     * @apiSuccessExample Response exitosa:
     *     HTTP/1.1 200 OK
     *     {}
     * @apiError TipoParamNoActualizado No se actualizó el TipoParam.
     * @apiErrorExample Respuesta de Error:
     *     HTTP/1.1 400 Not Modified
     *     {
     *       "error": "Hubo un error procesado la actualización en el Registro Unico."
     *     }
     */   
    @PUT
    @Path("{id}")
    @Secured
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response edit(@PathParam("id") Long id, ar.gob.ambiente.sacvefor.servicios.trazabilidad.TipoParam entity) {
        TipoParam tipo = tipoParamFacade.find(id);
        tipo.setNombre(entity.getNombre());
        try{
            tipoParamFacade.edit(tipo);
            // armo la respuesta exitosa
            return Response.ok().build();
        }catch(IllegalArgumentException | UriBuilderException ex){
            // armo la respuesta de error
            return Response.notModified().entity("Hubo un error procesado la actualización en el Componente de Trazabilidad. " + ex.getMessage()).build();
        }        
    }

    /**
     * @api {get} /tipoparams/:id Ver un TipoParam
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/trazabilidad/rest/tipoparams/2 -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetTipoParam
     * @apiGroup TipoParam
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {Long} id Identificador único del tipo de paramétrica
     * @apiDescription Método para obtener un TipoParam existente según el id remitido.
     * Obtiene el tipo de paramétrica mediante el método local find(Long id)
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.trazabilidad.TipoParam} TipoParam Detalle del tipo de paramétrica registrado.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *     {
     *      {
     *          "id": "2",
     *          "nombre": "TIPO_ITEM",
     *      }
     *     }
     * @apiError TipoParamNotFound No existe tipo de paramétrica registrada con ese id.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay tipo de paramétrica registrada con el id recibido"
     *     }
     */     
    @GET
    @Path("{id}")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public TipoParam find(@PathParam("id") Long id) {
        return tipoParamFacade.find(id);
    }

    /**
     * @api {get} /tipoparams Ver todas los tipos de paramétricas
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/trazabilidad/rest/tipoparams -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetTipoParams
     * @apiGroup TipoParam
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiDescription Método para obtener un listado de los tipos de paramétricas existentes.
     * Obtiene los tipos de paramétricas mediante el método local findAll()
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.trazabilidad.TipoParam} TipoParam Listado con todas los tipos de paramétricas registrados.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *     {
     *      {"tipoParams": [
     *          {"id": "1",
     *          "nombre": "ROL_USUARIOS"},
     *          {"id": "2",
     *          "nombre": "TIPO_ITEM"}
     *      ]
     *     }
     * @apiError TipoParamsNotFound No existen tipos de paramétricas registrados.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay tipos de paramétricas registrados"
     *     }
     */     
    @GET
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<TipoParam> findAll() {
        return tipoParamFacade.findAll();
    }
    
    /**
     * @api {get} /tipoparams/query?nombre=:nombre Ver tipo de paramétrica según su nombre
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/trazabilidad/rest/tipoparams/query?nombre=ROL_USUARIOS -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetTipoParamQuery
     * @apiGroup TipoParam
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     }
     * @apiParam {String} name Nombre del tipo de paramétrica
     * @apiDescription Método para obtener un tipo de paramétrica según su nombre.
     * Obtiene un tipo de paramétrica mediante el método local getExistente(String nombre)
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.trazabilidad.TipoParam} TipoParam Detalle del tipo de paramétrica registrado.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *      {
     *          {"id": "1",
     *          "nombre": "ROL_USUARIOS"}
     *      }
     * @apiError TipoParamNotFound No existe tipo de paramétrica registrado con ese nombre.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay tipo de paramétrica registrado con el nombre recibido"
     *     }
     */     
    @GET
    @Path("/query")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<TipoParam> findByQuery(@QueryParam("nombre") String nombre) {
        List<TipoParam> result = new ArrayList<>();
        if(nombre != null){
            TipoParam tipo = tipoParamFacade.getExistente(nombre);
            if(tipo != null){
                result.add(tipo);
            }
        }
        return result;
    }  

    /**
     * @api {get} /tipoparams/:id/parametricas Ver las paramétricas
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/trazabilidad/rest/tipoparams/1/parametricas -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetPareametricas
     * @apiGroup TipoParam
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {Long} id Identificador del tipo de paramétrica cuyas paramétricas se quiere obtener
     * @apiDescription Método para obtener las paramétricas asociados a un tipo existente según el id remitido.
     * Obtiene los usuarios mediante el método local findParametricasByTipo(Long id)
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.trazabilidad.TipoParam} Parametrica Listado de las paramétricas registradas vinculadas al tipo cuyo id se recibió.
     * @apiSuccessExample Respuesta existosa:
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
     *
     * @apiError ParametricasNotFound No existen paramétricas registradas vinculadas a la id del tipo.
     *
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay paramétricas registradas vinculados al id del tipo recibido."
     *     }
     */      
    @GET
    @Path("{id}/parametricas")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Parametrica> findParametricasByTipo(@PathParam("id") Long id){
        return tipoParamFacade.getParamByTipo(id);
    }    

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<TipoParam> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return tipoParamFacade.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(tipoParamFacade.count());
    }
}
