# Brahma
Микросервисная архитектура для регистрации терминалов:
- Gateway: REST → PostgreSQL + Kafka
- Processor: Kafka → PostgreSQL
- Dashboard: Web UI  