package com.example.Roomie.presentation.facility

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
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
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacilityGridScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: FacilityViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedRoomId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Pilih Ruangan") })
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is FacilityUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is FacilityUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        RoomLegend()
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(state.rooms) { room ->
                                RoomItem(
                                    room = room,
                                    isSelected = selectedRoomId == room.id,
                                    onClick = {
                                        if (room.status == RoomStatus.AVAILABLE) {
                                            selectedRoomId = room.id
                                            onNavigateToDetail(room.id)
                                        }
                                    }
                                )
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
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> Color.Blue
        room.status == RoomStatus.AVAILABLE -> Color(0xFF4CAF50)
        room.status == RoomStatus.BOOKED -> Color(0xFFF44336)
        room.status == RoomStatus.MAINTENANCE -> Color(0xFFFFEB3B)
        else -> Color.Gray
    }

    val contentColor = if (room.status == RoomStatus.MAINTENANCE) Color.Black else Color.White

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = room.name.replace("Ruang ", ""),
                color = contentColor,
                fontWeight = FontWeight.Bold
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
        Box(modifier = Modifier.size(16.dp).background(color, MaterialTheme.shapes.extraSmall))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}
