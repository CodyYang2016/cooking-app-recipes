package com.cookingapp.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpoonacularApi {

    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("query") query: String,
        @Query("number") number: Int = 20,
        @Query("addRecipeInformation") addRecipeInfo: Boolean = true
    ): RecipeSearchResponse

    @GET("recipes/findByIngredients")
    suspend fun searchByIngredients(
        @Query("ingredients") ingredients: String,
        @Query("number") number: Int = 20,
        @Query("ignorePantry") ignorePantry: Boolean = true
    ): List<IngredientSearchResponse>

    @GET("recipes/{id}/information")
    suspend fun getRecipeDetails(
        @Path("id") id: Int,
        @Query("includeNutrition") includeNutrition: Boolean = false
    ): RecipeDetailsResponse
}