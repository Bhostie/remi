package com.example.appcent_case_study.ui.steps

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.appcent_case_study.R
import com.example.appcent_case_study.data.AppDatabase
import com.example.appcent_case_study.data.LocalRecipeRepository
import com.example.appcent_case_study.databinding.FragmentStepsBinding

class StepsFragment: Fragment(R.layout.fragment_steps) {
    // This fragment will handle the steps of the recipe

    private var _binding: FragmentStepsBinding? = null
    private val binding get() = _binding!!

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


        // Observe the steps LiveData from the ViewModel
        stepsViewModel.stepList.observe(viewLifecycleOwner) { steps ->

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




    }

}