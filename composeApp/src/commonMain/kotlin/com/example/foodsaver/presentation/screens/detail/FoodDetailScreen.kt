package com.example.foodsaver.presentation.screens.detail

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodsaver.domain.model.FoodStatus
import com.example.foodsaver.presentation.components.StatusBadge
import com.example.foodsaver.presentation.components.getEmojiForCategory
import com.example.foodsaver.presentation.theme.*
import com.example.foodsaver.core.util.formatQuantity
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
            title = { Text("Hapus Stok Makanan") },
            text = { Text("Yakin ingin menghapus ${state.foodItem?.name} dari daftar?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteItem()
                        showDeleteDialog = false
                    },
                    modifier = Modifier.testTag("delete_food_button"),
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }, modifier = Modifier.testTag("btn_cancel_delete")) {
                    Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.testTag("food_detail_screen"),
        topBar = {
            TopAppBar(
                title = { Text("Detail Makanan", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("btn_back_detail")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(foodId) }, modifier = Modifier.testTag("btn_edit_food")) {
                        Icon(Icons.Default.Edit, contentDescription = "Ubah", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.testTag("btn_show_delete")) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
            } else if (state.error != null) {
                ErrorState(message = "Gagal memuat data nih. Coba lagi ya!", onRetry = { viewModel.loadFoodDetail(foodId) })
            } else {
                state.foodItem?.let { food ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("card_detail_header"),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    modifier = Modifier.size(100.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
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
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.testTag("txt_detail_name")
                                )
                                Text(
                                    text = food.category,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                StatusBadge(status = food.getStatus())
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("card_detail_info"),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                InfoRow(label = "Sisa stok", value = food.quantity.formatQuantity(food.unit))
                                InfoRow(label = "Disimpan di", value = food.storageLocation)
                                
                                val expiryDate = food.expiryDate.toLocalDateTime(TimeZone.currentSystemDefault()).date
                                InfoRow(
                                    label = "Batas kedaluwarsa", 
                                    value = "${expiryDate.dayOfMonth} / ${expiryDate.monthNumber} / ${expiryDate.year}"
                                )
                                
                                val status = food.getStatus()
                                val isDark = isSystemInDarkTheme()
                                val color = when(status) {
                                    FoodStatus.SAFE -> if (isDark) SafeTextDark else SafeTextLight
                                    FoodStatus.NEAR_EXPIRY -> if (isDark) WarningTextDark else WarningTextLight
                                    else -> if (isDark) ExpiredTextDark else ExpiredTextLight
                                }
                                InfoRow(
                                    label = "Status saat ini", 
                                    value = food.getStatusLabel(),
                                    valueColor = color
                                )
                            }
                        }

                        RecommendationCard(status = food.getStatus())
                        
                        if (!food.notes.isNullOrBlank()) {
                            Card(
                                modifier = Modifier.fillMaxWidth().testTag("card_detail_notes"),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text("Catatan tambahan", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = food.notes, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { viewModel.toggleConsumed() },
                                modifier = Modifier.fillMaxWidth().height(56.dp).testTag("btn_toggle_consumed"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (food.isConsumed) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(if (food.isConsumed) Icons.Default.Inventory else Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(if (food.isConsumed) "Tandai belum habis" else "Sudah saya habiskan", fontWeight = FontWeight.Bold)
                            }

                            if (!food.isConsumed) {
                                OutlinedButton(
                                    onClick = { viewModel.toggleDiscarded() },
                                    modifier = Modifier.fillMaxWidth().height(56.dp).testTag("btn_toggle_discarded"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = if (food.isDiscarded) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Icon(if (food.isDiscarded) Icons.Default.RestoreFromTrash else Icons.Default.DeleteForever, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(if (food.isDiscarded) "Batal buang stok" else "Dibuang karena rusak", fontWeight = FontWeight.Bold)
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
    val isDark = isSystemInDarkTheme()
    val (message, bgColor, textColor) = when(status) {
        FoodStatus.EXPIRED, FoodStatus.EXPIRED_TODAY -> Triple(
            "Sepertinya sudah lewat tanggalnya. Cek dulu sebelum dikonsumsi ya!",
            if (isDark) ExpiredBgDark else ExpiredBgLight,
            if (isDark) ExpiredTextDark else ExpiredTextLight
        )
        FoodStatus.NEAR_EXPIRY -> Triple(
            "Bahan ini harus segera diolah agar tidak terbuang.",
            if (isDark) WarningBgDark else WarningBgLight,
            if (isDark) WarningTextDark else WarningTextLight
        )
        FoodStatus.SAFE -> Triple(
            "Stok ini masih dalam kondisi segar dan aman disimpan.",
            if (isDark) SafeBgDark else SafeBgLight,
            if (isDark) SafeTextDark else SafeTextLight
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("card_detail_recommendation"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Inventory, contentDescription = null, tint = textColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = message, style = MaterialTheme.typography.bodyMedium, color = textColor, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(32.dp).testTag("error_state"), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text = message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Coba Lagi") }
    }
}
