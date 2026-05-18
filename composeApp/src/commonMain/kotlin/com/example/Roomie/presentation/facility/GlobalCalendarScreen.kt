package com.example.Roomie.presentation.facility

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Roomie.domain.model.Booking
import com.example.Roomie.domain.model.BookingStatus
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalCalendarScreen(
    onBack: () -> Unit,
    viewModel: ScheduleViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var selectedDate by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date) }
    var showDatePicker by remember { mutableStateOf(false) }

    val bookingsForDate = remember(state.bookings, selectedDate) {
        state.bookings.filter { booking ->
            val bookingDate = Instant.fromEpochMilliseconds(booking.startTime)
                .toLocalDateTime(TimeZone.currentSystemDefault()).date
            bookingDate == selectedDate && booking.status == BookingStatus.APPROVED
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Jadwal Penggunaan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Date Selector Header
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Tanggal Terpilih", style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = "${selectedDate.dayOfMonth} ${selectedDate.month} ${selectedDate.year}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Text("UBAH", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }
            }

            if (bookingsForDate.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📅", fontSize = 64.sp)
                        Spacer(Modifier.height(16.dp))
                        Text("Belum ada jadwal di tanggal ini", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text("Daftar Pemakaian (${bookingsForDate.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                    items(bookingsForDate) { booking ->
                        CalendarBookingItem(booking)
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = Instant.fromEpochMilliseconds(it)
                            .toLocalDateTime(TimeZone.UTC).date
                    }
                    showDatePicker = false
                }) { Text("PILIH") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("BATAL") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun CalendarBookingItem(booking: Booking) {
    val startTime = Instant.fromEpochMilliseconds(booking.startTime).toLocalDateTime(TimeZone.currentSystemDefault())
    val endTime = Instant.fromEpochMilliseconds(booking.endTime).toLocalDateTime(TimeZone.currentSystemDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MeetingRoom, null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(booking.roomName, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${startTime.hour.toString().padStart(2, '0')}:${startTime.minute.toString().padStart(2, '0')} - ${endTime.hour.toString().padStart(2, '0')}:${endTime.minute.toString().padStart(2, '0')}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(booking.subject ?: "Kegiatan Terjadwal", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
