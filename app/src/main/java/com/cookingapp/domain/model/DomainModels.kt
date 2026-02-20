package com.example.cookingapp.domain.model

import com.example.cookingapp.data.entity.AdjustmentReason
import com.example.cookingapp.data.entity.SourceType
import com.example.cookingapp.data.entity.UsageStatus

data class PantryItem(
    val id: Long = 0,
    val userId: String,
    val name: String,
    val quantity: Double,
    val unit: String
)

data class Recipe(
    val id: String,
    val userId: String,
    val title: String,
    val imageUrl: String? = null,
    val servings: Int,
    val sourceType: SourceType,
    val ingredients: List<RecipeIngredient> = emptyList(),
    val steps: List<RecipeStep> = emptyList()
)

data class RecipeIngredient(
    val id: Long = 0,
    val recipeId: String,
    val name: String,
    val quantity: Double,
    val unit: String
)

data class RecipeStep(
    val id: Long = 0,
    val recipeId: String,
    val stepNumber: Int,
    val instruction: String
)

data class RecipeWithMissing(
    val recipe: Recipe,
    val missingCount: Int,
    val missingIngredients: List<RecipeIngredient>
)

// Payload passed to FinalizeCookSessionUseCase
data class CookSessionPayload(
    val userId: String,
    val recipeId: String,
    val scaledServings: Int,
    val usages: List<IngredientUsageInput>
)

data class IngredientUsageInput(
    val recipeIngredientId: Long,
    val ingredientName: String,
    val plannedQuantity: Double,
    val actualQuantity: Double,
    val status: UsageStatus,
    val pantryItemId: Long?,           // which pantry item to deduct from
    val substituteItemId: Long? = null
)

data class CookSession(
    val id: Long,
    val userId: String,
    val recipeId: String,
    val scaledServings: Int,
    val cookedAt: Long
)