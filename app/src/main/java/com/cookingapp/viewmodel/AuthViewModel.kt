package com.example.cookingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookingapp.auth.AuthResult
import com.example.cookingapp.auth.FirebaseAuthManager
import com.example.cookingapp.auth.SessionManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

class AuthViewModel(
    private val authManager: FirebaseAuthManager,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    val userId = sessionManager.userIdFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        authManager.currentUser?.let { user ->
            viewModelScope.launch { sessionManager.saveUserId(user.uid) }
            _state.value = AuthUiState(isAuthenticated = true)
        }
    }

    fun signIn(email: String, password: String) = viewModelScope.launch {
        _state.value = AuthUiState(isLoading = true)
        when (val result = authManager.signIn(email, password)) {
            is AuthResult.Success -> {
                sessionManager.saveUserId(result.user.uid)
                _state.value = AuthUiState(isAuthenticated = true)
            }
            is AuthResult.Failure -> _state.value = AuthUiState(error = result.message)
        }
    }

    fun signUp(email: String, password: String) = viewModelScope.launch {
        _state.value = AuthUiState(isLoading = true)
        when (val result = authManager.signUp(email, password)) {
            is AuthResult.Success -> {
                sessionManager.saveUserId(result.user.uid)
                _state.value = AuthUiState(isAuthenticated = true)
            }
            is AuthResult.Failure -> _state.value = AuthUiState(error = result.message)
        }
    }

    fun signOut() = viewModelScope.launch {
        authManager.signOut()
        sessionManager.clearSession()
        _state.value = AuthUiState(isAuthenticated = false)
    }
}