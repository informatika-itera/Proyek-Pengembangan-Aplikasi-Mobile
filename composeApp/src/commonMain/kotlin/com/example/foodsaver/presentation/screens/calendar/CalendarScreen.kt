package com.example.foodsaver.presentation.screens.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.presentation.components.FoodItemCard
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onAddFoodClick: () -> Unit = {},
    viewModel: CalendarViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier.testTag("calendar_screen"),
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Food Calendar", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text("Lihat jadwal kedaluwarsa makananmu.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                val calendarFoods: List<FoodItem> = state.foodItems
                
                if (calendarFoods.isEmpty()) {
                    EmptyCalendarState(onAddFoodClick)
                } else {
                    val groupedFoods: Map<String, List<FoodItem>> = calendarFoods.groupBy { food ->
                        food.expiryDate.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize().testTag("calendar_list"),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        groupedFoods.keys.sorted().forEach { date ->
                            item(key = "header_$date") {
                                DateHeader(dateStr = date)
                            }

                            val foodsForDate = groupedFoods[date] ?: emptyList()
                            items(
                                items = foodsForDate,
                                key = { food -> food.id }
                            ) { food ->
                                FoodItemCard(
                                    item = food,
                                    onClick = { /* Handle click if needed */ },
                                    modifier = Modifier.testTag("food_card_${food.id}")
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
fun DateHeader(dateStr: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = dateStr,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun EmptyCalendarState(onAddFoodClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp).testTag("empty_state"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.EventBusy,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            "Belum ada jadwal kedaluwarsa",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Tambahkan makanan dengan tanggal expired untuk melihat jadwalnya.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onAddFoodClick,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("btn_empty_add_food")
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tambah Makanan", fontWeight = FontWeight.Bold)
        }
    }
}
