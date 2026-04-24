import os
from pathlib import Path
from dotenv import load_dotenv

load_dotenv()

class Settings:
    PROJECT_NAME: str = os.getenv("PROJECT_NAME", "Sentinal AI")

    # Inside Docker, we will set this to /app/model/sentinel_model.joblib
    DEFAULT_MODEL_PATH = os.path.join(Path(__file__).resolve().parent.parent, "m_learning", "model", "sentinel_model.joblib")
    MODEL_PATH: str = os.getenv("MODEL_PATH", DEFAULT_MODEL_PATH)
    
    # MLflow Configs
    MLFLOW_TRACKING_URI: str = os.getenv("MLFLOW_TRACKING_URI", "http://localhost:5000")
    MLFLOW_MODEL_NAME: str = os.getenv("MLFLOW_MODEL_NAME", "Sentinal_Fraud_Model")
    
    OTEL_EXPORTER_OTLP_ENDPOINT: str = os.getenv("OTEL_EXPORTER_OTLP_ENDPOINT", "http://localhost:4318/v1/traces")
    MODEL_STAGE: str = os.getenv("MODEL_STAGE", "Staging")

    # Kafka Configs
    KAFKA_SERVERS: str = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
    TOPIC_TRANSACTIONS: str = os.getenv("KAFKA_TRANSACTIONS_TOPIC", "transactions")
    TOPIC_RESULTS: str = os.getenv("KAFKA_RESULTS_TOPIC", "fraud_results")
    CONSUMER_GROUP: str = os.getenv("KAFKA_CONSUMER_GROUP_ID", "sentinal-group")

    # Backend API + Auth config (used by test/utility scripts)
    BACKEND_BASE_URL: str = os.getenv("BACKEND_BASE_URL", "http://localhost:8080")
    BACKEND_LOGIN_PATH: str = os.getenv("BACKEND_LOGIN_PATH", "/api/auth/login")
    BACKEND_ANALYZE_PATH: str = os.getenv("BACKEND_ANALYZE_PATH", "/api/transactions/analyze")
    BACKEND_USERNAME: str = os.getenv("BACKEND_USERNAME", "admin")
    BACKEND_PASSWORD: str = os.getenv("BACKEND_PASSWORD", "change-me-admin")

settings = Settings()