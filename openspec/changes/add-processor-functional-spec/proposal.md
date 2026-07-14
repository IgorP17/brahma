## Why

Текущие спецификации в `openspec/specs/` описывают **тесты** (как тестировать), но не описывают **функциональность** (что делают модули). Нет документации о том, как работают Kafka Consumer/Producer, сервисы, entity и gRPC сервисы.

## What Changes

- Создать `processor-functional` спецификацию с полным описанием функциональности `brahma-processor`
- Создать `gateway-functional` спецификацию для `brahma-gateway`
- Создать `webui-functional` спецификацию для `brahma-webui`
- Создать `common-functional` спецификацию для `brahma-common`
- Создать `integration` спецификацию для описания взаимодействия модулей

## Capabilities

### New Capabilities
- `processor-functional`: Полные требования к модулю brahma-processor (Kafka Consumer, Producer, Service, Entity, gRPC)
- `gateway-functional`: Полные требования к модулю brahma-gateway (REST API, Kafka Consumer, Producer, Entity)
- `webui-functional`: Полные требования к модулю brahma-webui (Web UI, Service, REST API)
- `common-functional`: Полные требования к модулю brahma-common (enums, helper classes)
- `integration`: Описание взаимодействия модулей через Kafka и REST/gRPC

### Modified Capabilities
- *нет изменений существующих спецификаций*

## Impact

**Создаваемые файлы:**
- `openspec/specs/processor-functional/spec.md`
- `openspec/specs/gateway-functional/spec.md`
- `openspec/specs/webui-functional/spec.md`
- `openspec/specs/common-functional/spec.md`
- `openspec/specs/integration/spec.md`

**Изменяемые файлы:**
- *нет*

**Удаление зависимостей:**
- *нет*
