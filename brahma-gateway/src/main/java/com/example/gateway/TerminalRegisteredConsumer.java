package com.example.gateway;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

@ApplicationScoped
public class TerminalRegisteredConsumer {

    private static final Logger log = Logger.getLogger(TerminalRegisteredConsumer.class);

    @Inject
    DataSource dataSource;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Incoming("registered-in")
    public void updateStatus(String jsonString) {  // Принимаем строку
        TerminalRegisteredMessage msg;
        try {
            msg = mapper.readValue(jsonString, TerminalRegisteredMessage.class);
        } catch (Exception e) {
            log.error("❌ Cannot deserialize message: " + jsonString);
            return;
        }

        String id = msg.id;
        String status = msg.status;

        log.info("🔄 Kafka: received status update for " + id + " → " + status);

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
                    log.info("ℹ️  Skip update: terminal not found: " + id);
                } else {
                    conn.commit();
                    log.info("✅ DB: gateway.terminals." + id + " updated to " + status);
                }
            }
        } catch (Exception e) {
            log.error("❌ DB update failed for " + id);
            log.error(e.getMessage());
        }
    }
}