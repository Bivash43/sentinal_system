import os
from pathlib import Path
from dotenv import load_dotenv

# Try to load .env from the root of sentinal_ml
dotenv_path = Path(__file__).resolve().parent.parent.parent / '.env'
load_dotenv(dotenv_path)

class MLConfig:
    MLFLOW_TRACKING_URI: str = os.getenv("MLFLOW_TRACKING_URI", "http://localhost:5000")
    MLFLOW_MODEL_NAME: str = os.getenv("MLFLOW_MODEL_NAME", "Sentinal_Fraud_Model")
    MODEL_STAGE: str = os.getenv("MODEL_STAGE", "Staging")

ml_config = MLConfig()
