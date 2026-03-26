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
- PostgreSQL (for running the application locally)

## How to Run

### Local Execution

1. Ensure a PostgreSQL instance is running.
2. Configure database credentials in `src/main/resources/application.yaml` or set environment variables:
   - `DB_USER`
   - `DB_PASSWORD`
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
