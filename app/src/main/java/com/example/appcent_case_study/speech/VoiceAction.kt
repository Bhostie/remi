package com.example.appcent_case_study.speech

enum class VoiceAction {
    NEXT_STEP,
    PREVIOUS_STEP,
    SHOW_INGREDIENTS,
    STOP_LISTENING,
    UNKNOWN_COMMAND,
    NO_SPEECH_INPUT // Added for clarity when no speech is detected by recognizer
}
