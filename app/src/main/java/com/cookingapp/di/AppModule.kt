package com.example.cookingapp.di

import android.content.Context
import com.example.cookingapp.auth.FirebaseAuthManager
import com.example.cookingapp.auth.SessionManager
import com.example.cookingapp.data.db.AppDatabase
import com.example.cookingapp.data.remote.RecipeApiService
import com.example.cookingapp.data.repository.*
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class AppContainer(context: Context) {

    private val db = AppDatabase.getInstance(context)

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.themealdb.com/api/json/v1/1/")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val apiService: RecipeApiService = retrofit.create(RecipeApiService::class.java)

    val sessionManager = SessionManager(context)
    val authManager = FirebaseAuthManager()

    val pantryRepository: PantryRepository = PantryRepositoryImpl(db.pantryDao())
    val recipeRepository: RecipeRepository = RecipeRepositoryImpl(db.recipeDao(), apiService)
    val cookSessionRepository: CookSessionRepository = CookSessionRepositoryImpl(db, db.cookSessionDao(), db.pantryDao())
}