import os
import json
import logging
from typing import Dict, Any, Optional, List
from fastapi import HTTPException
from openai import AsyncOpenAI
from src.core.config import settings

logger = logging.getLogger(__name__)

class ClinicalIntelligenceService:
    def __init__(self):
        self._cached_key = settings.OPENAI_API_KEY or os.getenv("OPENAI_API_KEY", "")
        self.client = AsyncOpenAI(api_key=self._cached_key) if self._cached_key else None

    def get_client(self, override_key: Optional[str] = None) -> Optional[AsyncOpenAI]:
        key = override_key or os.getenv("OPENAI_API_KEY", "") or settings.OPENAI_API_KEY
        if key:
            if not self.client or key != self._cached_key:
                self._cached_key = key
                self.client = AsyncOpenAI(api_key=key)
            return self.client
        return None

    async def synthesize_consultation(
        self,
        transcript_text: str,
        reading_level: str = "moderate",
        history_context: str = "",
        api_key: Optional[str] = None
    ) -> Dict[str, Any]:
        """
        Synthesizes clinical consultations using real OpenAI GPT-4o with structured clinical JSON validation.
        No synthetic fallbacks: returns real error if API key is missing or call fails.
        """
        client = self.get_client(api_key)
        if not client:
            logger.error("OPENAI_API_KEY not configured on backend server.")
            raise HTTPException(
                status_code=503,
                detail="OpenAI API is not configured on the backend server. Please set OPENAI_API_KEY in the Render Environment settings."
            )

        if not transcript_text or not transcript_text.strip():
            raise HTTPException(
                status_code=400,
                detail="Transcript text is empty. Spoken audio or consultation segments are required to generate a medical summary."
            )

        system_prompt = f"""
        You are MEL (Medical Empowerment Liaison), a clinical communication and documentation assistant.
        Analyze this consultation transcript between a Patient and a Healthcare Provider.
        
        Strict Guidelines:
        1. Adapt explanations to reading comprehension level: {reading_level} (simple = 5th grade, moderate = 8th grade, complex = 12th grade).
        2. Never invent diagnoses, treatments, or prescriptions. Only state explicitly discussed facts. If the patient reports symptoms (such as fever, cough, pain) but a formal diagnosis was not explicitly stated by a provider, set diagnoses_explicit to include the clinical focus (e.g., ["Acute Febrile Evaluation"] or ["Symptom Assessment"]).
        3. Preserve clinical terminology alongside simple plain-language translations.
        4. Detect any medication swallowing difficulties or adherence concerns.
        5. Identify unanswered questions or gaps left in the conversation.
        6. Formulate 4 empathetic, respectful follow-up questions for the patient's next appointment.
        7. All output fields MUST be returned strictly in clear, professional English. If the transcript has non-English or phonetic words, interpret and summarize them entirely in English.
        
        Prior Medical History Context:
        {history_context if history_context else "None provided."}
        
        Output MUST be pure JSON with these exact keys:
        {{
            "plain_language_overview": "string",
            "symptoms_reported": ["string"],
            "provider_explanations": ["string"],
            "diagnoses_explicit": ["string"],
            "medications_discussed": [{{"name": "string", "dosage": "string", "frequency": "string", "instructions": "string", "concerns_flagged": "string"}}],
            "treatment_plan": ["string"],
            "unanswered_questions": ["string"],
            "follow_up_questions": ["string"]
        }}
        """

        try:
            response = await client.chat.completions.create(
                model=settings.OPENAI_MODEL,
                messages=[
                    {"role": "system", "content": system_prompt},
                    {"role": "user", "content": f"Transcript:\n{transcript_text}"}
                ],
                temperature=0.2,
                max_tokens=1500,
                response_format={"type": "json_object"}
            )
            raw_json = response.choices[0].message.content
            return json.loads(raw_json)
        except Exception as e:
            logger.error(f"OpenAI clinical synthesis error: {e}")
            raise HTTPException(
                status_code=502,
                detail=f"OpenAI GPT-4o synthesis failed: {str(e)}"
            )

    async def translate_texts(
        self,
        texts: list[str],
        target_language: str = "Spanish",
        api_key: Optional[str] = None
    ) -> list[dict[str, str]]:
        """
        Translates spoken dialogue turns or clinical texts into the target language using OpenAI GPT-4o.
        Raises real error if service is unconfigured or fails.
        """
        if not texts:
            return []

        client = self.get_client(api_key)
        if not client:
            raise HTTPException(
                status_code=503,
                detail="OpenAI API is not configured on the backend server for translation. Please configure OPENAI_API_KEY in Render."
            )

        try:
            prompt = f"""You are an expert medical translator. Translate each of the following spoken statements or clinical texts into {target_language}.
Maintain medical precision, empathetic patient tone, and exact meaning.
Return ONLY a valid JSON object with key "translations" matching:
{{
  "translations": [
    {{"original": "original text", "translated": "translated text in {target_language}"}}
  ]
}}

Texts to translate:
{json.dumps(texts, ensure_ascii=False)}
"""
            completion = await client.chat.completions.create(
                model=settings.OPENAI_MODEL,
                messages=[
                    {"role": "system", "content": "You are a professional medical translator ensuring accurate cross-lingual patient communication."},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.1,
                response_format={"type": "json_object"}
            )
            raw = completion.choices[0].message.content or "{}"
            parsed = json.loads(raw)
            return parsed.get("translations", [])
        except Exception as e:
            logger.error(f"OpenAI translation error: {e}")
            raise HTTPException(
                status_code=502,
                detail=f"OpenAI translation failed: {str(e)}"
            )

    async def explain_medical_term(self, term: str, api_key: Optional[str] = None) -> dict[str, Any]:
        """
        Generates a comprehensive, patient-friendly explanation of any medical term,
        lab test, procedure, or drug using GPT-4o.
        Raises real error if API is unconfigured or fails.
        """
        if not term or not term.strip():
            raise HTTPException(
                status_code=400,
                detail="Medical term cannot be empty."
            )

        client = self.get_client(api_key)
        if not client:
            raise HTTPException(
                status_code=503,
                detail="OpenAI API is not configured on backend server. Please configure OPENAI_API_KEY in Render."
            )

        try:
            prompt = f"""You are MEL (Medical Empowerment Liaison), an expert clinical patient educator.
Explain the medical term: "{term.strip()}" in clear, plain language suitable for a patient at an 8th-grade reading level.

Return ONLY a valid JSON object matching this schema:
{{
  "term": "{term.strip()}",
  "pronunciation": "phonetic guide (e.g. pie-low-nuh-FRY-tis)",
  "plainMeaning": "2-3 clear sentences explaining what this is in everyday language without confusing medical jargon.",
  "whyItMatters": "Why this test, condition, or medication is clinically significant for the patient's care.",
  "doctorQuestions": [
    "Question 1 to ask the doctor",
    "Question 2 to ask the doctor",
    "Question 3 to ask the doctor"
  ],
  "commonSymptomsOrUses": "Common symptoms associated or why this test/medication is prescribed.",
  "category": "One of: Kidney & Bladder | Heart & Vessels | Medications & Rx | Imaging & Tests | General Health | Diagnostics"
}}"""

            completion = await client.chat.completions.create(
                model=settings.OPENAI_MODEL,
                messages=[
                    {"role": "system", "content": "You are an expert clinical education assistant empowering patients with clear medical knowledge."},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.2,
                response_format={"type": "json_object"}
            )
            raw = completion.choices[0].message.content or "{}"
            parsed = json.loads(raw)
            return parsed
        except Exception as e:
            logger.error(f"Error explaining term {term}: {e}")
            raise HTTPException(
                status_code=502,
                detail=f"OpenAI term explanation failed: {str(e)}"
            )
