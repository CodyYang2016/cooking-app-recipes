package com.example.cookingapp.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult {
    data class Success(val user: FirebaseUser) : AuthResult()
    data class Failure(val message: String) : AuthResult()
}

@Singleton
class FirebaseAuthManager @Inject constructor() {

    private val auth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun signIn(email: String, password: String): AuthResult = runCatching {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        AuthResult.Success(result.user!!)
    }.getOrElse { AuthResult.Failure(it.message ?: "Sign in failed") }

    suspend fun signUp(email: String, password: String): AuthResult = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        AuthResult.Success(result.user!!)
    }.getOrElse { AuthResult.Failure(it.message ?: "Sign up failed") }

    fun signOut() = auth.signOut()
}