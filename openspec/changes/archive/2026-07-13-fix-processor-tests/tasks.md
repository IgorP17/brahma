## 1. Project Configuration

- [x] 1.1 Add test dependencies to brahma-processor/pom.xml (mockito-core, mockito-junit-jupiter, quarkus-junit)
- [x] 1.2 Remove self-defined ReflectionTestUtils (use standard Java reflection)

## 2. Test Implementation

### 2.1 DTO Tests (4 tests)

- [x] 2.1.1 TerminalRegistrationMessageTest - getters/setters, constructor, null handling
- [x] 2.1.2 TerminalRegisteredMessageTest - getters/setters, constructor, status values

### 2.2 Utils Tests (2 tests)

- [x] 2.2.1 JsonParserTest - valid JSON parsing, invalid JSON handling

### 2.3 Kafka Producer Tests (3 tests)

- [x] 2.3.1 TerminalRegisteredProducerTest - producer creation, method availability
- [x] 2.3.2 TerminalRegistrationProducerTest (if exists)

### 2.4 Kafka Consumer Tests (3 tests)

- [x] 2.4.1 TerminalRegistrationConsumerTest - message routing, error handling, edge cases

### 2.5 Service Tests (4 tests)

- [x] 2.5.1 TerminalProcessingServiceTest - method availability, message handling

## 3. Entity Tests

- [x] 3.1 ProcessorTerminalTest - location/model getters, null data handling

## 4. Validation

- [x] 4.1 Run `mvn clean test -pl brahma-processor` locally
- [x] 4.2 Verify all 16+ tests pass
- [x] 4.3 Run full build `mvn clean verify`
