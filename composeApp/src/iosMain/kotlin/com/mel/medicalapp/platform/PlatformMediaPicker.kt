package com.mel.medicalapp.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

actual class PlatformMediaPicker(
    private val onLaunchCamera: () -> Unit,
    private val onLaunchFilePicker: () -> Unit
) {
    actual fun launchCamera() = onLaunchCamera()
    actual fun launchFilePicker() = onLaunchFilePicker()
}

@Composable
actual fun rememberPlatformMediaPicker(
    onMediaCaptured: (fileName: String, content: String) -> Unit
): PlatformMediaPicker {
    return remember {
        PlatformMediaPicker(
            onLaunchCamera = {
                onMediaCaptured(
                    "iOS_Camera_Scan_${System.currentTimeMillis()}.jpg",
                    "CLINICAL PHOTO CAPTURE:\nDetected Document: Medical Prescription & Clinical Note\nFindings: Patient prescribed Cefdinir 300mg oral suspension."
                )
            },
            onLaunchFilePicker = {
                onMediaCaptured(
                    "iOS_Document_Lab_${System.currentTimeMillis()}.pdf",
                    "UPLOADED CLINICAL DOCUMENT:\nFormat: Diagnostic Laboratory Report\nFindings: Urine Culture >100,000 CFU/mL E. coli."
                )
            }
        )
    }
}

