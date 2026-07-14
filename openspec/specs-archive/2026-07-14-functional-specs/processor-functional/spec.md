## ADDED Requirements

### Requirement: Processor Kafka Consumer receives registration messages
The TerminalRegistrationConsumer SHALL receive messages from the `registration-in` Kafka channel and route them to the processing service.

#### Scenario: Consumer receives valid registration message
- **WHEN** Kafka message arrives with valid terminal ID and JSON data
- **THEN** processingService.process() is called with the message

#### Scenario: Consumer handles processing error
- **WHEN** processingService throws exception
- **THEN** consumer logs error and does not rethrow

#### Scenario: Consumer handles invalid JSON
- **WHEN** Kafka message contains malformed JSON
- **THEN** processingService processes with null/empty data map

### Requirement: Processor Kafka Producer sends status updates
The TerminalRegisteredProducer SHALL send status update messages to the `registered-out` Kafka channel.

#### Scenario: Producer sends status message
- **WHEN** send(id, status) is called
- **THEN** emitter.send() is called once with TerminalRegisteredMessage

#### Scenario: Producer handles emitter error
- **WHEN** Emitter throws exception
- **THEN** method completes without throwing

### Requirement: Processor Service determines terminal status
The TerminalProcessingService SHALL determine terminal status based on location.

#### Scenario: Service processes Moscow terminal
- **WHEN** location contains "moscow" or "москва"
- **THEN** status = REGISTERED

#### Scenario: Service processes non-Moscow terminal
- **WHEN** location does not contain Moscow
- **THEN** status = REJECTED

#### Scenario: Service handles invalid JSON
- **WHEN** dataJson parsing fails
- **THEN** method completes without sending Kafka message

### Requirement: Processor Service updates database
The TerminalProcessingService SHALL persist terminal data to processor.terminals.

#### Scenario: Service creates new terminal
- **WHEN** terminal ID not found in DB
- **THEN** new record is created with status, source="KAFKA", timestamps

#### Scenario: Service updates existing terminal
- **WHEN** terminal ID exists in DB
- **THEN** record is updated with new data and status

### Requirement: Processor Entity stores terminal data
The ProcessorTerminal entity SHALL store JSON data, status, and metadata.

#### Scenario: Entity retrieves location
- **WHEN** getLocation() is called
- **THEN** returns location field from JSON data

#### Scenario: Entity retrieves model
- **WHEN** getModel() is called
- **THEN** returns model field from JSON data

### Requirement: Processor gRPC Service accepts direct registrations
The TerminalRegistrationGrpcService SHALL accept gRPC registration requests.

#### Scenario: gRPC service registers terminal
- **WHEN** registerTerminal() is called with valid request
- **THEN** terminal is saved to DB with source="GRPC"

#### Scenario: gRPC service determines status
- **WHEN** TerminalLogicHelper.determineStatus() is called
- **THEN** returns REGISTERED for Moscow, REJECTED otherwise

### Requirement: Processor Resource provides health endpoint
The ProcessorResource SHALL provide basic health check.

#### Scenario: Health endpoint called
- **WHEN** GET /health is called
- **THEN** response is "OK"

### Requirement: Processor message classes are data carriers
TerminalRegistrationMessage and TerminalRegisteredMessage SHALL carry terminal data.

#### Scenario: TerminalRegistrationMessage constructed
- **WHEN** created with id and dataJson
- **THEN** all fields are set correctly

#### Scenario: TerminalRegisteredMessage constructed
- **WHEN** created with id and status
- **THEN** all fields are set correctly

### Requirement: Processor JsonParser utility parses JSON
The JsonParser utility SHALL convert JSON strings to Map.

#### Scenario: Valid JSON parsed
- **WHEN** parseToMap() called with valid JSON
- **THEN** returns non-null Map

#### Scenario: Invalid JSON handled
- **WHEN** parseToMap() called with invalid JSON
- **THEN** returns null, error logged