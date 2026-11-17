from fastapi import APIRouter
from datetime import datetime, timezone
from typing import List, Dict, Any
from src.schemas.user import PatientProfileResponse

router = APIRouter(prefix="/patients", tags=["Patient Profile & History"])

@router.get("/me", response_model=PatientProfileResponse)
async def get_patient_profile():
    """
    Returns the authenticated patient's profile, language, and reading level preferences.
    """
    return PatientProfileResponse(
        id="pat-profile-99120",
        user_id="usr-patient-88da37",
        name="Kannathasan Mariappan",
        preferred_name="Kannathasan",
        primary_language="English",
        communication_style="Empowering & Direct",
        reading_level_preference="8th_grade",
        created_at=datetime.now(timezone.utc)
    )

@router.get("/me/history")
async def get_patient_medical_history() -> Dict[str, Any]:
    """
    Returns the patient's chronological visit history, diagnosed conditions, and active medications.
    """
    return {
        "patient_id": "pat-profile-99120",
        "conditions": [
            {"condition_name": "Mild Hypertension", "diagnosed_year": 2024, "status": "active"},
            {"condition_name": "Seasonal Allergic Rhinitis", "diagnosed_year": 2022, "status": "intermittent"}
        ],
        "active_medications": [
            {"name": "Lisinopril", "dosage": "10mg", "frequency": "Once daily morning"},
            {"name": "Cetirizine", "dosage": "10mg", "frequency": "As needed"}
        ],
        "past_visits_count": 4,
        "last_visit_date": "2026-11-12T10:30:00Z"
    }

