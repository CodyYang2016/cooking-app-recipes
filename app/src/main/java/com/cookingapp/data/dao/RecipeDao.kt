package com.example.cookingapp.data.dao

import androidx.room.*
import com.example.cookingapp.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes WHERE userId = :userId OR sourceType != 'USER' ORDER BY title ASC")
    fun observeAll(userId: String): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getById(id: String): RecipeEntity?

    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    suspend fun getIngredients(recipeId: String): List<RecipeIngredientEntity>

    @Query("SELECT * FROM recipe_steps WHERE recipeId = :recipeId ORDER BY stepNumber ASC")
    suspend fun getSteps(recipeId: String): List<RecipeStepEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecipe(recipe: RecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertIngredients(ingredients: List<RecipeIngredientEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSteps(steps: List<RecipeStepEntity>)

    @Transaction
    suspend fun upsertFull(
        recipe: RecipeEntity,
        ingredients: List<RecipeIngredientEntity>,
        steps: List<RecipeStepEntity>
    ) {
        upsertRecipe(recipe)
        upsertIngredients(ingredients)
        upsertSteps(steps)
    }

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)
}