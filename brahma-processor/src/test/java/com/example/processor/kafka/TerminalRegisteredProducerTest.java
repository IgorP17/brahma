package com.example.processor.kafka;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TerminalRegisteredProducerTest {

    @Test
    void testProducerCreatedSuccessfully() {
        TerminalRegisteredProducer producer = new TerminalRegisteredProducer();
        assertNotNull(producer, "Producer должен быть создан");
    }

    @Test
    void testProducerHasSendMethod() throws NoSuchMethodException {
        TerminalRegisteredProducer producer = new TerminalRegisteredProducer();
        assertNotNull(producer.getClass().getMethod("send", String.class, String.class));
    }

    @Test
    void testSendMethodAcceptsParameters() throws NoSuchMethodException {
        TerminalRegisteredProducer producer = new TerminalRegisteredProducer();
        var method = producer.getClass().getMethod("send", String.class, String.class);
        assertNotNull(method);
    }
}
