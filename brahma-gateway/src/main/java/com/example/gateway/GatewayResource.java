package com.example.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GatewayResource {

    @Inject
    DataSource dataSource;

    @Inject
    TerminalRegistrationProducer kafkaProducer;

    // 🔑 Правильный способ создать ObjectMapper (не newInstance!)
    private static final ObjectMapper mapper = new ObjectMapper();

    @POST
    @Path("/register")
    public Response registerTerminal(TerminalRegistration registration) {
        String id = registration.id;
        Map<String, Object> data = registration.data;
        String status = "NOT_REGISTERED";

        String dataJson;
        try {
            dataJson = mapper.writeValueAsString(data);
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"JSON serialize failed\"}").build();
        }

        // --- Выполняем JDBC вручную с транзакцией ---
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false); // начинаем транзакцию

            String sql = """
            INSERT INTO gateway.terminals (id, data, status, created_at, updated_at)
            VALUES (?, ?::jsonb, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                data = EXCLUDED.data::jsonb,
                status = EXCLUDED.status,
                updated_at = CURRENT_TIMESTAMP
            """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.setString(2, dataJson);
                ps.setString(3, status);
                ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

                ps.executeUpdate();
                conn.commit(); // коммитим БД-транзакцию
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("{\"error\":\"DB\"}").build();
        }

        // 📤 Теперь, ПОСЛЕ коммита БД, отправляем в Kafka
        System.out.println("🐛 DEBUG: Sending to Kafka AFTER DB commit...");
        try {
            kafkaProducer.send(id, dataJson);
            System.out.println("📤 Kafka: sent registration for " + id);
        } catch (Exception e) {
            System.err.println("❌ Kafka send failed: " + e.getMessage());
            // Не ломаем ответ — терминал уже сохранён
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