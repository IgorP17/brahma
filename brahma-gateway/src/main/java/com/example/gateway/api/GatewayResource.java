package com.example.gateway.api;

import com.example.gateway.dto.TerminalRegistration;
import com.example.gateway.grpc.TerminalRegistrationGrpcClient;
import com.example.gateway.kafka.TerminalRegistrationProducer;
import com.example.terminal.grpc.RegisterTerminalResponse;
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
import java.util.Collections;
import java.util.HashMap;
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

    @Inject
    TerminalRegistrationGrpcClient terminalRegistrationGrpcClient;

    @POST
    @Path("/register-grpc")
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response registerTerminalViaGrpc(
            @FormParam("id") String id,
            @FormParam("model") String model,
            @FormParam("location") String location) {

        log.infof("🌐 HTTP REQUEST: POST /register-grpc - Terminal ID: %s, Model: %s, Location: %s", id, model, location);

        var data = java.util.Map.of("model", model, "location", location);

        try {
            // Вызов gRPC
            RegisterTerminalResponse grpcResponse = terminalRegistrationGrpcClient.registerTerminal(id, data, "GRPC");

            // Сохранение в gateway.terminals
            GatewayTerminal t = new GatewayTerminal();
            t.id = id;
            // Map<String, String> → Map<String, Object> через ковариантность
            // явная копия
//            t.data = new java.util.HashMap<>(data);
            // еще безопаснее ?
            t.data = Collections.unmodifiableMap(new HashMap<>(data));
            t.status = TerminalStatus.valueOf(grpcResponse.getStatus());
            t.createdAt = LocalDateTime.now();
            t.updatedAt = null;
            t.source = "GRPC";
            t.receivedAt = LocalDateTime.now();
            t.persist();

            log.infof("✅ Terminal %s registered via gRPC, status: %s", id, grpcResponse.getStatus());
            var response = Map.of(
                    "id", id,
                    "status", grpcResponse.getStatus(),
                    "message", grpcResponse.getMessage(),
                    "receivedAt", grpcResponse.getReceivedAt()
            );
            return Response.ok(response).build();
        } catch (Exception e) {
            log.errorf("❌ gRPC call failed for terminal %s: %s", id, e.getMessage(), e);
            return Response.status(500).entity("gRPC error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/health")
    public String health() {
        return "OK";
    }
}