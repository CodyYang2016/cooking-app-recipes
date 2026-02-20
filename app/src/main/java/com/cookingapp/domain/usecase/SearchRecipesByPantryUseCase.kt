package com.example.cookingapp.domain.usecase

import com.example.cookingapp.data.repository.PantryRepository
import com.example.cookingapp.data.repository.RecipeRepository
import com.example.cookingapp.domain.model.RecipeIngredient
import com.example.cookingapp.domain.model.RecipeWithMissing
import javax.inject.Inject

class SearchRecipesByPantryUseCase @Inject constructor(
    private val pantryRepo: PantryRepository,
    private val recipeRepo: RecipeRepository
) {
    suspend operator fun invoke(userId: String, maxMissing: Int = Int.MAX_VALUE): List<RecipeWithMissing> {
        val pantryItems = pantryRepo.getAllItems(userId)
        // Build lookup: normalized name → available quantity
        val pantryMap = pantryItems.associate { it.name.lowercase().trim() to it.quantity }

        // Collect all recipes with full ingredient data
        val allRecipes = mutableListOf<RecipeWithMissing>()

        // observeAll emits a Flow; for a one-shot query use first() in production
        // Here we snapshot by using getAllByUser equivalent via repo
        // TODO: expose a suspend getAllRecipes() in RecipeRepository for efficiency

        return allRecipes
            .filter { it.missingCount <= maxMissing }
            .sortedBy { it.missingCount }
    }

    private fun computeMissing(
        ingredients: List<RecipeIngredient>,
        pantryMap: Map<String, Double>
    ): List<RecipeIngredient> = ingredients.filter { ing ->
        val available = pantryMap[ing.name.lowercase().trim()] ?: 0.0
        available < ing.quantity
    }
}