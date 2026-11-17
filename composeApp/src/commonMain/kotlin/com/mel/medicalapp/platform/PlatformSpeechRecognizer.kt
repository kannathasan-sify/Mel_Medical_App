package com.mel.medicalapp.platform

import androidx.compose.runtime.Composable

expect class PlatformSpeechRecognizer {
    fun startListening(
        onTextRecognized: (String) -> Unit,
        onRmsChanged: (Float) -> Unit
    )
    fun stopListening()
}

@Composable
expect fun rememberPlatformSpeechRecognizer(): PlatformSpeechRecognizer

