package com.example.recipesapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipesapp.R
import com.example.recipesapp.adapters.RecipeAdapter
import com.example.recipesapp.ui.ActivityLifeCycleObserver
import com.example.recipesapp.ui.RecipeViewModel
import com.example.recipesapp.ui.RecipesActivity
import com.example.recipesapp.utils.Constants
import com.example.recipesapp.utils.Constants.Companion.SEARCH_RECIPES_TIME_DELAY
import com.example.recipesapp.utils.Resource
import kotlinx.android.synthetic.main.fragment_search_recipes.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchRecipesFragment : Fragment(R.layout.fragment_search_recipes) {

    lateinit var viewModel: RecipeViewModel
    lateinit var recipeAdapter: RecipeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        var job: Job? = null
        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_RECIPES_TIME_DELAY)
                editable?.let {
                    if(editable.toString().isNotEmpty()){
                        //viewModel.searchRecipesPage = 1
                        viewModel.searchRecipeResponse = null
                        viewModel.searchRecipes(editable.toString())
                    }
                }
            }
        }

        recipeAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("recipe", it)
            }
            findNavController().navigate(R.id.action_searchRecipesFragment_to_recipeFragment, bundle)
        }

        activity?.lifecycle?.addObserver(ActivityLifeCycleObserver {

            viewModel = (activity as RecipesActivity).viewModel

            viewModel.searchRecipes.observe(viewLifecycleOwner, Observer { response ->
                when(response){
                    is Resource.Success -> {
                        hideProgressBar()
                        response.data?.let { recipeResponse ->
                            recipeAdapter.differ.submitList(recipeResponse.hits.map { it.recipe })

                            isLastPage = recipeResponse.count == recipeResponse.to
                            if(isLastPage){
                                rvSearchRecipes.setPadding(0,0,0,0)
                            }
                        }
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        response.message?.let { message ->
                            Toast.makeText(activity, "Error: $message", Toast.LENGTH_LONG).show()
                        }
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                }
            })
        })
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            isScrolling = true
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThenVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThenVisible && isScrolling
            if(shouldPaginate){
                viewModel.getNextPageByUrl()
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter()
        rvSearchRecipes.apply {
            adapter = recipeAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchRecipesFragment.scrollListener)
        }
    }
}