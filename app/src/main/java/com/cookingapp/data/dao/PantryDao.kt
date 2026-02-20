package com.example.cookingapp.data.dao

import androidx.room.*
import com.example.cookingapp.data.entity.PantryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PantryDao {

    @Query("SELECT * FROM pantry_items WHERE userId = :userId ORDER BY name ASC")
    fun observeByUser(userId: String): Flow<List<PantryItemEntity>>

    @Query("SELECT * FROM pantry_items WHERE userId = :userId")
    suspend fun getAllByUser(userId: String): List<PantryItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: PantryItemEntity): Long

    @Update
    suspend fun update(item: PantryItemEntity)

    @Delete
    suspend fun delete(item: PantryItemEntity)

    @Query("UPDATE pantry_items SET quantity = quantity + :delta WHERE id = :id")
    suspend fun adjustQuantity(id: Long, delta: Double)
}