package com.example.gateway;

public class TerminalRegisteredMessage {
    public String id;
    public String status; // "REGISTERED", "FAILED", etc.

    public TerminalRegisteredMessage() {}

    public TerminalRegisteredMessage(String id, String status) {
        this.id = id;
        this.status = status;
    }
}