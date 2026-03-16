package com.example.processor;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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
    DataSource dataSource; // 🔑 Добавь инъекцию DataSource

    @Incoming("registration-in")
    public void processRegistration(TerminalRegistrationMessage message) {
        String id = message.id;
        String dataJson = message.dataJson;

        System.out.println("📥 Kafka: received registration for " + id);

        // --- Выполняем JDBC вручную с транзакцией ---
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false); // начинаем транзакцию

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
                ps.setString(3, "REGISTERED"); // статус
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
            System.err.println("❌ DB: failed to save terminal " + id + ", error: " + e.getMessage());
            return; // Не отправляем подтверждение, если БД упала
        }

        // 📤 Теперь, ПОСЛЕ коммита БД, отправляем в Kafka
        System.out.println("✅ DB: terminal " + id + " updated to REGISTERED");
        try {
            kafkaProducer.send(id, "REGISTERED");
            System.out.println("📤 Kafka: sent confirmation for " + id);
        } catch (Exception e) {
            System.err.println("❌ Kafka send failed: " + e.getMessage());
        }
    }
}