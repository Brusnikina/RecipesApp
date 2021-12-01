package com.example.recipesapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipesapp.RecipesApplication
import com.example.recipesapp.models.Recipe
import com.example.recipesapp.utils.Resource
import com.example.recipesapp.models.RecipeResponse
import com.example.recipesapp.repository.RecipeRepository
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class RecipeViewModel(
        val app: Application,
        val repository: RecipeRepository
        ) : AndroidViewModel(app) {

    val randomRecipes: MutableLiveData<Resource<RecipeResponse>> = MutableLiveData()
    var randomRecipeResponse: RecipeResponse? = null

    val searchRecipes: MutableLiveData<Resource<RecipeResponse>> = MutableLiveData()
    var searchRecipeResponse: RecipeResponse? = null

    init {
        getRandomRecipes()
    }

    fun getRandomRecipes() = viewModelScope.launch {
        saveRandomRecipesCall()
    }

    fun searchRecipes(searchQuery: String) = viewModelScope.launch {
        saveSearchRecipesCall(searchQuery)
    }

    private fun handleRandomRecipesResponse(response: Response<RecipeResponse>): Resource<RecipeResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                if(randomRecipeResponse == null) {
                    randomRecipeResponse = resultResponse
                } else {
                    val oldRecipes = randomRecipeResponse?.hits
                    val newRecipes = resultResponse.hits
                    oldRecipes?.addAll(newRecipes)

                    randomRecipeResponse?._links?.next?.href = resultResponse._links.next.href
                    randomRecipeResponse?.to = resultResponse.to
                }
                return Resource.Success(randomRecipeResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchRecipesResponse(response: Response<RecipeResponse>): Resource<RecipeResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                if(searchRecipeResponse == null) {
                    searchRecipeResponse = resultResponse
                } else {
                    val oldRecipes = searchRecipeResponse?.hits
                    val newRecipes = resultResponse.hits
                    oldRecipes?.addAll(newRecipes)

                    searchRecipeResponse?._links?.next?.href = resultResponse._links.next.href
                    searchRecipeResponse?.to = resultResponse.to
                }
                return Resource.Success(searchRecipeResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun getNextPageByUrl() = viewModelScope.launch {
        val url = searchRecipeResponse?._links?.next?.href
        searchRecipes.postValue(Resource.Loading())
        val response = url?.let { repository.getNextPageByUrl(it) }
        searchRecipes.postValue(response?.let { handleSearchRecipesResponse(it) })
    }

    fun getSavedRecipes() = repository.getSavedRecipes()

    fun saveRecipe(recipe: Recipe) = viewModelScope.launch {
        repository.upsertSavedRecipe(recipe)
    }

    fun deleteRecipe(recipe: Recipe) = viewModelScope.launch {
        repository.deleteSavedRecipe(recipe)
    }

    private suspend fun saveRandomRecipesCall() {
        randomRecipes.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getRandomRecipes()
                randomRecipes.postValue(handleRandomRecipesResponse(response))
            } else {
                randomRecipes.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> randomRecipes.postValue(Resource.Error("Network failure"))
                else -> randomRecipes.postValue(Resource.Error("Conversion error"))
            }
        }
    }

    private suspend fun saveSearchRecipesCall(searchQuery: String) {
        searchRecipes.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.searchForRecipes(searchQuery)
                searchRecipes.postValue(handleSearchRecipesResponse(response))
            } else {
                searchRecipes.postValue(Resource.Error("No internet connection"))
            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchRecipes.postValue(Resource.Error("Network failure"))
                else -> searchRecipes.postValue(Resource.Error("Conversion error"))
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<RecipesApplication>().getSystemService(
                Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                    connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}