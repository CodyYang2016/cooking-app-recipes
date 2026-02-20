package com.example.cookingapp.ui.cook

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cookingapp.data.entity.UsageStatus
import com.example.cookingapp.domain.model.IngredientUsageInput
import com.example.cookingapp.viewmodel.AuthViewModel
import com.example.cookingapp.viewmodel.CookViewModel

@Composable
fun CookScreen(
    onFinished: () -> Unit,
    onBack: () -> Unit,
    vm: CookViewModel = hiltViewModel(),
    authVm: AuthViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val userId by authVm.userId.collectAsState()

    LaunchedEffect(state.finishSuccess) { if (state.finishSuccess) onFinished() }

    Scaffold(
        topBar = { TopAppBar(title = { Text(state.recipe?.title ?: "Cook") }) },
        bottomBar = {
            Button(
                onClick = { vm.finishCooking(userId ?: return@Button) },
                enabled = !state.isFinishing,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                if (state.isFinishing) CircularProgressIndicator(Modifier.size(18.dp))
                else Text("Finish Cooking")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {

            item {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text("Servings: ")
                    IconButton(onClick = { if (state.scaledServings > 1) vm.updateServings(state.scaledServings - 1) }) { Text("-") }
                    Text("${state.scaledServings}")
                    IconButton(onClick = { vm.updateServings(state.scaledServings + 1) }) { Text("+") }
                }
                Spacer(Modifier.height(16.dp))
            }

            itemsIndexed(state.usageInputs) { index, usage ->
                IngredientUsageRow(
                    usage = usage,
                    onUpdate = { vm.updateUsage(index, it) }
                )
                Divider()
            }

            state.finishError?.let {
                item { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@Composable
fun IngredientUsageRow(usage: IngredientUsageInput, onUpdate: (IngredientUsageInput) -> Unit) {
    var qty by remember(usage) { mutableStateOf(usage.actualQuantity.toString()) }

    Column(Modifier.padding(vertical = 8.dp)) {
        Text(usage.ingredientName, style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = qty,
                onValueChange = { raw ->
                    qty = raw
                    raw.toDoubleOrNull()?.let { d -> onUpdate(usage.copy(actualQuantity = d)) }
                },
                label = { Text("Used qty") },
                modifier = Modifier.weight(1f)
            )
            // Status dropdown
            var expanded by remember { mutableStateOf(false) }
            Box {
                OutlinedButton(onClick = { expanded = true }) { Text(usage.status.name) }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    UsageStatus.entries.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.name) },
                            onClick = { onUpdate(usage.copy(status = status)); expanded = false }
                        )
                    }
                }
            }
        }
    }
}