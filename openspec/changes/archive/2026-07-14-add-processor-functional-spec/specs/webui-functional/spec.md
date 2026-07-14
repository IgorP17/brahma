## ADDED Requirements

### Requirement: WebUI Resource serves HTML home page
The TerminalWebResource SHALL serve the main WebUI page at GET /.

#### Scenario: Home page requested
- **WHEN** GET / is called
- **THEN** index.html template is rendered with message and backendUrl

#### Scenario: Home page uses backend URL
- **WHEN** Backend URL configured via BACKEND_URL environment variable
- **THEN** URL is passed to template for API calls

### Requirement: WebUI Resource provides terminal lookup
The TerminalWebResource SHALL provide terminal information via REST API.

#### Scenario: Get terminal by ID
- **WHEN** GET /terminal/{id} is called
- **THEN** JSON response with gateway and processor terminal data

#### Scenario: Terminal not found
- **WHEN** terminal ID not found in either gateway or processor
- **THEN** response contains NOT_FOUND placeholders

### Requirement: WebUI Resource provides terminal deletion
The TerminalWebResource SHALL allow deleting terminals.

#### Scenario: Delete from both
- **WHEN** DELETE /terminal/{id} is called
- **THEN** terminal is removed from both gateway and processor

#### Scenario: Delete from gateway only
- **WHEN** DELETE /terminal/{id}/gateway is called
- **THEN** terminal is removed from gateway.terminals only

#### Scenario: Delete from processor only
- **WHEN** DELETE /terminal/{id}/processor is called
- **THEN** terminal is removed from processor.terminals only

### Requirement: WebUI Service searches gateway terminals
The TerminalService SHALL search for terminals in gateway.terminals.

#### Scenario: Terminal found in gateway
- **WHEN** findTerminalById() is called with existing ID
- **THEN** TerminalViewGateway returned with all fields populated

#### Scenario: Terminal not found in gateway
- **WHEN** findTerminalById() is called with non-existent ID
- **THEN** TerminalViewGateway returned with NOT_FOUND placeholders

### Requirement: WebUI Service searches processor terminals
The TerminalService SHALL search for terminals in processor.terminals.

#### Scenario: Terminal found in processor
- **WHEN** findProcessorTerminalById() is called with existing ID
- **THEN** TerminalViewProcessor returned with all fields populated

#### Scenario: Terminal not found in processor
- **WHEN** findProcessorTerminalById() is called with non-existent ID
- **THEN** TerminalViewProcessor returned with NOT_FOUND placeholders

### Requirement: WebUI Service deletes from gateway
The TerminalService SHALL delete terminals from gateway.terminals.

#### Scenario: Delete terminal from gateway
- **WHEN** deleteTerminalFromGateway() is called
- **THEN** record is deleted from gateway.terminals

### Requirement: WebUI Service deletes from processor
The TerminalService SHALL delete terminals from processor.terminals.

#### Scenario: Delete terminal from processor
- **WHEN** deleteTerminalFromProcessor() is called
- **THEN** record is deleted from processor.terminals

### Requirement: WebUI DTO classes are data carriers
TerminalViewGateway and TerminalViewProcessor SHALL carry terminal data for UI display.

#### Scenario: TerminalViewGateway fields
- **WHEN** created from GatewayTerminal
- **THEN** all display fields (id, model, location, status, timestamps) are populated

#### Scenario: TerminalViewProcessor fields
- **WHEN** created from ProcessorTerminal
- **THEN** all display fields (id, model, location, status, timestamps) are populated