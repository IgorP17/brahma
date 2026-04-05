package com.example.processor;

import com.example.common.TerminalStatus;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        String id = message.getId();
        String dataJson = message.getDataJson();

        System.out.println("📥 KAFKA IN MESSAGE:");
        System.out.println("   Topic: terminal.registration");
        System.out.println("   Term ID: " + id);
        System.out.println("   Data JSON: " + dataJson);
        System.out.println("   Headers: (none)");
        System.out.println("   ------------------------");

        String status;

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            // Проверим, существует ли терминал
            boolean exists = false;
            String checkSql = "SELECT 1 FROM processor.terminals WHERE id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, id);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    exists = rs.next();
                }
            }

            String location = extractLocationFromJson(dataJson);

            // Проверяем, содержит ли location "Moscow" или "Москва"
            if (isLocationMoscow(location)) {
                status = TerminalStatus.REGISTERED.name(); // "REGISTERED"
            } else {
                status = TerminalStatus.REJECTED.name(); // "REJECTED"
            }

            String sql;
            if (exists) {
                // Обновляем: status, updated_at = NOW()
                sql = """
                    UPDATE processor.terminals
                    SET data = ?::jsonb, status = ?, updated_at = CURRENT_TIMESTAMP
                    WHERE id = ?
                    """;
            } else {
                // Вставляем: status, created_at = NOW(), updated_at = NULL
                sql = """
                    INSERT INTO processor.terminals (id, data, status, created_at, updated_at)
                    VALUES (?, ?::jsonb, ?, ?, NULL)
                    """;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (exists) {
                    ps.setString(1, dataJson);
                    ps.setString(2, status);
                    ps.setString(3, id);
                } else {
                    ps.setString(1, id);
                    ps.setString(2, dataJson);
                    ps.setString(3, status);
                    ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                }
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

        System.out.println("✅ DB: terminal " + id + " updated to " + status);

        // Отправляем статус обратно в Kafka
        System.out.println("📤 KAFKA OUT MESSAGE:");
        System.out.println("   Topic: terminal.registered");
        System.out.println("   Term ID: " + id);
        System.out.println("   Status: " + status);
        System.out.println("   Headers: (none)");
        System.out.println("   ------------------------");

        kafkaProducer.send(id, status);
    }

    // проверяет, содержит ли location "Moscow" или "Москва"
    private boolean isLocationMoscow(String location) {
        if (location == null) {
            return false;
        }
        String lowerLoc = location.toLowerCase();
        return lowerLoc.contains("moscow") || lowerLoc.contains("москва");
    }

    // Вспомогательный метод для извлечения location из JSON-строки
    private String extractLocationFromJson(String dataJson) {
        System.out.println("🔍 Parsing JSON: " + dataJson);  // ← отладка

        int locStart = dataJson.indexOf("\"location\"");
        if (locStart == -1) {
            System.out.println("❌ 'location' key not found");
            return null;
        }

        int colon = dataJson.indexOf(":", locStart);
        if (colon == -1) {
            System.out.println("❌ No ':' after 'location'");
            return null;
        }

        int quote1 = dataJson.indexOf("\"", colon);
        if (quote1 == -1) {
            System.out.println("❌ No opening quote after ':'");
            return null;
        }

        int quote2 = dataJson.indexOf("\"", quote1 + 1);
        if (quote2 == -1) {
            System.out.println("❌ No closing quote");
            return null;
        }

        String extracted = dataJson.substring(quote1 + 1, quote2);
        System.out.println("✅ Extracted location: '" + extracted + "'");

        return extracted;
    }
}