package com.example.gateway.dto;

import java.util.Map;

public class TerminalRegistration {
    public String id;
    public Map<String, Object> data;
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}
