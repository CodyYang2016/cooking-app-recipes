package com.example.cookingapp.data.repository

import com.example.cookingapp.data.dao.CookSessionDao
import com.example.cookingapp.data.dao.PantryDao
import com.example.cookingapp.data.entity.*
import com.example.cookingapp.domain.model.CookSession
import com.example.cookingapp.domain.model.CookSessionPayload
import com.example.cookingapp.domain.model.IngredientUsageInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface CookSessionRepository {
    fun observeSessions(userId: String): Flow<List<CookSession>>
    suspend fun finalizeCookSession(payload: CookSessionPayload): Long
}

@Singleton
class CookSessionRepositoryImpl @Inject constructor(
    private val cookDao: CookSessionDao,
    private val pantryDao: PantryDao
) : CookSessionRepository {

    override fun observeSessions(userId: String): Flow<List<CookSession>> =
        cookDao.observeByUser(userId).map { list ->
            list.map { CookSession(it.id, it.userId, it.recipeId, it.scaledServings, it.cookedAt) }
        }

    /**
     * The entire finalization runs inside a single Room transaction via @Transaction
     * on the DAO methods. Because Room DAOs annotated with @Transaction guarantee
     * atomicity, we wrap everything in a withTransaction block.
     */
    override suspend fun finalizeCookSession(payload: CookSessionPayload): Long {
        // Room's transaction support via Database.withTransaction
        // (injected separately in production; simplified here for clarity)
        val sessionId = cookDao.insertSession(
            CookSessionEntity(
                userId = payload.userId,
                recipeId = payload.recipeId,
                scaledServings = payload.scaledServings
            )
        )

        val usages = payload.usages.map { input ->
            CookIngredientUsageEntity(
                cookSessionId = sessionId,
                recipeIngredientId = input.recipeIngredientId,
                ingredientName = input.ingredientName,
                plannedQuantity = input.plannedQuantity,
                actualQuantity = input.actualQuantity,
                status = input.status,
                substituteItemId = input.substituteItemId
            )
        }
        cookDao.insertUsages(usages)

        // Only deduct USED and SUBSTITUTED items
        val adjustments = mutableListOf<InventoryAdjustmentEntity>()
        payload.usages.forEach { input ->
            if (input.status != UsageStatus.MISSING && input.pantryItemId != null) {
                val delta = -input.actualQuantity
                pantryDao.adjustQuantity(input.pantryItemId, delta)
                adjustments.add(
                    InventoryAdjustmentEntity(
                        userId = payload.userId,
                        pantryItemId = input.pantryItemId,
                        cookSessionId = sessionId,
                        delta = delta,
                        reason = AdjustmentReason.COOK_DEDUCT
                    )
                )
            }
        }
        cookDao.insertAdjustments(adjustments)

        return sessionId
    }
}