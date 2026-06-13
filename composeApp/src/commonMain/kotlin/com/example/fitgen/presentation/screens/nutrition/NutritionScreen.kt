package com.example.fitgen.presentation.screens.nutrition

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitgen.domain.model.MealLog
import com.example.fitgen.domain.model.MealType
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    onNavigateToAddMeal: () -> Unit,
    viewModel: NutritionViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val months = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
    val dateString = "${today.dayOfMonth} ${months[today.monthNumber - 1]} ${today.year}"

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(90.dp),
                title = {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Nutrisi Anda",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Hari ini, $dateString",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = Color(0xFF800000)) }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp) // Extra padding for top gap & bottom nav
        ) {
            // Top Header has been moved to TopAppBar

            // Calorie & Macros Dashboard
            item {
                CalorieDashboardCard(
                    totalCalories = uiState.totalCaloriesToday,
                    targetCalories = uiState.calorieTarget,
                    progress = uiState.calorieProgress,
                    protein = uiState.totalProtein,
                    carbs = uiState.totalCarbs,
                    fat = uiState.totalFat
                )
            }

            // Water Tracker
            item {
                WaterTrackerCard(
                    waterGlasses = uiState.waterGlasses,
                    onAddWater = { viewModel.addWater() },
                    onRemoveWater = { viewModel.removeWater() }
                )
            }

            // Meal Logger Title
            item {
                Text(
                    text = "Catatan Makanan Hari Ini",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }

            // Meals List
            if (uiState.todayMeals.isEmpty()) {
                item {
                    EmptyMealState()
                }
            } else {
                MealType.entries.forEach { type ->
                    val mealsForType = uiState.todayMeals.filter { it.jenisMakan == type }
                    if (mealsForType.isNotEmpty()) {
                        item {
                            Text(
                                text = type.label,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                        items(mealsForType, key = { it.id }) { meal ->
                            MealLogItem(
                                meal = meal,
                                onDelete = { viewModel.deleteMeal(meal.id) }
                            )
                        }
                    }
                }
            }

            // Tips Carousel
            item {
                TipsCarousel()
            }
        }
    }
}


@Composable
private fun CalorieDashboardCard(
    totalCalories: Int,
    targetCalories: Int,
    progress: Float,
    protein: Double,
    carbs: Double,
    fat: Double
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(1000)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circular Progress for Calories
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(160.dp)
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round
                )
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF800000), // Maroon accent
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$totalCalories",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "/ $targetCalories kkal",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Macros
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MacroItem("Protein", protein, 120.0, Color(0xFF4CAF50)) // Green
                MacroItem("Karbo", carbs, 250.0, Color(0xFF2196F3)) // Blue
                MacroItem("Lemak", fat, 65.0, Color(0xFFFFC107)) // Yellow
            }
        }
    }
}

@Composable
private fun MacroItem(label: String, amount: Double, target: Double, color: Color) {
    val progress = if (target > 0) (amount / target).toFloat().coerceIn(0f, 1f) else 0f
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${amount.toInt()}g",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .width(60.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(50)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun WaterTrackerCard(
    waterGlasses: Int,
    onAddWater: () -> Unit,
    onRemoveWater: () -> Unit
) {
    val dailyTarget = 8

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D47A1)), // Dark Blue
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalDrink,
                        contentDescription = "Air",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Air Putih",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "$waterGlasses / $dailyTarget Gelas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                IconButton(
                    onClick = onRemoveWater,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Kurangi", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                IconButton(
                    onClick = onAddWater,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1976D2))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun EmptyMealState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    .padding(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Belum ada makanan hari ini",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Tekan tombol kamera di bawah untuk mencatat.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MealLogItem(meal: MealLog, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.namaMakanan,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${meal.kalori} kkal",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF800000)
                    )
                    Text(" • ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "P: ${meal.proteinG}g  K: ${meal.karbohidratG}g  L: ${meal.lemakG}g",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun TipsCarousel() {
    val tips = listOf(
        "Minum air putih minimal 8 gelas per hari membantu metabolisme.",
        "Konsumsi protein tinggi setelah latihan untuk pemulihan otot.",
        "Kurangi gula tambahan untuk menghindari lonjakan insulin.",
        "Jangan lupakan serat dari sayur dan buah setiap makan."
    )

    Column(modifier = Modifier.padding(vertical = 24.dp)) {
        Text(
            text = "Tips Nutrisi",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tips) { tip ->
                Card(
                    modifier = Modifier.width(260.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)) // Light Yellow/Amber
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Outlined.TipsAndUpdates,
                            contentDescription = "Tip",
                            tint = Color(0xFFFFA000)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = tip,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF4E342E)
                        )
                    }
                }
            }
        }
    }
}