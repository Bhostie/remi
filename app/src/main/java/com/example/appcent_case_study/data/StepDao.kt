package com.example.appcent_case_study.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.appcent_case_study.data.Step
import kotlinx.coroutines.flow.Flow

@Dao
interface StepDao {

    /**
     * Insert a Step (or replace if same primary key). Returns newly inserted row ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStep(step: Step): Long

    /**
     * Update an existing step.
     */
    @Update
    suspend fun updateStep(step: Step)

    /**
     * Delete a step by its id.
     */
    @Query("DELETE FROM steps WHERE id = :stepId")
    suspend fun deleteStepById(stepId: Long)

    /**
     * Delete all steps belonging to a given recipe (useful if you want to overwrite).
     */
    @Query("DELETE FROM steps WHERE recipe_id = :recipeId")
    suspend fun deleteStepsForRecipe(recipeId: Long)

    /**
     * Fetch all steps of a particular recipe — ordered by step_number ascending.
     */
    @Query("""
      SELECT * 
      FROM steps 
      WHERE recipe_id = :recipeId 
      ORDER BY step_number ASC
    """)
    fun getStepsForRecipe(recipeId: Long): Flow<List<Step>>
}
