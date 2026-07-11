## 1. Add Test Dependencies to pom.xml

- [x] 1.1 Add `quarkus-junit5` dependency to brahma-gateway/pom.xml
- [x] 1.2 Add `quarkus-test.mockito` dependency to brahma-gateway/pom.xml
- [x] 1.3 Add `rest-assured` dependency to brahma-gateway/pom.xml
- [x] 1.4 Run `mvn clean install` to verify dependencies resolve

## 2. Create Test Directory Structure

- [x] 2.1 Create `brahma-gateway/src/test/java/com/example/gateway/` directories
- [x] 2.2 Create subdirectories: `api/`, `kafka/`, `message/`, `dto/`

## 3. Write Unit Tests for DTO/Message Classes

- [x] 3.1 Write `KafkaTerminalMessageTest.java` - test constructor and getters/setters
- [x] 3.2 Write `TerminalRegisteredMessageTest.java` - test constructor and getters/setters
- [x] 3.3 Write `TerminalRegistrationTest.java` - test DTO getters/setters

## 4. Write Unit Tests for Kafka Producer

- [x] 4.1 Write `TerminalRegistrationProducerTest.java` with `@QuarkusTest`
- [x] 4.2 Mock `Emitter<KafkaTerminalMessage>` using mock injection
- [x] 4.3 Test `send()` method verifies emitter is called with correct message

## 5. Write Unit Tests for Kafka Consumer

- [x] 5.1 Write `TerminalRegisteredConsumerTest.java` with `@QuarkusTest`
- [x] 5.2 Mock `DataSource` using mock injection
- [x] 5.3 Test valid JSON message updates database correctly
- [x] 5.4 Test invalid JSON message is handled gracefully
- [x] 5.5 Test unknown terminal ID is handled gracefully

## 6. Write Unit Tests for REST API

- [x] 6.1 Write `GatewayResourceTest.java` with `@QuarkusTest`
- [x] 6.2 Mock `TerminalRegistrationProducer` and `TerminalRegistrationGrpcClient`
- [x] 6.3 Test POST `/api/register` returns 200 for valid data
- [x] 6.4 Test Kafka send failure returns 500
- [x] 6.5 Test GET `/health` returns "OK"

## 7. Run Tests and Verify Coverage

- [x] 7.1 Run `mvn test` and verify all tests pass
- [x] 7.2 Check test coverage for brahma-gateway
- [x] 7.3 Fix any failing tests or bugs found during testing

## 8. Update Documentation

- [x] 8.1 Update `GIGACODE.md` to mention unit tests
- [x] 8.2 Add test instructions to README.md (optional)
