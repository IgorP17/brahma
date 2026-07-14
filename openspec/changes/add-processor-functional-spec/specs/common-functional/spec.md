## ADDED Requirements

### Requirement: TerminalStatus enum defines terminal states
The TerminalStatus enum SHALL define all possible terminal registration states.

#### Scenario: REGISTERED status
- **WHEN** location is Moscow
- **THEN** status = REGISTERED

#### Scenario: REJECTED status
- **WHEN** location is not Moscow
- **THEN** status = REJECTED

#### Scenario: IN_PROCESS status
- **WHEN** terminal is being processed
- **THEN** status = IN_PROCESS

#### Scenario: ACTIVE status
- **WHEN** terminal is active
- **THEN** status = ACTIVE

#### Scenario: INACTIVE status
- **WHEN** terminal is inactive
- **THEN** status = INACTIVE

### Requirement: TerminalLogicHelper determines status from location
The TerminalLogicHelper SHALL determine terminal status based on location.

#### Scenario: Moscow location detected
- **WHEN** location contains "moscow" or "москва" (case insensitive)
- **THEN** isLocationMoscow() returns true

#### Scenario: Non-Moscow location
- **WHEN** location does not contain Moscow keywords
- **THEN** isLocationMoscow() returns false

#### Scenario: Status determination
- **WHEN** determineStatus() is called with location
- **THEN** returns REGISTERED for Moscow, REJECTED otherwise