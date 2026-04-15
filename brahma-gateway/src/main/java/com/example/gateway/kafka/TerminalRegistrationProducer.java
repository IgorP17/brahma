package com.example.gateway.kafka;

import com.example.gateway.message.KafkaTerminalMessage;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class TerminalRegistrationProducer {

    private static final Logger log = Logger.getLogger(TerminalRegistrationProducer.class);
    
    @Inject
    @Channel("registration-out")
    Emitter<KafkaTerminalMessage> emitter;

    public void send(String id, String dataJson) {
        log.info("📤 KAFKA OUT MESSAGE:");
        log.info("   Topic: terminal.registration");
        log.info("   Term ID: " + id);
        log.info("   Data JSON: " + dataJson);
        log.info("   Headers: (none)");
        log.info("   ------------------------");

        emitter.send(new KafkaTerminalMessage(id, dataJson));
    }
}