# Design: Integration Tests Stage

## Context

Проект использует Jenkins pipeline для деплоя трёх микросервисов (WebUI, Gateway, Processor). Интеграционные тесты уже существуют в модуле `brahma-webui-test` — это 4 Selenium/Selenide теста (JUnit 5), проверяющие полный цикл работы терминалов через веб-интерфейс.

## Decisions

### 1. Headless-режим для Jenkins
**Решение:** Запускать тесты с `-Dselenide.headless=true`

**Обоснование:** Jenkins работает на CI-сервере без GUI. Selenide поддерживает headless-режим через флаг системы.

### 2. Отдельный stage после деплоя
**Решение:** Новый stage `Integration Tests` после всех deploy stages

**Обоснование:**
- Деплой stages уже содержат health-check'и (curl rollout status)
- Тесты должны запускаться только когда все сервисы полностью готовы
- Отдельный stage позволяет включить/выключить тесты независимо от деплоя

### 3. `mvn verify` вместо `mvn test`
**Решение:** Использовать `mvn verify`

**Обоснование:**
- surefire-plugin настроен на `**/*IT.java` — это integration tests
- `verify` включает и `test`, и `integration-test` фазы
- Соответствует Maven conventions (IT-тесты в `verify`)

### 4. Без дополнительных зависимостей
**Решение:** Не добавлять новые зависимости в Jenkinsfile

**Обоснование:** `brahma-webui-test` уже имеет все зависимости (Selenide, WebDriverManager, JUnit 5) в pom.xml.

## Risks

| Risk | Mitigation |
|------|-----------|
| Тесты падают из-за таймаутов Kafka | Существующие тесты уже содержат `Thread.sleep(15000)` для ожидания обработки |
| WebDriverManager не может скачать драйвер | WebDriverManager кэширует драйверы; можно добавить pre-stage с `mvn dependency:resolve` |
| Тесты запускаются при деплое только одного сервиса | Stage идёт после ВСЕХ deploy stages — все сервисы будут запущены |
| Headless Firefox требует больше ресурсов | Jenkins-агент должен иметь достаточно RAM/CPU (стандартное требование) |
