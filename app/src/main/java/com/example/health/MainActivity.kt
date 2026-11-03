package com.example.health

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// Modelo de datos para cada registro de alimento
data class FoodItem(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CalorieTrackerApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieTrackerApp() {
    // Estado reactivo de la lista y meta diaria
    val foodList = remember { mutableStateListOf<FoodItem>() }
    var targetCalories by remember { mutableStateOf(2000) }
    var showTargetDialog by remember { mutableStateOf(false) }

    // Campos del formulario
    var nameInput by remember { mutableStateOf("") }
    var caloriesInput by remember { mutableStateOf("") }
    var proteinInput by remember { mutableStateOf("") }
    var carbsInput by remember { mutableStateOf("") }
    var fatInput by remember { mutableStateOf("") }

    // Totales calculados dinámicamente
    val totalCalories = foodList.sumOf { it.calories }
    val totalProtein = foodList.sumOf { it.protein.toDouble() }.toFloat()
    val totalCarbs = foodList.sumOf { it.carbs.toDouble() }.toFloat()
    val totalFat = foodList.sumOf { it.fat.toDouble() }.toFloat()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Calorías") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Target & Progress Dashboard
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Resumen Diario", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { showTargetDialog = true }) {
                            Text("Objetivo: $targetCalories kcal")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = (totalCalories.toFloat() / targetCalories.toFloat()).coerceIn(0f, 1f),
                        modifier = Modifier.fillMaxWidth().height(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Calorías: $totalCalories / $targetCalories kcal")

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Prot: ${totalProtein}g")
                        Text("Carbs: ${totalCarbs}g")
                        Text("Grasas: ${totalFat}g")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Formulario de entrada
            Text("Añadir Alimento", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Nombre del alimento") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = caloriesInput,
                    onValueChange = { caloriesInput = it },
                    label = { Text("Kcal") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = proteinInput,
                    onValueChange = { proteinInput = it },
                    label = { Text("Prot (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = carbsInput,
                    onValueChange = { carbsInput = it },
                    label = { Text("Carbs (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = fatInput,
                    onValueChange = { fatInput = it },
                    label = { Text("Grasas (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val cal = caloriesInput.toIntOrNull() ?: 0
                    val prot = proteinInput.toFloatOrNull() ?: 0f
                    val carb = carbsInput.toFloatOrNull() ?: 0f
                    val fat = fatInput.toFloatOrNull() ?: 0f

                    if (nameInput.isNotBlank() && cal > 0) {
                        foodList.add(FoodItem(name = nameInput, calories = cal, protein = prot, carbs = carb, fat = fat))
                        nameInput = ""
                        caloriesInput = ""
                        proteinInput = ""
                        carbsInput = ""
                        fatInput = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de alimentos
            Text("Registros de hoy", style = MaterialTheme.typography.titleMedium)

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(foodList) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(item.name, style = MaterialTheme.typography.titleSmall)
                                Text("P: ${item.protein}g | C: ${item.carbs}g | G: ${item.fat}g")
                            }
                            Text("${item.calories} kcal", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }

    // Diálogo para modificar objetivo de calorías
    if (showTargetDialog) {
        var tempTarget by remember { mutableStateOf(targetCalories.toString()) }
        AlertDialog(
            onDismissRequest = { showTargetDialog = false },
            title = { Text("Cambiar objetivo diario") },
            text = {
                OutlinedTextField(
                    value = tempTarget,
                    onValueChange = { tempTarget = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Calorías diarias") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    tempTarget.toIntOrNull()?.let { targetCalories = it }
                    showTargetDialog = false
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTargetDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}