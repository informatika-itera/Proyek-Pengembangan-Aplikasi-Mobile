package com.example.foodsaver.presentation.screens.detail

import androidx.compose.animation.*
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
import com.example.foodsaver.core.utility.formatQuantity
import com.example.foodsaver.core.utility.formatToDisplay
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.model.FoodStatus
import com.example.foodsaver.presentation.components.ErrorState
import com.example.foodsaver.presentation.components.StatusBadge
import com.example.foodsaver.presentation.components.getEmojiForCategory
import com.example.foodsaver.presentation.theme.*
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
        DeleteConfirmationDialog(
            foodName = state.foodItem?.name ?: "",
            onConfirm = {
                viewModel.deleteItem()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        modifier = Modifier.testTag("food_detail_screen"),
        topBar = {
            FoodDetailTopBar(
                onBackClick = onNavigateBack,
                onEditClick = { onNavigateToEdit(foodId) },
                onDeleteClick = { showDeleteDialog = true }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
                }
                state.error != null -> {
                    ErrorState(message = "Gagal memuat data nih. Coba lagi ya!", onRetry = { viewModel.loadFoodDetail(foodId) })
                }
                state.foodItem != null -> {
                    FoodDetailContent(
                        food = state.foodItem!!,
                        onToggleConsumed = viewModel::toggleConsumed,
                        onToggleDiscarded = viewModel::toggleDiscarded
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodDetailTopBar(
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    TopAppBar(
        title = { Text("Detail Makanan", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) },
        navigationIcon = {
            IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_back_detail")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
        },
        actions = {
            IconButton(onClick = onEditClick, modifier = Modifier.testTag("btn_edit_food")) {
                Icon(Icons.Default.Edit, contentDescription = "Ubah", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDeleteClick, modifier = Modifier.testTag("btn_show_delete")) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
private fun FoodDetailContent(
    food: FoodItem,
    onToggleConsumed: () -> Unit,
    onToggleDiscarded: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FoodHeaderCard(food)

        FoodInfoCard(food)

        FoodRecommendationBanner(status = food.getStatus())
        
        if (!food.notes.isNullOrBlank()) {
            FoodNotesCard(notes = food.notes)
        }

        Spacer(modifier = Modifier.height(8.dp))
        
        FoodActionButtons(
            isConsumed = food.isConsumed,
            isDiscarded = food.isDiscarded,
            onToggleConsumed = onToggleConsumed,
            onToggleDiscarded = onToggleDiscarded
        )
    }
}

@Composable
private fun FoodHeaderCard(food: FoodItem) {
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
}

@Composable
private fun FoodInfoCard(food: FoodItem) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("card_detail_info"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            InfoRow(label = "Sisa stok", value = food.quantity.formatQuantity(food.unit))
            InfoRow(label = "Disimpan di", value = food.storageLocation)
            InfoRow(label = "Batas kedaluwarsa", value = food.expiryDate.formatToDisplay())
            
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
}

@Composable
private fun FoodRecommendationBanner(status: FoodStatus) {
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
private fun FoodNotesCard(notes: String) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("card_detail_notes"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Catatan tambahan", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = notes, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun FoodActionButtons(
    isConsumed: Boolean,
    isDiscarded: Boolean,
    onToggleConsumed: () -> Unit,
    onToggleDiscarded: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = onToggleConsumed,
            modifier = Modifier.fillMaxWidth().height(56.dp).testTag("btn_toggle_consumed"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isConsumed) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(if (isConsumed) Icons.Default.Inventory else Icons.Default.CheckCircle, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (isConsumed) "Tandai belum habis" else "Sudah saya habiskan", fontWeight = FontWeight.Bold)
        }

        if (!isConsumed) {
            OutlinedButton(
                onClick = onToggleDiscarded,
                modifier = Modifier.fillMaxWidth().height(56.dp).testTag("btn_toggle_discarded"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isDiscarded) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                )
            ) {
                Icon(if (isDiscarded) Icons.Default.RestoreFromTrash else Icons.Default.DeleteForever, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (isDiscarded) "Batal buang stok" else "Dibuang karena rusak", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
private fun DeleteConfirmationDialog(
    foodName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hapus Stok Makanan") },
        text = { Text("Yakin ingin menghapus $foodName dari daftar?") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                modifier = Modifier.testTag("delete_food_button"),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Hapus", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.testTag("btn_cancel_delete")) {
                Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}
