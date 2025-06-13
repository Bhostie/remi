package com.example.appcent_case_study.ui.steps

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.appcent_case_study.R
import com.example.appcent_case_study.data.AppDatabase
import com.example.appcent_case_study.data.LocalRecipeRepository
import com.example.appcent_case_study.databinding.FragmentStepsBinding
import com.example.appcent_case_study.speech.SpeechHandler
import com.example.appcent_case_study.speech.SpeechInterface
import com.example.appcent_case_study.speech.VoiceAction

class StepsFragment: Fragment(R.layout.fragment_steps), SpeechInterface{
    // This fragment will handle the steps of the recipe

    private var _binding: FragmentStepsBinding? = null
    private val binding get() = _binding!!

    // Speech stuff
    private lateinit var speechHandler: SpeechHandler
    private var hasAudioPermission: Boolean = false

    companion object {
        private const val TAG = "StepsFragment"
        private const val REQUEST_AUDIO_PERMISSION = 1
    }

    private val stepsViewModel by lazy {
        val db = AppDatabase.getInstance(requireContext())
        val repo = LocalRecipeRepository(db)
        val factory = StepsViewModelFactory(repo, requireArguments().getLong("recipeId"))
        ViewModelProvider(this, factory)[StepsViewModel::class.java]
    }

    private var current_step: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        _binding = FragmentStepsBinding.bind(view)

        // Observe Recipe Object
        stepsViewModel.recipe.observe(viewLifecycleOwner) { recipe ->
            // Update the UI with the recipe name
            binding.tvRecipeName.text = recipe.name
        }

        // initiate speech recognition
        speechHandler = SpeechHandler(requireContext(), this)
        checkAndRequestPermissions()

        // Observe the steps LiveData from the ViewModel
        stepsViewModel.stepList.observe(viewLifecycleOwner) { steps ->

            binding.tvTotalSteps.text = "/${steps.size}"

            steps.sortedBy { it.number } // Sort steps by number
            binding.tvStepsTitle.text = "Step: ${steps[current_step].number}"

            binding.tvDescription.text = steps[0].description
        }

        binding.btnNext.setOnClickListener {
            // Handle next step button click
            val currentStep = stepsViewModel.stepList.value?.find { it.number == binding.tvStepsTitle.text.split(": ")[1].toInt() }
            if (currentStep != null) {
                val nextStepNumber = currentStep.number + 1
                val nextStep = stepsViewModel.stepList.value?.find { it.number == nextStepNumber }
                if (nextStep != null) {
                    binding.tvStepsTitle.text = "Step: ${nextStep.number}"
                    binding.tvDescription.text = nextStep.description
                } else {
                    // No more steps, maybe show a message or reset
                }
            }
        }
        binding.btnPrevious.setOnClickListener {
            // Handle previous step button click
            val currentStep = stepsViewModel.stepList.value?.find { it.number == binding.tvStepsTitle.text.split(": ")[1].toInt() }
            if (currentStep != null) {
                val previousStepNumber = currentStep.number - 1
                val previousStep = stepsViewModel.stepList.value?.find { it.number == previousStepNumber }
                if (previousStep != null) {
                    binding.tvStepsTitle.text = "Step: ${previousStep.number}"
                    binding.tvDescription.text = previousStep.description
                } else {
                    // No previous step, maybe show a message or reset
                }
            }
        }

