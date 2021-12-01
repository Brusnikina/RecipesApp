package com.example.recipesapp.api

import com.example.recipesapp.models.RecipeResponse
import com.example.recipesapp.utils.Constants.Companion.APP_ID
import com.example.recipesapp.utils.Constants.Companion.APP_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface RecipesAPI {

    @GET("/api/recipes/v2")
    suspend fun getRandomRecipes(
        @Query("type")
        type: String = "public",
        @Query("app_id")
        appId: String = APP_ID,
        @Query("app_key")
        appKey: String = APP_KEY,
        @Query("random")
        random: Boolean = true,
        @Query("imageSize")
        imageSize: String = "REGULAR",
        @Query("field")
        fields: Array<String> = arrayOf("label", "image", "source", "url", "yield", "calories", "mealType")
    ): Response<RecipeResponse>

    @GET("/api/recipes/v2")
    suspend fun searchForRecipes(
        @Query("type")
        type: String = "public",
        @Query("q")
        searchQuery: String,
        @Query("app_id")
        appId: String = APP_ID,
        @Query("app_key")
        appKey: String = APP_KEY,
        @Query("imageSize")
        imageSize: String = "REGULAR",
        @Query("field")
        fields: Array<String> = arrayOf("label", "image", "source", "url", "yield", "calories", "mealType")
    ): Response<RecipeResponse>

    @GET
    suspend fun getNextPageByUrl(@Url url: String) : Response<RecipeResponse>
}