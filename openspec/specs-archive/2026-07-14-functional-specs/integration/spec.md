## ADDED Requirements

### Requirement: Registration flow sends message from gateway to processor
When a terminal is registered, the gateway SHALL send a message to the processor via Kafka.

#### Scenario: Registration flow
- **WHEN** client POST /api/register to gateway
- **THEN** gateway sends Kafka message to `terminal.registration` topic
- **AND** processor consumes from `terminal.registration` via `registration-in` channel

### Requirement: Processor determines status and saves to DB
The processor SHALL determine terminal status based on location and save to database.

#### Scenario: Status determination
- **WHEN** processor receives registration message
- **THEN** status = REGISTERED if Moscow, REJECTED otherwise

#### Scenario: Database persistence
- **WHEN** status is determined
- **THEN** terminal is saved to `processor.terminals` with all metadata

### Requirement: Processor sends status back to gateway
After processing, the processor SHALL send status update to gateway via Kafka.

#### Scenario: Status update sent
- **WHEN** terminal is saved to processor.terminals
- **THEN** processor sends Kafka message to `terminal.registered` topic
- **AND** gateway consumes from `terminal.registered` via `registered-in` channel

### Requirement: Gateway updates local database with status
The gateway SHALL update its local database with status received from processor.

#### Scenario: Status update received
- **WHEN** gateway receives status update from Kafka
- **THEN** `gateway.terminals.status` is updated

#### Scenario: Unknown terminal
- **WHEN** terminal ID not found in gateway.terminals
- **THEN** log shows "Skip update: terminal not found"

### Requirement: gRPC alternate registration path
The processor SHALL accept direct gRPC registration requests.

#### Scenario: gRPC registration
- **WHEN** gRPC client calls TerminalRegistrationGrpcService.registerTerminal()
- **THEN** terminal is saved to `processor.terminals` with source="GRPC"

#### Scenario: Status in gRPC flow
- **WHEN** gRPC registration received
- **THEN** TerminalLogicHelper.determineStatus() is used for status

### Data Flow Summary

```
Client → HTTP POST /api/register → Gateway
                                    ↓
                            TerminalRegistrationProducer
                                    ↓
                          Kafka topic: terminal.registration
                                    ↓
                          TerminalRegistrationConsumer
                                    ↓
                        TerminalProcessingService
                                    ↓
                    ProcessorTerminal (DB: processor.terminals)
                                    ↓
                        TerminalRegisteredProducer
                                    ↓
                          Kafka topic: terminal.registered
                                    ↓
                        TerminalRegisteredConsumer
                                    ↓
                    GatewayTerminal (DB: gateway.terminals)
```

### Kafka Topics

| Topic | Producer | Consumer | Purpose |
|-------|----------|----------|---------|
| `terminal.registration` | Gateway | Processor | New registration requests |
| `terminal.registered` | Processor | Gateway | Status updates |

### Channels (SmallRye Reactive Messaging)

| Channel | Type | Purpose |
|---------|------|---------|
| `registration-out` | Emitter | Gateway → Kafka |
| `registration-in` | Incoming | Processor ← Kafka |
| `registered-out` | Emitter | Processor → Kafka |
| `registered-in` | Incoming | Gateway ← Kafka |