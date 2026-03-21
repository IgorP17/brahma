package com.example.gateway;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TerminalRegistrationProducer {

    @Inject
    @Channel("registration-out")
    Emitter<KafkaTerminalMessage> emitter;

    public void send(String id, String dataJson) {
        System.out.println("📤 KAFKA OUT MESSAGE:");
        System.out.println("   Topic: terminal.registration");
        System.out.println("   Term ID: " + id);
        System.out.println("   Data JSON: " + dataJson);
        System.out.println("   Headers: (none)");
        System.out.println("   ------------------------");

        emitter.send(new KafkaTerminalMessage(id, dataJson));
    }
}