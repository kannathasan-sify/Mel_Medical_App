package com.mel.medicalapp.platform

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class AndroidAudioPlayer(
    private val context: Context? = null
) : AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null
    private val playerScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null
    private var currentPath: String? = null

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionSeconds = MutableStateFlow(0)
    override val currentPositionSeconds: StateFlow<Int> = _currentPositionSeconds.asStateFlow()

    private val _totalDurationSeconds = MutableStateFlow(0)
    override val totalDurationSeconds: StateFlow<Int> = _totalDurationSeconds.asStateFlow()

    override fun play(filePath: String) {
        try {
            if (filePath.isBlank()) return

            val file = File(filePath)
            if (!file.exists() || file.length() == 0L) {
                android.util.Log.w("MelAudioPlayer", "Audio file does not exist or is empty: $filePath")
                return
            }

            stop()

            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(file.absolutePath)
                prepare()
                setOnCompletionListener {
                    _isPlaying.value = false
                    _currentPositionSeconds.value = 0
                    stopProgressTracking()
                }
                start()
            }

            mediaPlayer = player
            currentPath = filePath
            _isPlaying.value = true
            _totalDurationSeconds.value = (player.duration / 1000).coerceAtLeast(1)
            startProgressTracking()
        } catch (e: Exception) {
            android.util.Log.e("MelAudioPlayer", "Error playing audio file $filePath: ${e.message}", e)
            _isPlaying.value = false
        }
    }

    override fun pause() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                    _isPlaying.value = false
                    stopProgressTracking()
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("MelAudioPlayer", "Error pausing player: ${e.message}", e)
        }
    }

    override fun resume() {
        try {
            mediaPlayer?.let {
                it.start()
                _isPlaying.value = true
                startProgressTracking()
            }
        } catch (e: Exception) {
            android.util.Log.e("MelAudioPlayer", "Error resuming player: ${e.message}", e)
        }
    }

    override fun stop() {
        stopProgressTracking()
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                reset()
                release()
            }
        } catch (e: Exception) {
            android.util.Log.e("MelAudioPlayer", "Error stopping player: ${e.message}", e)
        }
        mediaPlayer = null
        _isPlaying.value = false
        _currentPositionSeconds.value = 0
    }

    override fun release() {
        stop()
        playerScope.cancel()
    }

    private fun startProgressTracking() {
        stopProgressTracking()
        progressJob = playerScope.launch {
            while (isActive && _isPlaying.value) {
                mediaPlayer?.let { player ->
                    try {
                        if (player.isPlaying) {
                            _currentPositionSeconds.value = (player.currentPosition / 1000)
                        }
                    } catch (_: Exception) {}
                }
                delay(250)
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }
}

@Composable
actual fun rememberPlatformAudioPlayer(): AudioPlayer {
    val context = LocalContext.current
    val player = remember { AndroidAudioPlayer(context) }
    DisposableEffect(player) {
        onDispose {
            player.release()
        }
    }
    return player
}

