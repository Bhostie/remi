package com.example.appcent_case_study.data
import kotlinx.coroutines.flow.Flow

class LocalRecipeRepository(private val db: AppDatabase) {

    private val recipeDao = db.recipeDao()
    private val stepDao   = db.stepDao()

    /** Return all recipes, Live–updating. */
    fun getAllRecipes(): Flow<List<Recipe>> = recipeDao.getAllRecipes()

    fun getRecipeById(recipeId: Long): Flow<Recipe> =
        recipeDao.getRecipeById(recipeId)

    fun getStepsByRecipeId(recipeId: Long): Flow<List<Step>> =
        stepDao.getStepsForRecipe(recipeId)

    /** Insert a new recipe and its steps (atomic). */
    suspend fun insertRecipeWithSteps(
        recipe: Recipe,
        steps: List<Step>
    ) {
        // 1. Insert recipe, get its generated ID
        val recipeId = recipeDao.insertRecipe(recipe)

        // 2. Insert each step, setting its recipeId
        val stepsToInsert = steps.map { it.copy(recipeId = recipeId) }
        stepsToInsert.forEach { stepDao.insertStep(it) }
    }

    /** Update a recipe (e.g. name, description). */
    suspend fun updateRecipe(recipe: Recipe) {
        recipeDao.updateRecipe(recipe)
    }

    /** Delete a recipe (and its steps via CASCADE). */
    suspend fun deleteRecipe(recipeId: Long) {
        recipeDao.deleteRecipeById(recipeId)
    }

    /** Get a flow of RecipeWithSteps by ID. */
    fun getRecipeWithSteps(recipeId: Long): Flow<RecipeWithSteps?> =
        recipeDao.getRecipeWithSteps(recipeId)

    /** Update or insert a single step. */
    suspend fun upsertStep(step: Step) {
        stepDao.insertStep(step)
    }

    /** Delete all steps of a recipe (e.g. before re‐inserting). */
    suspend fun clearStepsForRecipe(recipeId: Long) {
        stepDao.deleteStepsForRecipe(recipeId)
    }
}