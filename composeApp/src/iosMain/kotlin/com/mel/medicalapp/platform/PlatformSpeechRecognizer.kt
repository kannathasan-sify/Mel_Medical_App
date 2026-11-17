package com.mel.medicalapp.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

actual class PlatformSpeechRecognizer {
    actual fun startListening(
        onTextRecognized: (String) -> Unit,
        onRmsChanged: (Float) -> Unit
    ) {}
    actual fun stopListening() {}
}

@Composable
actual fun rememberPlatformSpeechRecognizer(): PlatformSpeechRecognizer {
    return remember { PlatformSpeechRecognizer() }
}

