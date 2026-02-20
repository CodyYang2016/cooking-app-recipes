package com.example.cookingapp.ui.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cookingapp.domain.model.RecipeWithMissing
import com.example.cookingapp.viewmodel.RecipeViewModel

@Composable
fun RecipeSearchScreen(
    onRecipeSelected: (String) -> Unit,
    vm: RecipeViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Find Recipes") }) }
    ) { padding ->
        Column(Modifier.padding(padding).padding(horizontal = 16.dp)) {

            // Search bar to fetch from API
            Row(
                Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { vm.updateSearchQuery(it) },
                    label = { Text("Search online recipes") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { vm.fetchFromApi(state.searchQuery) },
                    enabled = state.searchQuery.isNotBlank() && !state.isFetching
                ) {
                    if (state.isFetching) CircularProgressIndicator(Modifier.size(20.dp))
                    else Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.recipes.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No recipes found. Try searching online or add more pantry items.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.recipes, key = { it.recipe.id }) { recipeWithMissing ->
                        RecipeCard(
                            recipeWithMissing = recipeWithMissing,
                            onClick = { onRecipeSelected(recipeWithMissing.recipe.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeCard(recipeWithMissing: RecipeWithMissing, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(recipeWithMissing.recipe.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            if (recipeWithMissing.missingCount == 0) {
                Text(
                    "✓ You have all ingredients",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    "Missing ${recipeWithMissing.missingCount} ingredient(s): " +
                            recipeWithMissing.missingIngredients.joinToString(", ") { it.name },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}