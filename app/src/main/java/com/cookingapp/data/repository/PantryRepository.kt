package com.cookingapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.cookingapp.data.AppDatabase
import com.cookingapp.model.PantryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PantryRepository(private val database: AppDatabase) {

    val allPantryItems: Flow<List<PantryItem>> = database.pantryItemDao().getAllItems()

    val allPantryItemsLiveData: LiveData<List<PantryItem>> =
        allPantryItems.asLiveData()

    suspend fun insert(item: PantryItem) {
        database.pantryItemDao().insert(item)
    }

    suspend fun update(item: PantryItem) {
        database.pantryItemDao().update(item)
    }

    suspend fun delete(item: PantryItem) {
        database.pantryItemDao().delete(item)
    }

    suspend fun deleteAll() {
        database.pantryItemDao().deleteAll()
    }

    suspend fun getItemById(id: Long): PantryItem? {
        return database.pantryItemDao().getItemById(id)
    }

    // Fixed: Use .first() instead of .toList() for single collection
    suspend fun getAllPantryItems(): List<PantryItem> {
        return database.pantryItemDao().getAllItems().first()
    }

    suspend fun deductIngredient(ingredientName: String, amountUsed: Double) {
        val allItems = getAllPantryItems()
        val matchingItem = allItems.find {
            it.name.equals(ingredientName, ignoreCase = true)
        }

        matchingItem?.let { item ->
            val newQuantity = item.quantity - amountUsed
            if (newQuantity > 0) {
                update(item.copy(quantity = newQuantity))
            } else {
                delete(item)
            }
        }
    }

    fun insertItem(item: PantryItem, onComplete: () -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            insert(item)
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    // Fixed: Use .first() to check if empty
    suspend fun initializeSampleData() {
        val currentItems = database.pantryItemDao().getAllItems().first()
        if (currentItems.isEmpty()) {
            val sampleItems = listOf(
                PantryItem(name = "Eggs", quantity = 12.0, unit = "units"),
                PantryItem(name = "Milk", quantity = 1.0, unit = "gallon"),
                PantryItem(name = "Bread", quantity = 1.0, unit = "loaf"),
                PantryItem(name = "Rice", quantity = 2.0, unit = "cups"),
                PantryItem(name = "Tomatoes", quantity = 5.0, unit = "units")
            )
            sampleItems.forEach { insert(it) }
        }
    }
}