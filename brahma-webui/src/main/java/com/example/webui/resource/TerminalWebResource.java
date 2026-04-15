package com.example.webui.resource;

import com.example.webui.client.GatewayClient;
import com.example.webui.model.TerminalViewGateway;
import com.example.webui.service.TerminalService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import io.quarkus.qute.Template;
import jakarta.enterprise.context.RequestScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@Path("/")
@RequestScoped
public class TerminalWebResource {

    private static final Logger log = Logger.getLogger(TerminalWebResource.class);

    @Inject
    TerminalService terminalService;

    @Inject
    Template index; // ← src/main/resources/templates/index.html

    @Inject
    @RestClient
    GatewayClient gatewayClient;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getIndex() {
        log.info("🌐 HTTP REQUEST: GET /");
        return index
                .data("message", "Welcome to Brahma WebUI!")
                .data("terminals", java.util.Collections.emptyList())  // ← пустой список
                .render();
    }

    @GET
    @Path("/terminal/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTerminalById(@PathParam("id") String id) {
        log.infof("🌐 HTTP REQUEST: GET /terminal/%s", id);
        var gatewayTerm = terminalService.findTerminalById(id);
        var processorTerm = terminalService.findProcessorTerminalById(id);

        var result = new java.util.HashMap<String, Object>();
        result.put("gateway", gatewayTerm);
        result.put("processor", processorTerm);

        log.infof("✅ Got info about terminal %s", id);

        return Response.ok(result).build();
    }

    @POST
    @Path("/regterminal")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response registerTerminalViaAjax(@FormParam("id") String id,
                                            @FormParam("model") String model,
                                            @FormParam("location") String location) {
        log.infof("🌐 HTTP REQUEST: POST /register - Terminal ID: %s, Model: %s, Location: %s", id, model, location);
        // Собираем JSON-объект
        var request = new com.example.webui.client.TerminalRegistrationRequest();
        request.id = id;
        request.data = java.util.Map.of("model", model, "location", location);

        try {
            var response = gatewayClient.registerTerminal(request);
            if (response.getStatus() == 200) {
                log.infof("✅ Terminal %s registered successfully via web UI", id);
                return Response.ok("Terminal " + id + " is successfully pending registration.").build();
            } else {
                log.warnf("⚠️ Terminal %s registration failed via web UI, gateway responded with status: %d", id, response.getStatus());
                return Response.status(response.getStatus()).entity("Error registering terminal").build();
            }
        } catch (Exception e) {
            log.errorf("❌ Internal error during registration of terminal %s: %s", id, e.getMessage(), e);
            return Response.status(500).entity("Internal error: " + e.getMessage()).build();
        }
    }

    // === Удаление терминала ===

    @DELETE
    @Path("/terminal/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteTerminalFromBoth(@PathParam("id") String id) {
        log.infof("🌐 HTTP REQUEST: DELETE /terminal/%s", id);
        terminalService.deleteTerminalFromBoth(id);
        log.infof("✅ Terminal %s deleted from both gateway and processor", id);
        return Response.ok("Deleted from both").build();
    }

    @DELETE
    @Path("/terminal/{id}/gateway")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteTerminalFromGateway(@PathParam("id") String id) {
        log.infof("🌐 HTTP REQUEST: DELETE /terminal/%s", id);
        terminalService.deleteTerminalFromGateway(id);
        log.infof("✅ Terminal %s deleted from gateway", id);
        return Response.ok("Deleted from gateway").build();
    }

    @DELETE
    @Path("/terminal/{id}/processor")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteTerminalFromProcessor(@PathParam("id") String id) {
        log.infof("🌐 HTTP REQUEST: DELETE /terminal/%s", id);
        terminalService.deleteTerminalFromProcessor(id);
        log.infof("✅ Terminal %s deleted from processor", id);
        return Response.ok("Deleted from processor").build();
    }
}