package com.cookingapp

import android.app.Application
import com.cookingapp.data.AppDatabase
import com.cookingapp.data.repository.PantryRepository
import com.cookingapp.data.repository.RecipeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CookingApplication : Application() {

    private val database by lazy { AppDatabase.getInstance(this) }
    val pantryRepository by lazy { PantryRepository(database) }
    val recipeRepository by lazy { RecipeRepository(database) }

    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
            pantryRepository.initializeSampleData()
            recipeRepository.initializeSampleData()
        }
    }
}