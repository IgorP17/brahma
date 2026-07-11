## ADDED Requirements

### Requirement: brahma-gateway unit tests
The system SHALL have unit tests for all public methods in brahma-gateway module, testing logic without external dependencies (no real DB, Kafka, or gRPC calls).

#### Scenario: TerminalRegistrationProducer sends message
- **WHEN** `TerminalRegistrationProducer.send()` is called with valid ID and JSON
- **THEN** `Emitter.send()` is called exactly once with a `KafkaTerminalMessage` containing the same ID and JSON

#### Scenario: TerminalRegisteredConsumer updates database
- **WHEN** `TerminalRegisteredConsumer.updateStatus()` is called with valid JSON message
- **THEN** `DataSource.getConnection().prepareStatement().executeUpdate()` is called with SQL that updates `gateway.terminals.status` and `updated_at`

#### Scenario: TerminalRegisteredConsumer handles invalid JSON
- **WHEN** `TerminalRegisteredConsumer.updateStatus()` is called with malformed JSON
- **THEN** Error is logged, no database update occurs, method returns gracefully

#### Scenario: TerminalRegisteredConsumer handles unknown terminal ID
- **WHEN** `TerminalRegisteredConsumer.updateStatus()` is called for terminal ID that doesn't exist in DB
- **THEN** Log shows "Skip update: terminal not found", no exception thrown

#### Scenario: GatewayResource returns 200 for valid registration
- **WHEN** POST `/api/register` is called with valid terminal data
- **THEN** Response status is 200, body contains `"status":"pending"` and the terminal ID

#### Scenario: GatewayResource handles Kafka send failure
- **WHEN** Kafka producer fails to send message
- **THEN** Response status is 500 with error message

#### Scenario: DTO serialization works
- **WHEN** `KafkaTerminalMessage` or `TerminalRegisteredMessage` is created with constructor
- **THEN** All fields (id, dataJson/status) are correctly set and retrievable

#### Scenario: DTO has getters/setters
- **WHEN** Getters and setters are called on DTO objects
- **THEN** Values are correctly stored and returned
