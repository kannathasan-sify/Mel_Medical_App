import sys
import os

# Ensure backend root is on sys.path
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from src.core.config import settings
from src.api.v1.auth import router as auth_router
from src.api.v1.patients import router as patient_router
from src.api.v1.providers import router as provider_router
from src.api.v1.consents import router as consent_router
from src.api.v1.consultations import router as consultation_router
from src.websockets.transcript_ws import router as websocket_router

app = FastAPI(
    title=settings.PROJECT_NAME,
    version=settings.VERSION,
    description="HIPAA & GDPR-compliant Clinical AI Backend for MEL (Medical Empowerment Liaison)",
    openapi_url=f"{settings.API_V1_PREFIX}/openapi.json",
    docs_url=f"{settings.API_V1_PREFIX}/docs"
)

# CORS Middleware for mobile apps & web portal
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.ALLOWED_ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Register versioned API routers
app.include_router(auth_router, prefix=settings.API_V1_PREFIX)
app.include_router(patient_router, prefix=settings.API_V1_PREFIX)
app.include_router(provider_router, prefix=settings.API_V1_PREFIX)
app.include_router(consent_router, prefix=settings.API_V1_PREFIX)
app.include_router(consultation_router, prefix=settings.API_V1_PREFIX)

# Register WebSockets
app.include_router(websocket_router)

@app.get("/health", tags=["Operational Health"])
async def health_check():
    return {
        "status": "healthy",
        "service": "MEL Healthcare Backend",
        "version": settings.VERSION,
        "supabase_connected": True
    }
