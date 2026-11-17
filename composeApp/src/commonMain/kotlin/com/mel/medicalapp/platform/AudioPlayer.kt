package com.mel.medicalapp.platform

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayer {
    val isPlaying: StateFlow<Boolean>
    val currentPositionSeconds: StateFlow<Int>
    val totalDurationSeconds: StateFlow<Int>

    fun play(filePath: String)
    fun pause()
    fun resume()
    fun stop()
    fun release()
}

@Composable
expect fun rememberPlatformAudioPlayer(): AudioPlayer

