package com.example.Roomie.presentation.facility

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("GKU 2 - Ruangan", fontWeight = FontWeight.ExtraBold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is FacilityUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
                }
                is FacilityUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Custom Tab Row
                        ScrollableTabRow(
                            selectedTabIndex = state.selectedFloor - 1,
                            edgePadding = 20.dp,
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.primary,
                            divider = {},
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedFloor - 1]),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        ) {
                            (1..4).forEach { floor ->
                                Tab(
                                    selected = state.selectedFloor == floor,
                                    onClick = { viewModel.selectFloor(floor) },
                                    text = { 
                                        Text(
                                            "Lantai $floor", 
                                            fontWeight = if (state.selectedFloor == floor) FontWeight.Bold else FontWeight.Normal
                                        ) 
                                    },
                                    selectedContentColor = MaterialTheme.colorScheme.primary,
                                    unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }

                        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                            RoomLegend()
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(5),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
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
                                    RoomGridItem(
                                        room = room,
                                        onClick = { onNavigateToDetail(room.id) }
                                    )
                                }
                            }
                        }
                    }
                }
                is FacilityUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun RoomGridItem(
    room: Room,
    onClick: () -> Unit
) {
    val statusColor = when (room.status) {
        RoomStatus.AVAILABLE -> Color(0xFF4CAF50)
        RoomStatus.BOOKED -> MaterialTheme.colorScheme.error
        RoomStatus.MAINTENANCE -> MaterialTheme.colorScheme.primary
    }

    Surface(
        modifier = Modifier
            .then(
                if (room.type == RoomType.AULA) Modifier.fillMaxWidth().height(90.dp)
                else Modifier.aspectRatio(1f)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = statusColor.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(2.dp, statusColor.copy(alpha = 0.5f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = room.name,
                    color = statusColor,
                    fontWeight = FontWeight.ExtraBold,
                    style = if (room.type == RoomType.AULA) MaterialTheme.typography.headlineSmall 
                            else MaterialTheme.typography.titleMedium
                )
                if (room.type == RoomType.AULA) {
                    Text("Kapasitas Besar", style = MaterialTheme.typography.labelSmall, color = statusColor.copy(alpha = 0.7f))
                }
            }
        }
    }
}

@Composable
fun RoomLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LegendItem("Tersedia", Color(0xFF4CAF50))
        LegendItem("Penuh", MaterialTheme.colorScheme.error)
        LegendItem("Perbaikan", MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).background(color, RoundedCornerShape(2.dp)))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}
