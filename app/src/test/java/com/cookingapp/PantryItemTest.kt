package com.cookingapp.model

import org.junit.Assert.*
import org.junit.Test

class PantryItemTest {

    @Test
    fun testPantryItemCreation() {
        val item = PantryItem(
            name = "Milk",
            quantity = 2.5,
            unit = "liters"
        )

        assertEquals("Milk", item.name)
        assertEquals(2.5, item.quantity, 0.001)
        assertEquals("liters", item.unit)
    }

    @Test
    fun testPantryItemWithDefaultId() {
        val item = PantryItem(
            name = "Eggs",
            quantity = 12.0,
            unit = "pieces"
        )

        assertEquals(0L, item.id)
    }

    @Test
    fun testPantryItemWithCustomId() {
        val item = PantryItem(
            id = 99L,
            name = "Flour",
            quantity = 1.0,
            unit = "kg"
        )

        assertEquals(99L, item.id)
        assertEquals("Flour", item.name)
    }

    @Test
    fun testPantryItemCopy() {
        val original = PantryItem(
            name = "Butter",
            quantity = 250.0,
            unit = "grams"
        )

        val copied = original.copy(quantity = 500.0)

        assertEquals("Butter", copied.name)
        assertEquals(500.0, copied.quantity, 0.001)
        assertEquals("grams", copied.unit)
        assertNotEquals(original.quantity, copied.quantity)
    }
}