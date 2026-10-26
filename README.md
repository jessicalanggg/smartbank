# SmartBank — Sprint 1

A Java 21 / Spring Boot REST API with PostgreSQL, Flyway, Docker Compose, and tests. **Educational simulation only.** All data is synthetic. Authentication is intentionally not implemented yet; run locally only.

## Prerequisites
- Docker Desktop with Docker Compose
- Optional for running outside Docker: JDK 21 and Maven 3.9+

## Fastest start (PowerShell, from repository root)
```powershell
cd infra
docker compose up --build -d
docker compose ps
Invoke-RestMethod http://localhost:8080/actuator/health
Invoke-RestMethod http://localhost:8080/api/v1/accounts
Invoke-RestMethod http://localhost:8080/api/v1/accounts/11111111-1111-4111-8111-111111111111
```
First build downloads dependencies and may take several minutes. For logs:
```powershell
docker compose logs -f backend
```
Stop without deleting data:
```powershell
docker compose down
```
Reset database (deletes all local SmartBank data):
```powershell
docker compose down -v
```

## Run tests locally
From repository root:
```powershell
cd backend
mvn test
```
These Sprint 1 tests use mocks and do not require PostgreSQL. CI runs `mvn verify`.

## Run backend outside Docker
Start only PostgreSQL:
```powershell
cd infra
docker compose up -d db
cd ../backend
mvn spring-boot:run
```
Default DB credentials are for local development only. Override DB_URL, DB_USER, DB_PASSWORD with environment variables.

## API endpoints
- `GET /actuator/health`
- `GET /api/v1/accounts`
- `GET /api/v1/accounts/11111111-1111-4111-8111-111111111111`
- `GET /api/v1/accounts/{unknown-uuid}` -> 404

## Source structure
- `backend/src/main/java/com/smartbank/account` — controller, DTO, service, entity, repository
- `backend/src/main/java/com/smartbank/common` — API error handling
- `backend/src/main/resources/db/migration` — versioned SQL schema and demo records
- `backend/src/test` — JUnit / Mockito / MockMvc tests
- `infra/compose.yml` — local containers
- `.github/workflows/ci.yml` — automated Maven verification

## Learning objectives
Explain Controller -> Service -> Repository -> Database; dependency injection; DTOs vs entities; UUID primary keys; Flyway vs Hibernate schema validation; HTTP 200 vs 404; test doubles.

## Security limitations
No authentication, authorization, rate limiting, or audit ledger in Sprint 1. Account balances are read-only. **Do not expose this service to the internet or use real personal or financial data.** The next sprint adds security before financial mutations.
