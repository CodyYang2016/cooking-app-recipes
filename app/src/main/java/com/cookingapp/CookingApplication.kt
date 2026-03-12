package com.cookingapp

import android.app.Application
import com.cookingapp.data.AppDatabase
import com.cookingapp.data.repository.PantryRepository
import com.cookingapp.data.repository.RecipeRepository

class CookingApplication : Application() {

    // Singleton instances
    private val database by lazy { AppDatabase.getInstance(this) }
    val pantryRepository by lazy { PantryRepository(database) }
    val recipeRepository by lazy { RecipeRepository(database) }

    override fun onCreate() {
        super.onCreate()

        // Initialize sample data (you might want to do this only once)
        // This is just for development
//        CoroutineScope(Dispatchers.IO).launch {
//            pantryRepository.initializeSampleData()
//            recipeRepository.initializeSampleData()
//        }
    }
}