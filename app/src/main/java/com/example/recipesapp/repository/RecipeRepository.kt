package com.example.recipesapp.repository

import com.example.recipesapp.api.RetrofitInstance
import com.example.recipesapp.db.RecipeDatabase
import com.example.recipesapp.models.Recipe

class RecipeRepository(val db: RecipeDatabase) {

    suspend fun getRandomRecipes() = RetrofitInstance.api.getRandomRecipes()

    suspend fun searchForRecipes(searchQuery: String) =
            RetrofitInstance.api.searchForRecipes(searchQuery = searchQuery)

    suspend fun getNextPageByUrl(url: String) = RetrofitInstance.api.getNextPageByUrl(url)

    fun getSavedRecipes() = db.getRecipeDao().getSavedRecipes()

    suspend fun upsertSavedRecipe(recipe: Recipe) = db.getRecipeDao().upsert(recipe)

    suspend fun deleteSavedRecipe(recipe: Recipe) = db.getRecipeDao().delete(recipe)
}