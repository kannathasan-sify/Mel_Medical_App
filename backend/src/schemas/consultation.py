from pydantic import BaseModel, Field
from typing import List, Optional, Dict
from datetime import datetime
from enum import Enum

class ConsultationStatus(str, Enum):
    CREATED = "created"
    RECORDING = "recording"
    PROCESSING = "processing"
    COMPLETED = "completed"
    FAILED = "failed"

class MedicationDetail(BaseModel):
    name: str
    dosage: Optional[str] = None
    frequency: Optional[str] = None
    instructions: Optional[str] = None
    concerns_flagged: Optional[str] = None

class SourceReference(BaseModel):
    statement: str
    source_segment_ids: List[str] = Field(default_factory=list)
    source_start_ms: int = 0
    source_end_ms: int = 0
    classification: str = "provider_stated" # provider_stated | patient_reported | ai_inferred
    confidence: float = 0.95

class TranscriptSegmentCreate(BaseModel):
    sequence_order: int
    start_time_ms: int
    end_time_ms: int
    speaker_role: str = "UNKNOWN" # PATIENT | PROVIDER | UNKNOWN
    text_content: str
    confidence_score: float = 0.95
    is_uncertain: bool = False

class TranscriptSegmentResponse(TranscriptSegmentCreate):
    id: str
    consultation_id: str
    created_at: datetime

class ConsultationCreate(BaseModel):
    title: Optional[str] = "Clinical Consultation"
    doctor_name: Optional[str] = "Primary Care Physician"
    clinic_location: Optional[str] = "General Clinic"
    scheduled_at: Optional[datetime] = None

class ConsultationResponse(BaseModel):
    id: str
    patient_id: str
    title: str
    doctor_name: str
    clinic_location: str
    status: ConsultationStatus
    transcription_status: str = "pending" # pending | completed | failed
    summary_status: str = "pending"       # pending | processing | completed | failed
    created_at: datetime
    updated_at: datetime

class SynthesisRequest(BaseModel):
    consultation_id: str
    patient_id: str
    transcript_text: str
    reading_level: str = "moderate" # simple | moderate | complex
    patient_historical_notes: Optional[str] = None

class VisitSummaryResponse(BaseModel):
    consultation_id: str
    plain_language_overview: str
    symptoms_reported: List[str]
    provider_explanations: List[str]
    diagnoses_explicit: List[str]
    medications_discussed: List[MedicationDetail]
    treatment_plan: List[str]
    unanswered_questions: List[str]
    follow_up_questions: List[str]
    source_references: List[SourceReference] = Field(default_factory=list)
    emergency_flag: bool = False
    emergency_notice: Optional[str] = None

class ShareConsultationRequest(BaseModel):
    provider_id: str
    scope: str = "full_summary" # full_summary | questions_only | medications_only
    expires_in_days: int = 30
    patient_notes: Optional[str] = None

class ShareConsultationResponse(BaseModel):
    share_id: str
    consultation_id: str
    provider_id: str
    scope: str
    status: str = "active"
    created_at: datetime
    expires_at: datetime

class DocumentExportRequest(BaseModel):
    format: str = "pdf" # pdf | json | markdown
    include_full_transcript: bool = False
    reading_level: str = "moderate"

class DocumentExportResponse(BaseModel):
    export_id: str
    consultation_id: str
    format: str
    download_url: str
    generated_at: datetime

class TranslationItem(BaseModel):
    original: str
    translated: str

class TranslationRequest(BaseModel):
    consultation_id: Optional[str] = None
    target_language: str = "Spanish"
    texts: List[str] = []

class TranslationResponse(BaseModel):
    target_language: str
    translations: List[TranslationItem]

class ExplainTermRequest(BaseModel):
    term: str

class ExplainTermResponse(BaseModel):
    term: str
    pronunciation: str
    plainMeaning: str
    whyItMatters: str
    doctorQuestions: List[str]
    commonSymptomsOrUses: Optional[str] = None
    category: str = "General Health"
