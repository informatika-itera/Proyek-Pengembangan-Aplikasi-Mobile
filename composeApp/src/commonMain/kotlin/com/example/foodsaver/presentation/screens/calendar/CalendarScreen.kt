package com.example.foodsaver.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.model.FoodStatus
import com.example.foodsaver.presentation.components.getEmojiForCategory
import com.example.foodsaver.presentation.theme.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Calendar", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextMain) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = PrimaryGreen)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Jadwal Kedaluwarsa",
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                        Text(
                            "Pantau masa simpan stok makananmu",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryGreen)
                } else if (state.upcomingItems.isEmpty() && state.pastItems.isEmpty()) {
                    EmptyCalendarState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // UPCOMING SECTION
                        item {
                            Text(
                                "Mendatang",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextMain
                            )
                        }

                        if (state.upcomingItems.isEmpty()) {
                            item {
                                Text(
                                    "Tidak ada jadwal kedaluwarsa mendatang.",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        } else {
                            items(state.upcomingItems) { item ->
                                CalendarItemRow(item)
                            }
                        }

                        // PAST SECTION
                        if (state.pastItems.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Sudah Lewat",
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ExpiredRed
                                )
                            }

                            items(state.pastItems) { item ->
                                CalendarItemRow(item)
                            }
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CardWhite),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Text(
                                    "Tips: Konsumsi makanan di daftar 'Mendatang' sesegera mungkin untuk meminimalkan food waste.",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
fun CalendarItemRow(item: FoodItem) {
    val date = item.expiryDate.toLocalDateTime(TimeZone.currentSystemDefault()).date
    val status = item.getStatus()
    val isPast = status == FoodStatus.EXPIRED || status == FoodStatus.EXPIRED_TODAY
    val dateColor = if (isPast) ExpiredRed else PrimaryGreen
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.width(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                date.dayOfMonth.toString(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = dateColor
            )
            Text(
                date.month.name.take(3),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = dateColor.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(getEmojiForCategory(item.category), fontSize = 20.sp)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(item.name, fontWeight = FontWeight.Bold, color = TextMain, maxLines = 1)
                    Text(
                        item.getStatusLabel(), 
                        style = MaterialTheme.typography.labelSmall, 
                        color = if (isPast) ExpiredRed else TextSecondary,
                        fontWeight = if (isPast) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyCalendarState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.CalendarToday, 
            contentDescription = null, 
            modifier = Modifier.size(64.dp), 
            tint = PrimaryGreen.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Tidak ada jadwal kedaluwarsa.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
