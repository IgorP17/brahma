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

/////// TODO - SQL

<h3>Register via gRPC (test)</h3>
<form id="register-grpc-form">
    <div class="form-group">
        <label for="grpc-id">Terminal ID:</label>
        <input type="text" id="grpc-id" name="id" placeholder="e.g., TERM-00022" required>
    </div>
    <div class="form-group">
        <label for="grpc-model">Model:</label>
        <input type="text" id="grpc-model" name="model" placeholder="e.g., ABC" required>
    </div>
    <div class="form-group">
        <label for="grpc-location">Location:</label>
        <input type="text" id="grpc-location" name="location" placeholder="e.g., Moscow" required>
    </div>
    <input type="submit" value="Register via gRPC">
</form>

<div id="grpc-result"></div>

<script>
document.getElementById('register-grpc-form').addEventListener('submit', async function(e) {
    e.preventDefault();
    const formData = new FormData(this);
    const params = new URLSearchParams();
    params.append('id', formData.get('id'));
    params.append('model', formData.get('model'));
    params.append('location', formData.get('location'));

    const resultDiv = document.getElementById('grpc-result');
    resultDiv.style.display = 'block';
    resultDiv.textContent = 'Sending...';
    resultDiv.className = 'success';

    try {
        const response = await fetch('/register-grpc', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: params.toString()
        });

        const text = await response.text();
        if (response.ok) {
            resultDiv.textContent = '✅ ' + text;
            resultDiv.className = 'success';
        } else {
            resultDiv.textContent = '❌ ' + text;
            resultDiv.className = 'error';
        }
    } catch (err) {
        resultDiv.textContent = '❌ Network error: ' + err.message;
        resultDiv.className = 'error';
    }
});
</script>