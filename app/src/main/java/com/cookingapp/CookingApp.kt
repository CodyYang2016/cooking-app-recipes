package com.cookingapp

import android.app.Application

class CookingApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(applicationContext)
    }
}