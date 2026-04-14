package com.example.processor.consumer;

import com.example.processor.message.TerminalRegistrationMessage;
import com.example.processor.service.TerminalProcessingService;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.annotations.Blocking;
import org.eclipse.microprofile.reactive.messaging.Incoming;

public class TerminalRegistrationConsumer {

    private static final Logger log = Logger.getLogger(TerminalRegistrationConsumer.class);

    @Inject
    TerminalProcessingService processingService;

    @Incoming("registration-in")
    @Blocking
    public void processRegistration(TerminalRegistrationMessage message) {
        log.info("📥 Received Kafka message for terminal: " + message.getId());
        log.info("Data: " + message.getDataJson());

        try {
            processingService.process(message);
            log.info("✅ Successfully processed terminal: " + message.getId());
        } catch (Exception e) {
            log.error("❌ Error processing terminal " + message.getId() + ": " + e.getMessage(), e);
        }
    }
}