# Brahma WebUI Selenium Tests

Интеграционные тесты для веб-интерфейса brahma-webui с использованием Selenium/Selenide.

## Описание

Этот проект содержит автоматизированные E2E тесты для проверки функциональности веб-интерфейса регистрации терминалов:

- Регистрация терминалов через HTTP форму
- Регистрация терминалов через gRPC форму
- Поиск терминалов в обеих базах данных (gateway, processor)
- Удаление терминалов через веб-интерфейс

## Требования

### Системные требования

- **Java 21+** (OpenJDK или Oracle JDK)
- **Maven 3.8+**
- **Firefox** (последняя стабильная версия)
- **geckodriver** (устанавливается автоматически через WebDriverManager)

### Окружение

- **brahma-webui** должен быть запущен и доступен по адресу: `http://minikube:30882`
- PostgreSQL базы данных: `gateway.terminals` и `processor.terminals`
- Kafka брокер доступен для обработки сообщений

### Установка geckodriver (если нужно вручную)

```bash
# Скачать geckodriver с https://github.com/mozilla/geckodriver/releases
# Или использовать WebDriverManager (устанавливается автоматически)
```

## Структура проекта

```
brahma-webui-test/
├── pom.xml                          # Maven конфигурация
├── README.md                        # Этот файл
├── src/
│   ├── main/
│   │   └── resources/
│   │       └── logback.xml          # Конфигурация логирования
│   └── test/
│       ├── java/
│       │   └── com/example/brahmawebui/test/
│       │       ├── BaseTest.java           # Базовый класс для тестов
│       │       ├── TestConfig.java         # Тестовые константы
│       │       ├── TerminalRegistrationIT.java  # Интеграционные тесты
│       │       └── pages/
│       │           ├── TerminalSearchPage.java      # Поиск терминалов
│       │           ├── TerminalRegistrationPage.java # Регистрация
│       │           └── TerminalDeletionPage.java     # Удаление
│       └── resources/
└── target/                          # Результаты сборки и тестов
    └── screenshots/                 # Скриншоты при ошибках
```

## Запуск тестов

### Полная сборка и запуск

```bash
cd brahma-webui-test
mvn clean test
```

### Запуск только определенных тестов

```bash
# Запуск конкретного теста
mvn test -Dtest=TerminalRegistrationIT#testHttpRegistrationWithMoscowLocation

# Запуск теста по имени
mvn test -Dtest=TerminalRegistrationIT#testTerminalDeletion
```

### Запуск с переопределением URL

```bash
mvn test -Dwebui.url=http://localhost:8080
```

### Запуск без headless режима (для отладки)

```bash
# По умолчанию браузер открывается видимым
# Для headless режима:
mvn test -Dselenide.headless=true
```

### Запуск с включенным отладочным логированием

```bash
mvn test -Dlog.level=DEBUG
```

## Тестовые сценарии

### Тест 1: Регистрация через HTTP форму (Москва)
- Регистрация терминала TERM-00027 с location=Москва
- Проверка наличия в gateway.terminals
- Проверка наличия в processor.terminals
- Ожидаемый статус: Registered

### Тест 2: Удаление терминала
- Удаление терминала TERM-00027 через веб-форму
- Проверка отсутствия в gateway.terminals
- Проверка отсутствия в processor.terminals
- Ожидаемый результат: Terminal not found

### Тест 3: Регистрация через gRPC форму (Berlin)
- Регистрация терминала TERM-00027 с location=Berlin
- Проверка наличия в gateway.terminals
- Проверка статуса Rejected в processor.terminals

### Тест 4: Удаление после gRPC регистрации
- Удаление терминала TERM-00027 через веб-форму
- Проверка отсутствия в обеих базах данных
- Ожидаемый результат: Terminal not found

## Конфигурация

### Основные параметры

| Параметр | Значение по умолчанию | Описание |
|----------|----------------------|----------|
| `webui.url` | `http://minikube:30882` | URL веб-интерфейса |
| `selenide.browser` | `firefox` | Браузер для тестов |
| `selenide.headless` | `false` | Запуск в фоновом режиме |
| `selenide.timeout` | `5000` | Таймаут в мс |

### Переменные окружения

```bash
# URL веб-интерфейса
export WEBUI_URL=http://minikube:30882

# Режим headless
export SELENIIDE_HEADLESS=true
```

## Отладка

### Скриншоты

При ошибке теста автоматически создается скриншот в `target/screenshots/`.

### Логи

Подробные логи тестирования выводятся в консоль. Для включения отладочного логирования:

```bash
mvn test -Dlog.level=DEBUG
```

### Ручной запуск браузера

Для отладки можно запустить тесты с видимым браузером:

```bash
mvn test -Dselenide.headless=false
```

## Зависимости

- **Selenide 7.6.1** - современный wrapper над Selenium
- **JUnit 5** - фреймворк для тестирования
- **WebDriverManager 5.9.2** - автоматическое управление драйверами
- **Logback 1.5.12** - логирование

## Решение проблем

### Ошибка подключения к Firefox

```bash
# Проверить, что Firefox установлен
firefox --version

# Проверить geckodriver
geckodriver --version
```

### Ошибка таймаута

Увеличить таймаут:

```bash
mvn test -Dselenide.timeout=10000
```

### Ошибка подключения к веб-интерфейсу

Проверить доступность brahma-webui:

```bash
curl http://minikube:30882
kubectl get pods -n brahma
```

## Лицензия

Внутренний проект для тестирования brahma-webui.
