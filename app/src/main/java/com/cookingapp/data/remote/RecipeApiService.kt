package com.example.cookingapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// TODO: Replace base URL with your chosen free API (e.g. TheMealDB, Spoonacular free tier)
// Base URL: https://www.themealdb.com/api/json/v1/1/

interface RecipeApiService {

    @GET("search.php")
    suspend fun searchRecipes(@Query("s") query: String): MealListResponse

    @GET("lookup.php")
    suspend fun getRecipeById(@Query("i") id: String): MealListResponse
}

@Serializable
data class MealListResponse(
    @SerialName("meals") val meals: List<MealDto>? = null
)

@Serializable
data class MealDto(
    @SerialName("idMeal") val id: String,
    @SerialName("strMeal") val title: String,
    @SerialName("strMealThumb") val imageUrl: String? = null,
    @SerialName("strCategory") val category: String? = null,
    // TheMealDB encodes ingredients as strIngredient1..20 / strMeasure1..20
    // For brevity we parse them in the mapper
    @SerialName("strInstructions") val instructions: String? = null,
    @SerialName("strIngredient1") val ing1: String? = null,
    @SerialName("strMeasure1") val meas1: String? = null,
    @SerialName("strIngredient2") val ing2: String? = null,
    @SerialName("strMeasure2") val meas2: String? = null,
    // ... repeat through 20 or use reflection/custom deserializer in production
)