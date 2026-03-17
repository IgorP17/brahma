package com.example.gateway;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TerminalRegistrationProducer {

    @Inject
    @Channel("registration-out") // имя канала — совпадает с application.properties
    Emitter<KafkaTerminalMessage> emitter;

    public void send(String id, String dataJson) {
        emitter.send(new KafkaTerminalMessage(id, dataJson));
    }
}