package com.example.appcent_case_study.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    /**
     * URI or file path to the image (could be a drawable resource name or
     * an absolute/relative file path). For simplicity, we store as String.
     */
    @ColumnInfo(name = "image_uri")
    val imageUri: String? = null,

    @ColumnInfo(name = "description")
    val description: String,

    /**
     * We store ingredients as one large String (e.g. newline-separated).
     * If you want a List<String>, you’d need a TypeConverter.
     */
    @ColumnInfo(name = "ingredients")
    val ingredients: String
)

