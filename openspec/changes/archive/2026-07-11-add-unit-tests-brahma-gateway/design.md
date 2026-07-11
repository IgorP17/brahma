## Context

### Текущее состояние:
- `brahma-gateway` — Quarkus сервис для регистрации терминалов
- Тесты: **отсутствуют** (Jenkins использует `-DskipTests`)
- Компоненты:
  - `GatewayResource` — REST endpoints (`/api/register`, `/api/register-grpc`, `/health`)
  - `TerminalRegistrationProducer` — Kafka producer (topik `terminal.registration`)
  - `TerminalRegisteredConsumer` — Kafka consumer (topik `terminal.registered`), обновляет БД
  - `TerminalRegistrationGrpcClient` — gRPC клиент к processor

### Существующие зависимости:
- `quarkus-junit5` — еще не добавлен
- `quarkus-test.mockito` — еще не добавлен
- `rest-assured` — еще не добавлен

---

## Goals / Non-Goals

**Goals:**
1. ✅ Добавить модульные тесты для всех ключевых компонентов `brahma-gateway`
2. ✅ Использовать `quarkus-test.mockito` для интеграции с Quarkus CDI
3. ✅ Писать быстрые, изолированные тесты (без DB/Kafka)
4. ✅ Поддержка CI/CD (Jenkins запускает тесты без дополнительной настройки)

**Non-Goals:**
1. ❌ Не писать интеграционные тесты с запущенным Quarkus (оставить на будущее)
2. ❌ Не тестировать реальные вызовы Kafka (используем mock)
3. ❌ Не тестировать реальные вызовы gRPC (используем mock)
4. ❌ Не тестировать реальные SQL запросы (используем mock DataSource)

---

## Decisions

### 1. Фреймворк: Quarkus Test + Mockito
| Вариант | Плюсы | Минусы | Выбор |
|---------|-------|--------|-------|
| `quarkus-junit5` | Интеграция с Quarkus, `@QuarkusTest` | - | ✅ Выбран |
| `quarkus-test.mockito` | `@InjectMock` с CDI, автоматическое внедрение | Только для Quarkus | ✅ Выбран |
| `mockito-core` | Универсальный | Нет интеграции с CDI | - |

**Rationale:** `brahma-gateway` использует `@ApplicationScoped` и `@Inject` — `quarkus-test.mockito` идеально подходит.

---

### 2. Покрытие: Модульные тесты, не интеграционные

| Тип тестов | Охват | Использовать? |
|------------|-------|---------------|
| Unit с mock | Producer/Consumer/Resource | ✅ Да |
| QuarkusTest | Полный стек (DB/Kafka) | ❌ Нет (оставить на будущее) |
| REST Assured | HTTP endpoints | ✅ Да (для GatewayResource) |

**Rationale:** Модульные тесты быстрее, работают в любом CI, не требуют подготовки окружения.

---

### 3. Структура тестов
```
src/test/java/
└── com/example/gateway/
    ├── api/
    │   └── GatewayResourceTest.java          # REST endpoints
    ├── kafka/
    │   ├── TerminalRegistrationProducerTest.java   # Kafka producer
    │   └── TerminalRegisteredConsumerTest.java     # Kafka consumer
    ├── message/
    │   ├── KafkaTerminalMessageTest.java
    │   └── TerminalRegisteredMessageTest.java
    └── dto/
        └── TerminalRegistrationTest.java
```

**Rationale:** Сохраняем структуру `src/main/java`, удобно находить тесты.

---

### 4. Тесты Producer/Consumer
```java
@QuarkusTest
public class TerminalRegistrationProducerTest {
    @InjectMock
    @Channel("registration-out")
    Emitter<KafkaTerminalMessage> emitter;  // Mock
    
    @Test
    void testSend() {
        producer.send("TERM-001", "{\"model\":\"ABC\"}");
        // verify emitter.send() called
    }
}
```

**Rationale:** Mock `Emitter` — не тестируем реальный Kafka, только логику создания сообщения.

---

### 5. Тесты Consumer
```java
@QuarkusTest
public class TerminalRegisteredConsumerTest {
    @InjectMock
    DataSource dataSource;  // Mock
    
    @Test
    void testUpdateStatus() {
        consumer.updateStatus("{\"id\":\"TERM-001\",\"status\":\"REGISTERED\"}");
        // verify executeUpdate() called with correct SQL params
    }
}
```

**Rationale:** Mock `DataSource` — не тестируем реальную БД, только SQL логику.

---

## Risks / Trade-offs

| Risk | Mitigation |
|------|------------|
| **Моки не ловят реальные баги** | Оставить место для будущих интеграционных тестов (`@QuarkusTest` с реальным DB/Kafka) |
| **Mocking DataSource сложный** | Использовать `@QuarkusTest` + `@TestTransaction` для реальных SQL тестов в будущем |
| **REST Assured требует старт Quarkus** | Использовать `@QuarkusTest` — запускает контекст быстро |

---

## Migration Plan

1. **Добавить зависимости** в `brahma-gateway/pom.xml`
2. **Создать тестовый код** в `src/test/java/`
3. **Запустить тесты локально**: `mvn test`
4. **Убрать `-DskipTests`** из Jenkinsfile (если нужно)
5. **Push в Git** и проверить Jenkins

**Rollback:** Если что-то пошло не так — откатить коммит с тестами.

---

## Open Questions

1. **Нужно ли тестировать LoggingFilter?** — сложный для mock, но полезный для покрытия
2. **Тестировать gRPC client?** — можно mock, но сложно без реального gRPC сервера

---
