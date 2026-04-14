package com.example.processor.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

import java.util.Map;

public class JsonParser {

    private static final Logger log = Logger.getLogger(JsonParser.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static Map<String, Object> parseToMap(String jsonString) {
        try {
            return MAPPER.readValue(jsonString, Map.class);
        } catch (JsonProcessingException e) {
            log.error("❌ Failed to parse JSON: " + e.getMessage());
            return null;
        }
    }
}