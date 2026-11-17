package com.mel.medicalapp.platform

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

actual class PlatformSpeechRecognizer(
    private val context: Context
) {
    private var recognizer: SpeechRecognizer? = null
    private var isRecordingActive = false
    private var onSpeechResult: ((String) -> Unit)? = null
    private var onLevelChange: ((Float) -> Unit)? = null
    private val uiHandler = Handler(Looper.getMainLooper())

    actual fun startListening(
        onTextRecognized: (String) -> Unit,
        onRmsChanged: (Float) -> Unit
    ) {
        onSpeechResult = onTextRecognized
        onLevelChange = onRmsChanged
        isRecordingActive = true

        uiHandler.post {
            try {
                if (recognizer == null) {
                    recognizer = SpeechRecognizer.createSpeechRecognizer(context)
                }

                recognizer?.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {}
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {
                        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                        onLevelChange?.invoke(normalized)
                    }
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {}
                    override fun onError(error: Int) {
                        android.util.Log.d("SpeechRecognizer", "Recognizer status code: $error")
                        if (isRecordingActive) {
                            uiHandler.postDelayed({
                                if (isRecordingActive) restartRecognizer()
                            }, 600)
                        }
                    }
                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()?.trim()
                        if (!text.isNullOrBlank()) {
                            android.util.Log.i("SpeechRecognizer", "Recognized speech: $text")
                            onSpeechResult?.invoke(text)
                        }
                        if (isRecordingActive) {
                            uiHandler.postDelayed({
                                if (isRecordingActive) restartRecognizer()
                            }, 300)
                        }
                    }
                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })

                restartRecognizer()
            } catch (e: Exception) {
                android.util.Log.e("SpeechRecognizer", "Init error: ${e.message}")
            }
        }
    }

    private fun restartRecognizer() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }
            recognizer?.startListening(intent)
        } catch (_: Exception) {}
    }

    actual fun stopListening() {
        isRecordingActive = false
        uiHandler.post {
            try {
                recognizer?.stopListening()
                recognizer?.cancel()
            } catch (_: Exception) {}
        }
    }

    fun release() {
        isRecordingActive = false
        uiHandler.post {
            try {
                recognizer?.destroy()
                recognizer = null
            } catch (_: Exception) {}
        }
    }
}

@Composable
actual fun rememberPlatformSpeechRecognizer(): PlatformSpeechRecognizer {
    val ctx = LocalContext.current
    val instance = remember { PlatformSpeechRecognizer(ctx) }
    DisposableEffect(instance) {
        onDispose {
            instance.release()
        }
    }
    return instance
}

