import os
from pathlib import Path
from dotenv import load_dotenv

load_dotenv()

class Settings:
    PROJECT_NAME: str = os.getenv("PROJECT_NAME", "Sentinel AI")

    # Inside Docker, we will set this to /app/model/sentinel_model.joblib
    DEFAULT_MODEL_PATH = os.path.join(Path(__file__).resolve().parent.parent, "m_learning", "model", "sentinel_model.joblib")
    MODEL_PATH: str = os.getenv("MODEL_PATH", DEFAULT_MODEL_PATH)

    # Kafka Configs
    KAFKA_SERVERS: str = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
    TOPIC_TRANSACTIONS: str = os.getenv("KAFKA_TRANSACTIONS_TOPIC", "transactions")
    TOPIC_RESULTS: str = os.getenv("KAFKA_RESULTS_TOPIC", "fraud_results")
    CONSUMER_GROUP: str = os.getenv("KAFKA_CONSUMER_GROUP_ID", "sentinal-group")

settings = Settings()