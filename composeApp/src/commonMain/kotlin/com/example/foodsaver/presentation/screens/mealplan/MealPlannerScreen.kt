package com.example.foodsaver.presentation.screens.mealplan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.foodsaver.domain.model.MealPlan
import com.example.foodsaver.domain.model.MealType
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlannerScreen(
    onNavigateBack: () -> Unit,
    onRecipeClick: (String) -> Unit,
    viewModel: MealPlannerViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Planner") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Date Selector
            DateHeader(
                selectedDate = state.selectedDate,
                onPreviousDate = { viewModel.onDateSelected(state.selectedDate.minus(1, DateTimeUnit.DAY)) },
                onNextDate = { viewModel.onDateSelected(state.selectedDate.plus(1, DateTimeUnit.DAY)) }
            )

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.mealPlans.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada rencana makan untuk hari ini.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val grouped = state.mealPlans.groupBy { it.mealType }
                    
                    MealType.entries.forEach { type ->
                        val plans = grouped[type] ?: emptyList()
                        if (plans.isNotEmpty()) {
                            item {
                                Text(
                                    text = when(type) {
                                        MealType.BREAKFAST -> "Sarapan"
                                        MealType.LUNCH -> "Makan Siang"
                                        MealType.DINNER -> "Makan Malam"
                                    },
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            items(plans) { plan ->
                                MealPlanItem(
                                    plan = plan,
                                    onClick = { onRecipeClick(plan.recipeId) },
                                    onDelete = { viewModel.removeMealPlan(plan.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DateHeader(
    selectedDate: LocalDate,
    onPreviousDate: () -> Unit,
    onNextDate: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousDate) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Sebelumnya")
            }
            
            Text(
                text = "${selectedDate.dayOfMonth} ${selectedDate.month.name} ${selectedDate.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onNextDate) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Berikutnya")
            }
        }
    }
}

@Composable
fun MealPlanItem(
    plan: MealPlan,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = plan.recipeImageUrl,
                contentDescription = plan.recipeName,
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = plan.recipeName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
