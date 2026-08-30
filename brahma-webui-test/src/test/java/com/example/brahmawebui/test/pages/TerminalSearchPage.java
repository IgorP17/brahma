package com.example.brahmawebui.test.pages;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ElementsCollection;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page Object для поиска терминалов.
 */
public class TerminalSearchPage {

    private static final Logger log = LoggerFactory.getLogger(TerminalSearchPage.class);

    private SelenideElement searchInput = $("#search-id");
    private SelenideElement searchButton = $("#search-section button");
    private SelenideElement gatewayTable = $("#gateway-terminals-table");
    private SelenideElement processorTable = $("#processor-terminals-table");
    private SelenideElement gatewayHeaderRow = $("table:first-of-type thead tr");
    private SelenideElement processorHeaderRow = $("table:nth-of-type(2) thead tr");
    private ElementsCollection gatewayRows = $$("table:first-of-type tbody tr");
    private ElementsCollection processorRows = $$("table:nth-of-type(2) tbody tr");

    public void searchTerminal(String terminalId) {
        searchInput.clear();
        searchInput.setValue(terminalId);
        searchButton.click();
    }

    public void assertGatewayTerminalFound(String terminalId) {
        gatewayTable.should(appear).shouldHave(com.codeborne.selenide.Condition.text(terminalId));
    }

    public void assertProcessorTerminalFound(String terminalId) {
        processorTable.should(appear).shouldHave(com.codeborne.selenide.Condition.text(terminalId));
    }

    public void assertGatewayTableEmpty() {
        gatewayTable.shouldHave(com.codeborne.selenide.Condition.text("NOT_FOUND"));
    }

    public void assertProcessorTableEmpty() {
        processorTable.shouldHave(com.codeborne.selenide.Condition.text("NOT_FOUND"));
    }

    /**
     * Проверить статус терминала в gateway таблице.
     */
    public void assertGatewayTerminalStatus(String expectedStatus) {
        gatewayTable.shouldHave(com.codeborne.selenide.Condition.text(expectedStatus));
    }

    /**
     * Проверить статус терминала в processor таблице.
     */
    public void assertProcessorTerminalStatus(String expectedStatus) {
        processorTable.shouldHave(com.codeborne.selenide.Condition.text(expectedStatus));
    }

    public String getGatewayTableText() {
        return gatewayTable.getText();
    }

    public String getProcessorTableText() {
        return processorTable.getText();
    }

    /**
     * Проверить заголовки колонок gateway таблицы.
     */
    public void assertGatewayTableHeaders() {
        String headers = gatewayHeaderRow.getText();
        log.info("Gateway headers: {}", headers);
        Assertions.assertTrue(headers.contains("ID"), "Gateway header missing 'ID'");
        Assertions.assertTrue(headers.contains("Model"), "Gateway header missing 'Model'");
        Assertions.assertTrue(headers.contains("Location"), "Gateway header missing 'Location'");
        Assertions.assertTrue(headers.contains("Status"), "Gateway header missing 'Status'");
        Assertions.assertTrue(headers.contains("Source"), "Gateway header missing 'Source'");
    }

    /**
     * Проверить заголовки колонок processor таблицы.
     */
    public void assertProcessorTableHeaders() {
        String headers = processorHeaderRow.getText();
        log.info("Processor headers: {}", headers);
        Assertions.assertTrue(headers.contains("ID"), "Processor header missing 'ID'");
        Assertions.assertTrue(headers.contains("Model"), "Processor header missing 'Model'");
        Assertions.assertTrue(headers.contains("Location"), "Processor header missing 'Location'");
        Assertions.assertTrue(headers.contains("Status"), "Processor header missing 'Status'");
        Assertions.assertTrue(headers.contains("Source"), "Processor header missing 'Source'");
    }

    /**
     * Проверить количество строк в gateway таблице.
     */
    public void assertGatewayTableRowCount(int expectedCount) {
        int actualCount = gatewayRows.size();
        log.info("Gateway rows: {} (expected: {})", actualCount, expectedCount);
        Assertions.assertEquals(expectedCount, actualCount, "Gateway table row count mismatch");
    }

    /**
     * Проверить количество строк в processor таблице.
     */
    public void assertProcessorTableRowCount(int expectedCount) {
        int actualCount = processorRows.size();
        log.info("Processor rows: {} (expected: {})", actualCount, expectedCount);
        Assertions.assertEquals(expectedCount, actualCount, "Processor table row count mismatch");
    }

    /**
     * Логировать данные из gateway таблицы.
     */
    public void logGatewayTableData() {
        log.info("=== Gateway Terminals Table ===");
        String tableText = gatewayTable.getText();
        if (tableText.contains("NOT_FOUND")) {
            log.info("Gateway: Terminal not found");
        } else {
            log.info("Gateway: {}", tableText);
        }
    }

    /**
     * Логировать данные из processor таблицы.
     */
    public void logProcessorTableData() {
        log.info("=== Processor Terminals Table ===");
        String tableText = processorTable.getText();
        if (tableText.contains("NOT_FOUND")) {
            log.info("Processor: Terminal not found");
        } else {
            log.info("Processor: {}", tableText);
        }
    }
}
