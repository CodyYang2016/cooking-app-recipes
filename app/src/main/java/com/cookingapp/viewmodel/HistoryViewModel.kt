package com.example.cookingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookingapp.auth.SessionManager
import com.example.cookingapp.data.repository.CookSessionRepository
import com.example.cookingapp.domain.model.CookSession
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HistoryUiState(
    val sessions: List<CookSession> = emptyList(),
    val isLoading: Boolean = false
)

class HistoryViewModel(
    private val cookSessionRepo: CookSessionRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryUiState())
    val state: StateFlow<HistoryUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.userIdFlow.filterNotNull().collectLatest { uid ->
                cookSessionRepo.observeSessions(uid).collect { sessions ->
                    _state.update { it.copy(sessions = sessions, isLoading = false) }
                }
            }
        }
    }
}