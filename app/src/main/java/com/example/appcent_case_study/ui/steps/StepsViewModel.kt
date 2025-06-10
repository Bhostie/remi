package com.example.appcent_case_study.ui.steps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.appcent_case_study.data.LocalRecipeRepository
import com.example.appcent_case_study.data.Recipe
import com.example.appcent_case_study.data.Step

class StepsViewModel(private val repo: LocalRecipeRepository, recipeId: Long): ViewModel() {

    var stepList : LiveData<List<Step>> =
        repo.getStepsByRecipeId(recipeId).asLiveData()


    var recipe: LiveData<Recipe> = repo.getRecipeById(recipeId).asLiveData()



}