package com.example.appcent_case_study.ui.genres

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

import com.example.appcent_case_study.data.LocalRecipeRepository
import com.example.appcent_case_study.data.Recipe
import com.example.appcent_case_study.data.Step

class RecipeViewModel(private val repo: LocalRecipeRepository) : ViewModel() {


    // Flow from DAO → asLiveData
    val allRecipes: LiveData<List<Recipe>> =
        repo.getAllRecipes().asLiveData()

}