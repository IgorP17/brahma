# Brahma
Микросервисная архитектура для регистрации и управления терминалами.

> 🕉️ Названо в честь Брахмы — творца, который «запускает» новые терминалы.

## 🏗️ Архитектура

Проект состоит из трёх независимых сервисов:

### 1. `brahma-gateway` (App 1)
- **Роль:** Входная точка для регистрации терминалов и обновления статусов.
- **Порт:** 8080 (HTTP), gRPC клиент к processor на порту 9000
- **Функции:**
    - **HTTP регистрация:** принимает POST `/api/register` с JSON-данными терминала
    - **gRPC регистрация:** принимает POST `/api/register-grpc` с form-urlencode'd данными, передаёт их в processor через gRPC
    - Сохраняет данные терминала в таблицу `gateway.terminals` (PostgreSQL, схема `gateway`)
    - Отправляет сообщение в Kafka-топик `terminal.registration`
    - **Консумер `TerminalRegisteredConsumer`:** слушает топик `terminal.registered` и обновляет статус терминала в `gateway.terminals`
    - Логирование всех HTTP-запросов через `LoggingFilter`
- **Технологии:** Quarkus REST, gRPC Client, Hibernate ORM + Panache, Kafka Client (SmallRye Reactive Messaging)
- **Таблица `gateway.terminals`:**
  - `id` (PK) — идентификатор терминала
  - `data` (JSON) — данные терминала: `{"model": "...", "location": "..."}`
  - `status` (ENUM) — `IN_PROCESS`, `REGISTERED`, `REJECTED`, `ACTIVE`, `INACTIVE`
  - `source` — источник: `"KAFKA"` или `"GRPC"`
  - `received_at` — время получения запроса
  - `created_at` — время создания записи
  - `updated_at` — время последнего обновления

### 2. `brahma-processor` (App 2)
- **Роль:** Обработка регистрации и определение статуса терминала.
- **Порт:** 8081 (HTTP), 9000 (gRPC сервер)
- **Функции:**
    - **gRPC сервис:** реализует `TerminalRegistrationService` для приёма registration-запросов от gateway или других клиентов
    - **Kafka консумер:** слушает топик `terminal.registration` и обрабатывает регистрации
    - Сохраняет данные терминала в таблицу `processor.terminals` (PostgreSQL, схема `processor`)
    - Определяет статус по локации: если содержит "moscow"/"москва" → `REGISTERED`, иначе `REJECTED`
    - Публикует подтверждение в топик `terminal.registered` с ID и статусом
    - Использует Flyway для миграций (схема `processor`)
- **Технологии:** Kafka Consumer, gRPC Server, Hibernate ORM + Panache, Flyway
- **Таблица `processor.terminals`:**
  - `id` (PK) — идентификатор терминала
  - `data` (JSON) — данные терминала
  - `status` (ENUM) — `IN_PROCESS`, `REGISTERED`, `REJECTED`, `ACTIVE`, `INACTIVE`
  - `source` — источник: `"KAFKA"` или `"GRPC"`
  - `received_at` — время получения запроса
  - `created_at` — время создания записи
  - `updated_at` — время последнего обновления

### 3. `brahma-webui` (App 3)
- **Роль:** Веб-интерфейс для мониторинга и управления.
- **Функции:**
  - Показывает список терминалов из обеих БД (`gateway.terminals`, `processor.terminals`)
  - Поиск терминала по ID: отображает состояние в обеих таблицах
  - Форма регистрации: отправляет данные в `brahma-gateway`
  - Пустая таблица при загрузке — данные подгружаются по запросу
- **Технологии:** Quarkus Qute, REST Client, Hibernate ORM, JSON-колонки

### 4. `brahma-common` (App 4)
- **Роль:** Общие компоненты
- **Функции:**
  - Общий ENUM статусов терминалов

---

## 📊 Базы данных

- **PostgreSQL** — используется сервисами:
    - `brahma-gateway` → схема `gateway.terminals`
    - `brahma-processor` → схема `processor.terminals`
    - `brahma-webui` → чтение из обоих схем данных

### Структура таблиц:

