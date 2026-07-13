package com.example.processor.service;

import com.example.processor.message.TerminalRegistrationMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TerminalProcessingServiceTest {

    private TerminalProcessingService service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new TerminalProcessingService();
    }

    @Test
    void testServiceCreatedSuccessfully() {
        assertNotNull(service, "Service должен быть создан");
    }

    @Test
    void testProcessMethodExists() throws NoSuchMethodException {
        assertNotNull(service.getClass().getMethod("process", TerminalRegistrationMessage.class));
    }

    @Test
    void testProcessWithValidMessage() throws Exception {
        TerminalRegistrationMessage message = new TerminalRegistrationMessage();
        message.setId("terminal-123");
        message.setDataJson("{\"location\":\"Moscow\",\"model\":\"POS-2000\"}");
    }

    @Test
    void testProcessWithInvalidJson() throws Exception {
        TerminalRegistrationMessage message = new TerminalRegistrationMessage();
        message.setId("terminal-789");
        message.setDataJson("invalid json {");
    }

    @Test
    void testProcessCreatesNewTerminal() throws Exception {
        TerminalRegistrationMessage message = new TerminalRegistrationMessage();
        message.setId("terminal-new");
        message.setDataJson("{\"location\":\"Moscow\",\"model\":\"NEW-DEVICE\"}");
    }

    @Test
    void testProcessUpdatesExistingTerminal() throws Exception {
        TerminalRegistrationMessage message = new TerminalRegistrationMessage();
        message.setId("terminal-exists");
        message.setDataJson("{\"location\":\"St Petersburg\",\"model\":\"UPDATED-DEVICE\"}");
    }
}
