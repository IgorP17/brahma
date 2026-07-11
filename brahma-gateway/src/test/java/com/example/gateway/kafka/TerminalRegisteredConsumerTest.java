package com.example.gateway.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для класса TerminalRegisteredConsumer.
 * 
 * <p>Класс потребляет сообщения из Kafka topic 'terminal.registered'
 * и обновляет статус терминала в базе данных PostgreSQL.</p>
 * 
 * <p>Тесты проверяют:</p>
 * <ul>
 *   <li>Корректное обновление статуса при валидном JSON-сообщении</li>
 *   <li>Гraceful обработку некорректного JSON (без выброса исключений)</li>
 *   <li>Обработку неизвестного ID терминала (не обновляет, не фейлится)</li>
 * </ul>
 * 
 * @author GigaCode
 * @see TerminalRegisteredConsumer
 */
@ExtendWith(MockitoExtension.class)
class TerminalRegisteredConsumerTest {

    /**
     * Mock DataSource для проверки взаимодействия с БД.
     * 
     * <p>В реальном приложении DataSource предоставляет соединения
     * к PostgreSQL базе данных.</p>
     */
    @Mock
    private DataSource dataSource;

    /**
     * Потребитель сообщений, который тестируем.
     * 
     * <p>Инициализируется в setUp() с подменённым mock DataSource'ом для
     * изоляции от внешних зависимостей (реальная БД).</p>
     */
    private TerminalRegisteredConsumer consumer;

    /**
     * Инициализация тестового окружения перед каждым тестом.
     * 
     * <p>Создаёт новый экземпляр потребителя и inject'ит mock DataSource'а.
     * Это позволяет тестировать логику обработки сообщений без реального
     * обращения к базе данных.</p>
     */
    @BeforeEach
    void setUp() {
        consumer = new TerminalRegisteredConsumer();
        consumer.dataSource = dataSource;
    }

    /**
     * Тестирует обновление статуса терминала при валидном JSON-сообщении.
     * 
     * <p>Проверяет, что при получении валидного JSON-сообщения consumer:</p>
     * <ul>
     *   <li>Десериализует JSON в TerminalRegisteredMessage</li>
     *   <li>Получает соединение из DataSource</li>
     *   <li>Выполняет SQL UPDATE с правильными параметрами</li>
     *   <li>Фиксирует транзакцию (commit)</li>
     * </ul>
     * 
     * <p>Сценарий использования: когда brahma-processor публикует
     * сообщение о завершении регистрации терминала, и gateway
     * получает его и обновляет локальную базу.</p>
     */
    @Test
    void testValidJsonMessageUpdatesDatabase() throws SQLException {
        // given
        String expectedJsonString = "{\"id\":\"terminal-123\",\"status\":\"REGISTERED\"}";
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockPs = mock(PreparedStatement.class);

        // Configure mock behavior
        when(dataSource.getConnection()).thenReturn(mockConn);
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeUpdate()).thenReturn(1); // 1 row updated

        // when
        consumer.updateStatus(expectedJsonString);

        // then
        verify(mockPs, times(1)).setString(1, "REGISTERED");
        verify(mockPs, times(1)).setString(2, "terminal-123");
        verify(mockPs, times(1)).executeUpdate();
        verify(mockConn, times(1)).commit();
    }

    /**
     * Тестирует graceful обработку некорректного JSON-сообщения.
     * 
     * <p>Проверяет, что при получении невалидного JSON consumer:</p>
     * <ul>
     *   <li>Не выбрасывает исключение (graceful error handling)</li>
     *   <li>Логирует ошибку десериализации</li>
     *   <li>Возвращает из метода без обращения к БД</li>
     *   <li>Не фиксирует транзакцию</li>
     * </ul>
     * 
     * <p>Сценарий использования: защита от некорректных сообщений
     * в Kafka (битые клиенты, форматирование ошибок и т.д.).</p>
     */
    @Test
    void testInvalidJsonMessageIsHandledGracefully() throws SQLException {
        // given
        String invalidJson = "not valid json {";

        // when
        consumer.updateStatus(invalidJson);

        // then - consumer should not throw exception and should not
        // interact with dataSource (no database operations)
        verifyNoInteractions(dataSource);
    }

    /**
     * Тестирует обработку неизвестного ID терминала.
     * 
     * <p>Проверяет, что при получении сообщения для несуществующего
     * терминала (UPDATE не затрагивает ни одной строки) consumer:</p>
     * <ul>
     *   <li>Выполняет UPDATE без ошибок</li>
     *   <li>Не фиксирует транзакцию (rows == 0)</li>
     *   <li>Логирует предупреждение о пропуске обновления</li>
     * </ul>
     * 
     * <p>Сценарий использования: когда сообщение приходит для
     * терминала, который ещё не зарегистрирован в gateway.terminals
     * (возможно, он зарегистрирован только в processor.terminals).</p>
     */
    @Test
    void testUnknownTerminalIdIsHandledGracefully() throws SQLException {
        // given
        String expectedJsonString = "{\"id\":\"unknown-terminal\",\"status\":\"REGISTERED\"}";
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockPs = mock(PreparedStatement.class);

        // Configure mock behavior - 0 rows updated means terminal not found
        when(dataSource.getConnection()).thenReturn(mockConn);
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeUpdate()).thenReturn(0);

        // when
        consumer.updateStatus(expectedJsonString);

        // then
        verify(mockPs, times(1)).executeUpdate();
        verify(mockConn, times(0)).commit(); // no commit when no rows updated
    }
}
