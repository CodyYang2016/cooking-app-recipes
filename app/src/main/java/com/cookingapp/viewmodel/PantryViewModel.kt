package com.example.cookingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookingapp.auth.SessionManager
import com.example.cookingapp.data.repository.PantryRepository
import com.example.cookingapp.domain.model.PantryItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PantryUiState(
    val items: List<PantryItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class PantryViewModel(
    private val pantryRepo: PantryRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(PantryUiState())
    val state: StateFlow<PantryUiState> = _state.asStateFlow()

    private val userId = sessionManager.userIdFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        viewModelScope.launch {
            userId.filterNotNull().collectLatest { uid ->
                pantryRepo.observePantry(uid).collect { items ->
                    _state.update { it.copy(items = items) }
                }
            }
        }
    }

    fun addItem(name: String, quantity: Double, unit: String) = viewModelScope.launch {
        val uid = userId.value ?: return@launch
        pantryRepo.upsertItem(PantryItem(userId = uid, name = name, quantity = quantity, unit = unit))
    }

    fun updateItem(item: PantryItem) = viewModelScope.launch { pantryRepo.upsertItem(item) }
    fun deleteItem(item: PantryItem) = viewModelScope.launch { pantryRepo.deleteItem(item) }
}