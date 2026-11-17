package com.mel.medicalapp.platform

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class IosAudioRecorder : AudioRecorder {

    private val recorderScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var amplitudeJob: Job? = null

    private val _recordingState = MutableStateFlow(RecordingState.IDLE)
    override val recordingState: StateFlow<RecordingState> = _recordingState.asStateFlow()

    private val _amplitudeFlow = MutableStateFlow(0.0f)
    override val amplitudeFlow: StateFlow<Float> = _amplitudeFlow.asStateFlow()

    private var currentFilePath: String = ""

    override suspend fun startRecording(outputFilePath: String): Result<Unit> {
        currentFilePath = outputFilePath
        _recordingState.value = RecordingState.RECORDING
        startMockAmplitude()
        return Result.success(Unit)
    }

    override suspend fun pauseRecording(): Result<Unit> {
        _recordingState.value = RecordingState.PAUSED
        stopMockAmplitude()
        return Result.success(Unit)
    }

    override suspend fun resumeRecording(): Result<Unit> {
        _recordingState.value = RecordingState.RECORDING
        startMockAmplitude()
        return Result.success(Unit)
    }

    override suspend fun stopRecording(): Result<String> {
        stopMockAmplitude()
        _recordingState.value = RecordingState.STOPPED
        return Result.success(currentFilePath)
    }

    override suspend fun cancelRecording(): Result<Unit> {
        stopMockAmplitude()
        _recordingState.value = RecordingState.IDLE
        return Result.success(Unit)
    }

    override fun release() {
        stopMockAmplitude()
        recorderScope.cancel()
    }

    private fun startMockAmplitude() {
        stopMockAmplitude()
        amplitudeJob = recorderScope.launch {
            var step = 0
            while (isActive && _recordingState.value == RecordingState.RECORDING) {
                step++
                val mockVal = ((kotlin.math.sin(step.toDouble() * 0.4) + 1.0) / 2.0).toFloat() * 0.7f + 0.1f
                _amplitudeFlow.value = mockVal.coerceIn(0f, 1f)
                delay(120)
            }
        }
    }

    private fun stopMockAmplitude() {
        amplitudeJob?.cancel()
        amplitudeJob = null
        _amplitudeFlow.value = 0f
    }
}

actual fun createPlatformAudioRecorder(): AudioRecorder = IosAudioRecorder()
