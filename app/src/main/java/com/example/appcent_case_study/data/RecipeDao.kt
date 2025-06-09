package com.example.appcent_case_study.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import com.example.appcent_case_study.data.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    /**
     * Insert a new Recipe. If a recipe with the same id exists (unlikely on auto-generate),
     * replace it. Returns the newly inserted row’s ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long

    /**
     * Update an existing recipe (by primary key).
     */
    @Update
    suspend fun updateRecipe(recipe: Recipe): Int

    /**
     * Delete a recipe (and, due to CASCADE, its associated Steps).
     */
    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteRecipeById(recipeId: Long): Int

    /**
     * Fetch all recipes, sorted by name.
     */
    @Query("SELECT * FROM recipes ORDER BY name ASC")
    fun getAllRecipes(): Flow<List<Recipe>>

    /**
     * Fetch a single recipe by ID.
     */
    @Query("SELECT * FROM recipes WHERE id = :recipeId LIMIT 1")
    suspend fun getRecipeById(recipeId: Long): Recipe?

    /**
     * Load a Recipe and its Steps in one go.
     */
    @Transaction
    @Query("""
      SELECT * 
      FROM recipes 
      WHERE id = :recipeId 
      LIMIT 1
    """)
    fun getRecipeWithSteps(recipeId: Long): Flow<RecipeWithSteps?>
}
