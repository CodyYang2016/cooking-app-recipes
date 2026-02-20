package com.example.cookingapp.ui.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun RecipeDetailScreen(
    onStartCooking: (String) -> Unit,
    onBack: () -> Unit,
    vm: RecipeDetailViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val recipe = state.recipe

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe?.title ?: "Recipe") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            recipe?.let {
                Button(
                    onClick = { onStartCooking(it.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Start Cooking")
                }
            }
        }
    ) { padding ->
        if (state.isLoading || recipe == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(Modifier.padding(padding).padding(horizontal = 16.dp)) {

                // Image
                recipe.imageUrl?.let { url ->
                    item {
                        AsyncImage(
                            model = url,
                            contentDescription = recipe.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .padding(bottom = 16.dp)
                        )
                    }
                }

                // Servings info
                item {
                    Text("Serves ${recipe.servings}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(16.dp))
                    Text("Ingredients", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                }

                // Ingredients
                itemsIndexed(recipe.ingredients) { _, ingredient ->
                    Text("• ${ingredient.quantity} ${ingredient.unit} ${ingredient.name}")
                }

                // Steps
                item {
                    Spacer(Modifier.height(16.dp))
                    Text("Steps", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                }

                itemsIndexed(recipe.steps) { _, step ->
                    Column(Modifier.padding(bottom = 12.dp)) {
                        Text("Step ${step.stepNumber}", style = MaterialTheme.typography.labelLarge)
                        Text(step.instruction)
                    }
                }

                item { Spacer(Modifier.height(80.dp)) } // bottom bar spacing
            }
        }
    }
}