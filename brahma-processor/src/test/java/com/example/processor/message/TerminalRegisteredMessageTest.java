package com.example.processor.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для класса TerminalRegisteredMessage.
 */
class TerminalRegisteredMessageTest {

    @Test
    void testGettersAndSetters() {
        TerminalRegisteredMessage message = new TerminalRegisteredMessage();

        String expectedId = "terminal-123";
        String expectedStatus = "REGISTERED";

        message.setId(expectedId);
        message.setStatus(expectedStatus);

        assertEquals(expectedId, message.getId());
        assertEquals(expectedStatus, message.getStatus());
    }

    @Test
    void testRejectedStatus() {
        TerminalRegisteredMessage message = new TerminalRegisteredMessage();

        String expectedId = "terminal-456";
        String expectedStatus = "REJECTED";

        message.setId(expectedId);
        message.setStatus(expectedStatus);

        assertEquals(expectedId, message.getId());
        assertEquals(expectedStatus, message.getStatus());
    }

    @Test
    void testConstructor() {
        String expectedId = "terminal-constructor";
        String expectedStatus = "REGISTERED";

        TerminalRegisteredMessage message = new TerminalRegisteredMessage(expectedId, expectedStatus);

        assertEquals(expectedId, message.getId());
        assertEquals(expectedStatus, message.getStatus());
    }

    @Test
    void testEmptyId() {
        TerminalRegisteredMessage message = new TerminalRegisteredMessage();

        message.setId("");
        message.setStatus("REGISTERED");

        assertEquals("", message.getId());
        assertEquals("REGISTERED", message.getStatus());
    }
}
