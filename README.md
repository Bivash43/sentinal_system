# 🛡️ Project Sentinel: Real-Time Fraud Orchestration Platform

**Sentinel** is a production-grade, event-driven distributed system designed to detect fraudulent financial transactions. It bridges the gap between high-performance Backend Engineering (**Spring Boot**) and Predictive Analytics (**Python/ML**).

By using a **Dual-Path architecture**, the system mimics real-world fintech pipelines (like Stripe or PayPal) where transactions are validated through deterministic rules and asynchronous machine learning scoring.

---

## 🏗️ System Architecture

The system is decoupled into three main layers to ensure high availability and scalability:

1. **Ingestion & Rules Engine (Spring Boot):**
   - Receives RESTful transaction requests.
   - Executes "Hot Path" rules (e.g., limit checks, blacklisted IPs).
   - Persists transaction state to **PostgreSQL**.
   - Acts as a Kafka Producer to trigger deep analysis.
2. **Event Backbone (Apache Kafka):**
   - Acts as a fault-tolerant buffer.
   - Decouples the Java producer from the Python consumer.
3. **Intelligence Layer (FastAPI + XGBoost):**
   - Consumes events from Kafka.
   - Performs feature engineering (e.g., calculating velocity via **Redis**).
   - Generates fraud probability scores and emits decisions back to the system.
4. **Observability Suite (Prometheus & Grafana):**
   - Monitors system latency and ML model health (Drift).

---

## 🛠️ Tech Stack

| Component            | Technology                    | Role                                     |
| :------------------- | :---------------------------- | :--------------------------------------- |
| **Backend**          | Java 21, Spring Boot 3.x      | Orchestration, API, and Rules            |
| **Messaging**        | Apache Kafka                  | Event Streaming & Buffering              |
| **Machine Learning** | Python 3.11, FastAPI, XGBoost | Predictive Scoring & Inference           |
| **Primary DB**       | PostgreSQL                    | Transactional Source of Truth            |
| **Cache/Features**   | Redis                         | Real-time state (User Velocity)          |
| **Monitoring**       | Prometheus & Grafana          | System Health & Model Drift              |
| **Infrastructure**   | Docker & Docker Compose       | Local Deployment & Environment Isolation |

---

## 📡 Data Contract (Event Schema)

Transactions are passed via Kafka using a standardized JSON schema to ensure cross-language compatibility:

```json
{
  "transactionId": "uuid-v4",
  "userId": 55092,
  "amount": 1250.0,
  "currency": "USD",
  "merchantId": "M_8821",
  "location": "New York, US",
  "timestamp": "2026-03-03T16:30:00Z",
  "deviceIp": "192.168.1.45"
}
```

## 🚀 Senior-Level Engineering Patterns

### 1. **Asynchronous Decoupling**

The ML inference is computationally expensive. By using Kafka, the Ingestion Service remains highly responsive. If the ML service spikes in latency, transactions are queued rather than dropped.

### 2. **The "Audit Trail" Pattern**

Every ML decision is logged in the fraud_assessments table, including the model version used and the top features that contributed to the score. This is essential for regulatory compliance and model debugging.

### 3. **Feature Engineering with Redis**

To detect "Velocity Attacks" (many small transactions in seconds), the ML service queries Redis to get a count of transactions for a specific userId in the last 10 minutes, rather than hitting the main database.

### 4. Real-Time Model Drift Monitoring (Prometheus & Grafana)

Sentinel doesn't just serve predictions; it monitors its own accuracy.

- **Data Drift:** Tracks the distribution of input features (e.g., Transaction Amount) using a moving window.
- **Prediction Drift:** Monitors the "Mean Fraud Score." An abrupt upward shift suggests either a massive fraud attack or that the model's training data is stale.
- **Alerting:** Configured thresholds in Grafana trigger warnings when the "Population Stability Index" (PSI) or "KL Divergence" exceeds 0.2, indicating the model needs retraining.

## 💻 Local Development Setup

### 1. **Infrastructure (Docker)**

Ensure Docker is running and execute:

```bash
docker-compose up -d
```

This starts Kafka, Zookeeper, Postgres, and Redis.

### 2. **Backend (Java)**

```bash
cd sentinel-backend
./mvnw spring-boot:run
```

### 3. **ML Service (Python)**

```bash
cd sentinel-ml
pip install -r requirements.txt
uvicorn main:app --reload
```
