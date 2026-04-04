package com.cookingapp.data.repository

import androidx.lifecycle.LiveData          // Add back

import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.cookingapp.data.AppDatabase
import com.cookingapp.model.PantryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PantryRepository(private val database: AppDatabase) {

    // Using Flow for real-time updates
    val allPantryItems: Flow<List<PantryItem>> = database.pantryItemDao().getAllItems()

    // Convert Flow to LiveData for easier UI observation
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

    suspend fun getItemById(id: Long): PantryItem? {
        return database.pantryItemDao().getItemById(id)
    }

    // Add this function
    suspend fun getAllPantryItems(): List<PantryItem> {
        return database.pantryItemDao().getAllItemsOnce()
    }

    // Convenience method for coroutines
    fun insertItem(item: PantryItem, onComplete: () -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            insert(item)
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    // Initialize with sample data (for testing)
    suspend fun initializeSampleData() {
        val count = database.pantryItemDao().getItemCount()
        if (count == 0) {
            val sampleItems = listOf(
                PantryItem(name = "Eggs", quantity = 12.0, unit = "units"),
                PantryItem(name = "Chicken Breast", quantity = 2.0, unit = "lbs"),
                PantryItem(name = "Ground Beef", quantity = 1.0, unit = "lbs"),
                PantryItem(name = "Bacon", quantity = 8.0, unit = "strips"),
                PantryItem(name = "Black Pepper", quantity = 1.0, unit = "container")
            )
            sampleItems.forEach { insert(it) }
        }
    }

    suspend fun deductIngredient(name: String, amount: Double) {
        val items = database.pantryItemDao().getAllItemsOnce()
        val match = items.find { it.name.lowercase() == name.lowercase() }
        if (match != null) {
            val newQuantity = (match.quantity - amount).coerceAtLeast(0.0)
            database.pantryItemDao().update(match.copy(quantity = newQuantity))
        }
    }
}