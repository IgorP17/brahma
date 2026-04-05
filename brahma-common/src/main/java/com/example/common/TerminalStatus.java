package com.example.common;

public enum TerminalStatus {
    IN_PROCESS,      // Новый терминал, обрабатывается
    REGISTERED,      // Успешно зарегистрирован
    REJECTED,        // Отклонён
    ACTIVE,          // Активен
    INACTIVE         // Неактивен
}