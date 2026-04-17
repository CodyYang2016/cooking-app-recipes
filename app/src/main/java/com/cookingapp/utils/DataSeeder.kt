package com.cookingapp.utils

import com.cookingapp.data.repository.PantryRepository
import com.cookingapp.model.PantryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataSeeder(private val pantryRepository: PantryRepository) {

    fun seedLargePantry() {
        CoroutineScope(Dispatchers.IO).launch {
            val ingredients = listOf(
                "Flour", "Sugar", "Eggs", "Milk", "Butter", "Rice", "Pasta",
                "Tomatoes", "Onions", "Garlic", "Olive Oil", "Salt", "Pepper",
                "Chicken", "Beef", "Potatoes", "Carrots", "Celery", "Cheese",
                "Bread", "Honey", "Vinegar", "Soy Sauce", "Cinnamon", "Vanilla",
                "Baking Soda", "Baking Powder", "Yeast", "Cocoa Powder", "Oats",
                "Yogurt", "Spinach", "Lettuce", "Cucumber", "Bell Peppers"
            )

            val units = listOf("units", "cups", "tbsp", "tsp", "grams", "lbs", "oz", "pieces")

            val allItems = mutableListOf<PantryItem>()

            ingredients.forEach { ingredient ->
                repeat(2) {
                    allItems.add(
                        PantryItem(
                            name = ingredient,
                            quantity = (1..20).random().toDouble(),
                            unit = units.random()
                        )
                    )
                }
            }

            allItems.forEach { pantryRepository.insert(it) }
            println("Seeded ${allItems.size} pantry items")
        }
    }
}