package com.example.appcent_case_study.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class SpeechHandler(
    private val context: Context,
    private val listener: SpeechInterface
) {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent
    private var isListening = false
    private var isCommandMode = false
    private val mainHandler = Handler(Looper.getMainLooper()) // Added Handler

    companion object {
        private const val TAG = "SpeechHandler"
        private const val WAKE_WORD = "remy" // bonus of picking our fun acronym: already recognizable
    }

    // Define commands and their corresponding actions
    private val commandMap: Map<List<String>, VoiceAction> = mapOf(
        listOf("next step", "next") to VoiceAction.NEXT_STEP,
        listOf("previous step", "previous", "back") to VoiceAction.PREVIOUS_STEP,
        listOf("open ingredients", "show ingredients", "ingredients") to VoiceAction.SHOW_INGREDIENTS,
        listOf("stop listening", "cancel", "never mind") to VoiceAction.STOP_LISTENING
        // Add more commands and aliases here
    )

    init {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            initializeSpeechRecognizer()
        } else {
            Log.e(TAG, "Speech recognition not available on this device.")
            listener.onError("Speech recognition not available.")
            // Cannot proceed further if recognition is not available.
        }
    }

    private fun initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(TAG, "Ready for speech")
                isListening = true
            }

            override fun onBeginningOfSpeech() {
                Log.d(TAG, "Beginning of speech")
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                Log.d(TAG, "End of speech")
                isListening = false
            }

            override fun onError(error: Int) {
                Log.e(TAG, "Error: $error")
                val wasListening = isListening // Capture if it was listening before this error
                isListening = false
                val initialCommandModeState = isCommandMode // Capture state before potential change by listener
                var specificActionForListener: VoiceAction? = null

                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> {
                        Log.w(TAG, "Client side error. Attempting to reset and restart listening.")
                        "Client side error"
                    }
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                        listener.requestAudioPermission() // Ask fragment to handle permission
                        "Insufficient permissions"
                    }
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> {
                        if (initialCommandModeState) {
                            specificActionForListener = VoiceAction.NO_SPEECH_INPUT
                        }
                        "No speech match"
                    }
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                    SpeechRecognizer.ERROR_SERVER -> "Error from server"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                        if (initialCommandModeState) {
                            specificActionForListener = VoiceAction.NO_SPEECH_INPUT
                        }
                        "No speech input"
                    }
                    else -> "Unknown speech error"
                }

                listener.onError(errorMessage)
                specificActionForListener?.let { listener.onVoiceAction(it) }

                // Always revert to wake word listening after any error
                isCommandMode = false

                if (::speechRecognizer.isInitialized) {
                    if (wasListening) {
                        // If it was listening, explicitly cancel to ensure it stops cleanly
                        speechRecognizer.cancel()
                    }

                    if (error != SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                        // Post the restart to the main looper to decouple it slightly
                        // This gives the recognizer a moment to settle after an error.
                        mainHandler.post {
                            if (::speechRecognizer.isInitialized) { // Check again, could be destroyed
                                Log.d(TAG, "Attempting to restart listening for wake word after error.")
                                startListeningForWakeWord()
                            }
                        }
                    }
                }
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spokenText = matches[0].lowercase(Locale.getDefault())
                    Log.d(TAG, "Recognized: $spokenText")

                    if (isCommandMode) {
                        var matchedAction = VoiceAction.UNKNOWN_COMMAND
                        for ((phrases, action) in commandMap) {
                            if (phrases.any { phrase -> spokenText.contains(phrase) }) {
                                matchedAction = action
                                break
                            }
                        }
                        listener.onVoiceAction(matchedAction)
                        // After processing a command, revert to wake word mode
                        isCommandMode = false
                        startListeningForWakeWord()
                    } else { // Listening for wake word
                        if (spokenText.contains(WAKE_WORD)) {
                            listener.onWakeWordDetected()
                            isCommandMode = true
                            startListeningForCommand() // Listen for one command
                        } else {
                            // Not the wake word, continue listening for wake word
                            startListeningForWakeWord()
                        }
                    }
                } else {
                    // No matches from speech recognizer
                    if (isCommandMode) {
                        listener.onVoiceAction(VoiceAction.NO_SPEECH_INPUT)
                        isCommandMode = false // Revert to wake word mode
                        startListeningForWakeWord()
                    } else {
                        // Not in command mode and no results, continue listening for wake word
                        startListeningForWakeWord()
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                 val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                 if (matches != null && matches.isNotEmpty()) {
                     val partialText = matches[0].lowercase(Locale.getDefault())
                     Log.d(TAG, "Partial: $partialText")
                     // Optionally, try to detect wake word here for faster response,
                     // but be careful with accuracy and ensure it doesn't conflict with onResults
                 }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startListeningForWakeWord() {
        if (!::speechRecognizer.isInitialized) {
            Log.w(TAG, "Speech recognizer not initialized. Cannot start listening for wake word.")
            return
        }
        if (!isListening) {
            // isCommandMode = false; // This is now managed internally after command/error
            Log.d(TAG, "Starting to listen for WAKE WORD.")
            speechRecognizer.startListening(speechRecognizerIntent)
        }
    }

    fun startListeningForCommand() {
        if (!::speechRecognizer.isInitialized) {
            Log.w(TAG, "Speech recognizer not initialized. Cannot start listening for command.")
            return
        }
        if (!isListening) {
            // isCommandMode = true; // This is now managed internally when wake word detected
            Log.d(TAG, "Starting to listen for COMMAND.")
            speechRecognizer.startListening(speechRecognizerIntent)
        }
    }

    fun stopListening() {
        if (::speechRecognizer.isInitialized && isListening) {
            speechRecognizer.stopListening()
            isListening = false
            Log.d(TAG, "Speech recognizer stopped listening explicitly.")
        }
    }

    fun cancelListening() {
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.cancel()
            isListening = false
            Log.d(TAG, "Speech recognizer cancelled.")
        }
    }

    fun destroy() {
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.stopListening() // Stop any active listening
            speechRecognizer.destroy()
            Log.d(TAG, "Speech recognizer destroyed")
        }
    }

    // This function might not be strictly needed by the fragment anymore
    // but can be kept for internal logic or debugging if SpeechHandler needs to expose it.
    // For now, let's assume it's internal.
    // fun isCommandModeActive(): Boolean = isCommandMode

    // This function should ideally be managed internally by SpeechHandler.
    // The fragment should not need to set this directly with the new flow.
    internal fun setCommandMode(active: Boolean) { // Made internal or could be private
        isCommandMode = active
        Log.d(TAG, "Command mode set to: $active")
    }
}