package com.example.appcent_case_study.data

import androidx.room.Embedded
import androidx.room.Relation

data class RecipeWithSteps(
    @Embedded
    val recipe: Recipe,

    @Relation(
        parentColumn = "id",
        entityColumn = "recipe_id"
    )
    val steps: List<Step>
)