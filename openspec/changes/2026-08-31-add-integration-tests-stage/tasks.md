# Tasks: Add Integration Tests Stage

## 1. Update Jenkinsfile

- [ ] 1.1 Добавить параметр `RUN_INTEGRATION_TESTS` в блок `parameters`
- [ ] 1.2 Добавить stage `Integration Tests` после `Deploy Processor`
- [ ] 1.3 Stage использует `mvn verify -Dselenide.headless=true` в `brahma-webui-test`

## 2. Create OpenSpec Change

- [ ] 2.1 Создать директорию `openspec/changes/2026-08-31-add-integration-tests-stage/`
- [ ] 2.2 Создать `.openspec.yaml`
- [ ] 2.3 Создать `proposal.md`
- [ ] 2.4 Создать `design.md`
- [ ] 2.5 Создать `tasks.md` (этот файл)

## 3. Verify

- [ ] 3.1 `mvn clean install` проходит успешно
- [ ] 3.2 Jenkinsfile синтаксически корректен (проверить через `groovy -e "new groovy.lang.GroovyShell().parse(new File('Jenkinsfile'))"`)
- [ ] 3.3 OpenSpec change валиден (все файлы на месте)
