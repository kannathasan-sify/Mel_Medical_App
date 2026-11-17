package com.mel.medicalapp.platform

import androidx.compose.runtime.Composable

expect class PlatformMediaPicker {
    fun launchCamera()
    fun launchFilePicker()
}

@Composable
expect fun rememberPlatformMediaPicker(
    onMediaCaptured: (fileName: String, content: String) -> Unit
): PlatformMediaPicker

