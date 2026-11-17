# MEL UI/UX Design System & Implementation Plan
## Based on Patient Sketches & Supabase Integration (Google OAuth)

---

## 1. Executive Visual Analysis of the Hand-Drawn Sketches

From the uploaded sketches and notes, the design language for **MEL – Medical Empowerment Liaison** revolves around warmth, empowerment, and clarity rather than cold clinical software.

### A. Brand Identity & Emblem
- **Emblem / Logo:** A stylized arm muscle curled with a stethoscope inside (`Ask MEL` / `Medical Empowerment Liaison – empowering you in real time`).
- **Color Swatches (from the color swatch sheet):**
  - **Ivory (`#FFFBEB`):** Warm background tints.
  - **Apricot (`#F59E0B`):** Friendly, high-energy border highlights around questionnaires and cards.
  - **Lavender (`#8B5CF6`):** Resources, patient legal rights, and confidentiality notices.
  - **Light Pink (`#F472B6`):** Appointments, follow-ups, and calendar reminders.
  - **Coral Pink (`#F43F5E`):** Capture / upload and critical emergency alerts.
  - **Aqua Green (`#00A896`):** Consultation Scribe (recording visits & capturing notes).
  - **Sky Blue (`#0284C7`):** Learn / Research ("What's that?"), primary button accents.
  - **Deep Blue (`#1E3A8A` / `#0F172A`):** Trustworthy, accessible typography meeting WCAG 2.1 AA standards.

---

## 2. Screen-by-Screen Breakdown & Mapping

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ 1. Welcome / Auth Screen ("Welcome to Ask MEL")                             │
│   • Hero: Logo (Muscle + Stethoscope) + "empowering you in real time"       │
│   • Auth: Sign-In / Sign-Up / Forgot Password                               │
│   • Providers: Google Sign-In (Supabase OAuth), Apple ID, Email/Password    │
│   • Security: MFA options (Phone Code / Facial Scan)                        │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ 2. Guided Patient Onboarding Flow (Linear Progress Tracker)                 │
│   • Q1: Biggest challenge in appointments (Being heard, next steps, terms,   │
│         being rushed)                                                       │
│   • Q2: Gender assigned at birth (Female, Male, Prefer not to share + note) │
│   • Q3: Value ranking 1–5 (Autonomy, Honesty, Collaboration, Knowledge,     │
│         Trust)                                                              │
│   • Q5: Identity groups (LGBTQIA+, Immigrant, Disabled, Limited Income, etc)│
│   • Legal: Privacy statements & HIPAA/GDPR disclosure                       │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ 3. Main Dashboard ("Home Screen")                                           │
│   • Top Right: "Account Info" button                                        │
│   • Body: Dynamic greeting, quick start consultation scribe card            │
│   • 5 Colored Bottom Action Tiles:                                          │
│     1. [Light Pink] Appointments + Reminders                                │
│     2. [Coral Pink] Capture / Upload                                        │
│     3. [Aqua Green] Scribe (Record Visit / Take Notes) - Center Hero        │
│     4. [Sky Blue] Learn (Research, Review "What's that?")                   │
│     5. [Lavender] Resources (Legal Info, Patient Rights)                    │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ 4. "Account Info" Profile Screen                                            │
│   • Credentials: Username, Password Reset                                   │
│   • Demographics: Gender at birth, Age groups (18-24 to 60+)                │
│   • Advocacy: Stored ranked values & primary challenge                      │
│   • Reading Level: Simple (5th grade), Moderate (8th), Complex (12th grade) │
│   • Future Languages: Audio input & summary output language selectors       │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Supabase Integration Architecture

### Database & Authentication Setup
1. **Google OAuth via Supabase Auth:**
   - Enables native OAuth flows on Android and iOS.
   - Triggers `public.handle_new_user()` to populate demographic records in `patient_profiles`.
2. **PostgreSQL Row Level Security (RLS):**
   - Configured in [V2__supabase_schema_with_rls.sql](file:///c:/Users/DELL/kannathasan/Projects/AI/Mel_Medical_App/backend/migrations/V2__supabase_schema_with_rls.sql).
   - Every read and write to `consultations`, `transcript_segments`, and `visit_summaries` is constrained by `auth.uid() = patient_id`.

---

## 4. Compose Multiplatform Files Created

- **Theme Palette**: [Color.kt](file:///c:/Users/DELL/kannathasan/Projects/AI/Mel_Medical_App/composeApp/src/commonMain/kotlin/com/mel/app/presentation/theme/Color.kt)
- **Welcome / Auth Screen**: [WelcomeAuthScreen.kt](file:///c:/Users/DELL/kannathasan/Projects/AI/Mel_Medical_App/composeApp/src/commonMain/kotlin/com/mel/app/presentation/screens/auth/WelcomeAuthScreen.kt)
- **Onboarding Flow**: [OnboardingFlowScreen.kt](file:///c:/Users/DELL/kannathasan/Projects/AI/Mel_Medical_App/composeApp/src/commonMain/kotlin/com/mel/app/presentation/screens/onboarding/OnboardingFlowScreen.kt)
- **Home Dashboard**: [HomeScreen.kt](file:///c:/Users/DELL/kannathasan/Projects/AI/Mel_Medical_App/composeApp/src/commonMain/kotlin/com/mel/app/presentation/screens/dashboard/HomeScreen.kt)
- **Account Info**: [AccountInfoScreen.kt](file:///c:/Users/DELL/kannathasan/Projects/AI/Mel_Medical_App/composeApp/src/commonMain/kotlin/com/mel/app/presentation/screens/profile/AccountInfoScreen.kt)
- **Supabase Migration**: [V2__supabase_schema_with_rls.sql](file:///c:/Users/DELL/kannathasan/Projects/AI/Mel_Medical_App/backend/migrations/V2__supabase_schema_with_rls.sql)

