# Changelog

All notable changes to this project will be documented in this file.

The format is inspired by [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project aims to follow [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added
- Integrated Jaeger all-in-one distributed tracing observability stack within local orchestration to track W3C traceparents.
- Configured native OpenTelemetry Spans across Kafka boundaries using explicitly instantiated Micrometer Propagating Senders to enforce Jaeger System Architecture linkages.
- Expanded `.gitignore` with comprehensive exclusions for standard IDEs, Java/Maven build artifacts, and Python cache directories to keep version control clean.
- Created root-level `.env.example` as a template for securely injecting production environment variables.
- Integrated ShedLock distributed locking mechanism to prevent `OutboxScheduler` race conditions across horizontally scaled Spring Boot instances.
- Configured programmatic Kafka `NewTopic` beans overriding Spring Boot's internal partition defaults to enforce 32 parallel partitions.
- Transitioned AI Inference processing to evaluate fully-scaled 2D matrices across batched transactions seamlessly.
- Open-source style README with badges and contributor-facing sections.
- `CONTRIBUTING.md` to standardize contribution workflow.
- `CHANGELOG.md` to track release history.
- `sentinal_ml/tests/fire_transactions.py` script for sending mixed transaction loads (normal, velocity stress, high amount) to the analyze API.
- `VelocityServiceTest` with Mockito coverage for velocity-limit exceed path using mocked `StringRedisTemplate`.
- JWT authentication with `POST /api/auth/login`.
- DB-backed users (`app_users`) with RBAC-enabled user CRUD APIs.
- Seeded bootstrap admin creation on startup via `app.security.bootstrap-admin.*`.
- Role module (`app_roles`) with CRUD APIs and default role seeding (`ADMIN`, `ANALYST`, `VIEWER`).
- Seeded-admin-only guard for role CRUD operations.
- Forbidden security audit service with structured `SECURITY_AUDIT 403` log entries.
- 403 Prometheus metric counter (`sentinal.security.forbidden`).
- Request/response DTO split into `dto/request` and `dto/response`.
- `TransactionResponse` DTO for typed analyze API responses.
- Swagger/OpenAPI bearer security scheme so JWT authorize is available in Swagger UI.
- Auth-enabled ML transaction script flow (`fire_transactions.py`) that logs in first and sends Bearer tokens.
- DB-backed JWT refresh token rotation API (`POST /api/auth/refresh`) and token revocation (`POST /api/auth/logout`).
- Reduced JWT access token expiry to 15 minutes for heightened security, backed by a 7-day refresh token.
- Explicit endpoint exception exposure for `/error` path to prevent 401 masking of internal errors.
- Integrated Liquibase schema versioning to safely create and track database changes without relying on automatic DDL generation.
- Created programmatic `LiquibaseConfig` enforcing Liquibase schema execution prior to Hibernate data validations.
- Explicit foreign keys applied natively in PostgreSQL for relationships spanning users, refresh tokens, and transactions.
- Configured Kafka Dead Letter Queue (DLQ) with 3 retries and a fallback topic (`.DLT`).
- Added `DeadLetterConsumer` to safely audit perpetually failing Kafka messages.
- Implemented Transactional Outbox Pattern to guarantee at-least-once Kafka message delivery and safeguard against partial application crashes.
- Re-architected `FraudResultConsumer` with strict idempotency checks to safely ignore duplicate AI responses.
- Integrated Resilience4j Circuit Breaker dynamically preventing Redis connection bottlenecks by falling back natively to ML Worker inference.
- Added API and Web/Security layer test coverage for `AuthController` and `TransactionController` to validate JWTs and RBAC logic.
- Integrated `Testcontainers` for infrastructure-independent complete end-to-end integration testing.
- Added GitHub Actions workflow (`backend-ci.yml`) for automated Continuous Integration on push and pull requests to `main`.
- Integrated MLflow Tracking Server into local Docker Compose orchestration mapped deeply via relative volume networking.
- Refactored local Jupyter notebook training concepts into a formalized `train.py` pipeline wrapping XGBoost inside MLflow Autologging.
- Dynamically integrated MLflow Model Registry via the inference worker (`predictor.py`) bypassing static binary file reads entirely.

### Changed
- Parameterized backend security credentials and infrastructure endpoints in `application.properties` and `docker-compose.yml` to rely on robust OS-level environment variables for production deployment.
- Re-architected `Sentinal AI Worker` core orchestration from a synchronous loop to resilient chronological micro-batching using `KafkaConsumer.poll()`.
- Updated `TransactionProducer` serialization mapping deterministic `cardNumber` keys to assure synchronized chronological evaluations across partitions.
- Updated project documentation for portfolio presentation.
- Updated `README.md` with a manual transaction test script section.
- Updated `docker-compose.yml` to add Kafka healthchecks and health-gated service startup for `ai-worker`.
- Added Redis container healthcheck to improve local infrastructure startup resilience.
- Removed obsolete Compose `version` key.
- Migrated backend security from basic in-memory credentials to JWT + database user details service.
- Hardened persistence properties preventing unstructured schema modifications: `spring.jpa.hibernate.ddl-auto` mapped to `validate`.
- Updated API response shape for `POST /api/transactions/analyze` to return structured JSON.
- Expanded README and contributor docs with authentication, RBAC, role CRUD, and audit coverage.
- Updated ML env/config with backend auth settings and aligned script execution from both repo root and `sentinal_ml` directory.
- Migrated MLflow from local SQLite storage to a robust, scalable PostgreSQL-backed remote environment.
- Configured dynamic environment-based model staging (`MODEL_STAGE`) and decoupled hardcoded configurations.

### Fixed
- Fixed CI failure by ensuring `TracingContextTest` extends `AbstractIntegrationTest` to correctly instantiate Testcontainers databases.

## [0.1.0] - 2026-03-29

### Added

- Initial distributed fraud orchestration platform:
  - Spring Boot backend
  - Kafka messaging
  - Python ML worker
  - PostgreSQL and Redis integration
  - Prometheus and Grafana monitoring
