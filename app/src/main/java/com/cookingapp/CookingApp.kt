package com.example.cookingapp

import android.app.Application
import com.example.cookingapp.di.AppContainer

class CookingApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(applicationContext)
    }
}