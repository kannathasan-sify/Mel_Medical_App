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
                    override fun onReadyForSpeech(params: Bundle?) {
                        android.util.Log.d("SpeechRecognizer", "Ready for speech")
                    }
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {
                        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                        onLevelChange?.invoke(normalized)
                    }
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {}
                    override fun onError(error: Int) {
                        android.util.Log.d("SpeechRecognizer", "Recognizer status code: $error")
                        try {
                            recognizer?.cancel()
                        } catch (_: Exception) {}
                        if (isRecordingActive) {
                            val delayMs = if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY || error == SpeechRecognizer.ERROR_CLIENT) 800L else 400L
                            uiHandler.postDelayed({
                                if (isRecordingActive) restartRecognizer()
                            }, delayMs)
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
                            }, 250)
                        }
                    }
                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()?.trim()
                        if (!text.isNullOrBlank()) {
                            android.util.Log.i("SpeechRecognizer", "Partial speech recognized: $text")
                            onSpeechResult?.invoke(text)
                        }
                    }
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })

                restartRecognizer()
            } catch (e: Exception) {
                android.util.Log.e("SpeechRecognizer", "Init error: ${e.message}")
            }
        }
    }

    private fun restartRecognizer() {
        if (!isRecordingActive) return
        try {
            recognizer?.cancel()
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US")
                putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("en-US", "en-IN", "en-GB"))
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra("android.speech.extra.DICTATION_MODE", true)
            }
            recognizer?.startListening(intent)
        } catch (e: Exception) {
            android.util.Log.e("SpeechRecognizer", "restartRecognizer error: ${e.message}")
        }
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
                recognizer?.cancel()
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
