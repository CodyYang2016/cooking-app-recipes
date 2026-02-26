package com.cookingapp.model

/**
 * Represents a single item in the user's pantry.
 * Intentionally simple for this checkpoint — will become a Room entity later.
 */
data class PantryItem(
    val id: Int,
    val name: String,
    val quantity: Double,
    val unit: String
)
