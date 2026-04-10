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

import java.util.List;

@Path("/")
@RequestScoped
public class TerminalWebResource {
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
        return index
                .data("message", "Welcome to Brahma WebUI!")
                .data("terminals", java.util.Collections.emptyList())  // ← пустой список
                .render();
    }

    @GET
    @Path("/terminals")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TerminalViewGateway> getTerminals() {
        return terminalService.getAllTerminals();
    }

    @GET
    @Path("/terminal/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTerminalById(@PathParam("id") String id) {
        var gatewayTerm = terminalService.findTerminalById(id);
        var processorTerm = terminalService.findProcessorTerminalById(id);

        var result = new java.util.HashMap<String, Object>();
        result.put("gateway", gatewayTerm);
        result.put("processor", processorTerm);

        return Response.ok(result).build();
    }

    @POST
    @Path("/regterminal")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response registerTerminalViaAjax(@FormParam("id") String id,
                                            @FormParam("model") String model,
                                            @FormParam("location") String location) {
        // Собираем JSON-объект
        var request = new com.example.webui.client.TerminalRegistrationRequest();
        request.id = id;
        request.data = java.util.Map.of("model", model, "location", location);

        try {
            var response = gatewayClient.registerTerminal(request);
            if (response.getStatus() == 200) {
                return Response.ok("Terminal " + id + " is successfully pending registration.").build();
            } else {
                return Response.status(response.getStatus()).entity("Error registering terminal").build();
            }
        } catch (Exception e) {
            return Response.status(500).entity("Internal error: " + e.getMessage()).build();
        }
    }
}