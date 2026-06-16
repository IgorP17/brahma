package com.example.webui.resource;

import com.example.webui.service.TerminalService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import io.quarkus.qute.Template;
import jakarta.enterprise.context.RequestScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty; // ← Добавили импорт
import org.jboss.logging.Logger;

@Path("/")
@RequestScoped
public class TerminalWebResource {

    private static final Logger log = Logger.getLogger(TerminalWebResource.class);

    @Inject
    TerminalService terminalService;

    @Inject
    Template index; // ← src/main/resources/templates/index.html

    // ← Читаем переменную BACKEND_URL (возможно придет из YAML). Если её нет (локальный запуск), дефолтим на localhost:8080
    @ConfigProperty(name = "BACKEND_URL", defaultValue = "http://localhost:8080")
    String backendUrl;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getIndex() {
        log.infof("🔗 Using Backend URL: %s", backendUrl);
        log.info("🌐 HTTP REQUEST: GET /");
        return index
                .data("message", "Welcome to Brahma WebUI!")
                .data("terminals", java.util.Collections.emptyList())  // ← пустой список
                .data("backendUrl", backendUrl) // ← Передаем URL в index.html
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