package com.example.gateway.kafka;

import com.example.gateway.message.KafkaTerminalMessage;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для класса TerminalRegistrationProducer.
 * 
 * <p>Класс отвечает за отправку сообщений в Kafka topic 'terminal.registration'
 * при получении запроса регистрации терминала через REST API.</p>
 * 
 * <p>Использует Reactive Messaging с Emitter для асинхронной отправки сообщений
 * в Kafka. Тесты проверяют корректность создания сообщений и вызова emitter'а.</p>
 * 
 * @author GigaCode
 * @see TerminalRegistrationProducer
 */
@ExtendWith(MockitoExtension.class)
class TerminalRegistrationProducerTest {

    /**
     * Mock объект Emitter для проверки вызова метода send().
     * 
     * <p>В реальном приложении Emitter управляет отправкой сообщений
     * в Kafka через MicroProfile Reactive Messaging.</p>
     */
    @Mock
    private Emitter<KafkaTerminalMessage> emitter;

    /**
     * Производитель сообщений, который тестируем.
     * 
     * <p>Инициализируется в setUp() с подменённым mock emitter'ом для
     * изоляции от внешних зависимостей (Kafka broker).</p>
     */
    private TerminalRegistrationProducer producer;

    /**
     * Инициализация тестового окружения перед каждым тестом.
     * 
     * <p>Создаёт новый экземпляр производителя и inject'ит mock emitter'а.
     * Это позволяет тестировать логику производства сообщений без реальной
     * отправки в Kafka.</p>
     */
    @BeforeEach
    void setUp() {
        producer = new TerminalRegistrationProducer();
        producer.emitter = emitter;
    }

    /**
     * Тестирует отправку сообщения о регистрации терминала.
     * 
     * <p>Проверяет, что при вызове метода send() производитель:</p>
     * <ul>
     *   <li>Создаёт KafkaTerminalMessage с переданными ID и JSON-данными</li>
     *   <li>Вызывает emitter.send() ровно один раз</li>
     *   <li>Передаёт сформированное сообщение в emitter</li>
     * </ul>
     * 
     * <p>Сценарий использования: когда REST endpoint вызывает producer
     * для отправки сообщения в Kafka после сохранения терминала в БД.</p>
     */
    @Test
    void testSendShouldEmitMessage() {
        // given
        String expectedId = "terminal-123";
        String expectedDataJson = "{\"name\":\"test\"}";

        // when
        producer.send(expectedId, expectedDataJson);

        // then
        verify(emitter, times(1)).send(any(KafkaTerminalMessage.class));
    }

    /**
     * Тестирует отправку сообщения с различными данными.
     * 
     * <p>Проверяет, что производитель корректно обрабатывает
     * разные наборы данных при отправке сообщений.</p>
     * 
     * <p>Сценарий использования: когда различные клиенты могут
     * передавать разные структуры метаданных при регистрации.</p>
     */
    @Test
    void testSendWithDifferentData() {
        // given
        String expectedId = "terminal-456";
        String expectedDataJson = "{\"name\":\"test2\",\"type\":\"pos\"}";

        // when
        producer.send(expectedId, expectedDataJson);

        // then
        verify(emitter, times(1)).send(any(KafkaTerminalMessage.class));
    }
}
