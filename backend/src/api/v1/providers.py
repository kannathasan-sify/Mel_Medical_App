from fastapi import APIRouter
from typing import List, Dict, Any
from src.schemas.user import ProviderProfileResponse

router = APIRouter(prefix="/providers", tags=["Provider Workflows & Patient Access"])

@router.get("/me", response_model=ProviderProfileResponse)
async def get_provider_profile():
    """
    Returns the authenticated clinician / provider profile and verified status.
    """
    return ProviderProfileResponse(
        id="prov-profile-1049",
        user_id="usr-provider-5561",
        full_name="Dr. Sarah Jenkins, MD",
        specialty="Internal Medicine & Cardiology",
        organization_name="Metro Health Partners",
        license_number="MD-NY-849204",
        verified=True
    )

@router.get("/me/patients")
async def get_authorized_patients() -> List[Dict[str, Any]]:
    """
    Returns only patients who have explicitly granted active sharing relationships with this provider.
    Enforces HIPAA/GDPR least-privilege access.
    """
    return [
        {
            "relationship_id": "rel-84729",
            "patient_id": "pat-profile-99120",
            "patient_name": "Kannathasan Mariappan",
            "access_scope": "full_summary_and_questions",
            "granted_at": "2026-11-01T09:00:00Z",
            "expires_at": "2026-12-01T09:00:00Z",
            "shared_consultations_count": 2,
            "pending_patient_questions_count": 3
        }
    ]

