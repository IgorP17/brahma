package com.example.brahmawebui.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Базовый класс для всех тестов.
 * Настраивает Firefox, Selenide и логирование.
 */
public class BaseTest {

    @BeforeEach
    void setUp() {
        // Настройка Selenide - используем "firefox" для автоматического драйвера
        Configuration.browser = "firefox";
        Configuration.headless = Boolean.getBoolean("selenide.headless");
        Configuration.timeout = TestConfig.DEFAULT_TIMEOUT;
        Configuration.pageLoadTimeout = TestConfig.DEFAULT_TIMEOUT;
        Configuration.pageLoadStrategy = "eager";
        Configuration.browserSize = "1920x1080";

        // Скриншоты при ошибке
        Configuration.reportsFolder = "target/screenshots";
        Configuration.screenshots = true;
        Configuration.savePageSource = true;

        // Открытие страницы
        Selenide.open(TestConfig.WEBUI_URL);
    }

    @AfterEach
    void tearDown() {
        Selenide.closeWebDriver();
    }
}
