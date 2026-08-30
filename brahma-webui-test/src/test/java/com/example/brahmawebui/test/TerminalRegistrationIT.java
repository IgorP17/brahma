package com.example.brahmawebui.test;

import com.example.brahmawebui.test.pages.TerminalDeletionPage;
import com.example.brahmawebui.test.pages.TerminalRegistrationPage;
import com.example.brahmawebui.test.pages.TerminalSearchPage;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.title;
import static com.codeborne.selenide.Condition.*;

/**
 * Интеграционные тесты для проверки регистрации и удаления терминалов через веб-интерфейс.
 * 
 * Тестовые сценарии:
 * 1. Регистрация терминала через HTTP форму с location=Москва
 * 2. Удаление терминала через веб-форму, проверка отсутствия
 * 3. Регистрация терминала через gRPC форму с location=Berlin, проверка статуса Rejected
 * 4. Удаление терминала после gRPC регистрации, проверка отсутствия
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TerminalRegistrationIT extends BaseTest {

    private static final Logger log = LoggerFactory.getLogger(TerminalRegistrationIT.class);

    private TerminalSearchPage searchPage = new TerminalSearchPage();
    private TerminalRegistrationPage registrationPage = new TerminalRegistrationPage();
    private TerminalDeletionPage deletionPage = new TerminalDeletionPage();

    @BeforeEach
    void logBeforeEach() {
        log.info(">>> Начинаю тест: {}", title());
    }

    @AfterEach
    void logAfterEach() {
        log.info("<<< Завершил тест: {}", title());
    }

    // ==================== ТЕСТ 1: Регистрация через HTTP форму ====================

    /**
     * Test 1: Регистрация терминала через HTTP форму с location=Москва.
     * Проверка, что терминал появляется в обеих таблицах.
     */
    @Test
    @Order(1)
    @DisplayName("Тест 1: Регистрация терминала через HTTP форму (Москва)")
    void testHttpRegistrationWithMoscowLocation() {
        log.info("Тест 1: Регистрация через HTTP форму");

        // Регистрируем терминал через HTTP форму
        registrationPage.registerTerminalViaHttp(
                TestConfig.TERMINAL_ID,
                TestConfig.HTTP_MODEL,
                TestConfig.MOSCOW_LOCATION
        );

        // Проверяем успешную регистрацию
        registrationPage.assertHttpRegistrationSuccess();
        log.info("Терминал {} зарегистрирован через HTTP форму", TestConfig.TERMINAL_ID);

        // Ожидание обработки Kafka (асинхронная обработка)
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Ищем терминал через форму поиска
        searchPage.searchTerminal(TestConfig.TERMINAL_ID);

        // Проверяем, что терминал найден в gateway таблице
        searchPage.assertGatewayTerminalFound(TestConfig.TERMINAL_ID);
        log.info("Терминал найден в gateway.terminals через поиск");

        // Проверяем, что терминал найден в processor таблице
        searchPage.assertProcessorTerminalFound(TestConfig.TERMINAL_ID);
        log.info("Терминал найден в processor.terminals через поиск");

        // Логирование данных из таблиц
        searchPage.logGatewayTableData();
        searchPage.logProcessorTableData();

        // Проверка заголовков колонок
        searchPage.assertGatewayTableHeaders();
        searchPage.assertProcessorTableHeaders();

        // Проверка количества строк (должна быть 1 строка с терминалом)
        searchPage.assertGatewayTableRowCount(1);
        searchPage.assertProcessorTableRowCount(1);

        // Проверяем статусы терминала
        searchPage.assertGatewayTerminalStatus("Registered");
        searchPage.assertProcessorTerminalStatus("Registered");
        log.info("Статус терминала в обеих таблицах: Registered");
    }

    // ==================== ТЕСТ 2: Удаление терминала ====================

    /**
     * Test 2: Удаление терминала через веб-форму.
     * Проверка, что терминал отсутствует в обеих таблицах.
     */
    @Test
    @Order(2)
    @DisplayName("Тест 2: Удаление терминала из обеих БД")
    void testTerminalDeletion() {
        log.info("Тест 2: Удаление терминала");

        // Удаляем терминал из обеих БД
        deletionPage.deleteTerminalFromBoth(TestConfig.TERMINAL_ID);

        // Проверяем успешное удаление
        deletionPage.assertDeletionSuccess();
        log.info("Терминал {} удален через веб-форму", TestConfig.TERMINAL_ID);

        // Ищем терминал для проверки отсутствия
        searchPage.searchTerminal(TestConfig.TERMINAL_ID);

        // Проверяем, что терминал больше не найден
        searchPage.assertGatewayTableEmpty();
        searchPage.assertProcessorTableEmpty();
        log.info("Терминал {} не найден в обеих таблицах", TestConfig.TERMINAL_ID);
    }

    // ==================== ТЕСТ 3: Регистрация через gRPC форму ====================

    /**
     * Test 3: Регистрация терминала через gRPC форму с location=Berlin.
     * Проверка, что терминал появляется со статусом Rejected.
     */
    @Test
    @Order(3)
    @DisplayName("Тест 3: Регистрация терминала через gRPC форму (Berlin)")
    void testGrpcRegistrationWithBerlinLocation() {
        log.info("Тест 3: Регистрация через gRPC форму");

        // Регистрируем терминал через gRPC форму
        registrationPage.registerTerminalViaGrpc(
                TestConfig.TERMINAL_ID,
                TestConfig.GRPC_MODEL,
                TestConfig.BERLIN_LOCATION
        );

        // Проверяем успешную регистрацию
        registrationPage.assertGrpcRegistrationSuccess();
        log.info("Терминал {} зарегистрирован через gRPC форму", TestConfig.TERMINAL_ID);

        // Ожидание обработки Kafka (асинхронная обработка)
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Ищем терминал через форму поиска
        searchPage.searchTerminal(TestConfig.TERMINAL_ID);

        // Проверяем, что терминал найден в gateway таблице
        searchPage.assertGatewayTerminalFound(TestConfig.TERMINAL_ID);
        log.info("Терминал найден в gateway.terminals через поиск");

        // Проверяем статус Rejected в processor таблице
        searchPage.assertProcessorTerminalStatus("Rejected");
        log.info("Статус терминала в processor.terminals: Rejected");

        // Логирование данных из таблиц
        searchPage.logGatewayTableData();
        searchPage.logProcessorTableData();

        // Проверка заголовков колонок
        searchPage.assertGatewayTableHeaders();
        searchPage.assertProcessorTableHeaders();

        // Проверка количества строк (должна быть 1 строка с терминалом)
        searchPage.assertGatewayTableRowCount(1);
        searchPage.assertProcessorTableRowCount(1);
    }

    // ==================== ТЕСТ 4: Удаление после gRPC регистрации ====================

    /**
     * Test 4: Удаление терминала после gRPC регистрации.
     * Проверка, что терминал отсутствует в обеих таблицах.
     */
    @Test
    @Order(4)
    @DisplayName("Тест 4: Удаление терминала после gRPC регистрации")
    void testTerminalDeletionAfterGrpcRegistration() {
        log.info("Тест 4: Удаление терминала после gRPC регистрации");

        // Удаляем терминал из обеих БД
        deletionPage.deleteTerminalFromBoth(TestConfig.TERMINAL_ID);

        // Проверяем успешное удаление
        deletionPage.assertDeletionSuccess();
        log.info("Терминал {} удален через веб-форму", TestConfig.TERMINAL_ID);

        // Ищем терминал для проверки отсутствия
        searchPage.searchTerminal(TestConfig.TERMINAL_ID);

        // Проверяем, что терминал больше не найден
        searchPage.assertGatewayTableEmpty();
        searchPage.assertProcessorTableEmpty();
        log.info("Терминал {} не найден в обеих таблицах", TestConfig.TERMINAL_ID);
    }
}
