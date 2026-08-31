# Tasks: Add Integration Tests Stage

## 1. Update Jenkinsfile

- [x] 1.1 Добавить параметр `RUN_INTEGRATION_TESTS` в блок `parameters`
- [x] 1.2 Добавить stage `Integration Tests` после `Deploy Processor`
- [x] 1.3 Stage использует `mvn verify -Dselenide.headless=true` в `brahma-webui-test`

## 2. Create OpenSpec Change

- [x] 2.1 Создать директорию `openspec/changes/2026-08-31-add-integration-tests-stage/`
- [x] 2.2 Создать `.openspec.yaml`
- [x] 2.3 Создать `proposal.md`
- [x] 2.4 Создать `design.md`
- [x] 2.5 Создать `tasks.md` (этот файл)

## 3. Verify

- [x] 3.1 `mvn clean install` проходит успешно
- [x] 3.2 Jenkinsfile синтаксически корректен
- [x] 3.3 OpenSpec change валиден (все файлы на месте)
- [x] 3.4 Jenkins билд #12 прошёл — все 4 интеграционных теста прошли
