package com.mel.medicalapp.platform

import kotlinx.coroutines.flow.StateFlow

enum class RecordingState {
    IDLE,
    RECORDING,
    PAUSED,
    STOPPED
}

interface AudioRecorder {
    val recordingState: StateFlow<RecordingState>
    val amplitudeFlow: StateFlow<Float>

    suspend fun startRecording(outputFilePath: String): Result<Unit>
    suspend fun pauseRecording(): Result<Unit>
    suspend fun resumeRecording(): Result<Unit>
    suspend fun stopRecording(): Result<String> // returns file path or URI
    suspend fun cancelRecording(): Result<Unit> // discards and deletes recording file
    fun release()
}

expect fun createPlatformAudioRecorder(): AudioRecorder
