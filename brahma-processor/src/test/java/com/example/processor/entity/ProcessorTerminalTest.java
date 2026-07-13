package com.example.processor.entity;

import com.example.common.TerminalStatus;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для класса ProcessorTerminal.
 */
class ProcessorTerminalTest {

    @Test
    void testGetLocationExists() {
        ProcessorTerminal terminal = new ProcessorTerminal();
        terminal.id = "terminal-123";
        
        Map<String, Object> data = new HashMap<>();
        data.put("location", "Moscow");
        data.put("model", "POS-2000");
        terminal.data = data;

        String location = terminal.getLocation();

        assertEquals("Moscow", location);
    }

    @Test
    void testGetLocationNotFound() {
        ProcessorTerminal terminal = new ProcessorTerminal();
        terminal.id = "terminal-456";
        
        Map<String, Object> data = new HashMap<>();
        data.put("model", "POS-2000");
        terminal.data = data;

        String location = terminal.getLocation();

        assertNull(location);
    }

    @Test
    void testGetLocationWithNullData() {
        ProcessorTerminal terminal = new ProcessorTerminal();
        terminal.id = "terminal-789";
        terminal.data = null;

        // when - null data throws NullPointerException
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            terminal.getLocation();
        });

        // then - exception should be thrown
        assertNotNull(exception.getMessage());
    }

    @Test
    void testGetModelExists() {
        ProcessorTerminal terminal = new ProcessorTerminal();
        terminal.id = "terminal-101";
        
        Map<String, Object> data = new HashMap<>();
        data.put("location", "St Petersburg");
        data.put("model", "KIOSK-500");
        terminal.data = data;

        String model = terminal.getModel();

        assertEquals("KIOSK-500", model);
    }

    @Test
    void testGetModelNotFound() {
        ProcessorTerminal terminal = new ProcessorTerminal();
        terminal.id = "terminal-202";
        
        Map<String, Object> data = new HashMap<>();
        data.put("location", "Moscow");
        terminal.data = data;

        String model = terminal.getModel();

        assertNull(model);
    }

    @Test
    void testCompleteTerminalState() {
        ProcessorTerminal terminal = new ProcessorTerminal();
        terminal.id = "terminal-complete";
        
        Map<String, Object> data = new HashMap<>();
        data.put("location", "Moscow");
        data.put("model", "TERM-PRO");
        data.put("firmware", "v2.1.0");
        terminal.data = data;
        
        terminal.status = TerminalStatus.REGISTERED;
        terminal.source = "KAFKA";

        assertEquals("Moscow", terminal.getLocation());
        assertEquals("TERM-PRO", terminal.getModel());
        assertEquals(TerminalStatus.REGISTERED, terminal.status);
        assertEquals("KAFKA", terminal.source);
        assertEquals("terminal-complete", terminal.id);
    }
}
