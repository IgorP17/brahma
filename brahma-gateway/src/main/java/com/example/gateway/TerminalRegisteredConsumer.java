package com.example.gateway;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class TerminalRegisteredConsumer {

    @Inject
    DataSource dataSource;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Incoming("registered-in")
    public void updateStatus(String jsonString) {  // Принимаем строку
        TerminalRegisteredMessage msg;
        try {
            msg = mapper.readValue(jsonString, TerminalRegisteredMessage.class);
        } catch (Exception e) {
            System.err.println("❌ Cannot deserialize message: " + jsonString);
            return;
        }

        String id = msg.id;
        String status = msg.status;

        System.out.println("🔄 Kafka: received status update for " + id + " → " + status);

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            String sql = """
                UPDATE gateway.terminals
                SET status = ?, updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setString(2, id);
                int rows = ps.executeUpdate();
                if (rows == 0) {
                    System.out.println("ℹ️  Skip update: terminal not found: " + id);
                } else {
                    conn.commit();
                    System.out.println("✅ DB: gateway.terminals." + id + " updated to " + status);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ DB update failed for " + id);
        }
    }
}