package com.example.foodsaver.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.model.FoodStatus
import com.example.foodsaver.presentation.components.StatusBadge
import com.example.foodsaver.presentation.components.getEmojiForCategory
import com.example.foodsaver.presentation.theme.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailScreen(
    foodId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: FoodDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(foodId) {
        viewModel.loadFoodDetail(foodId)
    }

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) {
            onNavigateBack()
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Makanan", color = TextMain) },
            text = { Text("Apakah kamu yakin ingin menghapus ${state.foodItem?.name} dari inventory?", color = TextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteItem()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = ExpiredRed)
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Makanan", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextMain) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = TextMain)
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(foodId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryGreen)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = ExpiredRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryGreen)
            } else if (state.error != null) {
                ErrorState(message = state.error ?: "Terjadi kesalahan", onRetry = { viewModel.loadFoodDetail(foodId) })
            } else {
                state.foodItem?.let { food ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header Emoji & Status Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    modifier = Modifier.size(100.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    color = BackgroundLight
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = getEmojiForCategory(food.category), fontSize = 56.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = food.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextMain
                                )
                                Text(
                                    text = food.category,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                StatusBadge(status = food.getStatus())
                            }
                        }

                        // Info Detail Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                InfoRow(label = "Jumlah Stok", value = "${food.quantity} ${food.unit}")
                                InfoRow(label = "Lokasi Simpan", value = food.storageLocation)
                                InfoRow(
                                    label = "Tanggal Expired", 
                                    value = food.expiryDate.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
                                )
                                
                                val status = food.getStatus()
                                val color = when(status) {
                                    FoodStatus.SAFE -> PrimaryGreen
                                    FoodStatus.NEAR_EXPIRY -> WarningOrange
                                    FoodStatus.EXPIRED, FoodStatus.EXPIRED_TODAY -> ExpiredRed
                                }
                                InfoRow(
                                    label = "Sisa Waktu", 
                                    value = food.getStatusLabel(),
                                    valueColor = color
                                )
                            }
                        }

                        // Recommendation Card
                        RecommendationCard(status = food.getStatus())
                        
                        // Notes Card
                        if (!food.notes.isNullOrBlank()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = CardWhite),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        text = "Catatan",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMain
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = food.notes,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Action Buttons
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { viewModel.toggleConsumed() },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (food.isConsumed) Color.Gray else PrimaryGreen
                                )
                            ) {
                                Icon(if (food.isConsumed) Icons.Default.Inventory else Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(if (food.isConsumed) "Tandai Belum Dikonsumsi" else "Tandai Sudah Dikonsumsi", fontWeight = FontWeight.Bold)
                            }

                            if (!food.isConsumed) {
                                OutlinedButton(
                                    onClick = { viewModel.toggleDiscarded() },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = if (food.isDiscarded) TextSecondary else ExpiredRed
                                    )
                                ) {
                                    Icon(if (food.isDiscarded) Icons.Default.RestoreFromTrash else Icons.Default.DeleteForever, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(if (food.isDiscarded) "Batalkan Dibuang" else "Tandai Dibuang", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(status: FoodStatus) {
    val (message, bgColor, textColor) = when(status) {
        FoodStatus.EXPIRED, FoodStatus.EXPIRED_TODAY -> Triple(
            "Makanan ini sudah melewati tanggal kedaluwarsa. Periksa kondisinya sebelum dikonsumsi.",
            ExpiredBg,
            ExpiredRed
        )
        FoodStatus.NEAR_EXPIRY -> Triple(
            "Sebaiknya konsumsi makanan ini dalam waktu dekat.",
            WarningBg,
            WarningOrange
        )
        FoodStatus.SAFE -> Triple(
            "Makanan ini masih aman disimpan.",
            SafeBg,
            PrimaryGreen
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Inventory,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, valueColor: Color = TextMain) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(
            text = value, 
            style = MaterialTheme.typography.bodyMedium, 
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, color = ExpiredRed, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Coba Lagi") }
    }
}
