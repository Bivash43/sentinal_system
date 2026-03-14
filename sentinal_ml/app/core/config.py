import os
from pathlib import Path
from dotenv import load_dotenv

# Load the .env file
load_dotenv()


class Settings:
    PROJECT_NAME: str = os.getenv("PROJECT_NAME", "Sentinel AI")

    # Model Path Logic
    BASE_DIR = Path(__file__).resolve().parent.parent.parent
    MODEL_PATH: str = str(os.path.join(BASE_DIR, "m_learning", "model", "sentinel_model.joblib"))

    # Kafka Configs
    KAFKA_SERVERS: str = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
    TOPIC_TRANSACTIONS: str = os.getenv("KAFKA_TRANSACTIONS_TOPIC", "transactions")
    TOPIC_RESULTS: str = os.getenv("KAFKA_RESULTS_TOPIC", "fraud_results")


settings = Settings()