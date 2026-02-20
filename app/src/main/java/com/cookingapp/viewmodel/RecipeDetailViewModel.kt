package com.example.cookingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookingapp.auth.SessionManager
import com.example.cookingapp.data.repository.RecipeRepository
import com.example.cookingapp.domain.model.RecipeWithMissing
import com.example.cookingapp.domain.usecase.FetchAndCacheApiRecipesUseCase
import com.example.cookingapp.domain.usecase.SearchRecipesByPantryUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RecipeUiState(
    val recipes: List<RecipeWithMissing> = emptyList(),
    val isLoading: Boolean = false,
    val isFetching: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

class RecipeViewModel(
    private val recipeRepo: RecipeRepository,
    private val searchRecipesByPantryUseCase: SearchRecipesByPantryUseCase,
    private val fetchAndCacheApiRecipesUseCase: FetchAndCacheApiRecipesUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeUiState())
    val state: StateFlow<RecipeUiState> = _state.asStateFlow()

    private val userId = sessionManager.userIdFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init { loadRecipes() }

    fun loadRecipes() = viewModelScope.launch {
        val uid = userId.value ?: return@launch
        _state.update { it.copy(isLoading = true) }
        val results = searchRecipesByPantryUseCase(uid)
        _state.update { it.copy(recipes = results, isLoading = false) }
    }

    fun fetchFromApi(query: String) = viewModelScope.launch {
        val uid = userId.value ?: return@launch
        _state.update { it.copy(isFetching = true, searchQuery = query) }
        fetchAndCacheApiRecipesUseCase(query, uid).fold(
            onSuccess = { loadRecipes() },
            onFailure = { err -> _state.update { it.copy(error = err.message, isFetching = false) } }
        )
        _state.update { it.copy(isFetching = false) }
    }

    fun updateSearchQuery(query: String) = _state.update { it.copy(searchQuery = query) }
}