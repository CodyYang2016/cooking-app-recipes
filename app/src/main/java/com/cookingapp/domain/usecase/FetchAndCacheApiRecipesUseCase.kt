package com.example.cookingapp.domain.usecase

import com.example.cookingapp.data.repository.RecipeRepository
import javax.inject.Inject

class FetchAndCacheApiRecipesUseCase @Inject constructor(
    private val recipeRepo: RecipeRepository
) {
    suspend operator fun invoke(query: String, userId: String): Result<Unit> =
        runCatching { recipeRepo.fetchAndCacheFromApi(query, userId) }
}