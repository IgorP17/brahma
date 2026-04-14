package com.example.processor.kafka;

import com.example.processor.message.TerminalRegisteredMessage;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class TerminalRegisteredProducer {

    private static final Logger log = Logger.getLogger(TerminalRegisteredProducer.class);

    @Inject
    @Channel("registered-out") // ← имя канала из application.properties
    Emitter<TerminalRegisteredMessage> emitter;

    public void send(String id, String status) {
        log.info("📤 KAFKA OUT MESSAGE:");
        log.info("   Topic: terminal.registered");
        log.info("   Term ID: " + id);
        log.info("   Status: " + status);
        log.info("   Headers: (none)");
        log.info("   ------------------------");

        emitter.send(new TerminalRegisteredMessage(id, status));
    }
}
