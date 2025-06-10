package com.example.appcent_case_study.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.example.appcent_case_study.data.LocalRecipeRepository
import com.example.appcent_case_study.data.Recipe

class FavoritesViewModel(private val repo: LocalRecipeRepository) : ViewModel() {

    // Flow from DAO → asLiveData
    val allRecipes: LiveData<List<Recipe>> =
        repo.getAllRecipes().asLiveData()

}