package com.example.tripmate.presentation.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tripmate.domain.model.Trip
import com.example.tripmate.presentation.screens.home.HomeUiState
import com.example.tripmate.presentation.screens.home.TripViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TripStatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: TripViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val trips = when (val s = uiState) {
        is HomeUiState.Success -> s.trips
        else -> emptyList()
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Text(
                        "Statistik",
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        "Ringkasan perjalananmu",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "✈",
                    fontSize = 36.sp,
                    modifier = Modifier.align(Alignment.TopEnd),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                )
            }

            if (trips.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "✈",
                            fontSize = 64.sp,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Belum ada data trip",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        label = "Total Trip",
                        value = "${trips.size} perjalanan",
                        icon = Icons.Default.Luggage,
                        iconBg = Color(0xFFDEEAF5),
                        iconTint = Color(0xFF3A7EC5)
                    )
                    StatCard(
                        label = "Total Budget",
                        value = "Rp ${formatBudget(trips.sumOf { it.budget })}",
                        icon = Icons.Default.Wallet,
                        iconBg = Color(0xFFD4F0EC),
                        iconTint = MaterialTheme.colorScheme.secondary
                    )
                    StatCard(
                        label = "Rata-rata Budget",
                        value = "Rp ${formatBudget(trips.sumOf { it.budget } / trips.size)}",
                        icon = Icons.Default.PieChart,
                        iconBg = Color(0xFFEDE0F8),
                        iconTint = Color(0xFF8A4EC8)
                    )
                    trips.maxByOrNull { it.budget }?.let { trip ->
                        StatCard(
                            label = "Trip Termahal",
                            value = "${trip.destination} — Rp ${formatBudget(trip.budget)}",
                            icon = Icons.Default.EmojiEvents,
                            iconBg = Color(0xFFFFF0D4),
                            iconTint = Color(0xFFD4870A)
                        )
                    }
                    trips.minByOrNull { it.budget }?.let { trip ->
                        StatCard(
                            label = "Trip Terhemat",
                            value = "${trip.destination} — Rp ${formatBudget(trip.budget)}",
                            icon = Icons.Default.Savings,
                            iconBg = Color(0xFFDCEEFF),
                            iconTint = Color(0xFF3A8FD4)
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        "Semua Trip",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 0.6.sp
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(1.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            trips.forEachIndexed { i, trip ->
                                TripRow(trip)
                                if (i < trips.lastIndex)
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    label,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    value,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun TripRow(trip: Trip) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            trip.destination,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            "Rp ${formatBudget(trip.budget)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}

private fun formatBudget(budget: Double): String =
    budget.toLong().toString().reversed().chunked(3).joinToString(".").reversed()
