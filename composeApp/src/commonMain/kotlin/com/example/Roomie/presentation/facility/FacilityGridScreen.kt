package com.example.Roomie.presentation.facility

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.domain.model.UserRole
import com.example.Roomie.presentation.AppViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacilityGridScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToMultiBooking: (List<String>) -> Unit,
    onBack: () -> Unit,
    viewModel: FacilityViewModel = koinViewModel(),
    appViewModel: AppViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by appViewModel.currentUser.collectAsState()
    val isAdmin = currentUser?.role == UserRole.ADMIN

    var selectedRooms by remember { mutableStateOf(setOf<String>()) }
    var isSelectionMode by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("GKU 2", fontWeight = FontWeight.ExtraBold)
                        if (isSelectionMode) {
                            Text("${selectedRooms.size} Terpilih", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (isSelectionMode) { 
                            isSelectionMode = false
                            selectedRooms = emptySet() 
                        } else { 
                            onBack() 
                        } 
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    if (!isAdmin) {
                        IconButton(onClick = { isSelectionMode = !isSelectionMode }) {
                            Icon(
                                imageVector = if (isSelectionMode) Icons.Default.CheckCircle else Icons.Default.GridView,
                                contentDescription = null,
                                tint = if (isSelectionMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            if (isSelectionMode && selectedRooms.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    Button(
                        onClick = { onNavigateToMultiBooking(selectedRooms.toList()) },
                        modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("PINJAM ${selectedRooms.size} RUANGAN", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is FacilityUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is FacilityUiState.Success -> {
                    ScrollableTabRow(
                        selectedTabIndex = state.selectedFloor - 1,
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary,
                        divider = {}
                    ) {
                        (1..3).forEach { floor ->
                            Tab(
                                selected = state.selectedFloor == floor,
                                onClick = { viewModel.selectFloor(floor) },
                                text = { Text("Lantai $floor", fontWeight = FontWeight.Bold) }
                            )
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.filteredRooms) { room ->
                            val isSelected = selectedRooms.contains(room.id)
                            RoomGridItem(
                                room = room,
                                isSelected = isSelected,
                                isSelectionMode = isSelectionMode,
                                onClick = {
                                    if (isSelectionMode) {
                                        if (room.status == RoomStatus.AVAILABLE) {
                                            selectedRooms = if (isSelected) selectedRooms - room.id else selectedRooms + room.id
                                        }
                                    } else {
                                        onNavigateToDetail(room.id)
                                    }
                                }
                            )
                        }
                    }
                }
                is FacilityUiState.Error -> Text(state.message)
            }
        }
    }
}

@Composable
fun RoomGridItem(
    room: Room,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit
) {
    val statusColor = when (room.status) {
        RoomStatus.AVAILABLE -> Color(0xFF4CAF50)
        RoomStatus.BOOKED -> MaterialTheme.colorScheme.error
        RoomStatus.MAINTENANCE -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) statusColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 3.dp else 1.dp,
            color = if (isSelected) statusColor else statusColor.copy(alpha = 0.3f)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = room.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) statusColor else MaterialTheme.colorScheme.onSurface
                )
                if (!isSelectionMode) {
                    Text(
                        text = room.status.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(20.dp)
                )
            }
        }
    }
}
