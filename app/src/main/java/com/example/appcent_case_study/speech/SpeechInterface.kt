package com.example.appcent_case_study.speech

interface SpeechInterface {
    fun onWakeWordDetected()
    fun onVoiceAction(action: VoiceAction) // Changed from onCommandReceived
    fun onError(errorMessage: String)
    fun requestAudioPermission()
}
