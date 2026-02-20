package com.example.cookingapp.data.entity

import androidx.room.*
import java.time.Instant

@Entity(tableName = "pantry_items")
data class PantryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val name: String,
    val quantity: Double,
    val unit: String,
    val updatedAt: Long = Instant.now().epochSecond
)

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: String,           // UUID or API id
    val userId: String,
    val title: String,
    val imageUrl: String? = null,
    val servings: Int,
    val sourceType: SourceType,
    val sourceUrl: String? = null,
    val createdAt: Long = Instant.now().epochSecond
)

@Entity(
    tableName = "recipe_ingredients",
    foreignKeys = [ForeignKey(
        entity = RecipeEntity::class,
        parentColumns = ["id"],
        childColumns = ["recipeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("recipeId")]
)
data class RecipeIngredientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipeId: String,
    val name: String,
    val quantity: Double,
    val unit: String
)

@Entity(
    tableName = "recipe_steps",
    foreignKeys = [ForeignKey(
        entity = RecipeEntity::class,
        parentColumns = ["id"],
        childColumns = ["recipeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("recipeId")]
)
data class RecipeStepEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipeId: String,
    val stepNumber: Int,
    val instruction: String
)

@Entity(tableName = "cook_sessions")
data class CookSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val recipeId: String,
    val scaledServings: Int,
    val cookedAt: Long = Instant.now().epochSecond
)

@Entity(
    tableName = "cook_ingredient_usages",
    foreignKeys = [ForeignKey(
        entity = CookSessionEntity::class,
        parentColumns = ["id"],
        childColumns = ["cookSessionId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("cookSessionId")]
)
data class CookIngredientUsageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cookSessionId: Long,
    val recipeIngredientId: Long,
    val ingredientName: String,
    val plannedQuantity: Double,
    val actualQuantity: Double,
    val status: UsageStatus,
    val substituteItemId: Long? = null   // pantryItemId if SUBSTITUTED
)

@Entity(tableName = "inventory_adjustments")
data class InventoryAdjustmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val pantryItemId: Long,
    val cookSessionId: Long?,
    val delta: Double,                   // negative = deducted
    val reason: AdjustmentReason,
    val adjustedAt: Long = Instant.now().epochSecond
)