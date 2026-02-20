package com.example.cookingapp.domain.usecase

import com.example.cookingapp.data.entity.SourceType
import com.example.cookingapp.data.repository.RecipeRepository
import com.example.cookingapp.domain.model.Recipe
import com.example.cookingapp.domain.model.RecipeIngredient
import com.example.cookingapp.domain.model.RecipeStep
import java.util.UUID
import javax.inject.Inject

class ImportRecipeFromUrlUseCase @Inject constructor(
    private val recipeRepo: RecipeRepository
) {
    /**
     * TODO: Integrate a proper recipe scraper library or API.
     * For MVP, this accepts a pre-parsed Recipe and persists it with sourceUrl.
     */
    suspend operator fun invoke(
        url: String,
        userId: String,
        parsedTitle: String,
        parsedIngredients: List<RecipeIngredient>,
        parsedSteps: List<RecipeStep>,
        servings: Int
    ): Result<Unit> = runCatching {
        val recipe = Recipe(
            id = UUID.randomUUID().toString(),
            userId = userId,
            title = parsedTitle,
            servings = servings,
            sourceType = SourceType.URL,
            ingredients = parsedIngredients,
            steps = parsedSteps
        )
        recipeRepo.upsertFullRecipe(recipe)
    }
}