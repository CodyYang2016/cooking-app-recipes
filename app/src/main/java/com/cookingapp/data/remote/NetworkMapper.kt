package com.example.cookingapp.data.remote

import com.example.cookingapp.data.entity.*
import java.util.UUID

object NetworkMapper {

    fun MealDto.toRecipeEntity(userId: String): RecipeEntity = RecipeEntity(
        id = "api_$id",
        userId = userId,
        title = title,
        imageUrl = imageUrl,
        servings = 4,                           // TheMealDB doesn't provide servings
        sourceType = SourceType.API
    )

    fun MealDto.toIngredientEntities(recipeId: String): List<RecipeIngredientEntity> {
        // Collect non-null pairs from the DTO's numbered fields
        val pairs = listOfNotNull(
            ing1?.takeIf { it.isNotBlank() }?.let { it to (meas1 ?: "") },
            ing2?.takeIf { it.isNotBlank() }?.let { it to (meas2 ?: "") },
            // TODO: extend for all 20 pairs
        )
        return pairs.map { (name, measure) ->
            val (qty, unit) = parseMeasure(measure)
            RecipeIngredientEntity(recipeId = recipeId, name = name, quantity = qty, unit = unit)
        }
    }

    fun MealDto.toStepEntities(recipeId: String): List<RecipeStepEntity> =
        instructions
            ?.split("\n")
            ?.filter { it.isNotBlank() }
            ?.mapIndexed { i, line ->
                RecipeStepEntity(recipeId = recipeId, stepNumber = i + 1, instruction = line.trim())
            } ?: emptyList()

    private fun parseMeasure(measure: String): Pair<Double, String> {
        val parts = measure.trim().split(" ", limit = 2)
        val qty = parts.getOrNull(0)?.toDoubleOrNull() ?: 1.0
        val unit = parts.getOrNull(1) ?: ""
        return qty to unit
    }
}