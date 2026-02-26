package com.cookingapp

import android.content.Context

// Simple container class for Checkpoint 2
class AppContainer(context: Context) {
    // This will hold dependencies later (Room DB, Retrofit, Repositories)
    // For now, it can be empty or have simple initialization

    init {
        // You can log creation to verify it works
        println("AppContainer created with context: $context")
    }

    // For Checkpoint 2, we don't need any actual dependencies yet
    // Just having this class exists makes the error go away
}