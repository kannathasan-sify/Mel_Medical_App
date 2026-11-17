from pydantic import BaseModel, Field
from typing import Optional, List
from datetime import datetime
from enum import Enum

class ConsentCategory(str, Enum):
    AUDIO_RECORDING = "audio_recording"
    AI_PROCESSING = "ai_processing"
    PROVIDER_SHARING = "provider_sharing"

class ConsentStatus(str, Enum):
    GRANTED = "granted"
    REVOKED = "revoked"
    PENDING = "pending"

class ConsentRecordCreate(BaseModel):
    consultation_id: Optional[str] = None
    consent_category: ConsentCategory
    status: ConsentStatus = ConsentStatus.GRANTED
    purpose: str
    policy_version: str = "2026.1"
    participant_names: List[str] = Field(default_factory=list)
    signature_confirmation: bool = True

class ConsentRecordResponse(BaseModel):
    id: str
    user_id: str
    consultation_id: Optional[str] = None
    consent_category: ConsentCategory
    status: ConsentStatus
    purpose: str
    policy_version: str
    timestamp: datetime
    participant_names: List[str]
    withdrawal_timestamp: Optional[datetime] = None