#### gateway.terminals:
- `id` — идентификатор терминала (PK)
- `data` — JSON-данные терминала (model, location и т.д.)
- `status` — статус: `IN_PROCESS`, `REGISTERED`, `REJECTED`, `ACTIVE`, `INACTIVE`
- `source` — источник запроса: `"KAFKA"` или `"GRPC"`
- `received_at` — время получения запроса
- `created_at` — время создания записи
- `updated_at` — время последнего обновления

#### processor.terminals:
- `id` — идентификатор терминала (PK)
- `data` — JSON-данные терминала
- `status` — статус: `IN_PROCESS`, `REGISTERED`, `REJECTED`, `ACTIVE`, `INACTIVE`
  - Логика: `REGISTERED` если location содержит "moscow"/"москва", иначе `REJECTED`
- `source` — источник запроса: `"KAFKA"` или `"GRPC"`
- `received_at` — время получения запроса
- `created_at` — время создания записи
- `updated_at` — время последнего обновления

---

## 📨 Обмен сообщениями (Kafka)

### Топик `terminal.registration`:
- **Сообщение:** `{"id": "TERM-001", "dataJson": "{\"model\":\"ABC\",\"location\":\"Moscow\"}"}`
- **Отправитель:** `brahma-gateway` (через `TerminalRegistrationProducer`)
- **Получатель:** `brahma-processor` (через `TerminalRegistrationConsumer`)
- **Назначение:** новая заявка на регистрацию терминала

### Топик `terminal.registered`:
- **Сообщение:** `{"id": "TERM-001", "status": "REGISTERED"}` или `{"id": "TERM-001", "status": "REJECTED"}`
- **Отправитель:** `brahma-processor` (через `TerminalRegisteredProducer`)
- **Получатель:** `brahma-gateway` (через `TerminalRegisteredConsumer` для обновления статуса в БД)
- **Назначение:** подтверждение обработки регистрации с финальным статусом

---

## 🚀 Локальный запуск

### brahma-gateway
```bash
cd brahma-gateway
mvn quarkus:dev
```

**HTTP регистрация:**
```bash
curl -X POST http://localhost:8080/api/register \
  -H "Content-Type: application/json" \
  -d '{"id":"TERM-00021","data":{"model":"ABC","location":"Moscow"}}'
```

**gRPC регистрация через HTTP endpoint:**
```bash
curl -X POST http://localhost:8080/api/register-grpc \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data-urlencode "id=TERM-00021" \
  --data-urlencode "model=GW-FORM-API-TEST" \
  --data-urlencode "location=London"
```

Dev UI: http://localhost:8080/q/dev-ui/

---

### brahma-processor
```bash
cd brahma-processor
mvn quarkus:dev
```

### brahma-webui
```bash
cd brahma-webui
mvn quarkus:dev
```

grpc curl
```bash
sudo apt install golang-go
go install github.com/fullstorydev/grpcurl/cmd/grpcurl@latest
export PATH="$PATH:$HOME/go/bin" /// добавить в .bashrc
source ~/.bashrc

grpcurl -plaintext localhost:9000 list
grpcurl -plaintext localhost:9000 describe com.example.terminal.TerminalRegistrationService
grpcurl -plaintext localhost:9000 describe com.example.terminal.RegisterTerminalRequest
grpcurl -plaintext localhost:9000 describe com.example.terminal.RegisterTerminalRequest

grpcurl -plaintext \
  -d '{
    "id": "TERM-00021",
    "data": {
      "model": "TEST-GRPC",
      "location": "Moscow"
    },
    "source": "GRPC_TEST"
  }' \
  localhost:9000 \
  com.example.terminal.TerminalRegistrationService/RegisterTerminal
```

Вызов brahma-gateway по http для регистрации через grpc
```bash
curl -X POST http://localhost:8080/api/register-grpc \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data-urlencode "id=TERM-00021" \
  --data-urlencode "model=GW-FORM-API-TEST" \
  --data-urlencode "location=London"
```

=============================








Что делаем дальше?

Архитектура полностью готова и работает как часы. Варианты на будущее:

    Добавить Ingress Controller в Minikube, чтобы обращаться к WebUI по красивому доменному имени (например, http://brahma.local), а не через minikube:30882.
    Мониторинг (Prometheus + Grafana), чтобы рисовать красивые графики регистраций терминалов.
    Новая фича в коде (например, обработку ошибок, если Kafka упала, или websocket'ы для realtime-обновления таблицы в WebUI).
    Просто отдохнуть и насладиться результатом! 🍻