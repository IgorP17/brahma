# Proposal: Add Integration Tests Stage to Jenkins Pipeline

## Why

Сейчас Jenkins pipeline выполняет только unit-тесты (`mvn clean install`) и деплой. Нет автоматизированной проверки end-to-end после деплоя — интеграционные тесты из `brahma-webui-test` запускаются вручную. Это приводит к тому, что регрессионные баги (например, сломанная регистрация терминала) обнаруживаются только после попадания на продакшен.

## What Changes

### Новая возможность: Integration Tests Stage

Jenkins pipeline будет запускать Selenium/Selenide интеграционные тесты после успешного деплоя всех выбранных сервисов.

**Что добавляется:**
- Новый параметр `RUN_INTEGRATION_TESTS` (boolean, default: false)
- Новый stage `Integration Tests` в pipeline
- Тесты запускаются в headless-режиме (Firefox)
- Тесты проверяют полный цикл: HTTP-регистрация → поиск → удаление → gRPC-регистрация

**Поведение:**
- Stage выполняется только если `RUN_INTEGRATION_TESTS=true`
- Запускается после всех stage деплоя (Deploy WebUI, Deploy Gateway, Deploy Processor)
- Использует `mvn verify` в модуле `brahma-webui-test`
- При падении тестов pipeline fails

## Capabilities

| Capability | Description |
|------------|-------------|
| `jenkins-integration-tests` | Автоматический запуск Selenium-тестов после деплоя в Jenkins pipeline |

## Impact

- **Jenkinsfile** — добавлен параметр и stage
- **brahma-webui-test** — без изменений (используется существующий набор тестов)
- **OpenSpec** — создаётся новый change
