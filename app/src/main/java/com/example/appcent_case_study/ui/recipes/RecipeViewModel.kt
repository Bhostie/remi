package com.example.appcent_case_study.ui.genres

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

import com.example.appcent_case_study.data.LocalRecipeRepository
import com.example.appcent_case_study.data.Recipe
import com.example.appcent_case_study.data.Step

class RecipeViewModel(private val repo: LocalRecipeRepository) : ViewModel() {

    val data = repo.getAllRecipes()

    // Flow from DAO → asLiveData
    val allRecipes: LiveData<List<Recipe>> =
        repo.getAllRecipes().asLiveData()

    /** Fetches all recipes from the repository. */
    fun getMyData() {
        // This will automatically update the 'data' LiveData
        // since it is backed by a Flow in the repository.
    }

    /** Insert a new recipe with its steps. */
    suspend fun insertRecipeWithSteps(recipe: Recipe, steps: List<Step>) {
        repo.insertRecipeWithSteps(recipe, steps)
    }

    /** Update an existing recipe. */
    suspend fun updateRecipe(recipe: Recipe) {
        repo.updateRecipe(recipe)
    }

    /** Delete a recipe by its ID. */
    suspend fun deleteRecipe(recipeId: Long) {
        repo.deleteRecipe(recipeId)
    }


}