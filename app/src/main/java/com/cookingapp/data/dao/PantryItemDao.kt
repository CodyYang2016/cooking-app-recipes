package com.cookingapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.cookingapp.model.PantryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PantryItemDao {
    @Insert
    suspend fun insert(item: PantryItem)

    @Update
    suspend fun update(item: PantryItem)

    @Delete
    suspend fun delete(item: PantryItem)

    @Query("SELECT * FROM pantry_items ORDER BY name ASC")
    fun getAllItems(): Flow<List<PantryItem>>

    @Query("SELECT * FROM pantry_items WHERE id = :id")
    suspend fun getItemById(id: Long): PantryItem?

    @Query("DELETE FROM pantry_items")
    suspend fun deleteAll()
}