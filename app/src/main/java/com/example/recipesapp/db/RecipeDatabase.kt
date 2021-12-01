package com.example.recipesapp.db

import android.content.Context
import androidx.room.*
import com.example.recipesapp.models.Recipe

@Database(version = 1, entities = [Recipe::class], exportSchema = false)
@TypeConverters(Converters::class)
abstract class RecipeDatabase : RoomDatabase(){

    abstract fun getRecipeDao() : RecipeDao

    companion object{

        private var instance: RecipeDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                RecipeDatabase::class.java,
                "recipe_db")
                .build()
    }


}