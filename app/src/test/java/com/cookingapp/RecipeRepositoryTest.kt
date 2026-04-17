package com.cookingapp.data.repository

import com.cookingapp.data.AppDatabase
import com.cookingapp.data.dao.PantryItemDao
import com.cookingapp.model.PantryItem
import com.cookingapp.network.IngredientSearchResponse
import com.cookingapp.network.SpoonacularApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeRepositoryTest {

    @Mock
    private lateinit var database: AppDatabase

    @Mock
    private lateinit var pantryItemDao: PantryItemDao

    @Mock
    private lateinit var api: SpoonacularApi

    private lateinit var repository: RecipeRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(database.pantryItemDao()).thenReturn(pantryItemDao)
        repository = RecipeRepository(database)

        // Replace the api with our mock using reflection
        val apiField = RecipeRepository::class.java.getDeclaredField("api")
        apiField.isAccessible = true
        apiField.set(repository, api)
    }

    @Test
    fun searchRecipesByIngredients_withPantryItems_returnsMappedRecipes() = runTest {
        // Arrange: Create sample pantry items
        val pantryItems = listOf(
            PantryItem(name = "chicken", quantity = 2.0, unit = "pieces"),
            PantryItem(name = "rice", quantity = 1.0, unit = "cup"),
            PantryItem(name = "salt", quantity = 5.0, unit = "grams")
        )
        `when`(pantryItemDao.getAllItemsOnce()).thenReturn(pantryItems)

        // Arrange: Mock API response
        val apiResponse = listOf(
            IngredientSearchResponse(
                id = 1,
                title = "Chicken Rice",
                image = "chicken_rice.jpg",
                missedIngredientCount = 0,
                usedIngredientCount = 3
            ),
            IngredientSearchResponse(
                id = 2,
                title = "Grilled Chicken",
                image = "grilled_chicken.jpg",
                missedIngredientCount = 1,
                usedIngredientCount = 2
            )
        )
        `when`(api.searchByIngredients("chicken,rice,salt", 20, true)).thenReturn(apiResponse)

        // Act
        val result = repository.searchRecipesByIngredients()

        // Assert: Check results size
        assertEquals(2, result.size)

        // Assert: Check first recipe mapping
        assertEquals(1, result[0].id)
        assertEquals("Chicken Rice", result[0].title)
        assertEquals("chicken_rice.jpg", result[0].imageUrl)

        // Assert: Check second recipe mapping
        assertEquals(2, result[1].id)
        assertEquals("Grilled Chicken", result[1].title)
        assertEquals("grilled_chicken.jpg", result[1].imageUrl)

        // Assert: Verify API was called with correct ingredient string
        verify(api).searchByIngredients("chicken,rice,salt", 20, true)
    }

    @Test
    fun searchRecipesByIngredients_whenApiFails_returnsEmptyList() = runTest {
        // Arrange: Create sample pantry items
        val pantryItems = listOf(
            PantryItem(name = "tomato", quantity = 1.0, unit = "piece"),
            PantryItem(name = "cheese", quantity = 100.0, unit = "grams")
        )
        `when`(pantryItemDao.getAllItemsOnce()).thenReturn(pantryItems)

        // Arrange: Mock API to throw exception
        `when`(api.searchByIngredients("tomato,cheese", 20, true))
            .thenThrow(RuntimeException("Network error"))

        // Act
        val result = repository.searchRecipesByIngredients()

        // Assert: Should return empty list on failure
        assertTrue(result.isEmpty())

        // Assert: Verify API was attempted
        verify(api).searchByIngredients("tomato,cheese", 20, true)
    }
}