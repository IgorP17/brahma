package com.example.processor.utils;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для класса JsonParser.
 */
class JsonParserTest {

    @Test
    void testParseSingleField() {
        String json = "{\"name\":\"test\"}";
        Map<String, Object> result = JsonParser.parseToMap(json);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test", result.get("name"));
    }

    @Test
    void testParseMultipleFields() {
        String json = "{\"name\":\"test\",\"location\":\"Moscow\",\"floor\":2}";
        Map<String, Object> result = JsonParser.parseToMap(json);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("test", result.get("name"));
        assertEquals("Moscow", result.get("location"));
        assertEquals(2, result.get("floor"));
    }

    @Test
    void testParseEmptyObject() {
        String json = "{}";
        Map<String, Object> result = JsonParser.parseToMap(json);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testParseInvalidJson() {
        String json = "not valid json {";
        Map<String, Object> result = JsonParser.parseToMap(json);

        assertNull(result);
    }

    @Test
    void testParseNullInput() {
        // given
        String json = null;

        // when - null input throws IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            JsonParser.parseToMap(json);
        });

        // then - exception should be thrown
        assertNotNull(exception.getMessage());
    }
}
