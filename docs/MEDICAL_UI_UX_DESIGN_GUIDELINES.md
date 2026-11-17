# Clinical UI/UX Design System Guidelines
## MEL – Medical Empowerment Liaison
### Professional Healthcare UI Architecture & Patient-Centric Best Practices

---

## 1. Core Clinical Design Principles

Designing for healthcare requires a fundamentally different philosophy than commercial consumer apps or social media. Patients using **MEL** may be anxious, experiencing cognitive fatigue, in physical pain, or processing complex diagnoses.

### Key Tenets:
1. **Calm, High-Trust Visual Environment:** Avoid loud, saturated neons and chaotic motion. Use soft, soothing tints (ivory, mint-aqua, slate, calming blue) that reduce cortisol levels.
2. **Cognitive Load Reduction:** Present one clinical concept at a time. Group related items into clear visual cards with adequate whitespace.
3. **WCAG 2.1 AA Accessibility Standards:**
   - **Contrast Ratio:** Minimum **4.5:1** for standard text and **3:1** for bold/large headers.
   - **Touch Targets:** Minimum **48dp × 48dp** (critical for elderly patients or patients with tremors/motor impairment).
   - **Dynamic Type Support:** Text must scale up smoothly without breaking card layouts or overlapping.
4. **Unambiguous Clinical Hierarchy:** Clear distinction between:
   - What the **Patient** said (quoted verbatim).
   - What the **Provider** explained.
   - What the **AI synthesized** (marked with clear educational disclaimers).
   - **Emergency/Urgent flags** (distinct, high-visibility styling).

---

## 2. Professional Medical Color Palette & Semantics

| Token Name | Hex Code | Clinical Purpose & Meaning |
| :--- | :--- | :--- |
| **Medical Aqua** | `#00A896` | Health, healing, consultation scribe active state. Calm and modern. |
| **Clinical Navy** | `#1E3A8A` | Trust, authority, primary headers, readable typography. |
| **Apricot Warmth** | `#F59E0B` | Gentle guidance, questionnaire frames, non-threatening highlights. |
| **Emergency Red** | `#DC2626` | Immediate life-threatening alerts, Stop/End recording actions. |
| **Ivory Surface** | `#FFFBEB` | Warm note cards that feel softer than harsh stark white. |
| **Lavender Care** | `#8B5CF6` | Patient legal rights, informed consent, privacy notices. |
| **Muted Slate** | `#64748B` | Secondary medical details, dates, confidence indicators. |

---

## 3. Recommended Layout & Component Upgrades

### A. The Consultation Scribe Recording Experience
- **Ambient Glowing Halo:** Instead of aggressive flashing red lights, use a smooth, gentle breathing pulse around the microphone button in soothing Aqua Green.
- **Diarization Live Feedback:** Show live speaker chips (`[Patient]` / `[Doctor]`) so the user feels confident the conversation is being separated accurately.
- **Prominent Emergency Anchor:** A discreet but always-accessible **"Emergency Help (911/112)"** safety button pinned to the screen.

### B. Medical Visit Summary (Plain Language)
- **Reading Level Toggle:** Place a floating segmented control at the top of summaries allowing the patient to toggle between **5th Grade (Simple)**, **8th Grade (Standard)**, and **12th Grade (Detailed)** in real time.
- **Medication Cards with Pill Visuals:** Include pill/bottle icons, dosage instructions, and explicit warning tags (e.g., *"Take with food"*, *"Cannot be crushed – ask for liquid"*).
- **Interactive Medical Term Tooltips:** Any clinical jargon (e.g. *Pyelonephritis*) should be subtly underlined. Tapping it opens a mini bottom-sheet explaining the term in plain English without leaving the summary.

### C. Follow-up Questions (Empowerment Liaison)
- **Categorized Pill Filters:** Filter questions by `Medications`, `Next Steps`, `Clarifications`, or `Root Causes`.
- **"Ask Doctor Mode" (Large Text):** A button that expands questions into high-contrast full-screen cards so the patient can read them directly or hand their phone to the doctor during their appointment.

### D. Chronological Health Journey (Timeline)
- **Vertical Medical Timeline:** A clean connecting line linking visits with date badges, doctor names, and badge indicators showing unresolved questions from previous visits.

---

## 4. Accessibility & Inclusive Design Checklist

- [ ] **Elderly Patient Readability:** Default body font size no smaller than **14sp** with **20sp** line height.
- [ ] **Color Blindness Safety:** Never rely solely on color to convey clinical urgency. Always pair colors with clear icons (e.g. ⚠️ for warning, ℹ️ for info, ✓ for resolved).
- [ ] **Screen Reader Support:** Add descriptive `contentDescription` for every audio visualizer, toggle, and status icon.
- [ ] **Haptic Feedback:** Provide gentle haptic confirmation when starting/stopping recording so users with vision limitations know the state changed.
- [ ] **Multi-Language Support (Localization):** Design layouts with 25% extra horizontal padding to allow for longer words in Spanish, German, or Burmese.

---

## 5. UI Design Upgrade Implementation Roadmap

1. **Step 1: Design System & Typography Tokens** – Finalize high-contrast typography and accessible color styles.
2. **Step 2: Interactive Scribe Waveform & Ambient Pulsing** – Implement fluid canvas-drawn audio waves.
3. **Step 3: Interactive Jargon Bottom Sheets** – Enable one-tap plain language definitions inside visit notes.
4. **Step 4: "Ask Doctor" High-Contrast Question Mode** – Provide large-text card views for clinic rooms.
5. **Step 5: Dynamic Reading Level Switcher** – Allow instantaneous switching between Simple, Moderate, and In-Depth summaries.

