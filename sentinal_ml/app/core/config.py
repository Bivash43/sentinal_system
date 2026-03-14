import os
from pathlib import Path


class Settings:
    PROJECT_NAME: str = "Project Sentinel AI"

    # This finds the directory where config.py is, then goes up 2 levels to 'sentinal_ml'
    BASE_DIR = Path(__file__).resolve().parent.parent.parent

    # Path: sentinal_ml / m_learning / model / sentinel_model.joblib
    MODEL_PATH: str = str(os.path.join(BASE_DIR, "m_learning", "model", "sentinel_model.joblib"))


settings = Settings()