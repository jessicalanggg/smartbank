# SmartBank Sprint 1 Architecture

HTTP client -> AccountController -> AccountService -> AccountRepository -> PostgreSQL.

Spring MVC handles routing and JSON serialization. Spring Data JPA implements persistence. Flyway owns schema changes. The controller returns DTOs, not JPA entities. All accounts are synthetic and endpoints are intentionally unauthenticated in Sprint 1; never deploy this sprint as a public banking service.

## Sprint 2
Add user registration, Spring Security, account ownership, and integration tests. Do not implement transfers until authentication and ownership checks exist.
