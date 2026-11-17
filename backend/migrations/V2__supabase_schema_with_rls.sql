-- Supabase PostgreSQL Schema with Google OAuth & Row Level Security (RLS)
-- Target: MEL (Medical Empowerment Liaison)

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. PATIENT PROFILES (Linked to Supabase auth.users)
CREATE TABLE public.patient_profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT NOT NULL,
    full_name TEXT,
    date_of_birth DATE,
    gender_at_birth TEXT CHECK (gender_at_birth IN ('female', 'male', 'prefer_not_to_share')),
    age_group TEXT, -- e.g. '18-24', '25-29', '30-34', '35-39', '40-44', '45-49', '50-54', '55-59', '60+'
    identified_groups JSONB DEFAULT '[]'::jsonb, -- LGBTQIA+, Immigrant, Disabled, Limited Income, etc.
    ranked_values JSONB DEFAULT '[]'::jsonb, -- Autonomy, Honesty, Collaboration, Knowledge, Trust (1 to 5)
    primary_challenge TEXT, -- 'BEING_HEARD', 'NEXT_STEPS', 'CONFUSING_TERMS', 'BEING_RUSHED'
    medical_summary_detail_level TEXT DEFAULT 'moderate', -- 'simple' (5th grade), 'moderate' (8th grade), 'complex' (12th grade)
    mfa_preference TEXT DEFAULT 'phone_code', -- 'phone_code', 'facial_scan'
    preferred_language TEXT DEFAULT 'en',
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Enable RLS on patient_profiles
ALTER TABLE public.patient_profiles ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Patients can view and edit their own profile"
    ON public.patient_profiles
    FOR ALL
    USING (auth.uid() = id);

-- 2. ONBOARDING & LEGAL CONSENT
CREATE TABLE public.consent_records (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    consent_type TEXT NOT NULL, -- 'AUDIO_RECORDING', 'AI_PROCESSING', 'PRIVACY_POLICY'
    is_granted BOOLEAN NOT NULL,
    legal_text_version TEXT NOT NULL,
    granted_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE public.consent_records ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view and manage their own consents"
    ON public.consent_records
    FOR ALL
    USING (auth.uid() = user_id);

-- 3. CONSULTATIONS & VISITS
CREATE TABLE public.consultations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    provider_name TEXT,
    visit_type TEXT DEFAULT 'General Checkup',
    status TEXT DEFAULT 'RECORDING', -- 'RECORDING', 'PROCESSING', 'COMPLETED'
    started_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE public.consultations ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Patients manage only their consultations"
    ON public.consultations
    FOR ALL
    USING (auth.uid() = patient_id);

-- 4. TRANSCRIPT SEGMENTS
CREATE TABLE public.transcript_segments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    consultation_id UUID NOT NULL REFERENCES public.consultations(id) ON DELETE CASCADE,
    sequence_order INT NOT NULL,
    start_time_ms BIGINT NOT NULL,
    end_time_ms BIGINT NOT NULL,
    speaker_role TEXT DEFAULT 'UNKNOWN', -- 'PATIENT', 'PROVIDER', 'UNKNOWN'
    text_content TEXT NOT NULL,
    confidence_score NUMERIC(4, 3) DEFAULT 0.000,
    is_uncertain BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE public.transcript_segments ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Patients view transcript segments of their consultations"
    ON public.transcript_segments
    FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM public.consultations c
            WHERE c.id = transcript_segments.consultation_id
            AND c.patient_id = auth.uid()
        )
    );

-- 5. VISIT SUMMARIES (Plain-Language Empowerment Notes)
CREATE TABLE public.visit_summaries (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    consultation_id UUID UNIQUE NOT NULL REFERENCES public.consultations(id) ON DELETE CASCADE,
    plain_language_overview TEXT NOT NULL,
    symptoms_reported JSONB DEFAULT '[]'::jsonb,
    provider_explanations JSONB DEFAULT '[]'::jsonb,
    diagnoses_explicit JSONB DEFAULT '[]'::jsonb,
    medications_discussed JSONB DEFAULT '[]'::jsonb,
    treatment_plan JSONB DEFAULT '[]'::jsonb,
    unanswered_questions JSONB DEFAULT '[]'::jsonb,
    generated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE public.visit_summaries ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Patients view summaries of their consultations"
    ON public.visit_summaries
    FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM public.consultations c
            WHERE c.id = visit_summaries.consultation_id
            AND c.patient_id = auth.uid()
        )
    );

-- 6. FOLLOW-UP QUESTIONS (Empowerment Liaison)
CREATE TABLE public.follow_up_questions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    visit_summary_id UUID NOT NULL REFERENCES public.visit_summaries(id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    category TEXT DEFAULT 'GENERAL', -- 'CLARIFICATION', 'MEDICATION', 'NEXT_STEPS', 'HISTORY'
    is_patient_edited BOOLEAN DEFAULT FALSE,
    is_answered BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE public.follow_up_questions ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Patients view their follow-up questions"
    ON public.follow_up_questions
    FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM public.visit_summaries vs
            JOIN public.consultations c ON vs.consultation_id = c.id
            WHERE vs.id = follow_up_questions.visit_summary_id
            AND c.patient_id = auth.uid()
        )
    );

-- 7. APPOINTMENTS & REMINDERS (Sketch Pink Tile)
CREATE TABLE public.appointments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    provider_name TEXT,
    scheduled_at TIMESTAMPTZ NOT NULL,
    notes TEXT,
    is_completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE public.appointments ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Patients manage their appointments"
    ON public.appointments
    FOR ALL
    USING (auth.uid() = patient_id);

-- TRIGGER: Automatically populate patient_profiles when user signs in with Google / Supabase Auth
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.patient_profiles (id, email, full_name)
    VALUES (
        new.id,
        new.email,
        COALESCE(new.raw_user_meta_data->>'full_name', new.raw_user_meta_data->>'name', '')
    )
    ON CONFLICT (id) DO NOTHING;
    RETURN new;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();

