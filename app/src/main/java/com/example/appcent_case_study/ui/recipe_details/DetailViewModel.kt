package com.example.appcent_case_study.ui.recipe_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.appcent_case_study.data.LocalRecipeRepository
import com.example.appcent_case_study.data.Recipe
class DetailViewModel(private val repo: LocalRecipeRepository, recipeId: Long) : ViewModel() {

    var recipeById : LiveData<Recipe> =
        repo.getRecipeById(recipeId).asLiveData()
 }