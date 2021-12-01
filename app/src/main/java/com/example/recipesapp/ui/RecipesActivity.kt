package com.example.recipesapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.recipesapp.R
import com.example.recipesapp.db.RecipeDatabase
import com.example.recipesapp.repository.RecipeRepository
import kotlinx.android.synthetic.main.activity_recipes.*

class RecipesActivity : AppCompatActivity() {

    lateinit var viewModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes)

        val repository = RecipeRepository(RecipeDatabase(this))
        val viewModelProviderFactory = RecipeViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(RecipeViewModel::class.java)

        bottomNavigationView.setupWithNavController(recipesNavHostFragment.findNavController())
    }
}