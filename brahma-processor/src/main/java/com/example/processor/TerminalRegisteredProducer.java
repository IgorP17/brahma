package com.example.processor;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TerminalRegisteredProducer {

    @Inject
    @Channel("registered-out") // ← имя канала из application.properties
    Emitter<TerminalRegisteredMessage> emitter;

    public void send(String id, String status) {
        emitter.send(new TerminalRegisteredMessage(id, status));
    }
}
