
package ar.gob.ambiente.sacvefor.trazabilidad.ctrl.client;

import java.util.ResourceBundle;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;

/**
 * Cliente REST Jersey generado para el recurso ControlFacadeREST de la API de Control y Verificación<br>
 * [controles]<br>
 * USAGE:
 * <pre>
 *        ControlClient client = new ControlClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 * Cliente para el servicio de gestión de Controles realizados del API-CCV
 * @author rincostante
 */
public class ControlClient {

    /**
     * Variable privada: WebTarget path de acceso a la API de Control y Verificación
     */
    private WebTarget webTarget;
    
    /**
     * Variable privada: Client cliente a setear a partir de webTarget
     */
    private Client client;
    
    /**
     * Variable privada estática y final: String url general de acceso al servicio.
     * A partir de datos configurados en archivo de propiedades
     */
    private static final String BASE_URI = ResourceBundle.getBundle("/Config").getString("ServerServicios") + "/"
            + "" + ResourceBundle.getBundle("/Config").getString("UrlCtrlVerif");

    /**
     * Constructor que instancia el cliente y el webTarget
     */
    public ControlClient() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("controles");
    }

    public String countREST() throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("count");
        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    /**
     * Método para obtener uno o más Registro/s de Control realizado/s según un parámetro que podrá ser la localidad, 
     * la matrícula del transporte o el destino. Solo se podrá pasar el valor de un parámetro, los restantes deberán ser nulos.
     * En formato XML
     * GET /controles/query?idLoc=:idLoc
     * GET /controles/query?resultado=:resultado
     * GET /controles/query?codGuia=:codGuia
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Control
     * @param idLoc String identificación de la localidad del Puesto de control en el que se realizó el registro
     * @param resultado String Resultado del control de la Guía
     * @param codGuia String código de la Guía cuyo/s control/es se desea obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return Control control o controles obtenido/s según los parámetros enviados
     * @throws ClientErrorException Excepcion a ejecutar
     */    
    public <T> T findByQuery_XML(Class<T> responseType, String idLoc, String resultado, String codGuia, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (idLoc != null) {
            resource = resource.queryParam("idLoc", idLoc);
        }
        if (resultado != null) {
            resource = resource.queryParam("resultado", resultado);
        }
        if (codGuia != null) {
            resource = resource.queryParam("codGuia", codGuia);
        }
        resource = resource.path("query");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método para obtener uno o más Registro/s de Control realizado/s según un parámetro que podrá ser la localidad, 
     * la matrícula del transporte o el destino. Solo se podrá pasar el valor de un parámetro, los restantes deberán ser nulos.
     * En formato JSON
     * GET /controles/query?idLoc=:idLoc
     * GET /controles/query?resultado=:resultado
     * GET /controles/query?codGuia=:codGuia
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Guia
     * @param idLoc String identificación de la localidad del Puesto de control en el que se realizó el registro
     * @param resultado String Resultado del control de la Guía
     * @param codGuia String código de la Guía cuyo/s control/es se desea obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return Control control o controles obtenido/s según los parámetros enviados
     * @throws ClientErrorException Excepcion a ejecutar
     */ 
    public <T> T findByQuery_JSON(Class<T> responseType, String idLoc, String resultado, String codGuia, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (idLoc != null) {
            resource = resource.queryParam("idLoc", idLoc);
        }
        if (resultado != null) {
            resource = resource.queryParam("resultado", resultado);
        }
        if (codGuia != null) {
            resource = resource.queryParam("codGuia", codGuia);
        }
        resource = resource.path("query");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método que obtiene un Control registrado habilitado según su id en formato XML
     * GET /controles/:id
     * @param <T> Tipo genérico
     * @param responseType Entidad en la que se setearán los datos serializados obtenidos, en este caso será Control
     * @param id String id del Control a obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return <T> Control control obtenido según el id remitido
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T find_XML(Class<T> responseType, String id, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método que obtiene un Control registrado habilitado según su id en formato JSON
     * GET /controles/:id
     * @param <T> Tipo genérico
     * @param responseType Entidad en la que se setearán los datos serializados obtenidos, en este caso será Control
     * @param id String id del Control a obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return <T> Control control obtenido según el id remitido
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T find_JSON(Class<T> responseType, String id, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    public <T> T findRange_XML(Class<T> responseType, String from, String to) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}/{1}", new Object[]{from, to}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T findRange_JSON(Class<T> responseType, String from, String to) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}/{1}", new Object[]{from, to}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    /**
     * Método que obtiene todos los controles registrados habilitados en formato XML
     * GET /controles
     * @param <T> Tipo genérico
     * @param responseType javax.ws.rs.core.Response
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response resultados de la consulta
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findAll_XML(Class<T> responseType, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método que obtiene todos los controles registrados habilitados en formato JSON
     * GET /controles
     * @param <T> Tipo genérico
     * @param responseType javax.ws.rs.core.Response
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response resultados de la consulta
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findAll_JSON(Class<T> responseType, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método para cerrar el cliente
     */
    public void close() {
        client.close();
    }
}
