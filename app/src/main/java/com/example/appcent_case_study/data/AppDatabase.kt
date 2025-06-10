package com.example.appcent_case_study.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [Recipe::class, Step::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao
    abstract fun stepDao(): StepDao

    companion object {
        // Volatile annotation ensures visibility across threads
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room
                    .databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "recipe_database"
                    )

                    // load initial data from the bundled asset:
                    .createFromAsset("databases/updated_sample_recipes.db")
                    // keep this during development so Room will wipe and re-load if your schema changes
                    .fallbackToDestructiveMigration() //TODO: REMOVE IN PRODUCTION
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}



