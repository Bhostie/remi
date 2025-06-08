package com.example.appcent_case_study.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "steps",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipe_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recipe_id")]
)
data class Step(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    /**
     * Foreign key back to Recipe.id
     */
    @ColumnInfo(name = "recipe_id")
    val recipeId: Long,

    /**
     * The sequence number of this step (1, 2, 3, …).
     */
    @ColumnInfo(name = "step_number")
    val number: Int,

    /**
     * URI or file path to image/video for this step.
     */
    @ColumnInfo(name = "media_uri")
    val mediaUri: String? = null,

    @ColumnInfo(name = "description")
    val description: String
)

