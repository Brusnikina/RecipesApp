package com.example.recipesapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.recipesapp.models.Recipe

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(recipe: Recipe): Long

    @Delete
    suspend fun delete(recipe: Recipe)

    @Query("SELECT * FROM recipes")
    fun getSavedRecipes(): LiveData<List<Recipe>>
}