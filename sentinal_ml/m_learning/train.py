import os
import pandas as pd
import mlflow
import mlflow.xgboost
from xgboost import XGBClassifier
from sklearn.metrics import precision_score, recall_score, f1_score
from core.config import ml_config

# Ensure we're in the right directory for relative 'data/' paths
os.chdir(os.path.dirname(os.path.abspath(__file__)))

mlflow.set_tracking_uri(ml_config.MLFLOW_TRACKING_URI)
mlflow.set_experiment("Sentinal_Fraud_Detection")

print("Loading data...")
X_train = pd.read_parquet('data/X_train.parquet')
X_test = pd.read_parquet('data/X_test.parquet')
y_train = pd.read_parquet('data/y_train.parquet')
y_test = pd.read_parquet('data/y_test.parquet')

with mlflow.start_run():
    n_estimators = 100
    max_depth = 4
    learning_rate = 0.1
    
    mlflow.log_params({
        "n_estimators": n_estimators,
        "max_depth": max_depth,
        "learning_rate": learning_rate
    })

    model = XGBClassifier(n_estimators=n_estimators, max_depth=max_depth, learning_rate=learning_rate)

    print("The AI is studying the patterns... please wait.")
    model.fit(X_train, y_train)
    
    print("Evaluating model...")
    y_pred = model.predict(X_test)
    
    precision = precision_score(y_test, y_pred)
    recall = recall_score(y_test, y_pred)
    f1 = f1_score(y_test, y_pred)
    
    mlflow.log_metrics({
        "precision": precision,
        "recall": recall,
        "f1": f1
    })
    
    print(f"Metrics - Precision: {precision:.4f}, Recall: {recall:.4f}, F1: {f1:.4f}")

    print("Registering model to MLflow...")
    mlflow.xgboost.log_model(
        xgb_model=model,
        artifact_path="xgboost-model",
        registered_model_name=ml_config.MLFLOW_MODEL_NAME
    )
    
    client = mlflow.tracking.MlflowClient()
    versions = client.search_model_versions(f"name='{ml_config.MLFLOW_MODEL_NAME}'")
    # Sort by version number numerically, taking the highest
    latest_version = sorted([int(v.version) for v in versions])[-1]
    
    client.transition_model_version_stage(
        name=ml_config.MLFLOW_MODEL_NAME,
        version=str(latest_version),
        stage=ml_config.MODEL_STAGE,
        archive_existing_versions=False
    )
    
    print(f"Training complete and model pushed to '{ml_config.MODEL_STAGE}' stage!")