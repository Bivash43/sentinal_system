from fastapi import FastAPI
from app.api.endpoints import router as api_router
from app.core.config import settings
from prometheus_fastapi_instrumentator import Instrumentator

app = FastAPI(title=settings.PROJECT_NAME)

# Include our routes
app.include_router(api_router, prefix="/api/v1")
Instrumentator().instrument(app).expose(app)
@app.get("/")
async def root():
    return {"message": "Sentinel AI Service is running"}