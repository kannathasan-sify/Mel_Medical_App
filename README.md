# MEL (Medical Empowerment Liaison)

MEL is a healthcare communication, AI medical scribe, and patient empowerment platform developed for Android and iOS using Kotlin Multiplatform (Compose Multiplatform), with a modular backend API.

---

## Technical Documentation & Architecture Blueprint

- [System Architecture, Compliance & Design Specification](file:///c:/Users/DELL/kannathasan/Projects/AI/Mel_Medical_App/ARCHITECTURE.md)
- [PostgreSQL Schema & Migrations](file:///c:/Users/DELL/kannathasan/Projects/AI/Mel_Medical_App/backend/migrations/V1__init_schema.sql)
- [Multiplatform Core Models](file:///c:/Users/DELL/kannathasan/Projects/AI/Mel_Medical_App/composeApp/src/commonMain/kotlin/com/mel/app/domain/model/ConsultationModels.kt)
- [Emergency Triage Sentinel](file:///c:/Users/DELL/kannathasan/Projects/AI/Mel_Medical_App/composeApp/src/commonMain/kotlin/com/mel/app/core/emergency/EmergencyDetector.kt)
- [Platform Audio Capture Contract](file:///c:/Users/DELL/kannathasan/Projects/AI/Mel_Medical_App/composeApp/src/commonMain/kotlin/com/mel/app/platform/AudioRecorder.kt)
- [Compose Scribe Screen](file:///c:/Users/DELL/kannathasan/Projects/AI/Mel_Medical_App/composeApp/src/commonMain/kotlin/com/mel/app/presentation/screens/scribe/ScribeRecordingScreen.kt)

---

## Project Structure Overview

```
Mel_Medical_App/
├── ARCHITECTURE.md                          # Master architectural & regulatory blueprint
├── build.gradle.kts                         # Root gradle configuration
├── settings.gradle.kts                      # Module mappings
├── gradle/libs.versions.toml                # Centralized Version Catalog (KMP, CMP, Ktor, SQLDelight)
│
├── composeApp/                              # Shared Kotlin Multiplatform Application
│   ├── src/commonMain/                      # Shared business logic, domain, data & Compose UI
│   │   ├── kotlin/com/mel/app/
│   │   │   ├── core/                        # Emergency sentinel, error handling, security
│   │   │   ├── domain/                      # Clean Architecture models, use cases, repository contracts
│   │   │   ├── data/                        # Ktor client, DTOs, SQLDelight database
│   │   │   ├── presentation/                # Compose Multiplatform UI screens & viewmodels
│   │   │   └── platform/                    # Multiplatform expect/actual bridges (Audio, Crypto)
│   ├── src/androidMain/                     # Android platform implementations (AudioRecord, etc.)
│   └── src/iosMain/                         # iOS platform implementations (AVAudioEngine, etc.)
│
├── iosApp/                                  # Xcode workspace & SwiftUI wrapper
└── backend/                                 # Modular Backend Service
    ├── migrations/                          # PostgreSQL DDL migrations
    └── src/
        ├── api/v1/                          # REST API Endpoints
        ├── core/                            # HIPAA audit logger & security
        ├── models/                          # Database models
        └── services/ai/                     # STT, Diarization, LLM summary & question engines
```

