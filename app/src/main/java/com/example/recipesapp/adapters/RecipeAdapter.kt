package com.example.recipesapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipesapp.R
import com.example.recipesapp.models.Recipe
import kotlinx.android.synthetic.main.item_recipe.view.*


class RecipeAdapter : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer<Recipe>(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        return RecipeViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.item_recipe,
                parent,
                false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(recipe.image).into(ivRecipeImage)
            tvTitle.text = recipe.label
            tvServing.text = String.format("%.0f servings", recipe.yield)
            tvCalories.text = String.format("%.0f kcal", recipe.calories / recipe.yield)
            tvMealType.text = recipe.mealType.joinToString()
            tvSource.text = recipe.source
            setOnClickListener {
                onItemClickListener?.let { it(recipe) }
            }
        }
    }

    private var onItemClickListener: ((Recipe) -> Unit)? = null

    fun setOnItemClickListener(listener: (Recipe) -> Unit) {
        onItemClickListener = listener
    }
}