package com.example.appcent_case_study.ui.recipe_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.appcent_case_study.data.LocalRecipeRepository
import com.example.appcent_case_study.data.Recipe
import com.example.appcent_case_study.data.RecipeWithSteps
import kotlinx.coroutines.flow.Flow

class DetailViewModel(private val repo: LocalRecipeRepository, recipeId: Long) : ViewModel() {



    var recipeById : LiveData<Recipe> =
        repo.getRecipeById(recipeId).asLiveData()
    fun getRecipeById(recipeId: Long): LiveData<Recipe> {
        return repo.getRecipeById(recipeId).asLiveData()
    }


 }