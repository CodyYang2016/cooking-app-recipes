package com.cookingapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cookingapp.R
import com.cookingapp.network.ApiRecipe

class RecipeAdapter(
    private val onRecipeClick: (ApiRecipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private var recipes = listOf<ApiRecipe>()

    fun submitList(newRecipes: List<ApiRecipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount() = recipes.size

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvRecipeTitle)
        private val tvTime = itemView.findViewById<TextView>(R.id.tvRecipeTime)
        private val tvServings = itemView.findViewById<TextView>(R.id.tvRecipeServings)

        fun bind(recipe: ApiRecipe) {
            tvTitle.text = recipe.title
            tvTime.text = recipe.readyInMinutes?.let { "Ready in $it min" } ?: ""
            tvServings.text = recipe.servings?.let { "Serves $it" } ?: ""
            itemView.setOnClickListener { onRecipeClick(recipe) }
        }
    }
}