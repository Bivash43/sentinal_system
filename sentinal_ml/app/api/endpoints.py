from fastapi import APIRouter, HTTPException
from pydantic import BaseModel, Field
from app.services.predictor import predictor

router = APIRouter()

class TransactionData(BaseModel):
    # Expecting 30 numerical features (V1-V28, Time, Amount)
    features: list[float] = Field(..., min_items=30, max_items=30)

@router.post("/predict")
async def predict(data: TransactionData):
    try:
        result = predictor.predict(data.features)
        return result
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))