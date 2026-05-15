package com.example.Roomie.presentation.facility

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.domain.model.RoomType
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacilityGridScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: FacilityViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("GKU 2 - ITERA") })
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is FacilityUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is FacilityUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Pemilihan Lantai (TabRow)
                        ScrollableTabRow(
                            selectedTabIndex = state.selectedFloor - 1,
                            edgePadding = 16.dp,
                            containerColor = MaterialTheme.colorScheme.surface,
                            divider = {}
                        ) {
                            (1..4).forEach { floor ->
                                Tab(
                                    selected = state.selectedFloor == floor,
                                    onClick = { viewModel.selectFloor(floor) },
                                    text = { Text("Lantai $floor") }
                                )
                            }
                        }

                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            RoomLegend()
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(5),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = state.filteredRooms,
                                    key = { it.id },
                                    span = { room ->
                                        if (room.type == RoomType.AULA) GridItemSpan(5) 
                                        else GridItemSpan(1)
                                    }
                                ) { room ->
                                    RoomItem(
                                        room = room,
                                        onClick = {
                                            // Sekarang semua status bisa diklik untuk melihat detail
                                            onNavigateToDetail(room.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                is FacilityUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun RoomItem(
    room: Room,
    onClick: () -> Unit
) {
    val backgroundColor = when (room.status) {
        RoomStatus.AVAILABLE -> Color(0xFF4CAF50)
        RoomStatus.BOOKED -> Color(0xFFF44336)
        RoomStatus.MAINTENANCE -> Color(0xFFFFEB3B)
    }

    val contentColor = if (room.status == RoomStatus.MAINTENANCE) Color.Black else Color.White

    Card(
        modifier = Modifier
            .then(
                if (room.type == RoomType.AULA) Modifier.fillMaxWidth().height(80.dp)
                else Modifier.aspectRatio(1f)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = room.name,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                style = if (room.type == RoomType.AULA) MaterialTheme.typography.titleLarge 
                        else MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun RoomLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LegendItem("Tersedia", Color(0xFF4CAF50))
        LegendItem("Penuh", Color(0xFFF44336))
        LegendItem("Perbaikan", Color(0xFFFFEB3B))
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(color, MaterialTheme.shapes.extraSmall))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}
