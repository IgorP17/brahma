package com.example.processor.message;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для класса TerminalRegistrationMessage.
 * 
 * <p>Класс представляет собой DTO для получения из Kafka topic 'terminal.registration'.</p>
 * 
 * <p>Содержит ID терминала и JSON-данные в виде строки.</p>
 * 
 * @author GigaCode
 * @see TerminalRegistrationMessage
 */
class TerminalRegistrationMessageTest {

    @Test
    void testGettersAndSetters() {
        TerminalRegistrationMessage message = new TerminalRegistrationMessage();

        String expectedId = "terminal-123";
        String expectedDataJson = "{\"name\":\"Test Terminal\",\"type\":\"pos\"}";

        message.setId(expectedId);
        message.setDataJson(expectedDataJson);

        assertEquals(expectedId, message.getId());
        assertEquals(expectedDataJson, message.getDataJson());
    }

    @Test
    void testDataWithMultipleFields() {
        TerminalRegistrationMessage message = new TerminalRegistrationMessage();

        String expectedId = "terminal-456";
        String expectedDataJson = "{\"name\":\"POS Terminal\",\"type\":\"pos\",\"location\":\"Store A\",\"floor\":2}";

        message.setId(expectedId);
        message.setDataJson(expectedDataJson);

        assertEquals(expectedId, message.getId());
        assertNotNull(message.getDataJson());
        assertTrue(message.getDataJson().contains("\"name\":\"POS Terminal\""));
        assertTrue(message.getDataJson().contains("\"location\":\"Store A\""));
    }

    @Test
    void testNullData() {
        TerminalRegistrationMessage message = new TerminalRegistrationMessage();

        String expectedId = "terminal-789";

        message.setId(expectedId);
        message.setDataJson(null);

        assertEquals(expectedId, message.getId());
        assertNull(message.getDataJson());
    }

    @Test
    void testConstructor() {
        String expectedId = "terminal-constructor";
        String expectedDataJson = "{\"name\":\"Test\",\"type\":\"kiosk\"}";

        TerminalRegistrationMessage message = new TerminalRegistrationMessage(expectedId, expectedDataJson);

        assertEquals(expectedId, message.getId());
        assertEquals(expectedDataJson, message.getDataJson());
    }
}
