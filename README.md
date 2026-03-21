# Brahma
Микросервисная архитектура для регистрации и управления терминалами.

> 🕉️ Названо в честь Брахмы — творца, который «запускает» новые терминалы.

## 🏗️ Архитектура

Проект состоит из трёх независимых сервисов:

### 1. `brahma-gateway` (App 1)
- **Роль:** Входная точка для регистрации терминалов.
- **Функции:**
    - Принимает HTTP POST-запросы на `/api/register`
    - Сохраняет данные терминала в таблицу `gateway.terminals` (PostgreSQL)
    - Отправляет сообщение в Kafka-топик `terminal.registration`
- **Технологии:** Quarkus REST, Hibernate ORM, Kafka Client

### 2. `brahma-processor` (App 2)
- **Роль:** Обработка регистрации и активация терминала.
- **Функции:**
    - Слушает Kafka-топик `terminal.registration`
    - Извлекает ID и данные терминала
    - Сохраняет в свою таблицу `processor.terminals` (PostgreSQL)
    - Отправляет подтверждение в топик `terminal.registered`
- **Технологии:** Kafka Consumer, Hibernate ORM

### 3. `brahma-dashboard` (App 3)
- **Роль:** Веб-интерфейс для мониторинга.
- **Функции:**
    - Показывает список терминалов из обеих БД (`gateway.terminals`, `processor.terminals`)
    - Позволяет отправлять запросы в `brahma-gateway`
- **Технологии:** Quarkus Qute, REST Client

---

## 📊 Базы данных

- **PostgreSQL** — используется двумя сервисами:
    - `brahma-gateway` → схема `gateway.terminals`
    - `brahma-processor` → схема `processor.terminals`

### Структура таблиц:

#### gateway.terminals:
- `id` — идентификатор терминала (PK)
- `data` — JSON-данные терминала
- `status` — статус: `NOT_REGISTERED`, `REGISTERED`, `ACTIVE`

#### processor.terminals:
- `id` — идентификатор терминала (PK)
- `data` — JSON-данные терминала
- `status` — статус: `ACTIVE`, `INACTIVE`

---

## 📨 Обмен сообщениями (Kafka)

- **Топик `terminal.registration`:**  
  Сообщение: `{"id": "TERM-001", "data": {...}}`  
  Отправитель: `brahma  Получатель: `brahma-processor`

- **Топик `terminal.registered`:**  
  Сообщение: `{"id": "TERM-001", "status": "ACTIVE"}`  
  Отправитель: `brahma-processor`  
  Получатель: `brahma-gateway` (для обновления статуса)

---

## 🚀 Локальный запуск

```bash
# Запустить gateway (Dev Mode)
cd brahma-gateway
mvn quarkus:dev

# Пример запроса:
curl -X POST http://localhost:8080/api/register \
  -H "Content-Type: application/json" \
  -d '{"id":"TERM-00021","data":{"model":"ABC","location":"Moscow"}}'

http://localhost:8080/q/dev-ui/