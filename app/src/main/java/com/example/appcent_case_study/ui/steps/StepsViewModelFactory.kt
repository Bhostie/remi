package com.example.appcent_case_study.ui.steps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appcent_case_study.data.LocalRecipeRepository

class StepsViewModelFactory(
    private val repo: LocalRecipeRepository,
    private val recipeId: Long
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StepsViewModel::class.java)) {
            return StepsViewModel(repo, recipeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}