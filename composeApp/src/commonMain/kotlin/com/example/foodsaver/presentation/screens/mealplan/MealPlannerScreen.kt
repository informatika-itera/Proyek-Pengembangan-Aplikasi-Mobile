package com.example.foodsaver.presentation.screens.mealplan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                title = { 
                    Text("Jadwal Masakmu", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) 
                },
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Belum ada rencana masak nih.",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Yuk, cari resep dan mulai atur jadwalmu!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
                                        MealType.BREAKFAST -> "Menu Sarapan"
                                        MealType.LUNCH -> "Menu Makan Siang"
                                        MealType.DINNER -> "Menu Makan Malam"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
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
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
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
            
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val monthIndo = when(selectedDate.month) {
                Month.JANUARY -> "Januari"
                Month.FEBRUARY -> "Februari"
                Month.MARCH -> "Maret"
                Month.APRIL -> "April"
                Month.MAY -> "Mei"
                Month.JUNE -> "Juni"
                Month.JULY -> "Juli"
                Month.AUGUST -> "Agustus"
                Month.SEPTEMBER -> "September"
                Month.OCTOBER -> "Oktober"
                Month.NOVEMBER -> "November"
                Month.DECEMBER -> "Desember"
                else -> selectedDate.month.name
            }

            val dateLabel = when (selectedDate) {
                today -> "Hari Ini"
                today.plus(1, DateTimeUnit.DAY) -> "Besok"
                today.minus(1, DateTimeUnit.DAY) -> "Kemarin"
                else -> "${selectedDate.dayOfMonth} $monthIndo ${selectedDate.year}"
            }

            Text(
                text = dateLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = plan.recipeName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
            }
        }
    }
}
