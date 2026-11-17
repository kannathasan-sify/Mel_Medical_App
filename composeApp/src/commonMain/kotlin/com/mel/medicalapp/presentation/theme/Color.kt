package com.mel.medicalapp.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * MEL Color Palette derived directly from the design sketches & medical branding:
 * - Logo: Arm muscle with stethoscope in Aqua Green + Sky Blue + Coral/Rose accents
 * - Color Swatches: Ivory, Apricot, Lavender, Light Pink, Coral Pink, Aqua Green, Sky Blue, Grey, Blue
 */
object MelColors {
    // Primary Brand Tones
    val AquaGreen = Color(0xFF00A896)      // Scribe / Record Visit & Logo Accent
    val AquaGreenLight = Color(0xFFE6F7F5)
    val AquaGreenDark = Color(0xFF007568)
    val SkyBlue = Color(0xFF0284C7)        // Core Action / Accent
    val SkyBlueLight = Color(0xFFE0F2FE)
    val DeepBlue = Color(0xFF1E3A8A)       // Primary Typography & TopBars
    val NavyBlue = Color(0xFF0F172A)       // High contrast headers

    // Warm & Caring Healthcare Palette (from sketches)
    val Apricot = Color(0xFFF59E0B)        // Screen Borders & Highlights
    val ApricotLight = Color(0xFFFEF3C7)   // Warm notice containers
    val Ivory = Color(0xFFFFFBEB)          // Background tint
    val Lavender = Color(0xFF8B5CF6)       // Resources & Legal info
    val LavenderLight = Color(0xFFF3E8FF)
    val LightPink = Color(0xFFF472B6)      // Appointments & Reminders
    val LightPinkLight = Color(0xFFFCE7F3)
    val CoralPink = Color(0xFFF43F5E)      // Capture/Upload & Urgent Alerts
    val CoralLight = Color(0xFFFFE4E6)

    // Neutral Surfaces & WCAG 2.1 AA Contrast Tones
    val SurfaceBackground = Color(0xFFF8FAFC)
    val CardSurface = Color(0xFFFFFFFF)
    val TextPrimary = Color(0xFF0F172A)
    val TextSecondary = Color(0xFF475569)
    val TextMuted = Color(0xFF94A3B8)
    val BorderSubtle = Color(0xFFE2E8F0)
    val GreySwatch = Color(0xFF64748B)

    // Status Tones
    val Success = Color(0xFF10B981)
    val Warning = Color(0xFFF59E0B)
    val Error = Color(0xFFEF4444)
}

