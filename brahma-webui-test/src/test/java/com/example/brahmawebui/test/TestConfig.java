package com.example.brahmawebui.test;

/**
 * Конфигурация тестовых данных и параметров.
 */
public class TestConfig {

    private TestConfig() {}

    // URL веб-интерфейса
    public static final String WEBUI_URL = System.getProperty("webui.url", "http://minikube:30882");

    // Тестовые данные терминала
    public static final String TERMINAL_ID = "TERM-00027";
    public static final String HTTP_MODEL = "TEST-001";
    public static final String GRPC_MODEL = "TEST-002";
    public static final String MOSCOW_LOCATION = "Москва";
    public static final String BERLIN_LOCATION = "Berlin";

    // Ожидания (мс)
    public static final long DEFAULT_TIMEOUT = 5000;
    public static final long ASYNC_OPERATION_TIMEOUT = 15000;
}
