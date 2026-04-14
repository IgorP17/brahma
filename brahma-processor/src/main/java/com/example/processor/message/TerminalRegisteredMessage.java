package com.example.processor.message;

/**
 * DTO для отправки в Kafka топик 'terminal.registered'.
 */
public class TerminalRegisteredMessage {
    public String id;
    public String status; // "REGISTERED", "FAILED", etc.

    public TerminalRegisteredMessage() {}

    public TerminalRegisteredMessage(String id, String status) {
        this.id = id;
        this.status = status;
    }

    // Getters & Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
