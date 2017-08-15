
package ar.gob.ambiente.sacvefor.trazabilidad.service;

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
     * Método para crear un TipoParam.
     * @param entity: El TipoParam a persistir
     * @return : Un código de respuesta (201) con la uri de acceso a la Entidad creada o un código de error (400)
     */    
    @POST
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
     * Método para editar un TipoParam existente
     * @param id : Id del TipoParam a editar
     * @param entity : TipoParam a editar
     * @return : El código de repuesta que corresponda según se haya realizado o no la operación: 200 o 400
     */    
    @PUT
    @Path("{id}")
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
     * Método para obtener el TipoParam correspondiente al id recibido
     * Ej: [PATH]/tipoparams/1
     * @param id: id del TipoParam a obtener
     * @return
     */    
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public TipoParam find(@PathParam("id") Long id) {
        return tipoParamFacade.find(id);
    }

    /**
     * Método que retorna todos los TipoParam registrados
     * Ej: [PATH]/tipoparams
     * @return 
     */    
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<TipoParam> findAll() {
        return tipoParamFacade.findAll();
    }
    
    /**
     * Método que, según los parámetros recibidos ejecuta uno u otro método
     * @param nombre: nombre del TipoParam a buscar
     * Ej: [PATH]/tipoparams/query?nombre=ROL_USUARIOS
     * @return 
     */        
    @GET
    @Path("/query")
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
     * Método que devuelve todas las Parametricas correspondientes al TipoParam cuyo id se recibe como parámetro
     * Ej: [PATH]/tipoparams/1/parametricas
     * @param id
     * @return 
     */
    @GET
    @Path("{id}/parametricas")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Parametrica> findParametricasByTipo(@PathParam("id") Long id){
        return tipoParamFacade.getParamByTipo(id);
    }    

    /**
     * Método que obtiene un listado de TipoParam cuyos id se encuentran entre los parámetros de inicio y fin recibidos
     * Ej: [PATH]/tipoparams/1/10
     * @param from: parámetro 'desde' el cual se inicia el listado
     * @param to: parámetro 'hasta' el cual se completa el listado
     * @return 
     */
    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<TipoParam> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return tipoParamFacade.findRange(new int[]{from, to});
    }

    /**
     * Método que devuelve un entero con la totalidad de los TipoParam registrados
     * Ej: [PATH]/tipoparams/count
     * @return 
     */    
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(tipoParamFacade.count());
    }
}
