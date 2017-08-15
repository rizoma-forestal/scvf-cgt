
package ar.gob.ambiente.sacvefor.trazabilidad.service;

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
     * Método para crear una Parametrica.
     * @param entity: La Parametrica a persistir
     * @return : Un código de respuesta (201) con la uri de acceso a la Entidad creada o un código de error (400)
     */     
    @POST
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
     * Método para editar una Parametrica existente
     * @param id : Id de la Parametrica a editar
     * @param entity : Parametrica a editar
     * @return : El código de repuesta que corresponda según se haya realizado o no la operación: 200 o 400
     */        
    @PUT
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
     * Método para obtener la Parametrica correspondiente al id recibido
     * Ej: [PATH]/parametricas/1
     * @param id: id de la Parametrica a obtener
     * @return
     */    
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Parametrica find(@PathParam("id") Long id) {
        return paramFacade.find(id);
    }

    /**
     * Método que retorna todos las Parametricas registradas
     * Ej: [PATH]/parametricas
     * @return 
     */     
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Parametrica> findAll() {
        return paramFacade.findAll();
    }

    /**
     * Método que devuelve todos los Usuarios correspondientes al Rol (Paramétrica) cuyo id se recibe como parámetro
     * Ej: [PATH]/parametricas/1/usuarios
     * @param id
     * @return 
     */
    @GET
    @Path("{id}/usuarios")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usuario> findUsuariosByRol(@PathParam("id") Long id){
        return paramFacade.getUsuariosByRol(id);
    }        
    
    /**
     * Método que obtiene un listado de Paramétricas cuyos id se encuentran entre los parámetros de inicio y fin recibidos
     * Ej: [PATH]/parametricas/1/10
     * @param from: parámetro 'desde' el cual se inicia el listado
     * @param to: parámetro 'hasta' el cual se completa el listado
     * @return 
     */    
    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Parametrica> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return paramFacade.findRange(new int[]{from, to});
    }

    /**
     * Método que devuelve un entero con la totalidad de las Paramétricas registradas
     * Ej: [PATH]/parametricas/count
     * @return 
     */        
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(paramFacade.count());
    }

}
