package com.example.recipesapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.recipesapp.R
import com.example.recipesapp.ui.ActivityLifeCycleObserver
import com.example.recipesapp.ui.RecipeViewModel
import com.example.recipesapp.ui.RecipesActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_recipe.*

class RecipeFragment : Fragment(R.layout.fragment_recipe) {

    lateinit var viewModel: RecipeViewModel
    val args: RecipeFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipe = args.recipe
        webViewProgressBar.visibility = View.VISIBLE
        webView.apply {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    webViewProgressBar?.visibility = View.GONE
                }
            }
            loadUrl(recipe.url)
        }

        activity?.lifecycle?.addObserver(ActivityLifeCycleObserver{
            viewModel = (activity as RecipesActivity).viewModel
        })

        fab.setOnClickListener {
            viewModel.saveRecipe(recipe)
            Snackbar.make(view, getString(R.string.recipe_saved), Snackbar.LENGTH_SHORT).show()
        }
    }
}