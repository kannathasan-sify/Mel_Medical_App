package com.mel.medicalapp.domain

import com.mel.medicalapp.core.emergency.EmergencyDetector
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MedicalSafetyTest {

    @Test
    fun testEmergencyDetectorTriggersImmediate911ForChestPain() {
        val input = "Doctor, I've had sudden crushing chest pain radiating to arm since yesterday."
        val result = EmergencyDetector.evaluateText(input, region = "US")

        assertTrue(result.triggered)
        assertEquals("911", result.emergencyNumber)
        assertTrue(result.recommendationText.contains("life-threatening"))
    }

    @Test
    fun testEmergencyDetectorTriggers112InEurope() {
        val input = "Patient is gasping for air with severe shortness of breath."
        val result = EmergencyDetector.evaluateText(input, region = "EU")

        assertTrue(result.triggered)
        assertEquals("112", result.emergencyNumber)
    }

    @Test
    fun testEmergencyDetectorPassesNormalCheckup() {
        val input = "Patient presenting for routine annual checkup and refill of blood pressure medication."
        val result = EmergencyDetector.evaluateText(input, region = "US")

        assertFalse(result.triggered)
        assertEquals("", result.recommendationText)
    }
}

