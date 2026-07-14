## ADDED Requirements

### Requirement: Gateway Kafka Producer sends registration requests
The TerminalRegistrationProducer SHALL send terminal registration messages to the `terminal.registration` Kafka topic.

#### Scenario: Producer sends registration message
- **WHEN** send(id, dataJson) is called with valid data
- **THEN** emitter.send() is called once with KafkaTerminalMessage containing ID and JSON

#### Scenario: Producer logs outbound message
- **WHEN** send() is called
- **THEN** log shows topic name, terminal ID, and data JSON

### Requirement: Gateway Kafka Consumer receives status updates
The TerminalRegisteredConsumer SHALL receive status update messages from the `terminal.registered` Kafka topic and update the database.

#### Scenario: Consumer receives status update
- **WHEN** Kafka message arrives with terminal ID and status
- **THEN** database record is updated with new status

#### Scenario: Consumer handles invalid JSON
- **WHEN** message contains malformed JSON
- **THEN** error is logged, no database update occurs

#### Scenario: Consumer handles unknown terminal ID
- **WHEN** terminal ID not found in gateway.terminals
- **THEN** log shows "Skip update: terminal not found", no exception

### Requirement: Gateway Resource accepts HTTP registrations
The GatewayResource SHALL accept POST requests to /api/register and forward to Kafka.

#### Scenario: Valid registration request
- **WHEN** POST /api/register with valid terminal data
- **THEN** status 200, body contains "status":"pending" and terminal ID

#### Scenario: Invalid registration request
- **WHEN** POST /api/register with missing or invalid data
- **THEN** status 400 with error message

#### Scenario: Kafka send failure
- **WHEN** Kafka producer fails to send message
- **THEN** status 500 with error message

### Requirement: Gateway Entity stores terminal data
The GatewayTerminal entity SHALL store JSON data and status for gateway.terminals.

#### Scenario: Entity retrieves location
- **WHEN** getLocation() is called
- **THEN** returns location field from JSON data

#### Scenario: Entity retrieves model
- **WHEN** getModel() is called
- **THEN** returns model field from JSON data

### Requirement: Gateway message classes are data carriers
KafkaTerminalMessage and TerminalRegisteredMessage SHALL carry terminal data between services.

#### Scenario: KafkaTerminalMessage constructed
- **WHEN** created with id and dataJson
- **THEN** all fields are set correctly

#### Scenario: TerminalRegisteredMessage constructed
- **WHEN** created with id and status
- **THEN** all fields are set correctly

### Requirement: Gateway gRPC client calls processor service
The TerminalRegistrationGrpcClient SHALL make gRPC calls to processor for terminal registration.

#### Scenario: gRPC client calls processor
- **WHEN** registerTerminal() is called
- **THEN** gRPC request is sent to processor service

#### Scenario: gRPC client handles response
- **WHEN** processor returns response
- **THEN** response is parsed and returned to caller