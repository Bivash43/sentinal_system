# Project Sentinal

[![Java](https://img.shields.io/badge/Java-21-blue)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-6DB33F)](https://spring.io/projects/spring-boot)
[![Python](https://img.shields.io/badge/Python-3.11-yellow)](https://www.python.org/)
[![Docker Compose](https://img.shields.io/badge/Docker_Compose-Local_Stack-2496ED)](https://www.docker.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](./LICENSE)

Portfolio project that simulates a real-world fraud detection pipeline using event-driven architecture: Spring Boot API + Kafka + Python ML worker + PostgreSQL + Redis + Prometheus/Grafana.

## Portfolio Highlights

- Built as a distributed system, not a monolith.
- Uses asynchronous messaging for resilient fraud scoring.
- Combines rule-based velocity checks with ML inference.
- Includes observability with metrics and dashboards.
- Designed to show backend engineering + MLOps fundamentals.

## Table of Contents

- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Quick Start](#quick-start)
- [Authentication and RBAC](#authentication-and-rbac)
- [API Examples](#api-examples)
- [Manual Test Script](#manual-test-script)
- [Configuration](#configuration)
- [Observability](#observability)
- [Contributing](#contributing)
- [Changelog](#changelog)
- [Roadmap](#roadmap)
- [License](#license)

## Architecture

```text
Client -> Spring Boot API -> PostgreSQL (Liquibase managed)
                     |
                     v
             Kafka (transactions - 32 partitions)
                     |
                     v
       Python ML Worker (XGBoost Vectorized Batching)
                     |
                     v
             Kafka (fraud_results - 32 partitions)
                     |
                     v
         Spring Consumer -> PostgreSQL (APPROVED/FRAUD_FLAGGED)
```

AuthN/AuthZ layer:

```text
Client -> POST /api/auth/login -> JWT
Client + Bearer JWT -> protected APIs
RBAC -> method + endpoint checks
403 -> audit log + Prometheus counter
```

### Services

- `sentinal_backend`: REST API, validation, velocity check (Redis), Kafka producer/consumer, Liquibase schema management, persistence. Includes Transactional Outbox Pattern and Dead Letter Queue (DLQ) implementation for exactly-once guarantees and resiliency.
- `sentinal_ml`: Horizontally scalable Kafka worker that uses asynchronous micro-batching to rapidly evaluate transactions against MLflow Production models.
- `docker-compose.yml`: local infra for PostgreSQL, Redis, Kafka, Prometheus, Grafana, MLflow Registry, and worker container.

## Tech Stack

| Area | Technology |
| --- | --- |
| Backend | Java 21, Spring Boot, Spring Data JPA, Spring Security, Spring Kafka, Resilience4j |
| ML Worker | Python 3.11, kafka-python, scikit-learn, xgboost |
| MLOps | MLflow 2.10.x |
| Data | PostgreSQL, Redis |
| Messaging | Apache Kafka |
| Monitoring | Spring Actuator, Micrometer, Prometheus, Grafana |
| Infra | Docker Compose |

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.9+ (or use wrapper)
- Python 3.11+
- Docker + Docker Compose

### 1) Start infrastructure

```bash
docker compose up -d --build
```

Compose now waits for `postgres` and `kafka` healthchecks before starting `ai-worker`, which helps avoid startup connection errors.

### 2) Run backend

```bash
cd sentinal_backend
./mvnw spring-boot:run
```

### 3) Run worker locally (optional)

```bash
cd sentinal_ml
python -m venv .venv
source .venv/bin/activate   # PowerShell: .venv\Scripts\Activate.ps1
pip install -r requirements.txt
python -m app.worker
```

### 4) Run backend tests

```bash
cd sentinal_backend
./mvnw test
```
*Note: Backend tests include comprehensive E2E Security and API layer evaluations confirming JWT processing and RBAC logic against our secured endpoints.*

## Authentication and RBAC

### Login flow

1. Login with bootstrap admin credentials.
2. Copy returned JWT token.
3. Send token as `Authorization: Bearer <token>` for protected endpoints.

### Authorization model

- `POST /api/auth/login` is public.
- `POST /api/auth/refresh` is public.
- `POST /api/auth/logout` is protected (requires Bearer token).
- `POST /api/transactions/analyze` requires `ANALYST` or `ADMIN`.
- `/api/users/*` supports RBAC CRUD:
  - Create/Update/Delete: `ADMIN`
  - List/Get: `ADMIN` or `ANALYST`
  - `/api/users/me`: any authenticated user
- `/api/roles/*` is restricted to the seeded bootstrap admin username only.

## API Examples

### 1) Login

- **Endpoint:** `POST /api/auth/login`
- **URL:** `http://localhost:8080/api/auth/login`

```json
{
  "username": "admin",
  "password": "change-me-admin"
}
```

Response:

```json
{
  "token": "<jwt-access-token>",
  "refreshToken": "<jwt-refresh-token-uuid>",
  "username": "admin",
  "role": "ADMIN"
}
```

### 2) Refresh Token (public)

- **Endpoint:** `POST /api/auth/refresh`
- **URL:** `http://localhost:8080/api/auth/refresh`

```json
{
  "refreshToken": "<jwt-refresh-token-uuid>"
}
```

Response:

```json
{
  "accessToken": "<new-jwt-access-token>",
  "refreshToken": "<new-jwt-refresh-token>"
}
```

### 3) Logout (protected)

- **Endpoint:** `POST /api/auth/logout`
- **Headers:** `Authorization: Bearer <jwt-access-token>`
- **URL:** `http://localhost:8080/api/auth/logout`

Response:
```text
Log out successful
```

### 4) Analyze transaction (protected)

- **Endpoint:** `POST /api/transactions/analyze`
- **Headers:** `Authorization: Bearer <jwt-token>`
- **URL:** `http://localhost:8080/api/transactions/analyze`

```json
{
  "amount": 1250.0,
  "cardNumber": "4111111111111111",
  "currency": "USD",
  "merchantId": "M_8821",
  "features": [-0.613695895305449, 3.69877241384725, -5.53494116176501, 5.62048638535855, 1.64926285038792, -2.33514498091385, -0.907188472777421, 0.706362095310651, -3.74764612466131, -4.23098383571181, 4.43631907416962, -4.50380121641622, -0.954361345864011, -9.86137210765166, -0.505329037399437, 0.269281953090911, 0.591319064375358, 1.79599192045908, -1.08520791024384, 0.354772932978025, 0.319260753518317, -0.471378905146116, -0.075890410682347, -0.667909264857987, -0.642848415713801, 0.0706001067101182, 0.488409527815439, 0.292344974289491, -0.35322939296682354, 1.2437049904612003]
}
```

Response:

```json
{
  "transactionId": "2c8ec16c-3a2d-4d93-b5c1-ef66fcff89b5",
  "status": "PENDING",
  "message": "Transaction is being analyzed."
}
```

### 5) Role CRUD (seeded admin only)

- `POST /api/roles`
- `GET /api/roles`
- `GET /api/roles/{id}`
- `PUT /api/roles/{id}`
- `DELETE /api/roles/{id}`

Example create payload:

```json
{
  "name": "RISK_REVIEWER",
  "description": "Can review flagged transactions",
  "active": true
}
```

### Local URLs

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI: `http://localhost:8080/api-docs`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`
- MLflow Registry: `http://localhost:5000`

## Manual Test Script

Use the request script to send 20 transactions against the analyze endpoint:

- 5 normal transactions
- 10 high-frequency transactions (same card number, to hit Velocity Guard)
- 5 high-amount transactions (to exercise ML scoring paths)

This script authenticates first and then sends Bearer JWT requests. Ensure these are set in `sentinal_ml/.env`:

- `BACKEND_BASE_URL`
- `BACKEND_LOGIN_PATH`
- `BACKEND_ANALYZE_PATH`
- `BACKEND_USERNAME`
- `BACKEND_PASSWORD`

```bash
cd sentinal_ml
python tests/fire_transactions.py
```

## Configuration

### Backend

Set values in `sentinal_backend/src/main/resources/application.properties` or provide them via environment variables:

- DB: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` (`spring.datasource.*` defaults to local).
- Kafka: `KAFKA_BOOTSTRAP_SERVERS` (`spring.kafka.bootstrap-servers` default is local).
- Redis: `REDIS_HOST`, `REDIS_PORT`
- Velocity rules: `velocity.limit.*`
- Resilience4j: `resilience4j.circuitbreaker.*`

Security notes:

- Replace `JWT_SECRET`, `ADMIN_USERNAME`, `ADMIN_PASSWORD` in your production environment. A `.env.example` file is provided in the project root to securely inject these into the Docker environment.

### ML Worker

Copy `sentinal_ml/.env.example` to `sentinal_ml/.env`:

```bash
PROJECT_NAME='Project Sentinal AI'
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_TRANSACTIONS_TOPIC=transactions
KAFKA_RESULTS_TOPIC=fraud_results
BACKEND_BASE_URL=http://localhost:8080
BACKEND_LOGIN_PATH=/api/auth/login
BACKEND_ANALYZE_PATH=/api/transactions/analyze
BACKEND_USERNAME=admin
BACKEND_PASSWORD=change-me-admin

# MLflow
MLFLOW_TRACKING_URI=http://localhost:5000
MLFLOW_MODEL_NAME=Sentinal_Fraud_Model
MODEL_STAGE=Staging
```

## Continuous Integration & Testing

This project uses **GitHub Actions** for Continuous Integration. On every push and pull request to `main`:
1. The pipeline provisions `ubuntu-latest` with Java 21 and Maven.
2. **Testcontainers** automatically spins up isolated instances of **PostgreSQL, Redis, and Kafka**.
3. The end-to-end Spring integration tests are executed securely against this ephemeral infrastructure. 

*To run tests locally, you must have Docker running on your host machine natively.*

## Observability

- Backend metrics: `GET /actuator/prometheus`
- Prometheus scrape config: `prometheus.yml`
- Grafana default local login: `admin / admin`
- 403 audit log pattern: `SECURITY_AUDIT 403 ...`
- 403 metric: `sentinel_security_forbidden_total`

## Contributing

Contributions, issues, and feature requests are welcome.

Please read [CONTRIBUTING.md](./CONTRIBUTING.md) before opening a PR.

## Changelog

See [CHANGELOG.md](./CHANGELOG.md) for notable changes and release history.

## Roadmap

- Productionize active learning cycle on real-time fraud data.
- **Enterprise Scaling**: Replace the Scheduler-based Transactional Outbox pattern with Change Data Capture (Debezium + Kafka Connect) to eliminate database polling entirely.

## License

Distributed under the MIT License. See [LICENSE](./LICENSE) for details.
