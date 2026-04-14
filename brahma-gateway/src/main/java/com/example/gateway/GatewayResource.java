package com.example.gateway;

import jakarta.transaction.Transactional;
import com.example.common.TerminalStatus;
import com.example.gateway.entity.GatewayTerminal;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.Map;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class GatewayResource {

    private static final Logger log = Logger.getLogger(GatewayResource.class);

    @Inject
    TerminalRegistrationProducer kafkaProducer;

    private static final ObjectMapper mapper = new ObjectMapper();

    @POST
    @Path("/register")
    @Transactional
    public Response registerTerminal(TerminalRegistration registration) {
        String id = registration.id;
        Map<String, Object> data = registration.data;
        TerminalStatus status = TerminalStatus.IN_PROCESS;

        log.info("🌐 HTTP: received registration request for " + id);

        // Проверим, есть ли терминал
        GatewayTerminal terminal = GatewayTerminal.findById(id);
        if (terminal == null) {
            terminal = new GatewayTerminal();
            terminal.id = id;
            terminal.data = data;
            terminal.status = status;
            terminal.createdAt = LocalDateTime.now();
            terminal.updatedAt = null;  // 🔥 NULL при создании
            terminal.persist();
        } else {
            terminal.data = data;
            terminal.status = status;
            terminal.updatedAt = null;  // 🔥 Сброс при обновлении через HTTP
            terminal.persist();
        }

        // Отправить в Kafka
        String dataJson;
        try {
            dataJson = mapper.writeValueAsString(data);
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"JSON serialize failed\"}").build();
        }

        log.info("🐛 Sending to Kafka AFTER DB commit...");
        try {
            kafkaProducer.send(id, dataJson);
            log.info("📤 Kafka: sent registration for " + id);
        } catch (Exception e) {
            log.error("❌ Kafka send failed: " + e.getMessage(), e);
        }

        return Response.ok()
                .entity("{\"status\":\"pending\",\"id\":\"" + id + "\"}")
                .build();
    }

    @GET
    @Path("/health")
    public String health() {
        return "OK";
    }
}