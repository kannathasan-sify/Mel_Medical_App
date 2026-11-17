package com.mel.medicalapp.core.emergency

/**
 * Immediate, deterministic client-side triage sentinel.
 * Runs instantly on transcribed or typed text without waiting for LLM or cloud inference.
 */
object EmergencyDetector {

    private val CRITICAL_PATTERNS = listOf(
        Regex("""\b(chest\s+pain|crushing\s+chest|radiating\s+to\s+arm|radiating\s+to\s+jaw)\b""", RegexOption.IGNORE_CASE),
        Regex("""\b(difficulty\s+breathing|can't\s+breathe|severe\s+shortness\s+of\s+breath|gasping)\b""", RegexOption.IGNORE_CASE),
        Regex("""\b(facial\s+droop|arm\s+weakness|slurred\s+speech|sudden\s+numbness|stroke)\b""", RegexOption.IGNORE_CASE),
        Regex("""\b(uncontrolled\s+bleeding|coughing\s+blood|vomiting\s+blood)\b""", RegexOption.IGNORE_CASE),
        Regex("""\b(loss\s+of\s+consciousness|passed\s+out|unresponsive)\b""", RegexOption.IGNORE_CASE),
        Regex("""\b(anaphylaxis|throat\s+closing|swelling\s+of\s+tongue)\b""", RegexOption.IGNORE_CASE),
        Regex("""\b(suicidal\s+thoughts|want\s+to\s+end\s+my\s+life)\b""", RegexOption.IGNORE_CASE)
    )

    data class EmergencyAlert(
        val triggered: Boolean,
        val matchedKeyword: String?,
        val recommendationText: String,
        val emergencyNumber: String
    )

    fun evaluateText(text: String, region: String = "US"): EmergencyAlert {
        for (pattern in CRITICAL_PATTERNS) {
            val match = pattern.find(text)
            if (match != null) {
                val emergencyNum = if (region.equals("EU", ignoreCase = true)) "112" else "911"
                return EmergencyAlert(
                    triggered = true,
                    matchedKeyword = match.value,
                    recommendationText = "You have described potentially life-threatening symptoms (${match.value}). MEL is an educational assistant and cannot provide emergency medical care. Please immediately call emergency services.",
                    emergencyNumber = emergencyNum
                )
            }
        }
        return EmergencyAlert(
            triggered = false,
            matchedKeyword = null,
            recommendationText = "",
            emergencyNumber = if (region.equals("EU", ignoreCase = true)) "112" else "911"
        )
    }
}

