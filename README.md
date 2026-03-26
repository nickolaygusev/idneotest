# Blood Sample Management Service

A Spring Boot-based backend service for managing clinical blood samples, including registration, retrieval with filtering, and processing lifecycle management.

## Architecture Decisions

- **Layered Architecture**: Follows the standard `controller` -> `service` -> `domain` -> `repository` pattern.
- **Domain-Driven Design (Partial)**: Core business logic resides in the service layer, while the domain model represents the state.
- **DTOs & Mapping**: Uses Java records for DTOs and MapStruct for automated, type-safe mapping between entities and DTOs.
- **Event-Driven Notification**: Publishes a `BloodSampleProcessedEvent` when a sample is marked as PROCESSED, handled by an internal `@EventListener`.
- **Database Migrations**: Uses Liquibase to manage the PostgreSQL schema (table creation, indexes, constraints).
- **Error Handling**: Centralized exception handling via `@ControllerAdvice` providing standardized error responses.
- **REST Testing**: Uses `WebTestClient` for integration testing with Testcontainers (PostgreSQL).

## Prerequisites

- Java 21
- Maven 3.9+
- Docker (for running tests with Testcontainers)
- PostgreSQL (optional, if not using Docker Compose)

## How to Run

### Docker Compose (Recommended)

To run the entire stack (Application + PostgreSQL database) using Docker Compose:

1. Ensure Docker and Docker Compose are installed.
2. Verify the settings in the `.env` file (you can rename provided `.env.example` with default credentials into `.env`).
3. Build and start the containers:
   ```bash
   docker-compose up --build
   ```
4. The application will be available at `http://localhost:8080`.
5. Database connection settings can be managed via the `.env` file without changing the configuration files.

### Configuration
Sensitive data and connection URLs are managed via the `.env` file in the project root. You can modify database credentials, ports, and connection strings there without touching the code or Docker configuration.

### Local Execution (Manual)

1. Ensure a PostgreSQL instance is running.
2. Configure database credentials in `src/main/resources/application.yaml` or set environment variables:
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   - `SPRING_DATASOURCE_URL`
3. Run the application using Maven:
   ```bash
   ./mvnw spring-boot:run
   ```

### Documentation

- **OpenAPI UI**: Once the app is running, access the Swagger UI at `http://localhost:8080/swagger-ui.html`.
- **Postman**: A collection is provided in `BloodSampleManagement.postman_collection.json`.

## How to Run Tests

The project includes unit tests and integration tests (using Testcontainers).

```bash
./mvnw clean test
```

## Database Migrations

Liquibase migrations are automatically applied on application startup. 
- **Master Changelog**: `src/main/resources/db/changelog/db.changelog-master.xml`
- **Changesets**: `src/main/resources/db/changelog/changesets/`

To verify migrations without starting the full app, you can use the Liquibase Maven plugin (if configured) or check the `DATABASECHANGELOG` table in your DB.

## Validation & Filtering

- **Validation**: Jakarta Validation is used on DTOs (e.g., `patientId` not null, `collectedAt` not in the future).
- **Filtering**: `GET /blood-sample` supports dynamic filtering using JPA Specifications for `status`, `patientId`, and `collectedAt` date range.
