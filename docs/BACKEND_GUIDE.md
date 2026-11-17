# BACKEND ARCHITECTURE & RUN GUIDE
## MEL – Medical Empowerment Liaison Backend API Service

---

## 1. Overview
The MEL backend is a production-grade, modular **FastAPI** service engineered to handle clinical AI orchestration, OpenAI GPT-4o synthesis with structured JSON schemas, Supabase authentication & PostgREST proxying, and patient empowerment intelligence.

### Key Capabilities:
- **HIPAA/GDPR Clinical Isolation:** Raw audio, transcripts, and structured patient summaries are processed under strict privacy boundaries.
- **Dynamic Reading Level Adaptation:** Automatically adjusts clinical explanations between 5th grade simple, 8th grade standard, and 12th grade in-depth modes.
- **Zero-Downtime Fallback:** Seamlessly operates even before an OpenAI API key is configured by using certified deterministic clinical rule fixtures.
- **Interactive Swagger / OpenAPI Documentation:** Full interactive testing available at `/api/v1/docs`.

---

## 2. Directory Structure

```
backend/
├── Dockerfile                     # Production container spec
├── docker-compose.yml             # Local / cloud orchestration
├── requirements.txt               # Dependencies (FastAPI, Pydantic, OpenAI, Supabase)
├── migrations/                    # PostgreSQL & Supabase DDL migrations
│   ├── V1__init_schema.sql
│   └── V2__supabase_schema_with_rls.sql
└── src/
    ├── main.py                    # Application entry point & CORS
    ├── core/
    │   └── config.py              # Supabase & OpenAI settings
    ├── schemas/
    │   └── consultation.py        # Pydantic schemas for requests & responses
    ├── services/ai/
    │   └── clinical_synthesizer.py# OpenAI GPT-4o synthesis service
    └── api/v1/
        └── consultations.py       # Versioned REST API endpoints
```

---

## 3. Running the Backend

### Option A: Local Python Environment
```bash
cd backend

# 1. Install dependencies
pip install -r requirements.txt

# 2. Start the server with live reload
uvicorn src.main:app --reload --host 0.0.0.0 --port 8000
```

### Option B: Docker
```bash
cd backend
docker-compose up --build
```

---

## 4. Testing Endpoints

Once running, access:
- **Swagger Documentation:** `http://localhost:8000/api/v1/docs`
- **Health Check:** `http://localhost:8000/health`
- **Clinical Synthesis Endpoint:** `POST http://localhost:8000/api/v1/consultations/synthesize`

