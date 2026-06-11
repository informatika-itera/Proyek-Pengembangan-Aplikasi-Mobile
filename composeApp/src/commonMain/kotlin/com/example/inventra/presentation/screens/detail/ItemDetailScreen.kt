package com.example.inventra.presentation.screens.detail

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.inventra.core.localization.AppStrings
import com.example.inventra.domain.model.UserDivision
import com.example.inventra.presentation.components.GlassCard
import com.example.inventra.presentation.components.LoadingIndicator
import com.example.inventra.presentation.util.getDisplayName
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    itemId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit = {}
) {
    val viewModel: ItemDetailViewModel = koinViewModel { parametersOf(itemId) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isAdmin = currentUser?.role == com.example.inventra.domain.model.UserRole.ADMIN
    val strings = AppStrings.current
    val uriHandler = LocalUriHandler.current

    // Semua dialog state di level tertinggi composable
    var showBorrowDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var borrowerName by remember { mutableStateOf("") }
    var borrowError by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf("") }

    // Resolve successState di luar Scaffold agar bisa diakses di bottomBar
    val successState = uiState as? ItemDetailUiState.Success

    LaunchedEffect(showBorrowDialog) {
        if (showBorrowDialog && borrowerName.isBlank()) {
            borrowerName = currentUser?.name ?: ""
        }
    }

    LaunchedEffect(snackbarMessage) {
        if (snackbarMessage.isNotBlank()) {
            snackbarHostState.showSnackbar(snackbarMessage)
            snackbarMessage = ""
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        strings.itemName, // Or "Detail Barang" localized? User said "Detail Barang"
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.cancel,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                actions = {
                    if (successState != null && isAdmin) {
                        IconButton(onClick = { onNavigateToEdit(successState.item.id) }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = strings.editProfile,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = strings.deleteItem,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )
        },
        bottomBar = {
            // Gunakan successState yang sudah di-resolve di atas
            if (successState != null) {
                val item = successState.item
                val whatsappUrl = "https://wa.me/6287714891011?text=" +
                        "Halo Admin, saya ingin info peminjaman *${item.name}* " +
                        "dari inventaris HMIF ITERA."

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { uriHandler.openUri(whatsappUrl) },
                            modifier = Modifier
                                .weight(1.2f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                strings.contactPIC,
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Button(
                            onClick = {
                                borrowError = ""
                                showBorrowDialog = true
                            },
                            enabled = item.isBorrowable,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (item.isBorrowable) strings.borrow else strings.outOfStock,
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        when (val state = uiState) {
            is ItemDetailUiState.Loading -> {
                LoadingIndicator(modifier = Modifier.padding(paddingValues))
            }

            is ItemDetailUiState.NotFound -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        strings.itemNotFound,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            is ItemDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }

            is ItemDetailUiState.Success -> {
                val item = state.item

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Image area
                    var scale by remember { mutableStateOf(1f) }
                    var offset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    scale = (scale * zoom).coerceIn(1f, 4f)
                                    if (scale > 1f) {
                                        offset += pan
                                    } else {
                                        offset = androidx.compose.ui.geometry.Offset.Zero
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!item.imageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.name,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer(
                                        scaleX = scale,
                                        scaleY = scale,
                                        translationX = offset.x,
                                        translationY = offset.y
                                    )
                            )
                        } else {
                            Icon(
                                Icons.Default.Inventory2,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                        }

                        // Status badge
                        Surface(
                            color = if (item.isBorrowable)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                            else
                                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    if (item.isBorrowable) Icons.Default.CheckCircle
                                    else Icons.Default.Error,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (item.isBorrowable)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    (if (item.isBorrowable) strings.available else strings.borrowed).uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.isBorrowable)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            item.category.getDisplayName(strings).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        item.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    if (item.description.isNotBlank()) {
                        Text(
                            item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .padding(4.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        val locationParts = item.location.split(" ").filter { it.isNotBlank() }
                        Column {
                            locationParts.forEach { part ->
                                if (part.startsWith("http")) {
                                    Text(
                                        text = part,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold,
                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .clickable { uriHandler.openUri(part) }
                                            .padding(horizontal = 2.dp)
                                    )
                                } else {
                                    Text(
                                        text = part,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                            if (item.location.isBlank()) {
                                Text(strings.locationNotSet, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Info Card
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    strings.itemInformation,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            InfoRow(strings.condition, item.condition.getDisplayName(strings))
                            InfoRow(strings.totalStock, "${item.totalStock} unit")
                            InfoRow(strings.availableStock, "${item.availableStock} unit")
                            InfoRow(strings.pic, item.picName.ifBlank { "-" })
                            InfoRow(
                                label = strings.picPhone,
                                value = item.picPhone.ifBlank { "-" },
                                isLast = true,
                                isClickable = item.picPhone.isNotBlank(),
                                onClick = {
                                    val phone = item.picPhone.replace(Regex("[^0-9]"), "")
                                    val formattedPhone = if (phone.startsWith("0")) "62" + phone.substring(1) else phone
                                    uriHandler.openUri("https://wa.me/$formattedPhone")
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Policy Card
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Policy,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    strings.loanPolicy,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            PolicyItem(
                                Icons.Default.CalendarToday,
                                strings.maxDuration,
                                "2 Hari Kalender" // Hardcoded in current version, but should be localized if possible
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            PolicyItem(
                                Icons.Default.Payments,
                                strings.overdueFine,
                                "Rp 10.000 / Hari",
                                isError = true
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            PolicyItem(
                                Icons.Default.Person,
                                strings.admin,
                                "Nabila Ramadhani Mujahidin (Bendahara Umum)"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // ==================== BORROW DIALOG ====================
    if (showBorrowDialog) {
        AlertDialog(
            onDismissRequest = {
                showBorrowDialog = false
                borrowError = ""
            },
            title = { Text(strings.requestBorrow, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        strings.requestBorrowDesc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    OutlinedTextField(
                        value = borrowerName,
                        onValueChange = { borrowerName = it; borrowError = "" },
                        label = { Text(strings.borrowerNameLabel) },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (borrowError.isNotBlank()) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                borrowError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (borrowerName.isNotBlank()) {
                            borrowError = ""
                            viewModel.requestBorrow(
                                borrowerName = borrowerName,
                                borrowerDivision = "",   // tidak dipakai lagi
                                onSuccess = {
                                    showBorrowDialog = false
                                    borrowerName = ""
                                    borrowError = ""
                                    snackbarMessage = strings.requestSent
                                },
                                onError = { msg -> borrowError = msg }
                            )
                        }
                    },
                    enabled = borrowerName.isNotBlank()
                ) { Text(strings.submitRequest) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showBorrowDialog = false
                    borrowError = ""
                }) { Text(strings.cancel) }
            }
        )
    }

    // ==================== DELETE DIALOG ====================
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(strings.deleteItem, fontWeight = FontWeight.Bold) },
            text = {
                Text(strings.deleteItemConfirm)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteItem { onNavigateBack() }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text(strings.deleteItem) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}

// ==================== PRIVATE COMPOSABLES ====================

@Composable
private fun InfoRow(
    label: String,
    value: String,
    isLast: Boolean = false,
    isClickable: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isClickable) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            value,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isClickable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            textDecoration = if (isClickable) androidx.compose.ui.text.style.TextDecoration.Underline else null
        )
    }
    if (!isLast) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun PolicyItem(
    icon: ImageVector,
    title: String,
    desc: String,
    isError: Boolean = false
) {
    val color = if (isError) MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.secondary
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            color = color.copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .padding(8.dp)
                    .size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}