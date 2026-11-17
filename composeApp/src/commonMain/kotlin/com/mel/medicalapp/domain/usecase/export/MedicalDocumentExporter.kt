package com.mel.medicalapp.domain.usecase.export

import com.mel.medicalapp.domain.model.FollowUpQuestion
import com.mel.medicalapp.domain.model.VisitSummary

object MedicalDocumentExporter {

    /**
     * Generates a clean, formatted consultation summary document (printable markdown / text)
     * suitable for patient-authorized sharing, printing, or PDF conversion.
     */
    fun generateDocumentText(
        patientName: String,
        summary: VisitSummary,
        questions: List<FollowUpQuestion>
    ): String {
        val sb = StringBuilder()
        sb.appendLine("=================================================================")
        sb.appendLine("            MEL - MEDICAL EMPOWERMENT LIAISON                    ")
        sb.appendLine("               CLINICAL VISIT SUMMARY & NOTES                    ")
        sb.appendLine("=================================================================")
        sb.appendLine("Patient: $patientName")
        sb.appendLine("Date: ${summary.generatedAt}")
        sb.appendLine("Notice: For patient educational and communication purposes only.")
        sb.appendLine("-----------------------------------------------------------------")
        sb.appendLine()
        sb.appendLine("1. WHAT HAPPENED (PLAIN-LANGUAGE OVERVIEW):")
        sb.appendLine(summary.plainLanguageOverview)
        sb.appendLine()
        sb.appendLine("2. DIAGNOSES DISCUSSED:")
        summary.diagnosesExplicit.forEach { sb.appendLine("   - $it") }
        sb.appendLine()
        sb.appendLine("3. TREATMENT PLAN & IMMEDIATE RECOMMENDATIONS:")
        summary.treatmentPlan.forEach { sb.appendLine("   - $it") }
        sb.appendLine()
        sb.appendLine("4. MEDICATIONS & SAFETY FLAGS:")
        summary.medicationsDiscussed.forEach { med ->
            sb.appendLine("   - ${med.name} ${med.dosage ?: ""} (${med.frequency ?: ""})")
            if (!med.instructions.isNullOrBlank()) sb.appendLine("     Instructions: ${med.instructions}")
            if (!med.concernsFlagged.isNullOrBlank()) sb.appendLine("     * Flag: ${med.concernsFlagged}")
        }
        sb.appendLine()
        sb.appendLine("5. UNANSWERED QUESTIONS / TOPICS NEEDING CLARIFICATION:")
        summary.unansweredQuestions.forEach { sb.appendLine("   - $it") }
        sb.appendLine()
        sb.appendLine("6. PATIENT PREPARED QUESTIONS FOR NEXT VISIT:")
        questions.forEachIndexed { i, q ->
            val status = if (q.isAnswered) "[X] Answered" else "[ ] Unanswered"
            sb.appendLine("   $status ${i + 1}. [${q.category.name}] ${q.questionText}")
        }
        sb.appendLine()
        sb.appendLine("=================================================================")
        sb.appendLine("End of Medical Visit Document")
        return sb.toString()
    }
}

