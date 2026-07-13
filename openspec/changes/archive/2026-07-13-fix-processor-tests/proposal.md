## Why

Текущие юнит-тесты для `brahma-processor` не компилируются и не запускаются. Проблемы:

1. **Отсутствуют зависимости в pom.xml** - тесты не могут найти JUnit, Mockito, AssertJ
2. **Неправильная структура тестов** - непонятно, как тестировать Kafka Consumer/Producer без реальной инфраструктуры
3. **ReflectionTestUtils** - не работает с Quarkus аннотациями

## What Changes

- Исправить `brahma-processor/pom.xml` - добавить явные зависимости для тестов
- Переписать тесты с правильной архитектурой:
  - **DTO классы** - простые тесты сеттеров/геттеров
  - **Kafka Producer** - тесты с mock Emitter
  - **Kafka Consumer** - тесты с mock Service (вместо реального потребления)
  - **Service** - тесты с mock Producer (без реальной БД)
  - **Entity** - тесты геттеров/сеттеров

## Capabilities

### New Capabilities
- `processor-unit-tests`: Исправленные юнит-тесты для `brahma-processor` модуля с рабочей конфигурацией

### Modified Capabilities
- *нет изменений существующих спецификаций*

## Impact

**Изменяемые файлы:**
- `brahma-processor/pom.xml` - добавить test dependencies (mockito-core, mockito-junit-jupiter, quarkus-junit)
- `brahma-processor/src/test/` - переписать все тестовые классы

**Удаление зависимостей:**
- Убрать自定义 `ReflectionTestUtils` - использовать стандартные подходы Mockito
