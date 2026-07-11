## Why

Текущая Jenkins-стратегия использует `mvn clean install -DskipTests` — тесты **не запускаются**. Это означает:
- Билд проходит даже если код сломан
- Нет гарантии, что изменения не сломают логику
- Баги улетают в прод без обнаружения

**Решение:** Добавить модульные тесты для `brahma-gateway` с использованием `quarkus-test.mockito` — быстрые, изолированные тесты, работающие в CI/CD без зависимости от DB/Kafka.

---

## What Changes

### Новые зависимости (brahma-gateway/pom.xml):
- `quarkus-junit5` — Quarkus тестовый фреймворк
- `quarkus-test.mockito` — интеграция Mockito с Quarkus CDI
- `rest-assured` — тестирование REST API

### Новые тестовые файлы (brahma-gateway/src/test/java/):
1. `TerminalRegistrationProducerTest` — тесты Kafka producer
2. `TerminalRegisteredConsumerTest` — тесты Kafka consumer и SQL обновлений
3. `GatewayResourceTest` — тесты REST endpoints
4. `KafkaTerminalMessageTest` — тесты message DTO
5. `TerminalRegisteredMessageTest` — тесты message DTO
6. `TerminalRegistrationTest` — тесты DTO

### Архитектура:
```
brahma-gateway/
├── src/main/java/...       # existing
└── src/test/java/          # new
    └── com/example/gateway/
        ├── api/
        │   └── GatewayResourceTest.java
        ├── kafka/
        │   ├── TerminalRegistrationProducerTest.java
        │   └── TerminalRegisteredConsumerTest.java
        ├── message/
        │   ├── KafkaTerminalMessageTest.java
        │   └── TerminalRegisteredMessageTest.java
        └── dto/
            └── TerminalRegistrationTest.java
```

---

## Capabilities

### New Capabilities
- **brahma-gateway-unit-tests**: Модульные тесты для brahma-gateway с использованием Quarkus Test и Mockito
  - Тесты Kafka Producer/Consumer
  - Тесты REST endpoints
  - Тесты DTO и сообщений

### Modified Capabilities
*(нет — не изменяются требования к существующим функциям)*

---

## Impact

### Изменяемые файлы:
- `brahma-gateway/pom.xml` — добавить test dependencies
- `brahma-gateway/src/test/java/` — создать тестовые классы

### Зависимости:
- `quarkus-junit5` — для `@QuarkusTest`
- `quarkus-test.mockito` — для `@InjectMock`
- `rest-assured` — для `given().when().then()` REST тестов

### CI/CD:
- Jenkins будет запускать тесты автоматически (без `-DskipTests`)
- Тесты выполняются быстро (секунды), не требуют DB/Kafka
