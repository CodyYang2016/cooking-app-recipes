package com.example.cookingapp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cookingapp.domain.usecase.*
import com.example.cookingapp.viewmodel.*

class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val searchUseCase = SearchRecipesByPantryUseCase(
            container.pantryRepository,
            container.recipeRepository
        )
        val finalizeUseCase = FinalizeCookSessionUseCase(container.cookSessionRepository)
        val fetchUseCase = FetchAndCacheApiRecipesUseCase(container.recipeRepository)
        val importUseCase = ImportRecipeFromUrlUseCase(container.recipeRepository)

        @Suppress("UNCHECKED_CAST")
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(container.authManager, container.sessionManager) as T
            modelClass.isAssignableFrom(PantryViewModel::class.java) ->
                PantryViewModel(container.pantryRepository, container.sessionManager) as T
            modelClass.isAssignableFrom(RecipeViewModel::class.java) ->
                RecipeViewModel(container.recipeRepository, searchUseCase, fetchUseCase, container.sessionManager) as T
            modelClass.isAssignableFrom(RecipeDetailViewModel::class.java) ->
                RecipeDetailViewModel(container.recipeRepository) as T
            modelClass.isAssignableFrom(CookViewModel::class.java) ->
                CookViewModel(container.recipeRepository, finalizeUseCase) as T
            modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
                HistoryViewModel(container.cookSessionRepository, container.sessionManager) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}