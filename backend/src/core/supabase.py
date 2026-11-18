import os
import uuid
import logging
from typing import List, Dict, Any, Optional
from datetime import datetime, timezone
from supabase import create_client, Client
from src.core.config import settings

logger = logging.getLogger(__name__)

class SupabaseService:
    def __init__(self):
        self.url = settings.SUPABASE_URL
        self.key = settings.SUPABASE_SERVICE_ROLE_KEY or settings.SUPABASE_ANON_KEY
        self._client: Optional[Client] = None
        self._init_client()

    def _init_client(self):
        try:
            self.key = os.getenv("SUPABASE_SERVICE_ROLE_KEY", "") or settings.SUPABASE_SERVICE_ROLE_KEY or os.getenv("SUPABASE_ANON_KEY", "") or settings.SUPABASE_ANON_KEY
            if self.url and self.key:
                self._client = create_client(self.url, self.key)
                logger.info("Supabase client initialized successfully.")
        except Exception as e:
            logger.error(f"Failed to initialize Supabase client: {e}")
            self._client = None

    @property
    def client(self) -> Optional[Client]:
        if not self._client:
            self._init_client()
        return self._client

    def save_patient_profile(self, patient_id: str, email: str = "patient@melapp.com", full_name: str = "Patient") -> Optional[Dict[str, Any]]:
        """
        Ensures a patient profile exists in public.patient_profiles table.
        """
        if not self.client:
            return None
        pid = self._to_valid_uuid(patient_id)
        row = {
            "id": pid,
            "email": email,
            "full_name": full_name,
            "medical_summary_detail_level": "moderate",
            "preferred_language": "en"
        }
        try:
            res = self.client.table("patient_profiles").upsert(row).execute()
            logger.info(f"Upserted patient_profile {pid} in Supabase.")
            return res.data[0] if res.data else row
        except Exception as e:
            logger.warning(f"Could not upsert patient profile: {e}")
            return None

    def save_consultation(
        self,
        consultation_id: str,
        patient_id: str,
        visit_type: str = "Clinical Consultation",
        provider_name: str = "Attending Physician",
        status: str = "COMPLETED"
    ) -> Optional[Dict[str, Any]]:
        """
        Saves or updates a consultation record in public.consultations table.
        """
        if not self.client:
            logger.warning("Supabase client not available.")
            return None
        
        # Ensure UUID format for Supabase compatibility
        cid = self._to_valid_uuid(consultation_id)
        pid = self._to_valid_uuid(patient_id)
        now_iso = datetime.now(timezone.utc).isoformat()

        row = {
            "id": cid,
            "patient_id": pid,
            "provider_name": provider_name,
            "visit_type": visit_type,
            "status": status,
            "started_at": now_iso,
            "ended_at": now_iso,
            "created_at": now_iso
        }
        try:
            res = self.client.table("consultations").upsert(row).execute()
            logger.info(f"Saved consultation {cid} to Supabase: {res.data}")
            return res.data[0] if res.data else row
        except Exception as e:
            logger.error(f"Error saving consultation to Supabase: {e}")
            return None

    def save_visit_summary(
        self,
        consultation_id: str,
        plain_language_overview: str,
        symptoms_reported: List[str],
        provider_explanations: List[str],
        diagnoses_explicit: List[str],
        medications_discussed: List[Dict[str, Any]],
        treatment_plan: List[str],
        unanswered_questions: List[str]
    ) -> Optional[Dict[str, Any]]:
        """
        Saves visit summary in public.visit_summaries table.
        """
        if not self.client:
            return None

        cid = self._to_valid_uuid(consultation_id)
        summary_id = str(uuid.uuid4())
        now_iso = datetime.now(timezone.utc).isoformat()

        row = {
            "id": summary_id,
            "consultation_id": cid,
            "plain_language_overview": plain_language_overview,
            "symptoms_reported": symptoms_reported,
            "provider_explanations": provider_explanations,
            "diagnoses_explicit": diagnoses_explicit,
            "medications_discussed": medications_discussed,
            "treatment_plan": treatment_plan,
            "unanswered_questions": unanswered_questions,
            "generated_at": now_iso
        }
        try:
            res = self.client.table("visit_summaries").upsert(row, on_conflict="consultation_id").execute()
            logger.info(f"Saved visit summary {summary_id} to Supabase: {res.data}")
            return res.data[0] if res.data else row
        except Exception as e:
            logger.error(f"Error saving visit summary to Supabase: {e}")
            return None

    def save_follow_up_questions(
        self,
        summary_id: str,
        questions: List[str]
    ) -> List[Dict[str, Any]]:
        """
        Saves follow-up questions in public.follow_up_questions table.
        """
        if not self.client or not questions:
            return []

        sid = self._to_valid_uuid(summary_id)
        rows = [
            {
                "id": str(uuid.uuid4()),
                "visit_summary_id": sid,
                "question_text": q,
                "category": "CLARIFICATION",
                "is_patient_edited": False,
                "is_answered": False,
                "created_at": datetime.now(timezone.utc).isoformat()
            }
            for q in questions
        ]
        try:
            res = self.client.table("follow_up_questions").insert(rows).execute()
            logger.info(f"Saved {len(rows)} follow-up questions to Supabase.")
            return res.data or rows
        except Exception as e:
            logger.error(f"Error saving follow-up questions to Supabase: {e}")
            return []

    def save_transcript_segments(
        self,
        consultation_id: str,
        segments: List[Dict[str, Any]]
    ) -> List[Dict[str, Any]]:
        """
        Saves spoken transcript segments in public.transcript_segments table.
        """
        if not self.client or not segments:
            return []

        cid = self._to_valid_uuid(consultation_id)
        rows = []
        for idx, seg in enumerate(segments):
            rows.append({
                "id": str(uuid.uuid4()),
                "consultation_id": cid,
                "sequence_order": idx + 1,
                "start_time_ms": seg.get("startTimeMs", 0),
                "end_time_ms": seg.get("endTimeMs", 0),
                "speaker_role": seg.get("speaker", "PATIENT"),
                "text_content": seg.get("text", ""),
                "confidence_score": seg.get("confidence", 0.98),
                "is_uncertain": False,
                "created_at": datetime.now(timezone.utc).isoformat()
            })
        try:
            res = self.client.table("transcript_segments").insert(rows).execute()
            logger.info(f"Saved {len(rows)} transcript segments to Supabase.")
            return res.data or rows
        except Exception as e:
            logger.error(f"Error saving transcript segments to Supabase: {e}")
            return []

    def get_patient_consultations(self, patient_id: str) -> List[Dict[str, Any]]:
        """
        Retrieves all consultations for a patient from public.consultations,
        joining their respective visit summary and key findings.
        """
        if not self.client:
            return []

        pid = self._to_valid_uuid(patient_id)
        try:
            res = self.client.table("consultations").select("*, visit_summaries(*)").eq("patient_id", pid).order("created_at", desc=True).execute()
            return res.data or []
        except Exception as e:
            logger.error(f"Error fetching consultations from Supabase: {e}")
            return []

    def delete_consultation(self, consultation_id: str) -> bool:
        """
        Deletes a consultation and cascades delete of summaries and transcripts.
        """
        if not self.client:
            return False

        cid = self._to_valid_uuid(consultation_id)
        try:
            self.client.table("consultations").delete().eq("id", cid).execute()
            logger.info(f"Deleted consultation {cid} from Supabase.")
            return True
        except Exception as e:
            logger.error(f"Error deleting consultation from Supabase: {e}")
            return False

    def _to_valid_uuid(self, val: str) -> str:
        """
        Ensures a string is a valid UUID, generating a deterministic UUID if it's a short string like 'cons-001'.
        """
        try:
            return str(uuid.UUID(val))
        except (ValueError, AttributeError):
            return str(uuid.uuid5(uuid.NAMESPACE_DNS, str(val)))

supabase_service = SupabaseService()
