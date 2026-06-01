package com.example.fitkos.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreen(
    onNavigateToMealLog: () -> Unit,
    onNavigateToAddMeal: () -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToWaterTracker: () -> Unit,
    onNavigateToExercise: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 0.dp, bottom = 8.dp), // Hapus gap atas dan kurangi gap bawah
            verticalArrangement = Arrangement.spacedBy(16.dp) // Merapatkan jarak antar seksi
        ) {
            HeaderSection(uiState, onNavigateToAI)

            QuickActionSection(
                onNavigateToAddMeal = onNavigateToAddMeal
            )

            SummaryGrid(
                uiState = uiState,
                onNavigateToMealLog = onNavigateToMealLog,
                onNavigateToAI = onNavigateToAI,
                onNavigateToWaterTracker = onNavigateToWaterTracker,
                onNavigateToExercise = onNavigateToExercise
            )

            TargetHarianSection(uiState)

            DashboardTipsSection()
        }
    }
}

@Composable
private fun HeaderSection(
    uiState: DashboardUiState,
    onNavigateToAI: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp), // Jarak minimal dari Top Bar agar tidak "kepotong" tapi tetap rapat
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Halo, ${uiState.userName}! ✨",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            )

            Text(
                text = "Yuk jaga kesehatan hari ini",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = onNavigateToAI,
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = "Tanya AI",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun QuickActionSection(
    onNavigateToAddMeal: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Aksi Cepat",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        QuickActionCard(
            title = "Tambah Makan",
            subtitle = "Catat makanan baru hari ini",
            icon = "➕",
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFE8F5E9),
            onClick = onNavigateToAddMeal
        )
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: String,
    modifier: Modifier = Modifier,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(92.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 26.sp
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SummaryGrid(
    uiState: DashboardUiState,
    onNavigateToMealLog: () -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToWaterTracker: () -> Unit,
    onNavigateToExercise: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Ringkasan Hari Ini",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                title = "Minum Air",
                value = "${uiState.waterGlasses}/${uiState.waterTarget}",
                unit = "gelas",
                icon = "💧",
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToWaterTracker() },
                color = Color(0xFFE3F2FD)
            )

            SummaryCard(
                title = "Olahraga",
                value = uiState.exerciseMinutes.toString(),
                unit = "menit",
                icon = "🏃",
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToExercise() },
                color = Color(0xFFFFF3E0)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                title = "Catatan Makan",
                value = "${uiState.mealCount}",
                unit = "hari ini",
                icon = "🍽️",
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToMealLog() },
                color = Color(0xFFF1F8E9)
            )

            SummaryCard(
                title = "Tanya AI",
                value = "AI",
                unit = "Asisten Sehat",
                icon = "✨",
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToAI() },
                color = Color(0xFFF3E5F5)
            )
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    unit: String,
    icon: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    val contentColor = Color(0xFF1C1B1F)
    Card(
        modifier = modifier.height(108.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )

                    Text(
                        text = unit,
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = 0.75f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TargetHarianSection(
    uiState: DashboardUiState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Target Harian",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            TargetItem(
                icon = "💧",
                title = "Air",
                current = uiState.waterGlasses,
                target = uiState.waterTarget,
                unit = "gelas",
                progress = if (uiState.waterTarget > 0) {
                    uiState.waterGlasses.toFloat() / uiState.waterTarget
                } else {
                    0f
                }
            )

            TargetItem(
                icon = "🏃",
                title = "Olahraga",
                current = uiState.exerciseMinutes,
                target = uiState.exerciseTarget,
                unit = "menit",
                progress = if (uiState.exerciseTarget > 0) {
                    uiState.exerciseMinutes.toFloat() / uiState.exerciseTarget
                } else {
                    0f
                }
            )
        }
    }
}

@Composable
private fun TargetItem(
    icon: String,
    title: String,
    current: Int,
    target: Int,
    unit: String,
    progress: Float
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = "$current / $target $unit",
                style = MaterialTheme.typography.labelSmall
            )
        }

        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer
        )
    }
}

@Composable
private fun DashboardTipsSection() {
    val tips = listOf(
        DashboardTip("💡", "Masak Nasi Sendiri", "Hemat & lebih higienis.", Color(0xFFE8F5E9)),
        DashboardTip("💧", "Air Putih", "Minum 1 gelas setelah bangun.", Color(0xFFE3F2FD)),
        DashboardTip("🍎", "Cemilan Buah", "Ganti gorengan dengan buah.", Color(0xFFFFF3E0)),
        DashboardTip("🧘", "Peregangan", "Stretching tiap 2 jam duduk.", Color(0xFFF3E5F5)),
        DashboardTip("🥛", "Minum Susu", "Kalsium untuk tulang kuat.", Color(0xFFF1F8E9)),
        DashboardTip("😴", "Tidur Cukup", "7-8 jam untuk pemulihan.", Color(0xFFE8EAF6)),
        DashboardTip("🍳", "Sarapan Protein", "Telur rebus sangat praktis.", Color(0xFFFFF9C4)),
        DashboardTip("🚲", "Jalan Kaki", "Gunakan tangga daripada lift.", Color(0xFFE0F2F1)),
        DashboardTip("🥗", "Sayur Mayur", "Selalu sertakan sayur di makan siang.", Color(0xFFF1F8E9)),
        DashboardTip("🍵", "Teh Hijau", "Antioksidan alami tanpa gula.", Color(0xFFE8F5E9)),
        DashboardTip("📵", "Digital Detox", "Kurangi gadget sebelum tidur.", Color(0xFFECEFF1)),
        DashboardTip("🏃", "Lari Pagi", "Udara pagi bagus untuk paru-paru.", Color(0xFFFFF3E0)),
        DashboardTip("🥤", "Kurangi Manis", "Pilih minuman tanpa gula tambahan.", Color(0xFFFFEBEE)),
        DashboardTip("🍗", "Protein Cukup", "Ayam atau tempe sangat bagus.", Color(0xFFFFF3E0)),
        DashboardTip("🧠", "Meditasi", "Luangkan 5 menit untuk relaksasi.", Color(0xFFE8EAF6)),
        DashboardTip("🚶", "10 Ribu Langkah", "Target jalan kaki setiap hari.", Color(0xFFE0F2F1)),
        DashboardTip("🧹", "Bersih Kamar", "Kamar rapi, pikiran tenang.", Color(0xFFF1F8E9)),
        DashboardTip("📖", "Baca Buku", "Nutrisi untuk pikiran kamu.", Color(0xFFFFF9C4)),
        DashboardTip("🌞", "Sinar Matahari", "Dapatkan vitamin D di pagi hari.", Color(0xFFFFF3E0)),
        DashboardTip("🧂", "Kurangi Garam", "Menghindari risiko darah tinggi.", Color(0xFFFFEBEE))
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Tips Sehat Anak Kos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 0.dp) // Hapus gap bawah berlebih
        ) {
            items(tips) { tip ->
                Card(
                    modifier = Modifier.width(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = tip.color)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(tip.icon, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(tip.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text(tip.desc, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

private data class DashboardTip(val icon: String, val title: String, val desc: String, val color: Color)
