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
        val currentItems = database.pantryItemDao().getAllItems()
        if (currentItems is kotlinx.coroutines.flow.Flow &&
            currentItems is java.util.Collection<*> &&
            currentItems.isEmpty()) {

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