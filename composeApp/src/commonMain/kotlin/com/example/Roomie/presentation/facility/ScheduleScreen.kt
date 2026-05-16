package com.example.Roomie.presentation.facility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Roomie.domain.model.Booking
import com.example.Roomie.domain.model.BookingStatus
import com.example.Roomie.presentation.util.AppStrings
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    onBack: () -> Unit,
    viewModel: ScheduleViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppStrings.HOME_SCHEDULE) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is ScheduleUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is ScheduleUiState.Success -> {
                    if (state.bookings.isEmpty()) {
                        EmptySchedule(Modifier.align(Alignment.Center))
                    } else {
                        BookingList(state.bookings)
                    }
                }
                is ScheduleUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun BookingList(bookings: List<Booking>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Mendatang", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        items(bookings) { booking ->
            BookingCard(booking)
        }
    }
}

@Composable
fun BookingCard(booking: Booking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = booking.subject ?: "Peminjaman Ruangan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "${booking.buildingName} - ${booking.roomName}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                IconButton(onClick = { /* Tampilkan QR Code */ }) {
                    Icon(Icons.Default.QrCode, contentDescription = "E-Permit", tint = MaterialTheme.colorScheme.primary)
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatBookingTime(booking.startTime, booking.endTime),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun EmptySchedule(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Belum ada jadwal", style = MaterialTheme.typography.titleMedium)
        Text("Ruangan yang Anda pinjam bakal muncul di sini", style = MaterialTheme.typography.bodySmall)
    }
}

fun formatBookingTime(start: Long, end: Long): String {
    val startDt = Instant.fromEpochMilliseconds(start).toLocalDateTime(TimeZone.currentSystemDefault())
    val endDt = Instant.fromEpochMilliseconds(end).toLocalDateTime(TimeZone.currentSystemDefault())
    
    val day = "${startDt.dayOfMonth}/${startDt.monthNumber}"
    val startTime = "${startDt.hour.toString().padStart(2, '0')}:${startDt.minute.toString().padStart(2, '0')}"
    val endTime = "${endDt.hour.toString().padStart(2, '0')}:${endDt.minute.toString().padStart(2, '0')}"
    
    return "$day | $startTime - $endTime"
}
