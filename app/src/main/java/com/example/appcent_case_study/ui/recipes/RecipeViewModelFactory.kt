package com.example.appcent_case_study.ui.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appcent_case_study.data.LocalRecipeRepository
import com.example.appcent_case_study.ui.genres.RecipeViewModel

/**
 * A factory that knows how to create a RecipeViewModel with a constructor
 * that takes a LocalRecipeRepository.
 */
class RecipeViewModelFactory(
    private val repo: LocalRecipeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            return RecipeViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}