        // Ingredients Button
        val ingredients_button = requireActivity().findViewById<ImageView>(R.id.ingredients_button)
        ingredients_button.setOnClickListener {
            stepsViewModel.recipe.value?.let { recipe ->
                val ingredientsList = recipe.ingredients // adjust based on your actual model
                val ingredientsText = recipe.ingredients.split(",").joinToString("\n") { it.trim() }

                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Ingredients")
                    .setMessage(ingredientsText)
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

    }

    // --- Fragment Lifecycle ---
    override fun onResume() {
        super.onResume()
        if (hasAudioPermission && ::speechHandler.isInitialized){
            // SpeechHandler will decide if it's wake word or command based on its internal state,
            // but onResume, we typically want to ensure it's in wake word mode if not active.
            // The SpeechHandler's own logic should bring it back to wake word after commands/errors.
            // So, just ensuring it starts if it was paused.
            speechHandler.startListeningForWakeWord() // Default to wake word on resume
        } else if (!hasAudioPermission && ::speechHandler.isInitialized){
            Log.w(TAG, "onResume: Missing audio permission.")
            // Potentially show a persistent message or disable voice features UI
        }
    }

    override fun onPause() {
        super.onPause()
        if (::speechHandler.isInitialized) {
            speechHandler.cancelListening() // Use cancel to stop immediately and free resources
        }
    }

    override fun onDestroyView() {
        if (::speechHandler.isInitialized){
            speechHandler.destroy()
        }
        _binding = null // Ensure binding is nulled here
        super.onDestroyView()
    }

    // --- Speech stuff ---
    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.RECORD_AUDIO
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ){
            requestAudioPermission()
        } else {
            hasAudioPermission = true
            speechHandler.startListeningForWakeWord()
        }
    }

    override fun requestAudioPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.RECORD_AUDIO),
            REQUEST_AUDIO_PERMISSION
        )
    }

    // idk how to to do this in the correct undeprecated way :3
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasAudioPermission = true
                speechHandler.startListeningForWakeWord()
                Log.d(TAG, "Audio permission granted")
            } else {
                // Handle permission denied
                hasAudioPermission = false
                Toast.makeText(requireContext(), "Audio Permission required to use speech recognition", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Audio permission denied")
            }
        }
    }

    override fun onWakeWordDetected() {
        Log.i(TAG, "Wake word detected")
        Toast.makeText(requireContext(), "Remy listening...", Toast.LENGTH_SHORT).show()
        // SpeechHandler automatically transitions to listen for a command.
        // No need to call speechHandler.setCommandMode(true) or speechHandler.startListeningForCommand()
//        TODO(" Light up the listening indicator or sth")
    }

    override fun onVoiceAction(action: VoiceAction) {
        Log.i(TAG, "Voice Action received: $action")
        // SpeechHandler will automatically revert to wake word listening after this.

        when (action) {
            VoiceAction.NEXT_STEP -> {
                Log.d(TAG, "Executing: Next Step")
                binding.btnNext.performClick()
                Toast.makeText(requireContext(), "Going to next step", Toast.LENGTH_SHORT).show()
            }
            VoiceAction.PREVIOUS_STEP -> {
                Log.d(TAG, "Executing: Previous Step")
                binding.btnPrevious.performClick()
                Toast.makeText(requireContext(), "Going to previous step", Toast.LENGTH_SHORT).show()
            }
            VoiceAction.SHOW_INGREDIENTS -> {
                Log.d(TAG, "Executing: Show Ingredients")
                requireActivity().findViewById<ImageView>(R.id.ingredients_button)?.performClick()
                Toast.makeText(requireContext(), "Showing ingredients", Toast.LENGTH_SHORT).show()
            }
            VoiceAction.STOP_LISTENING -> {
                Log.d(TAG, "Executing: Stop Listening")
                Toast.makeText(requireContext(), "Okay, stopping.", Toast.LENGTH_SHORT).show()
                // SpeechHandler already handles reverting to wake word mode.
                // If STOP_LISTENING implies truly stopping all voice interaction until manually restarted,
                // SpeechHandler might need a more explicit stop/pause that isn't just reverting to wake word.
                // For now, assuming it means "cancel current command listening and go back to wake word".
            }
            VoiceAction.UNKNOWN_COMMAND -> {
                Log.d(TAG, "Unknown command")
                Toast.makeText(requireContext(), "Sorry, I didn't understand that.", Toast.LENGTH_SHORT).show()
            }
            VoiceAction.NO_SPEECH_INPUT -> {
                Log.d(TAG, "No speech input detected or no match in command mode.")
                Toast.makeText(requireContext(), "Didn't catch that. Say 'Remy' again.", Toast.LENGTH_SHORT).show()
            }
        }
        // No need to manage speechHandler's listening state here.
    }


    override fun onError(errorMessage: String) {
        Log.e(TAG, "Voice Handler Error: $errorMessage")
        // Only show errors. SpeechHandler is responsible for attempting to recover to wake word listening.
        if (errorMessage != "No speech match" && errorMessage != "No speech input") {
            // Avoid double toast if NO_SPEECH_INPUT already handled by onVoiceAction
            Toast.makeText(requireContext(), "Speech error: $errorMessage. Say 'Remy' to try again.", Toast.LENGTH_LONG).show()
        }


        // SpeechHandler will attempt to restart listening for the wake word on most errors.
        // If permissions error, requestAudioPermission() was called.
        // Fragment doesn't need to manage restart logic here.
    }
}