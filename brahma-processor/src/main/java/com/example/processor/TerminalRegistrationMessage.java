package com.example.processor;

/**
 * DTO для получения из Kafka топика 'terminal.registration'.
 */
public class TerminalRegistrationMessage {
    public String id;
    public String dataJson; // JSON-строка

    public TerminalRegistrationMessage() {}

    public TerminalRegistrationMessage(String id, String dataJson) {
        this.id = id;
        this.dataJson = dataJson;
    }

    // Getters & Setters (GigaIDE: Alt+Insert → Generate)

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }
}
