package com.example.gateway;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
// Убираем: import jakarta.transaction.Transactional;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@ApplicationScoped
public class TerminalRegisteredConsumer {

    @Inject
    DataSource dataSource;

    // Убираем: @Transactional
    @Incoming("registered-in")
    public void onTerminalRegistered(TerminalRegisteredMessage message) {
        String id = message.id;
        String status = message.status;

        System.out.println("📥 Kafka: received registration confirmation for " + id + " → status: " + status);

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false); // начинаем транзакцию

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
                    System.err.println("⚠️  No terminal found with id: " + id);
                }
                conn.commit(); // коммитим БД-транзакцию
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ DB update failed for " + id + ": " + e.getMessage());
        }

        System.out.println("✅ DB: terminal " + id + " status updated to " + status);
    }
}