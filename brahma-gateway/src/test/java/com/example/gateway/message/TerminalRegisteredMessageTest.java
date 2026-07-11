package com.example.gateway.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для класса TerminalRegisteredMessage.
 * 
 * <p>Класс представляет собой DTO для подтверждений регистрации терминала,
 * передаваемых из сервиса brahma-processor обратно в brahma-gateway.</p>
 * 
 * <p>Сообщение содержит ID терминала и его статус (REGISTERED, FAILED и т.д.).</p>
 * 
 * @author GigaCode
 * @see TerminalRegisteredMessage
 */
class TerminalRegisteredMessageTest {

    /**
     * Тестирует конструктор по умолчанию.
     * 
     * <p>Проверяет инициализацию полей значением null при создании объекта
     * без параметров, что необходимо для JSON-десериализации.</p>
     */
    @Test
    void testDefaultConstructor() {
        // when
        TerminalRegisteredMessage message = new TerminalRegisteredMessage();

        // then
        assertNull(message.id, "ID терминала должен быть null после создания через конструктор по умолчанию");
        assertNull(message.status, "Статус должен быть null после создания через конструктор по умолчанию");
    }

    /**
     * Тестирует параметризованный конструктор.
     * 
     * <p>Проверяет корректную установку ID терминала и статуса
     * при создании сообщения через конструктор с параметрами.</p>
     * 
     * <p>Сценарий использования: когда brahma-processor формирует
     * сообщение о завершении регистрации терминала.</p>
     */
    @Test
    void testParameterizedConstructor() {
        // given
        String expectedId = "terminal-123";
        String expectedStatus = "REGISTERED";

        // when
        TerminalRegisteredMessage message = new TerminalRegisteredMessage(expectedId, expectedStatus);

        // then
        assertEquals(expectedId, message.id, "ID терминала должен совпадать с переданным в конструктор");
        assertEquals(expectedStatus, message.status, "Статус должен совпадать с переданным в конструктор");
    }

    /**
     * Тестирует сеттеры и геттеры полей класса.
     * 
     * <p>Проверяет возможность модификации сообщения после его создания.
     * Это может быть полезно при повторной обработке или пересоздании сообщения.</p>
     */
    @Test
    void testSettersAndGetters() {
        // given
        TerminalRegisteredMessage message = new TerminalRegisteredMessage();

        String expectedId = "terminal-789";
        String expectedStatus = "FAILED";

        // when
        message.id = expectedId;
        message.status = expectedStatus;

        // then
        assertEquals(expectedId, message.id, "Геттер ID должен возвращать установленное значение");
        assertEquals(expectedStatus, message.status, "Геттер статуса должен возвращать установленное значение");
    }
}
