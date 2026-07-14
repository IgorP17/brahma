## Context

**Текущее состояние:**
- `brahma-processor` — Kafka consumer/producer, gRPC сервис, обрабатывает регистрации терминалов
- `brahma-gateway` — REST API, Kafka consumer/producer, сохраняет в `gateway.terminals`
- `brahma-webui` — Web UI, CRUD операции над терминалами из gateway и processor
- `brahma-common` — shared enums (TerminalStatus) и helper (TerminalLogicHelper)

**Архитектура:**
```
Client → Gateway (HTTP) → Kafka (terminal.registration) → Processor (Kafka consumer)
                                                    ↓
                                              Kafka (terminal.registered)
                                                    ↓
                                              Gateway (Kafka consumer updates DB)
```

**Полный flow:**
1. Client POST /api/register → Gateway → sends Kafka message to `terminal.registration`
2. Processor consumes from `terminal.registration`, determines status (REGISTERED/REJECTED)
3. Processor saves to `processor.terminals`, sends to `terminal.registered`
4. Gateway consumes from `terminal.registered`, updates `gateway.terminals.status`

**Ограничения:**
- PostgreSQL с schema separation (gateway.*, processor.*)
- Kafka topics: `terminal.registration`, `terminal.registered`, `registration-in`, `registered-out`
- Quarkus 3.32.3 с SmallRye Reactive Messaging
- Hibernate ORM + Panache для DB access

## Goals / Non-Goals

**Цели:**
- ✅ Полное документирование функциональности всех 4 модулей
- ✅ Описание Kafka messaging паттернов (producer/consumer)
- ✅ Описание gRPC сервисов в processor
- ✅ Описание REST endpoints во всех модулях
- ✅ Документирование entity моделей и business logic
- ✅ Документирование обратного потока статусов (processor → gateway через Kafka)

**Не цели:**
- ❌ Дублирование unit test спецификаций (они уже есть)
- ❌ Детальное описание тестовых случаев (это в отдельных спецификациях)
- ❌ Интеграционные тесты (не в scope)

## Decisions

**Решение 1: Разделить спецификации по модулям**
```
Почему: Каждый модуль имеет свою бизнес-логику и ответственность
Альтернатива: Одна большая спецификация для всего проекта
Решение: Отдельные спецификации для каждого модуля + отдельная для интеграции
```

**Решение 2: Описание Kafka каналов в processor-functional**
```
Почему: Ключевая функциональность processor — обработка Kafka сообщений
Содержание: Consumer (registration-in), Producer (registered-out)
```

**Решение 3: Описание gRPC сервисов в processor-functional**
```
Почему: Processor также поддерживает gRPC для прямых регистраций
Содержание: TerminalRegistrationGrpcService методы и response
```

**Решение 4: Описание WebUI CRUD в webui-functional**
```
Почему: WebUI предоставляет интерфейс для управления терминалами
Содержание: GET/DELETE endpoints для gateway и processor терминалов
```

**Решение 5: Описание обратного потока статусов в integration**
```
Почему: Статусы идут от processor к gateway через Kafka (terminal.registered)
Содержание: Processor Producer → Gateway Consumer flow
```

## Risks / Trade-offs

**[Нет описания бизнес-правил в common]**
- Решение: TerminalLogicHelper содержит простую логику (Moscow check), может быть вынесена в отдельную спецификацию

**[Возможные дубликаты с test спецификациями]**
- Решение: Functional спецификации описывают WHAT (что делает), test спецификации описывают HOW (как тестировать)

**[Изменения в коде могут устаревать спецификации]**
- Решение: Спецификации нужно обновлять при крупных изменениях, но не при рефакторинге без изменений поведения