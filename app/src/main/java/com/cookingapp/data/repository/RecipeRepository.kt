package com.example.cookingapp.data.repository

import com.example.cookingapp.data.dao.RecipeDao
import com.example.cookingapp.data.entity.*
import com.example.cookingapp.data.remote.NetworkMapper.toIngredientEntities
import com.example.cookingapp.data.remote.NetworkMapper.toRecipeEntity
import com.example.cookingapp.data.remote.NetworkMapper.toStepEntities
import com.example.cookingapp.data.remote.RecipeApiService
import com.example.cookingapp.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface RecipeRepository {
    fun observeAll(userId: String): Flow<List<Recipe>>
    suspend fun getFullRecipe(id: String): Recipe?
    suspend fun upsertFullRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipe: Recipe)
    suspend fun fetchAndCacheFromApi(query: String, userId: String)
}

@Singleton
class RecipeRepositoryImpl @Inject constructor(
    private val dao: RecipeDao,
    private val api: RecipeApiService
) : RecipeRepository {

    override fun observeAll(userId: String): Flow<List<Recipe>> =
        dao.observeAll(userId).map { list -> list.map { it.toDomain() } }

    override suspend fun getFullRecipe(id: String): Recipe? {
        val entity = dao.getById(id) ?: return null
        val ingredients = dao.getIngredients(id).map { it.toDomain() }
        val steps = dao.getSteps(id).map { it.toDomain() }
        return entity.toDomain(ingredients, steps)
    }

    override suspend fun upsertFullRecipe(recipe: Recipe) {
        dao.upsertFull(recipe.toEntity(), recipe.ingredients.map { it.toEntity() }, recipe.steps.map { it.toEntity() })
    }

    override suspend fun deleteRecipe(recipe: Recipe) = dao.deleteRecipe(recipe.toEntity())

    override suspend fun fetchAndCacheFromApi(query: String, userId: String) {
        val meals = api.searchRecipes(query).meals ?: return
        meals.forEach { dto ->
            val recipeEntity = dto.toRecipeEntity(userId)
            val ingredients = dto.toIngredientEntities(recipeEntity.id)
            val steps = dto.toStepEntities(recipeEntity.id)
            dao.upsertFull(recipeEntity, ingredients, steps)
        }
    }

    // --- Mappers ---

    private fun RecipeEntity.toDomain(
        ingredients: List<RecipeIngredient> = emptyList(),
        steps: List<RecipeStep> = emptyList()
    ) = Recipe(id, userId, title, imageUrl, servings, sourceType, ingredients, steps)

    private fun Recipe.toEntity() = RecipeEntity(id, userId, title, imageUrl, servings, sourceType)

    private fun RecipeIngredientEntity.toDomain() =
        RecipeIngredient(id, recipeId, name, quantity, unit)

    private fun RecipeIngredient.toEntity() =
        RecipeIngredientEntity(id, recipeId, name, quantity, unit)

    private fun RecipeStepEntity.toDomain() = RecipeStep(id, recipeId, stepNumber, instruction)
    private fun RecipeStep.toEntity() = RecipeStepEntity(id, recipeId, stepNumber, instruction)
}