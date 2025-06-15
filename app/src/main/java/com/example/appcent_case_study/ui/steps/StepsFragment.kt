package com.example.appcent_case_study.ui.steps

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

    private var ingredientsDialog: AlertDialog? = null // needed to store a reference so I can highlight if it is already open


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
            showIngredientsDialog()
        }

    }

    private fun showIngredientsDialog() {
        if (ingredientsDialog?.isShowing == true) highlightAlertDialog(ingredientsDialog) // Don't show if already showing)
        else stepsViewModel.recipe.value?.let { recipe ->
            val ingredientsText = recipe.ingredients.split(",").joinToString("\n") { it.trim() }

            ingredientsDialog = AlertDialog.Builder(requireContext())
                .setTitle("Ingredients")
                .setMessage(ingredientsText)
                .setPositiveButton("OK", null)
                .setOnDismissListener {
                    ingredientsDialog = null // Clear reference when dialog is dismissed
                }
                .show()
        }
    }

    private fun closeIngredientsDialog() {
        ingredientsDialog?.dismiss()
        ingredientsDialog = null
    }

    private fun toggleIngredientsDialog(){
        if (ingredientsDialog?.isShowing == true) {
            closeIngredientsDialog()
        } else {
            showIngredientsDialog()
        }
    }

    private fun highlightAlertDialog(dialog: AlertDialog?) {
        dialog?.takeIf { it.isShowing }?.window?.decorView?.let { decorView ->
            // Create a scale animation for a "pulse" effect
            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.05f, 1f)
            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.05f, 1f)

            val animator = ObjectAnimator.ofPropertyValuesHolder(decorView, scaleX, scaleY).apply {
                duration = 600 // Duration in milliseconds for the entire pulse (e.g., 300ms out, 300ms in)
                interpolator = AccelerateDecelerateInterpolator()
            }
            animator.start()
        }
    }

    // --- Fragment Lifecycle ---
    override fun onResume() {
        super.onResume()
        if (hasAudioPermission && ::speechHandler.isInitialized){
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
            speechHandler.stopListening()
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

    // voice indicator stuff
    override fun onRmsChange(rmsdB: Float) {
        if (rmsdB == 0f) return // Avoid division by zero

        // normalize to typical range
        val normalizedRms = (rmsdB - 0) / (12f - 0)
        val targetAlpha = 0.3f + normalizedRms * 0.7f
        _binding?.voiceRecognitionOutline?.alpha = targetAlpha
//        Log.v(TAG, "RMS: $rmsdB, Alpha: $targetAlpha")
    }

    private fun updateListeningIndicatorColor(colorResId: Int){
        _binding?.voiceRecognitionOutline?.let { outlineView ->
            val outline_background = outlineView.background
            if (outline_background is GradientDrawable){
                val mutableBackground = outline_background.mutate() as GradientDrawable
                val newColor = ContextCompat.getColor(requireContext(), colorResId)
                val width = resources.getDimensionPixelSize(R.dimen.voice_outline_width)
                mutableBackground.setStroke(width, newColor)
                outlineView.background = mutableBackground
            }
        }
    }

    private fun flashOutlineWithColor(colorResId: Int){
        val delayMillis = 1500L

        updateListeningIndicatorColor(colorResId)
        _binding?.voiceRecognitionOutline?.visibility = View.VISIBLE
        _binding?.voiceRecognitionOutline?.postDelayed({
            _binding?.voiceRecognitionOutline?.visibility = View.INVISIBLE
        }, delayMillis)
    }

    private fun flashOutlineRed(){
        // error or invalid command
        flashOutlineWithColor(R.color.red)
    }

    private fun setOutlineToGreen(){
        // wake word heard
        updateListeningIndicatorColor(R.color.green)
    }

    private fun flashOutlineGrey(){
        // no speech detected
        flashOutlineWithColor(R.color.black) // there is already an alpha
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
        setOutlineToGreen()
        _binding?.voiceRecognitionOutline?.visibility = View.VISIBLE
        _binding?.voiceRecognitionOutline?.alpha = 0.3f
        // SpeechHandler automatically transitions to listen for a command.
        // No need to call speechHandler.setCommandMode(true) or speechHandler.startListeningForCommand()
//        TODO(" Light up the listening indicator or sth")
    }

    override fun onVoiceAction(action: VoiceAction) {
        Log.i(TAG, "Voice Action received: $action")
        _binding?.voiceRecognitionOutline?.visibility = View.INVISIBLE
        _binding?.voiceRecognitionOutline?.alpha = 0.3f
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
                showIngredientsDialog()
                Toast.makeText(requireContext(), "Showing ingredients", Toast.LENGTH_SHORT).show()
            }
            VoiceAction.CLOSE_INGREDIENTS -> {
                Log.d(TAG, "Executing: Close Ingredients")
                closeIngredientsDialog()
                Toast.makeText(requireContext(), "Closing ingredients", Toast.LENGTH_SHORT).show()
            }
            VoiceAction.TOGGLE_INGREDIENTS -> {
                Log.d(TAG, "Executing: Toggle Ingredients")
                toggleIngredientsDialog()
                Toast.makeText(requireContext(), "Toggling ingredients", Toast.LENGTH_SHORT).show()
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
                flashOutlineRed()
                Toast.makeText(requireContext(), "Sorry, I didn't understand that.", Toast.LENGTH_SHORT).show()
            }
            VoiceAction.NO_SPEECH_INPUT -> {
                Log.d(TAG, "No speech input detected or no match in command mode.")
                flashOutlineGrey()

                Toast.makeText(requireContext(), "Didn't catch that. Say 'Remy' again.", Toast.LENGTH_SHORT).show()
            }
        }
        // No need to manage speechHandler's listening state here.
    }


    override fun onError(errorMessage: String) {
        Log.e(TAG, "Voice Handler Error: $errorMessage")
        // Only show errors. SpeechHandler is responsible for attempting to recover to wake word listening.
        // Adam: I got continuous client side errors, so I am doing this dumbfuck solution kthxbye
        if (errorMessage != "No speech match" && errorMessage != "No speech input" && errorMessage!= "Client side error") {
            // Avoid double toast if NO_SPEECH_INPUT already handled by onVoiceAction
            flashOutlineRed()
            Toast.makeText(requireContext(), "Speech error: $errorMessage. Say 'Remy' to try again.", Toast.LENGTH_LONG).show()
        }


        // SpeechHandler will attempt to restart listening for the wake word on most errors.
        // If permissions error, requestAudioPermission() was called.
        // Fragment doesn't need to manage restart logic here.
    }
}