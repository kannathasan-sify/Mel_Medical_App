package com.mel.medicalapp.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class IosAudioPlayer : AudioPlayer {
    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionSeconds = MutableStateFlow(0)
    override val currentPositionSeconds: StateFlow<Int> = _currentPositionSeconds.asStateFlow()

    private val _totalDurationSeconds = MutableStateFlow(0)
    override val totalDurationSeconds: StateFlow<Int> = _totalDurationSeconds.asStateFlow()

    override fun play(filePath: String) {
        _isPlaying.value = true
    }

    override fun pause() {
        _isPlaying.value = false
    }

    override fun resume() {
        _isPlaying.value = true
    }

    override fun stop() {
        _isPlaying.value = false
        _currentPositionSeconds.value = 0
    }

    override fun release() {
        stop()
    }
}

@Composable
actual fun rememberPlatformAudioPlayer(): AudioPlayer {
    return remember { IosAudioPlayer() }
}
