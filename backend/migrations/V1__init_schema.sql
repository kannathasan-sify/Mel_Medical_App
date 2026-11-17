-- PostgreSQL Schema Migration: MEL Core Healthcare Platform
-- Conforms to HIPAA Security Rule & GDPR Art. 9 Special Category Health Data

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 1. USERS & ROLES
CREATE TYPE user_status_enum AS ENUM ('PENDING', 'ACTIVE', 'SUSPENDED', 'DELETED');
CREATE TYPE user_role_enum AS ENUM ('PATIENT', 'PROVIDER', 'ADMINISTRATOR');

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email_verified BOOLEAN DEFAULT FALSE,
    status user_status_enum DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role user_role_enum NOT NULL,
    assigned_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, role)
);

-- 2. PATIENT PROFILES
CREATE TABLE patient_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    full_name VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    preferred_language VARCHAR(10) DEFAULT 'en',
    emergency_contact_encrypted BYTEA,
    communication_preferences JSONB DEFAULT '{"email": true, "sms": false}'::jsonb,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 3. CONSENT RECORDS (HIPAA & GDPR Compliant Immutable Audit)
CREATE TYPE consent_type_enum AS ENUM ('AUDIO_RECORDING', 'AI_PROCESSING', 'DATA_SHARING', 'EMERGENCY_ACCESS');

CREATE TABLE consent_records (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patient_profiles(id) ON DELETE CASCADE,
    consent_type consent_type_enum NOT NULL,
    is_granted BOOLEAN NOT NULL,
    legal_text_version VARCHAR(50) NOT NULL,
    ip_address INET,
    user_agent TEXT,
    granted_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    revoked_at TIMESTAMPTZ
);

-- 4. CONSULTATIONS
CREATE TYPE consultation_status_enum AS ENUM ('SCHEDULED', 'RECORDING', 'PROCESSING', 'COMPLETED', 'CANCELLED');

CREATE TABLE consultations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patient_profiles(id) ON DELETE CASCADE,
    status consultation_status_enum DEFAULT 'SCHEDULED',
    visit_type VARCHAR(100) DEFAULT 'General Consultation',
    started_at TIMESTAMPTZ,
    ended_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 5. AUDIO ASSETS (Strict separation from clinical DB)
CREATE TABLE audio_assets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    consultation_id UUID NOT NULL REFERENCES consultations(id) ON DELETE CASCADE,
    storage_uri VARCHAR(1024) NOT NULL,
    duration_seconds INT DEFAULT 0,
    audio_format VARCHAR(50) DEFAULT 'audio/m4a',
    encryption_key_id VARCHAR(255) NOT NULL,
    retention_expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 6. TRANSCRIPTS & SEGMENTS
CREATE TABLE transcripts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    consultation_id UUID UNIQUE NOT NULL REFERENCES consultations(id) ON DELETE CASCADE,
    engine_version VARCHAR(100) NOT NULL,
    overall_confidence NUMERIC(4, 3) DEFAULT 0.000,
    human_reviewed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TYPE speaker_role_enum AS ENUM ('PATIENT', 'PROVIDER', 'UNKNOWN');

CREATE TABLE transcript_segments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    transcript_id UUID NOT NULL REFERENCES transcripts(id) ON DELETE CASCADE,
    sequence_order INT NOT NULL,
    start_time_ms BIGINT NOT NULL,
    end_time_ms BIGINT NOT NULL,
    speaker_role speaker_role_enum DEFAULT 'UNKNOWN',
    text_content TEXT NOT NULL,
    confidence_score NUMERIC(4, 3) DEFAULT 0.000,
    is_uncertain BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 7. VISIT SUMMARIES (Clinical Intelligence)
CREATE TABLE visit_summaries (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    consultation_id UUID UNIQUE NOT NULL REFERENCES consultations(id) ON DELETE CASCADE,
    plain_language_overview TEXT NOT NULL,
    symptoms_reported JSONB DEFAULT '[]'::jsonb,
    provider_explanations JSONB DEFAULT '[]'::jsonb,
    diagnoses_explicit JSONB DEFAULT '[]'::jsonb,
    medications_discussed JSONB DEFAULT '[]'::jsonb,
    treatment_plan JSONB DEFAULT '[]'::jsonb,
    unanswered_questions JSONB DEFAULT '[]'::jsonb,
    generated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 8. FOLLOW-UP QUESTIONS (Empowerment Engine)
CREATE TYPE question_category_enum AS ENUM ('CLARIFICATION', 'MEDICATION', 'NEXT_STEPS', 'HISTORY', 'GENERAL');

CREATE TABLE follow_up_questions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    visit_summary_id UUID NOT NULL REFERENCES visit_summaries(id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    category question_category_enum DEFAULT 'GENERAL',
    is_patient_edited BOOLEAN DEFAULT FALSE,
    is_answered BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 9. AUDIT LOG (HIPAA Mandated Immutable Event Logging)
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    actor_id UUID NOT NULL REFERENCES users(id),
    action VARCHAR(50) NOT NULL,
    resource_type VARCHAR(100) NOT NULL,
    resource_id UUID NOT NULL,
    ip_address INET,
    user_agent TEXT,
    event_timestamp TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- INDEXES FOR PERFORMANCE & ROW LOOKUPS
CREATE INDEX idx_consultations_patient ON consultations(patient_id);
CREATE INDEX idx_transcripts_consultation ON transcripts(consultation_id);
CREATE INDEX idx_segments_transcript ON transcript_segments(transcript_id, sequence_order);
CREATE INDEX idx_summaries_consultation ON visit_summaries(consultation_id);
CREATE INDEX idx_consent_patient ON consent_records(patient_id);
CREATE INDEX idx_audit_actor ON audit_logs(actor_id, event_timestamp);

