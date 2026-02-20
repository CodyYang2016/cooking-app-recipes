package com.example.cookingapp.data.repository

import com.example.cookingapp.data.dao.PantryDao
import com.example.cookingapp.data.entity.PantryItemEntity
import com.example.cookingapp.domain.model.PantryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface PantryRepository {
    fun observePantry(userId: String): Flow<List<PantryItem>>
    suspend fun getAllItems(userId: String): List<PantryItem>
    suspend fun upsertItem(item: PantryItem)
    suspend fun deleteItem(item: PantryItem)
}

@Singleton
class PantryRepositoryImpl @Inject constructor(
    private val dao: PantryDao
) : PantryRepository {

    override fun observePantry(userId: String): Flow<List<PantryItem>> =
        dao.observeByUser(userId).map { list -> list.map { it.toDomain() } }

    override suspend fun getAllItems(userId: String): List<PantryItem> =
        dao.getAllByUser(userId).map { it.toDomain() }

    override suspend fun upsertItem(item: PantryItem) =
        dao.upsert(item.toEntity()).let {}

    override suspend fun deleteItem(item: PantryItem) =
        dao.delete(item.toEntity())

    // Mappers
    private fun PantryItemEntity.toDomain() = PantryItem(id, userId, name, quantity, unit)
    private fun PantryItem.toEntity() = PantryItemEntity(id, userId, name, quantity, unit)
}