## ADDED Requirements

### Requirement: Регистрация терминала через HTTP форму
Система SHALL позволять регистрировать терминал через веб-форму HTTP с полем location.

#### Scenario: Успешная регистрация терминала в Москве
- **WHEN** пользователь вводит ID TERM-00027, модель TEST-001 и location=Москва, нажимает Register
- **THEN** терминал сохраняется в gateway.terminals с статусом Registered
- **AND** терминал сохраняется в processor.terminals с статусом Registered

### Requirement: Поиск терминала через веб-форму
Система SHALL позволять искать терминал по ID и отображать информацию из обеих БД.

#### Scenario: Поиск зарегистрированного терминала
- **WHEN** пользователь вводит ID TERM-00027 и нажимает Search
- **THEN** отображаются данные из gateway.terminals (статус: Registered)
- **AND** отображаются данные из processor.terminals (статус: Registered)

### Requirement: Удаление терминала через веб-форму
Система SHALL позволять удалять терминал из обеих баз данных через UI.

#### Scenario: Успешное удаление терминала
- **WHEN** пользователь вводит ID TERM-00027 и нажимает Delete
- **THEN** терминал удаляется из gateway.terminals
- **AND** терминал удаляется из processor.terminals
- **AND** при повторном поиске терминал не найден

### Requirement: Регистрация через gRPC форму
Система SHALL предоставлять отдельную форму для gRPC регистрации с проверкой статуса.

#### Scenario: Регистрация терминала через gRPC с location=Berlin
- **WHEN** пользователь использует gRPC форму, вводит ID TERM-00027, модель TEST-002 и location=Berlin
- **THEN** терминал регистрируется в gateway.terminals
- **AND** терминал появляется в processor.terminals со статусом Rejected

### Requirement: Проверка отсутствия терминала после удаления
Система SHALL корректно отображать, что терминал не найден после удаления.

#### Scenario: Поиск удаленного терминала
- **WHEN** пользователь вводит ID TERM-00027 после удаления и нажимает Search
- **THEN** отображается сообщение "Terminal not found" или пустой результат
