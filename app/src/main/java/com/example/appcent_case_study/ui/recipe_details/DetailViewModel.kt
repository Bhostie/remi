package com.example.appcent_case_study.ui.recipe_details

import androidx.lifecycle.ViewModel
import com.example.appcent_case_study.data.LocalRecipeRepository
import com.example.appcent_case_study.data.RecipeWithSteps
import kotlinx.coroutines.flow.Flow

class DetailViewModel(private val repo: LocalRecipeRepository) : ViewModel() {


    lateinit var data: Flow<RecipeWithSteps?>

    fun getRecipeWithSteps(recipeId: Long) {
        data = repo.getRecipeWithSteps(recipeId)
    }



 }