## Context

**Текущее состояние:**
- Модуль `brahma-processor` имеет бизнес-логику (Consumer, Producer, Service) но без тестов
- Попытка добавить тесты привела к ошибкам компиляции (отсутствующие зависимости, неправильные импорты)

**Ограничения:**
- Используется Quarkus 3.32.3 с SmallRye Reactive Messaging
- Kafka Consumer работает через аннотацию `@Incoming`
- Kafka Producer использует `Emitter<T>` для отправки сообщений
- Тесты должны быть unit-тестами (без реальной БД и Kafka)

## Goals / Non-Goals

**Цели:**
- ✅ Рабочие unit-тесты, компилирующиеся и запускающиеся через `mvn test`
- ✅ Покрытие: DTO, Consumer, Producer, Service, Utils
- ✅ Использовать только Mockito (без Spring, QuarkusTest)
- ✅ Дублировать структуру из `brahma-gateway`

**Не цели:**
- ❌ Интеграционные тесты с реальной БД
- ❌ Тесты с реальным Kafka (используем mock Emitter)
- ❌ Покрытие entity (они простоPOJO с геттерами/сеттерами)

## Decisions

**Решение 1: Use package-private dependency injection**
```
Почему: Mockito не работает с @Inject напрямую
Альтернатива: QuarkusTest (тоже требует конфигурации)
Решение: ReflectionTestUtils (из родного Java reflection, без Spring)
```

**Решение 2: Mock Emitter instead of real Kafka**
```
Почему: Unit-тесты не должны зависеть от внешних систем
Реализация: when(emitter.send()).thenReturn(future)
```

**Решение 3: Test Service with mock Producer**
```
Почему: Не нужно тестировать реальную отправку в Kafka
Реализация: verify(kafkaProducer).send(id, status)
```

## Risks / Trade-offs

**[Сложность mock'инга Emitter]**
- Решение: Использовать `CompletableFuture.completedFuture(null)` для имитации успеха

**[ReflectionTestUtils может сломаться с обфускацией]**
- Решение: Использовать стандартный Java reflection через getDeclaredField()

**[Нет покрытия интеграции]**
- Решение: Это unit-тесты - интеграционные тесты не входят в scope
