package com.cookingapp.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.Serializable as JavaSerializable

@Serializable
data class RecipeSearchResponse(
    val results: List<ApiRecipe>,
    val offset: Int,
    val number: Int,
    val totalResults: Int
)

@Serializable
data class ApiRecipe(
    val id: Int,
    val title: String,
    @SerialName("image")
    val imageUrl: String?,
    @SerialName("imageType")
    val imageType: String?,
    @SerialName("readyInMinutes")
    val readyInMinutes: Int?,
    @SerialName("servings")
    val servings: Int?,
    @SerialName("sourceUrl")
    val sourceUrl: String?
) : JavaSerializable

@Serializable
data class IngredientSearchResponse(
    val id: Int,
    val title: String,
    val image: String?,
    @SerialName("missedIngredientCount")
    val missedIngredientCount: Int,
    @SerialName("usedIngredientCount")
    val usedIngredientCount: Int
)

@Serializable
data class RecipeDetailsResponse(
    val id: Int,
    val title: String,
    val image: String?,
    @SerialName("readyInMinutes")
    val readyInMinutes: Int,
    val servings: Int,
    val instructions: String?,
    @SerialName("extendedIngredients")
    val extendedIngredients: List<Ingredient>
)

@Serializable
data class Ingredient(
    val id: Int,
    val name: String,
    val amount: Double,
    val unit: String
)