package com.example.gateway.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для класса KafkaTerminalMessage.
 * 
 * <p>Класс представляет собой DTO для сообщений Kafka, передаваемых
 * между сервисами brahma-gateway и brahma-processor.</p>
 * 
 * <p>Проверяет корректность работы конструкторов, геттеров и сеттеров.</p>
 * 
 * @author GigaCode
 * @see KafkaTerminalMessage
 */
class KafkaTerminalMessageTest {

    /**
     * Тестирует конструктор по умолчанию.
     * 
     * <p>Проверяет, что при создании сообщения без параметров
     * все поля инициализируются значением null.</p>
     * 
     * <p>Это важно для JSON-десериализации через Jackson, 
     * которая требует public无参 конструктор.</p>
     */
    @Test
    void testDefaultConstructor() {
        // when
        KafkaTerminalMessage message = new KafkaTerminalMessage();

        // then
        assertNull(message.id, "ID должен быть null после создания через конструктор по умолчанию");
        assertNull(message.dataJson, "dataJson должен быть null после создания через конструктор по умолчанию");
    }

    /**
     * Тестирует параметризованный конструктор.
     * 
     * <p>Проверяет, что при создании сообщения с параметрами
     * значения корректно устанавливаются в поля объекта.</p>
     * 
     * <p>Сценарий использования: когда необходимо создать сообщение
     * для отправки в Kafka с уже известными значениями ID и JSON-данных.</p>
     */
    @Test
    void testParameterizedConstructor() {
        // given
        String expectedId = "terminal-123";
        String expectedDataJson = "{\"name\":\"test\"}";

        // when
        KafkaTerminalMessage message = new KafkaTerminalMessage(expectedId, expectedDataJson);

        // then
        assertEquals(expectedId, message.id, "ID должен совпадать с переданным в конструктор");
        assertEquals(expectedDataJson, message.dataJson, "dataJson должен совпадать с переданным в конструктор");
    }

    /**
     * Тестирует сеттеры и геттеры полей класса.
     * 
     * <p>Проверяет, что после установки значений через сеттеры,
     * геттеры возвращают корректные значения.</p>
     * 
     * <p>Сценарий использования: когда необходимо модифицировать
     * сообщение после его создания (например, при повторной отправке).</p>
     */
    @Test
    void testSettersAndGetters() {
        // given
        KafkaTerminalMessage message = new KafkaTerminalMessage();

        String expectedId = "terminal-456";
        String expectedDataJson = "{\"name\":\"test2\"}";

        // when
        message.id = expectedId;
        message.dataJson = expectedDataJson;

        // then
        assertEquals(expectedId, message.id, "Геттер ID должен возвращать установленное значение");
        assertEquals(expectedDataJson, message.dataJson, "Геттер dataJson должен возвращать установленное значение");
    }
}
