## ADDED Requirements

### Requirement: Processor DTO classes are testable
All data transfer objects (DTOs) in the processor module SHALL be testable with unit tests that verify getters and setters.

#### Scenario: TerminalRegistrationMessage can be constructed and accessed
- **WHEN** creating a TerminalRegistrationMessage with ID and data
- **THEN** all fields can be set and retrieved correctly

#### Scenario: TerminalRegisteredMessage can be constructed
- **WHEN** creating a TerminalRegisteredMessage with ID and status
- **THEN** all fields can be set and retrieved correctly

### Requirement: Kafka Producer is testable with mock Emitter
The TerminalRegisteredProducer SHALL be testable by mocking the Emitter to verify message sending without real Kafka.

#### Scenario: Producer sends message to Kafka
- **WHEN** calling send(terminalId, status)
- **THEN** emitter.send() is called once with correct message

#### Scenario: Producer handles mock emitter errors
- **WHEN** Emitter throws exception
- **THEN** method completes without throwing (error logged)

### Requirement: Kafka Consumer is testable with mock Service
The TerminalRegistrationConsumer SHALL be testable by mocking the processing service to verify message routing.

#### Scenario: Consumer routes message to service
- **WHEN** processRegistration() is called with a message
- **THEN** processingService.process() is called with the message

#### Scenario: Consumer handles service errors gracefully
- **WHEN** processingService throws exception
- **THEN** consumer does not throw (error logged)

### Requirement: Processing Service is testable with mock Producer
The TerminalProcessingService SHALL be testable by mocking the Kafka producer to verify status updates.

#### Scenario: Service processes valid registration
- **WHEN** process() is called with valid message
- **THEN** Kafka producer sends status update

#### Scenario: Service handles invalid JSON
- **WHEN** process() is called with invalid JSON
- **THEN** method completes without exception, producer not called

### Requirement: Utils and Parser classes are testable
The JsonParser utility class SHALL be testable with unit tests for JSON parsing scenarios.

#### Scenario: Valid JSON is parsed to Map
- **WHEN** parseToMap() is called with valid JSON
- **THEN** returns non-null Map with parsed data

#### Scenario: Invalid JSON returns null
- **WHEN** parseToMap() is called with invalid JSON
- **THEN** returns null
