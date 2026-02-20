package com.example.cookingapp.data.dao

import androidx.room.*
import com.example.cookingapp.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CookSessionDao {

    @Query("SELECT * FROM cook_sessions WHERE userId = :userId ORDER BY cookedAt DESC")
    fun observeByUser(userId: String): Flow<List<CookSessionEntity>>

    @Insert
    suspend fun insertSession(session: CookSessionEntity): Long

    @Insert
    suspend fun insertUsages(usages: List<CookIngredientUsageEntity>)

    @Insert
    suspend fun insertAdjustments(adjustments: List<InventoryAdjustmentEntity>)

    @Query("SELECT * FROM cook_ingredient_usages WHERE cookSessionId = :sessionId")
    suspend fun getUsages(sessionId: Long): List<CookIngredientUsageEntity>

    @Query("SELECT * FROM inventory_adjustments WHERE cookSessionId = :sessionId")
    suspend fun getAdjustments(sessionId: Long): List<InventoryAdjustmentEntity>
}