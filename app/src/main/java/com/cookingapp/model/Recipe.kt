package com.cookingapp.model

/**
 * Represents a recipe result from search.
 * Intentionally simple for this checkpoint — will become a Room entity later.
 */
data class Recipe(
    val id: Int,
    val title: String,
    val category: String,
    val servings: Int
)
