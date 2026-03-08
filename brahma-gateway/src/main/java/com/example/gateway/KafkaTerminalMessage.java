package com.example.gateway;

public class KafkaTerminalMessage {
    public String id;
    public String dataJson; // JSON-строка

    public KafkaTerminalMessage() {}

    public KafkaTerminalMessage(String id, String dataJson) {
        this.id = id;
        this.dataJson = dataJson;
    }

    // Getters & Setters (GigaIDE: Alt+Insert → Generate)

}