package com.example.gateway.dto;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для класса TerminalRegistration.
 * 
 * <p>Класс представляет собой DTO для запросов регистрации терминала,
 * получаемых от клиентов через REST API endpoints.</p>
 * 
 * <p>Содержит ID терминала и произвольные метаданные в виде Map.</p>
 * 
 * @author GigaCode
 * @see TerminalRegistration
 */
class TerminalRegistrationTest {

    /**
     * Тестирует сеттеры и геттеры класса.
     * 
     * <p>Проверяет корректность работы методов доступа к полям.
     * ID терминала - уникальный идентификатор, data - произвольные метаданные.</p>
     * 
     * <p>Сценарий использования: когда REST endpoint получает JSON и
     * десериализует его в объект TerminalRegistration.</p>
     */
    @Test
    void testGettersAndSetters() {
        // given
        TerminalRegistration registration = new TerminalRegistration();

        String expectedId = "terminal-123";
        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("name", "Test Terminal");
        expectedData.put("type", "pos");

        // when
        registration.setId(expectedId);
        registration.setData(expectedData);

        // then
        assertEquals(expectedId, registration.getId(), "ID терминала должен совпадать с установленным значением");
        assertEquals(expectedData, registration.getData(), "Метаданные должны совпадать с установленными значениями");
    }

    /**
     * Тестирует терминал с множеством полей метаданных.
     * 
     * <p>Проверяет, что класс корректно обрабатывает сложные JSON-данные
     * с несколькими полями конфигурации терминала.</p>
     * 
     * <p>Сценарий использования: когда клиент передает подробную информацию
     * о терминале: модель, локация, дополнительные параметры.</p>
     */
    @Test
    void testDataWithMultipleFields() {
        // given
        TerminalRegistration registration = new TerminalRegistration();

        String expectedId = "terminal-456";
        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("name", "POS Terminal");
        expectedData.put("type", "pos");
        expectedData.put("location", "Store A");
        expectedData.put("floor", 2);

        // when
        registration.setId(expectedId);
        registration.setData(expectedData);

        // then
        assertEquals(expectedId, registration.getId(), "ID терминала должен совпадать");
        assertNotNull(registration.getData(), "Метаданные не должны быть null");
        assertEquals(4, registration.getData().size(), "Количество полей метаданных должно быть 4");
        assertEquals("POS Terminal", registration.getData().get("name"), "Название терминала должно совпадать");
        assertEquals("pos", registration.getData().get("type"), "Тип терминала должен совпадать");
    }

    /**
     * Тестирует обработку null значений для метаданных.
     * 
     * <p>Проверяет, что объект корректно обрабатывает случай,
     * когда метаданные отсутствуют (явно установлены в null).</p>
     * 
     * <p>Сценарий использования: когда клиент отправляет только ID
     * терминала без дополнительных метаданных.</p>
     */
    @Test
    void testNullData() {
        // given
        TerminalRegistration registration = new TerminalRegistration();

        String expectedId = "terminal-789";

        // when
        registration.setId(expectedId);
        registration.setData(null);

        // then
        assertEquals(expectedId, registration.getId(), "ID терминала должен совпадать");
        assertNull(registration.getData(), "Метаданные должны быть null");
    }
}
