# Repository Guidelines

A microservices-based terminal registration system built with Quarkus, PostgreSQL, and Kafka.

## Project Structure

This is a multi-module Maven project with four modules:

```
brahma/
├── brahma-common/      # Shared components (ENUMs, helpers)
├── brahma-gateway/     # Entry point for terminal registration (HTTP/gRPC)
├── brahma-processor/   # Kafka consumer, processes registrations
└── brahma-webui/       # Web interface for monitoring and management
```

Each module has standard structure:
- `src/main/java/` — application source code
- `src/main/resources/` — configuration and templates
- `src/test/java/` — unit/integration tests

## Build and Run

**Full build (all modules):**
```bash
mvn clean install
```

**Run gateway (dev mode):**
```bash
cd brahma-gateway && mvn quarkus:dev
```

**Run processor (dev mode):**
```bash
cd brahma-processor && mvn quarkus:dev
```

**Run webui (dev mode):**
```bash
cd brahma-webui && mvn quarkus:dev
```

**Full deployment (Docker + Kubernetes):**
```bash
# Build and push to Minikube
eval $(minikube docker-env)
cd brahma-gateway && docker build -t brahma-gateway:latest .
cd ../brahma-processor && docker build -t brahma-processor:latest .
cd ../brahma-webui && docker build -t brahma-webui:latest .
kubectl apply -f k8s/
```

## Coding Standards

- **Language:** Java 21
- **Framework:** Quarkus 3.32.3
- **Build Tool:** Maven 3.8+
- **Code style:** Follows Quarkus conventions
  - Use `-parameters` compiler arg for reflection
  - Package structure: `com.example.{module}.{layer}`
  - Naming: PascalCase for classes, camelCase for methods/fields

**Key technologies:**
- REST (JAX-RS) + Jackson for JSON
- Hibernate ORM + Panache for database access
- Kafka Client for messaging
- gRPC for inter-service communication
- PostgreSQL with schema separation (`gateway.*`, `processor.*`)

## Testing

- **Framework:** JUnit 5 + Quarkus Test
- **Run tests:** `mvn test`
- **Run tests for module:** `cd brahma-gateway && mvn test`
- **Coverage:** Unit tests required for new features

*Note:* Integration tests are currently disabled (`<skipITs>true</skipITs>`)

## Commit and PR Guidelines

- **Commit messages:** Descriptive, imperative mood ("Add feature", "Fix bug")
- **Branch naming:** Descriptive (e.g., `feature/terminal-validation`, `bugfix/kafka-retry`)
- **PR requirements:**
  - Build succeeds (`mvn clean install`)
  - Tests pass
  - No new compiler warnings

## Architecture Summary

**Registration flow:**
1. Client → `brahma-gateway` (HTTP POST `/api/register`)
2. Gateway saves to `gateway.terminals`, sends Kafka message to `terminal.registration`
3. `brahma-processor` consumes from `terminal.registration`
4. Processor saves to `processor.terminals`, publishes to `terminal.registered`
5. Gateway consumes `terminal.registered`, updates terminal status

**Kafka topics:**
- `terminal.registration` — new terminal registration requests
- `terminal.registered` — confirmation of activation

## Configuration

- Environment variables: `DB_HOST`, `DB_USER`, `DB_PASSWORD`, `KAFKA_BROKER`
- See `application.properties` in each module's `src/main/resources/`

## Kubernetes Deployment

**Prerequisites:**
- Minikube or Kubernetes cluster
- Docker accessible to Minikube
- PostgreSQL and Kafka available at `host.minikube.internal`

**Deploy:**
```bash
# Enable Minikube Docker environment
eval $(minikube docker-env)

# Build all images
cd brahma-gateway && docker build -t brahma-gateway:latest .
cd ../brahma-processor && docker build -t brahma-processor:latest .
cd ../brahma-webui && docker build -t brahma-webui:latest .

# Apply Kubernetes manifests
kubectl apply -f k8s/
```

**Access after deployment:**
- WebUI: http://minikube:30882
- Gateway: http://minikube:30880

**Check status:**
```bash
kubectl get pods
kubectl logs -f deployment/brahma-gateway
```

## CI/CD (Jenkins)

**Jenkinsfile** in project root supports:
- Maven build with optional cache clean
- Docker image build in Minikube
- Auto-deploy to Kubernetes with health checks

**Parameters:**
- `DEPLOY_WEBUI` — deploy WebUI (default: false)
- `DEPLOY_GATEWAY` — deploy Gateway (default: false)
- `DEPLOY_PROCESSOR` — deploy Processor (default: false)
- `CLEAN_MAVEN_CACHE` — clean .m2 before build (default: false)
