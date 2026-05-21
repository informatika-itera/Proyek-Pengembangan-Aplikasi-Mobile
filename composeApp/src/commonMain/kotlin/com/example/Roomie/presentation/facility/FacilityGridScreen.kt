package com.example.Roomie.presentation.facility

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.domain.model.UserRole
import com.example.Roomie.presentation.AppViewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacilityGridScreen(
    onNavigateToDetail: (String, String) -> Unit, // Updated with date
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
    var showDatePicker by remember { mutableStateOf(false) }

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
                    // Date Selector Bar
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Melihat Jadwal Untuk:", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    text = "${state.selectedDate.dayOfMonth}/${state.selectedDate.monthNumber}/${state.selectedDate.year}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            Text("GANTI", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    // Floor Selector
                    ScrollableTabRow(
                        selectedTabIndex = state.selectedFloor - 1,
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary,
                        divider = {}
                    ) {
                        (1..4).forEach { floor ->
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
                                        onNavigateToDetail(room.id, state.selectedDate.toString())
                                    }
                                }
                            )
                        }
                    }

                    // Date Picker Dialog
                    if (showDatePicker) {
                        val datePickerState = rememberDatePickerState()
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let {
                                        val date = Instant.fromEpochMilliseconds(it)
                                            .toLocalDateTime(TimeZone.UTC).date
                                        viewModel.selectDate(date)
                                    }
                                    showDatePicker = false
                                }) { Text("PILIH") }
                            }
                        ) {
                            DatePicker(state = datePickerState)
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
