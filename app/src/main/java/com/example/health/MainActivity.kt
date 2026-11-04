package com.example.health

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
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
import kotlin.math.roundToInt

// Modelos y enums
data class FoodItem(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val category: MealCategory
)

enum class MealCategory(val title: String) {
    BREAKFAST("Desayuno"),
    LUNCH("Almuerzo"),
    DINNER("Cena"),
    OTHER("Otros")
}

enum class Gender(val title: String) {
    MALE("Hombre"),
    FEMALE("Mujer")
}

enum class GoalType(val title: String) {
    MAINTAIN("Mantenerme"),
    LOSE("Bajar de peso"),
    GAIN("Subir de peso")
}

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
    val foodList = remember { mutableStateListOf<FoodItem>() }

    // Objetivos diarios (Calorías y Macros)
    var targetCalories by remember { mutableStateOf(2000) }
    var targetProtein by remember { mutableStateOf(150) }
    var targetCarbs by remember { mutableStateOf(200) }
    var targetFat by remember { mutableStateOf(65) }

    // Estados de diálogos
    var showTargetDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    // Estado para saber qué categorías están expandidas (burbujas)
    val expandedCategories = remember { mutableStateMapOf<MealCategory, Boolean>() }

    // Estado para editar un alimento
    var editingFoodId by remember { mutableStateOf<Long?>(null) }

    // Datos del perfil de usuario
    var age by remember { mutableStateOf("25") }
    var height by remember { mutableStateOf("175") }
    var weight by remember { mutableStateOf("70") }
    var gender by remember { mutableStateOf(Gender.MALE) }
    var goalType by remember { mutableStateOf(GoalType.MAINTAIN) }
    var weeklyRate by remember { mutableStateOf("0.5") }

    // Campos del formulario de alimentos
    var nameInput by remember { mutableStateOf("") }
    var caloriesInput by remember { mutableStateOf("") }
    var proteinInput by remember { mutableStateOf("") }
    var carbsInput by remember { mutableStateOf("") }
    var fatInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(MealCategory.BREAKFAST) }
    var expandedCategoryDropdown by remember { mutableStateOf(false) }

    // Totales globales consumidos
    val totalCalories = foodList.sumOf { it.calories }
    val totalProtein = foodList.sumOf { it.protein.toDouble() }.toFloat()
    val totalCarbs = foodList.sumOf { it.carbs.toDouble() }.toFloat()
    val totalFat = foodList.sumOf { it.fat.toDouble() }.toFloat()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contador de Calorías") },
                actions = {
                    TextButton(onClick = { showProfileDialog = true }) {
                        Text("👤 Perfil", style = MaterialTheme.typography.bodyMedium)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // 1. RESUMEN GLOBAL (Arriba del todo)
            item {
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
                                Text("Obj: $targetCalories kcal")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = (totalCalories.toFloat() / targetCalories.toFloat()).coerceIn(0f, 1f),
                            modifier = Modifier.fillMaxWidth().height(8.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Calorías: $totalCalories / $targetCalories kcal")

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Prot: ${totalProtein.roundToInt()} / ${targetProtein}g", style = MaterialTheme.typography.bodySmall)
                            Text("Carbs: ${totalCarbs.roundToInt()} / ${targetCarbs}g", style = MaterialTheme.typography.bodySmall)
                            Text("Grasas: ${totalFat.roundToInt()} / ${targetFat}g", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // 2. BURBUJAS / APARTADOS POR CATEGORÍA (Debajo del resumen)
            item {
                Text("Registros por Categoría", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(MealCategory.values()) { category ->
                val itemsInCategory = foodList.filter { it.category == category }
                val categoryCalories = itemsInCategory.sumOf { it.calories }
                val isExpanded = expandedCategories[category] == true

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            // Al hacer clic en la burbuja, alterna su estado expandido
                            expandedCategories[category] = !isExpanded
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Cabecera de la burbuja
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${category.title} ${if (isExpanded) "▲" else "▼"}",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("$categoryCalories kcal", style = MaterialTheme.typography.bodyMedium)
                        }

                        // Contenido desplegable al pulsar la burbuja
                        if (isExpanded) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            if (itemsInCategory.isEmpty()) {
                                Text("No hay alimentos registrados aquí", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            } else {
                                itemsInCategory.forEach { item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(item.name, style = MaterialTheme.typography.bodyMedium)
                                            Text("P: ${item.protein}g | C: ${item.carbs}g | G: ${item.fat}g | ${item.calories} kcal", style = MaterialTheme.typography.bodySmall)
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            TextButton(
                                                onClick = {
                                                    editingFoodId = item.id
                                                    nameInput = item.name
                                                    caloriesInput = item.calories.toString()
                                                    proteinInput = item.protein.toString()
                                                    carbsInput = item.carbs.toString()
                                                    fatInput = item.fat.toString()
                                                    selectedCategory = item.category
                                                },
                                                contentPadding = PaddingValues(4.dp)
                                            ) {
                                                Text("Editar", style = MaterialTheme.typography.bodySmall)
                                            }
                                            TextButton(
                                                onClick = {
                                                    foodList.remove(item)
                                                    if (editingFoodId == item.id) {
                                                        editingFoodId = null
                                                        nameInput = ""
                                                        caloriesInput = ""
                                                        proteinInput = ""
                                                        carbsInput = ""
                                                        fatInput = ""
                                                    }
                                                },
                                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                                contentPadding = PaddingValues(4.dp)
                                            ) {
                                                Text("Borrar", style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. FORMULARIO PARA AÑADIR/EDITAR ALIMENTO (Abajo del todo)
            item {
                Text(
                    text = if (editingFoodId == null) "Añadir Alimento" else "Editar Alimento",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Nombre del alimento") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedCategoryDropdown,
                    onExpandedChange = { expandedCategoryDropdown = !expandedCategoryDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory.title,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Comida") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoryDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategoryDropdown,
                        onDismissRequest = { expandedCategoryDropdown = false }
                    ) {
                        MealCategory.values().forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.title) },
                                onClick = {
                                    selectedCategory = category
                                    expandedCategoryDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

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

                Spacer(modifier = Modifier.height(8.dp))

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

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val cal = caloriesInput.toIntOrNull() ?: 0
                            val prot = proteinInput.toFloatOrNull() ?: 0f
                            val carb = carbsInput.toFloatOrNull() ?: 0f
                            val fat = fatInput.toFloatOrNull() ?: 0f

                            if (nameInput.isNotBlank() && cal > 0) {
                                if (editingFoodId == null) {
                                    foodList.add(
                                        FoodItem(
                                            name = nameInput,
                                            calories = cal,
                                            protein = prot,
                                            carbs = carb,
                                            fat = fat,
                                            category = selectedCategory
                                        )
                                    )
                                    // Auto expandir la burbuja donde se añadió
                                    expandedCategories[selectedCategory] = true
                                } else {
                                    val index = foodList.indexOfFirst { it.id == editingFoodId }
                                    if (index != -1) {
                                        foodList[index] = FoodItem(
                                            id = editingFoodId!!,
                                            name = nameInput,
                                            calories = cal,
                                            protein = prot,
                                            carbs = carb,
                                            fat = fat,
                                            category = selectedCategory
                                        )
                                    }
                                    editingFoodId = null
                                }
                                nameInput = ""
                                caloriesInput = ""
                                proteinInput = ""
                                carbsInput = ""
                                fatInput = ""
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (editingFoodId == null) "Registrar Alimento" else "Guardar Cambios")
                    }

                    if (editingFoodId != null) {
                        OutlinedButton(
                            onClick = {
                                editingFoodId = null
                                nameInput = ""
                                caloriesInput = ""
                                proteinInput = ""
                                carbsInput = ""
                                fatInput = ""
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
            }
        }
    }

    // DIÁLOGOS DE OBJETIVO Y PERFIL (Sin cambios estructurales)
    if (showTargetDialog) {
        var tempTarget by remember { mutableStateOf(targetCalories.toString()) }
        AlertDialog(
            onDismissRequest = { showTargetDialog = false },
            title = { Text("Cambiar objetivo manual") },
            text = {
                OutlinedTextField(
                    value = tempTarget,
                    onValueChange = { tempTarget = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Calorías diarias objetivo") }
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

    if (showProfileDialog) {
        var tempAge by remember { mutableStateOf(age) }
        var tempHeight by remember { mutableStateOf(height) }
        var tempWeight by remember { mutableStateOf(weight) }
        var tempGender by remember { mutableStateOf(gender) }
        var tempGoal by remember { mutableStateOf(goalType) }
        var tempRate by remember { mutableStateOf(weeklyRate) }

        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text("Perfil y Objetivos") },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = tempAge,
                        onValueChange = { tempAge = it },
                        label = { Text("Edad (años)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempHeight,
                        onValueChange = { tempHeight = it },
                        label = { Text("Altura (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempWeight,
                        onValueChange = { tempWeight = it },
                        label = { Text("Peso (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { tempGender = Gender.MALE },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (tempGender == Gender.MALE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Hombre")
                        }
                        Button(
                            onClick = { tempGender = Gender.FEMALE },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (tempGender == Gender.FEMALE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Mujer")
                        }
                    }

                    Text("Objetivo:", style = MaterialTheme.typography.bodyMedium)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        GoalType.values().forEach { goal ->
                            Button(
                                onClick = { tempGoal = goal },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (tempGoal == goal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(4.dp)
                            ) {
                                Text(
                                    text = when(goal) {
                                        GoalType.MAINTAIN -> "Mantener"
                                        GoalType.LOSE -> "Bajar"
                                        GoalType.GAIN -> "Subir"
                                    },
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    if (tempGoal != GoalType.MAINTAIN) {
                        OutlinedTextField(
                            value = tempRate,
                            onValueChange = { tempRate = it },
                            label = { Text("Ritmo (kg/semana, ej: 0.5)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    age = tempAge
                    height = tempHeight
                    weight = tempWeight
                    gender = tempGender
                    goalType = tempGoal
                    weeklyRate = tempRate

                    val w = weight.toFloatOrNull() ?: 70f
                    val h = height.toFloatOrNull() ?: 175f
                    val a = age.toIntOrNull() ?: 25

                    val bmr = if (gender == Gender.MALE) {
                        (10 * w) + (6.25 * h) - (5 * a) + 5
                    } else {
                        (10 * w) + (6.25 * h) - (5 * a) - 161
                    }

                    val tdee = bmr * 1.375
                    val rate = weeklyRate.toFloatOrNull() ?: 0.5f
                    val dailyAdjustment = (rate * 1100f).roundToInt()

                    targetCalories = when (goalType) {
                        GoalType.MAINTAIN -> tdee.roundToInt()
                        GoalType.LOSE -> (tdee - dailyAdjustment).roundToInt().coerceAtLeast(1200)
                        GoalType.GAIN -> (tdee + dailyAdjustment).roundToInt()
                    }

                    targetProtein = (w * 2.0f).roundToInt()
                    targetFat = ((targetCalories * 0.25f) / 9f).roundToInt()
                    val proteinCalories = targetProtein * 4
                    val fatCalories = targetFat * 9
                    val remainingCaloriesForCarbs = (targetCalories - proteinCalories - fatCalories).coerceAtLeast(0)
                    targetCarbs = (remainingCaloriesForCarbs / 4f).roundToInt()

                    showProfileDialog = false
                }) {
                    Text("Calcular y Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}