package com.cookingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.cookingapp.CookingApplication
import com.cookingapp.data.repository.PantryRepository
import com.cookingapp.model.PantryItem
import kotlinx.coroutines.launch

class PantryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PantryRepository =
        (application as CookingApplication).pantryRepository

    // LiveData for UI to observe
    val allPantryItems: LiveData<List<PantryItem>> = repository.allPantryItemsLiveData

    // CRUD Operations
    fun insertItem(name: String, quantity: Double, unit: String) {
        viewModelScope.launch {
            val item = PantryItem(name = name, quantity = quantity, unit = unit)
            repository.insert(item)
        }
    }

    fun updateItem(item: PantryItem) {
        viewModelScope.launch {
            repository.update(item)
        }
    }

    fun deleteItem(item: PantryItem) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

    fun updateQuantity(item: PantryItem, newQuantity: Double) {
        viewModelScope.launch {
            val updatedItem = item.copy(quantity = newQuantity)
            repository.update(updatedItem)
        }
    }

    // For demo/testing - add sample data
    fun addSampleData() {
        viewModelScope.launch {
            repository.initializeSampleData()
        }
    }

    // Clear all data (for testing)
    fun clearAll() {
        viewModelScope.launch {
            // You'd need a deleteAll method in repository
        }
    }
}