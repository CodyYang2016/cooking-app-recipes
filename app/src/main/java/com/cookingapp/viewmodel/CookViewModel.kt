package com.example.cookingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookingapp.data.entity.UsageStatus
import com.example.cookingapp.data.repository.RecipeRepository
import com.example.cookingapp.domain.model.*
import com.example.cookingapp.domain.usecase.FinalizeCookSessionUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CookUiState(
    val recipe: Recipe? = null,
    val scaledServings: Int = 1,
    val usageInputs: List<IngredientUsageInput> = emptyList(),
    val isFinishing: Boolean = false,
    val finishError: String? = null,
    val finishSuccess: Boolean = false
)

class CookViewModel(
    private val recipeRepo: RecipeRepository,
    private val finalizeCookSessionUseCase: FinalizeCookSessionUseCase,
    private val recipeId: String
) : ViewModel() {

    private val _state = MutableStateFlow(CookUiState())
    val state: StateFlow<CookUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val recipe = recipeRepo.getFullRecipe(recipeId) ?: return@launch
            _state.update {
                it.copy(
                    recipe = recipe,
                    scaledServings = recipe.servings,
                    usageInputs = buildDefaultUsages(recipe, recipe.servings, recipe.servings)
                )
            }
        }
    }

    fun updateServings(newServings: Int) {
        val recipe = _state.value.recipe ?: return
        _state.update {
            it.copy(
                scaledServings = newServings,
                usageInputs = buildDefaultUsages(recipe, newServings, recipe.servings)
            )
        }
    }

    fun updateUsage(index: Int, updated: IngredientUsageInput) {
        _state.update { state ->
            state.copy(usageInputs = state.usageInputs.toMutableList().also { it[index] = updated })
        }
    }

    fun finishCooking(userId: String) = viewModelScope.launch {
        val state = _state.value
        val recipe = state.recipe ?: return@launch
        _state.update { it.copy(isFinishing = true, finishError = null) }
        val payload = CookSessionPayload(
            userId = userId,
            recipeId = recipe.id,
            scaledServings = state.scaledServings,
            usages = state.usageInputs
        )
        finalizeCookSessionUseCase(payload).fold(
            onSuccess = { _state.update { it.copy(isFinishing = false, finishSuccess = true) } },
            onFailure = { err -> _state.update { it.copy(isFinishing = false, finishError = err.message) } }
        )
    }

    private fun buildDefaultUsages(recipe: Recipe, scaledServings: Int, baseServings: Int): List<IngredientUsageInput> {
        val scale = scaledServings.toDouble() / baseServings.toDouble()
        return recipe.ingredients.map { ing ->
            IngredientUsageInput(
                recipeIngredientId = ing.id,
                ingredientName = ing.name,
                plannedQuantity = ing.quantity * scale,
                actualQuantity = ing.quantity * scale,
                status = UsageStatus.USED,
                pantryItemId = null
            )
        }
    }
}