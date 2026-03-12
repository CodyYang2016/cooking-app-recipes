package com.cookingapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.cookingapp.data.AppDatabase
import com.cookingapp.model.Recipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeRepository(private val database: AppDatabase) {

    val allRecipes: Flow<List<Recipe>> = database.recipeDao().getAllRecipes()

    val allRecipesLiveData: LiveData<List<Recipe>> =
        allRecipes.asLiveData()

    suspend fun insert(recipe: Recipe) {
        database.recipeDao().insert(recipe)
    }

    suspend fun update(recipe: Recipe) {
        database.recipeDao().update(recipe)
    }

    suspend fun delete(recipe: Recipe) {
        database.recipeDao().delete(recipe)
    }

    suspend fun getRecipesByCategory(category: String): List<Recipe>? {
        val flow = database.recipeDao().getRecipesByCategory(category)
        // This is simplified - in practice you'd observe the Flow
        return null
    }

    fun insertRecipe(recipe: Recipe, onComplete: () -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            insert(recipe)
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    suspend fun initializeSampleData() {
        val currentItems = database.recipeDao().getAllRecipes()
        if (currentItems is kotlinx.coroutines.flow.Flow &&
            currentItems is java.util.Collection<*> &&
            currentItems.isEmpty()) {

            val sampleRecipes = listOf(
                Recipe(title = "Scrambled Eggs", category = "Breakfast", servings = 2),
                Recipe(title = "Grilled Cheese", category = "Lunch", servings = 1),
                Recipe(title = "Fried Rice", category = "Dinner", servings = 4),
                Recipe(title = "Pasta", category = "Dinner", servings = 2),
                Recipe(title = "Salad", category = "Lunch", servings = 2)
            )

            sampleRecipes.forEach { insert(it) }
        }
    }
}