package com.cookingapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.cookingapp.data.AppDatabase
import com.cookingapp.model.Recipe
import com.cookingapp.network.ApiRecipe
import com.cookingapp.network.NetworkClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.cookingapp.network.RecipeDetailsResponse
import android.util.Log

class RecipeRepository(private val database: AppDatabase) {

    private val api = NetworkClient.spoonacularApi

    val allRecipes: Flow<List<Recipe>> = database.recipeDao().getAllRecipes()
    val allRecipesLiveData: LiveData<List<Recipe>> = allRecipes.asLiveData()

    suspend fun insert(recipe: Recipe) {
        database.recipeDao().insert(recipe)
    }

    suspend fun delete(recipe: Recipe) {
        database.recipeDao().delete(recipe)
    }

    suspend fun update(recipe: Recipe) {
        database.recipeDao().update(recipe)
    }

    suspend fun initializeSampleData() {
        // Add sample recipes if needed for testing
    }

    suspend fun searchRecipesOnline(query: String): List<ApiRecipe> {
        return try {
            val response = api.searchRecipes(query)
            Log.d("API", "Results: ${response.results.size}")
            response.results
        } catch (e: Exception) {
            Log.e("API", "Search failed: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    // FIXED: Collect the Flow to get the list
    suspend fun searchRecipesByIngredients(): List<ApiRecipe> {
        return try {
            // Collect the Flow to get the actual list
            val pantryItemsFlow = database.pantryItemDao().getAllItems()
            val pantryItems = pantryItemsFlow.first()  // This requires the import

            // Build ingredient string
            val ingredientString = pantryItems.joinToString(",") { item -> item.name }

            if (ingredientString.isBlank()) {
                Log.d("API", "No ingredients in pantry")
                return emptyList()
            }

            Log.d("API", "Searching with ingredients: $ingredientString")
            val results = api.searchByIngredients(ingredientString)

            results.map { result ->
                ApiRecipe(
                    id = result.id,
                    title = result.title,
                    imageUrl = result.image,
                    imageType = null,
                    readyInMinutes = null,
                    servings = null,
                    sourceUrl = null
                )
            }
        } catch (e: Exception) {
            Log.e("API", "Search by ingredients failed: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getRecipeDetails(id: Int): RecipeDetailsResponse? {
        return try {
            api.getRecipeDetails(id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}