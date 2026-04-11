import mlflow.xgboost
import pandas as pd
from app.core.config import settings

mlflow.set_tracking_uri(settings.MLFLOW_TRACKING_URI)

class FraudPredictor:
    def __init__(self):
        print(f"Loading {settings.MODEL_STAGE} model from MLflow ({settings.MLFLOW_TRACKING_URI})...")
        model_uri = f"models:/{settings.MLFLOW_MODEL_NAME}/{settings.MODEL_STAGE}"
        self.model = mlflow.xgboost.load_model(model_uri)
        print("Model loaded successfully!")
        
        # These MUST match the order and names used during training
        self.feature_names = [
            'V1', 'V2', 'V3', 'V4', 'V5', 'V6', 'V7', 'V8', 'V9', 'V10',
            'V11', 'V12', 'V13', 'V14', 'V15', 'V16', 'V17', 'V18', 'V19', 'V20',
            'V21', 'V22', 'V23', 'V24', 'V25', 'V26', 'V27', 'V28',
            'normAmount', 'normTime'
        ]

    def predict(self, features: list):
        # We turn the list into a DataFrame and APPLY the labels (columns)
        input_df = pd.DataFrame([features], columns=self.feature_names)

        # Now XGBoost will be happy because the names match!
        prediction = self.model.predict(input_df)
        probability = self.model.predict_proba(input_df)

        return {
            "is_fraud": int(prediction[0]),
            "confidence": float(max(probability[0]))
        }


predictor = FraudPredictor()