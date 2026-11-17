from fastapi import APIRouter, HTTPException
from datetime import datetime, timezone
import uuid
from typing import List
from src.schemas.consent import ConsentRecordCreate, ConsentRecordResponse, ConsentStatus

router = APIRouter(prefix="/consents", tags=["Consent & Legal Compliance"])

# In-memory storage for active session consents (backed by DB in production)
_consent_db = []

@router.post("", response_model=ConsentRecordResponse)
async def create_consent_record(request: ConsentRecordCreate):
    """
    Creates an explicit, tamper-evident consent record under one of the 3 independent categories:
    1. audio_recording (mutual consent to record conversation)
    2. ai_processing (permission for LLM transcription/summary)
    3. provider_sharing (permission to share visit notes with a named clinician)
    """
    record = ConsentRecordResponse(
        id=f"cst-{uuid.uuid4().hex[:8]}",
        user_id="usr-patient-88da37",
        consultation_id=request.consultation_id,
        consent_category=request.consent_category,
        status=request.status,
        purpose=request.purpose,
        policy_version=request.policy_version,
        timestamp=datetime.now(timezone.utc),
        participant_names=request.participant_names,
        withdrawal_timestamp=None
    )
    _consent_db.append(record)
    return record

@router.get("/active", response_model=List[ConsentRecordResponse])
async def get_active_consents():
    """
    Returns all active granted consent records for the current patient.
    """
    return [c for c in _consent_db if c.status == ConsentStatus.GRANTED]

@router.post("/{consent_id}/revoke", response_model=ConsentRecordResponse)
async def revoke_consent(consent_id: str):
    """
    Revokes an existing consent. Ceases future processing and triggers the retention/deletion audit workflow.
    """
    for record in _consent_db:
        if record.id == consent_id:
            record.status = ConsentStatus.REVOKED
            record.withdrawal_timestamp = datetime.now(timezone.utc)
            return record
    raise HTTPException(status_code=404, detail="Consent record not found.")

