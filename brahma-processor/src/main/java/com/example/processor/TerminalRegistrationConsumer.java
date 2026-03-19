package com.example.processor;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@ApplicationScoped
public class TerminalRegistrationConsumer {

    @Inject
    TerminalRegisteredProducer kafkaProducer;

    @Inject
    DataSource dataSource;

    @Incoming("registration-in")
    public void processRegistration(TerminalRegistrationMessage message) {
        String id = message.id;
        String dataJson = message.dataJson;

        // 🔥 ЛОГ ПРИЁМА ИЗ KAFKA — как HTTP REQUEST
        System.out.println("📥 KAFKA IN MESSAGE:");
        System.out.println("   Topic: terminal.registration");
        System.out.println("   Term ID: " + id);
        System.out.println("   Data JSON: " + dataJson);
        System.out.println("   Headers: (none)");
        System.out.println("   ------------------------");

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            String sql = """
                INSERT INTO processor.terminals (id, data, status, created_at, updated_at)
                VALUES (?, ?::jsonb, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    data = EXCLUDED.data::jsonb,
                    status = EXCLUDED.status,
                    updated_at = CURRENT_TIMESTAMP
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.setString(2, dataJson);
                ps.setString(3, "REGISTERED");
                ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

                ps.executeUpdate();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ DB update failed for " + id + ": " + e.getMessage());
            return;
        }

        System.out.println("✅ DB: terminal " + id + " updated to REGISTERED");

        // 🔥 ЛОГ ОТПРАВКИ В KAFKA — как KAFKA OUT
        System.out.println("📤 KAFKA OUT MESSAGE:");
        System.out.println("   Topic: terminal.registered");
        System.out.println("   Term ID: " + id);
        System.out.println("   Status: REGISTERED");
        System.out.println("   Headers: (none)");
        System.out.println("   ------------------------");

        kafkaProducer.send(id, "REGISTERED");
    }
}