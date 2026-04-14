package com.example.processor.service;

import com.example.processor.entity.ProcessorTerminal;
import com.example.common.TerminalStatus;
import com.example.processor.kafka.TerminalRegisteredProducer;
import com.example.processor.message.TerminalRegistrationMessage;
import com.example.processor.utils.JsonParser;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.Map;

@ApplicationScoped
public class TerminalProcessingService {

    private static final Logger log = Logger.getLogger(TerminalProcessingService.class);

    @Inject
    TerminalRegisteredProducer kafkaProducer;

    @Transactional
    public void process(TerminalRegistrationMessage message) {
        String id = message.getId();
        String dataJson = message.getDataJson();

        log.info("📥 Processing registration for terminal: " + id);

        // 1. Parse JSON
        Map<String, Object> dataMap = JsonParser.parseToMap(dataJson);
        if (dataMap == null) {
            log.error("❌ Failed to parse data JSON for terminal: " + id);
            return;
        }

        // 2. Determine location
        String location = (String) dataMap.get("location");
        log.info("📍 Location extracted: " + location);

        // 3. Determine status
        TerminalStatus status;
        if (isLocationMoscow(location)) {
            status = TerminalStatus.REGISTERED;
        } else {
            status = TerminalStatus.REJECTED;
        }
        log.info("✅ Status determined: " + status);

        // 4. Update DB
        updateOrCreateTerminal(id, dataMap, status);

        // 5. Send to Kafka
        kafkaProducer.send(id, status.name());
        log.info("📤 Sent status update to Kafka for terminal: " + id + ", status: " + status);
    }

    private boolean isLocationMoscow(String location) {
        if (location == null) return false;
        String lowerLoc = location.toLowerCase();
        return lowerLoc.contains("moscow") || lowerLoc.contains("москва");
    }

    private void updateOrCreateTerminal(String id, Map<String, Object> data, TerminalStatus status) {
        ProcessorTerminal terminal = ProcessorTerminal.findById(id);
        if (terminal == null) {
            terminal = new ProcessorTerminal();
            terminal.id = id;
            terminal.data = data;
            terminal.status = status;
            terminal.createdAt = LocalDateTime.now();
            terminal.updatedAt = null;
            terminal.persist();
            log.info("✅ Terminal " + id + " created in DB.");
        } else {
            terminal.data = data;
            terminal.status = status;
            terminal.updatedAt = LocalDateTime.now();
            terminal.persist();
            log.info("✅ Terminal " + id + " updated in DB.");
        }
    }
}