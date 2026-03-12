package com.cookingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.cookingapp.CookingApplication
import com.cookingapp.data.repository.RecipeRepository
import com.cookingapp.model.Recipe
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecipeRepository =
        (application as CookingApplication).recipeRepository

    val allRecipes: LiveData<List<Recipe>> = repository.allRecipesLiveData

    fun insertRecipe(title: String, category: String, servings: Int) {
        viewModelScope.launch {
            val recipe = Recipe(title = title, category = category, servings = servings)
            repository.insert(recipe)
        }
    }

    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.update(recipe)
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.delete(recipe)
        }
    }

    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            val updatedRecipe = recipe.copy(isFavorite = !recipe.isFavorite)
            repository.update(updatedRecipe)
        }
    }

    fun addSampleData() {
        viewModelScope.launch {
            repository.initializeSampleData()
        }
    }
}