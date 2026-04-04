package com.cookingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cookingapp.CookingApplication
import com.cookingapp.data.repository.RecipeRepository
import com.cookingapp.model.Recipe
import com.cookingapp.network.ApiRecipe
import kotlinx.coroutines.launch
import com.cookingapp.network.RecipeDetailsResponse


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

    private val _searchResults = MutableLiveData<List<ApiRecipe>>()
    val searchResults: LiveData<List<ApiRecipe>> = _searchResults

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun searchRecipes(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val results = repository.searchRecipesOnline(query)
            _searchResults.value = results
            _isLoading.value = false
        }
    }

    fun searchByPantryIngredients() {
        viewModelScope.launch {
            _isLoading.value = true
            val results = repository.searchRecipesByIngredients()
            _searchResults.value = results
            _isLoading.value = false
        }
    }

    private val _recipeDetails = MutableLiveData<RecipeDetailsResponse?>()
    val recipeDetails: LiveData<RecipeDetailsResponse?> = _recipeDetails

    fun loadRecipeDetails(id: Int) {
        viewModelScope.launch {
            _recipeDetails.value = repository.getRecipeDetails(id)
        }
    }

    fun deductPantryIngredients(recipeId: Int) {
        viewModelScope.launch {
            val details = repository.getRecipeDetails(recipeId) ?: return@launch
            val pantryRepository = (getApplication<CookingApplication>()).pantryRepository
            for (ingredient in details.extendedIngredients) {
                pantryRepository.deductIngredient(ingredient.name, ingredient.amount)
            }
        }
    }
}