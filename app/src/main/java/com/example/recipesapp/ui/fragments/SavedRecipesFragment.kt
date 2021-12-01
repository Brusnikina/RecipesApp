package com.example.recipesapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipesapp.R
import com.example.recipesapp.adapters.RecipeAdapter
import com.example.recipesapp.ui.ActivityLifeCycleObserver
import com.example.recipesapp.ui.RecipeViewModel
import com.example.recipesapp.ui.RecipesActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_recipes.*

class SavedRecipesFragment : Fragment(R.layout.fragment_saved_recipes) {

    lateinit var viewModel: RecipeViewModel
    lateinit var recipeAdapter: RecipeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        recipeAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("recipe", it)
            }
            findNavController().navigate(R.id.action_savedRecipesFragment_to_recipeFragment, bundle)
        }

        activity?.lifecycle?.addObserver(ActivityLifeCycleObserver{
            viewModel = (activity as RecipesActivity).viewModel

            viewModel.getSavedRecipes().observe(viewLifecycleOwner, Observer { recipes ->
                recipeAdapter.differ.submitList(recipes)
            })
        })

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val recipe = recipeAdapter.differ.currentList[position]
                viewModel.deleteRecipe(recipe)
                Snackbar.make(view, getString(R.string.recipe_deleted), Snackbar.LENGTH_LONG). apply {
                    setAction("Undo"){
                        viewModel.saveRecipe(recipe)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvSavedRecipes)
    }

    private fun setupRecyclerView(){
        recipeAdapter = RecipeAdapter()
        rvSavedRecipes.apply {
            adapter = recipeAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}