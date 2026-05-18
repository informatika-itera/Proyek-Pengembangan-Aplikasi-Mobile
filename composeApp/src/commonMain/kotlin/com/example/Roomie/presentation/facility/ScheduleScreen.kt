package com.example.Roomie.presentation.facility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Roomie.domain.model.Booking
import com.example.Roomie.domain.model.BookingStatus
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
    val state by viewModel.uiState.collectAsState()
    var selectedPermit by remember { mutableStateOf<Booking?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Jadwal Pinjam", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
            } else if (state.bookings.isEmpty()) {
                Text("Belum ada peminjaman", modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.bookings) { booking ->
                        BookingCard(
                            booking = booking,
                            onCancel = { viewModel.cancelBooking(booking.id) },
                            onViewPermit = { selectedPermit = booking }
                        )
                    }
                }
            }
        }
    }

    selectedPermit?.let { booking ->
        EPermitDialog(booking = booking, onDismiss = { selectedPermit = null })
    }
}

@Composable
fun BookingCard(
    booking: Booking,
    onCancel: () -> Unit,
    onViewPermit: () -> Unit
) {
    val statusColor = when(booking.status) {
        BookingStatus.APPROVED -> Color(0xFF4CAF50)
        BookingStatus.PENDING -> Color(0xFFFFA500)
        BookingStatus.REJECTED, BookingStatus.CANCELLED -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.2f))
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        booking.status.displayName,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(formatDate(booking.startTime), style = MaterialTheme.typography.labelSmall)
            }

            Column {
                Text(booking.roomName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                Text(booking.buildingName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Text(booking.subject ?: "Tanpa Judul", style = MaterialTheme.typography.bodyMedium)
            
            Text(
                "${formatTime(booking.startTime)} - ${formatTime(booking.endTime)}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (booking.status == BookingStatus.APPROVED) {
                    Button(
                        onClick = onViewPermit,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.QrCode, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("E-PERMIT", style = MaterialTheme.typography.labelMedium)
                    }
                }
                
                if (booking.status == BookingStatus.PENDING) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Cancel, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("BATAL", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun EPermitDialog(booking: Booking, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("TUTUP") }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("E-PERMIT DIGITAL", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                Text("Roomie ITERA", style = MaterialTheme.typography.labelSmall)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Mock QR Code
                Surface(
                    modifier = Modifier.size(180.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.Black)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.QrCode, null, modifier = Modifier.size(140.dp), tint = Color.Black)
                    }
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(booking.id, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text("STATUS: DISETUJUI", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                }

                HorizontalDivider(modifier = Modifier.alpha(0.2f))

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    PermitInfoRow("Ruangan", booking.roomName)
                    PermitInfoRow("Gedung", booking.buildingName)
                    PermitInfoRow("Tanggal", formatDate(booking.startTime))
                    PermitInfoRow("Waktu", "${formatTime(booking.startTime)} - ${formatTime(booking.endTime)}")
                    PermitInfoRow("Tujuan", booking.subject ?: "-")
                }
                
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Tunjukkan layar ini kepada petugas keamanan jika diperlukan.", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        },
        shape = RoundedCornerShape(28.dp)
    )
}

@Composable
fun PermitInfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}

private fun formatDate(timestamp: Long): String {
    val dt = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dt.dayOfMonth}/${dt.monthNumber}/${dt.year}"
}

private fun formatTime(timestamp: Long): String {
    val dt = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dt.hour.toString().padStart(2, '0')}:${dt.minute.toString().padStart(2, '0')}"
}
