package com.example.pocketguard.presentation.screens.add_transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pocketguard.domain.model.TransactionCategory
import com.example.pocketguard.domain.model.TransactionType
import com.example.pocketguard.presentation.components.LoadingIndicator
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

/* =====================================================================
 * COLORS & CONSTANTS
 * ===================================================================== */
private val GreenDark = Color(0xFF1B5E20)
private val GreenLight = Color(0xFF43A047)
private val IncomeGreen = Color(0xFF2E7D32)
private val ExpenseRed = Color(0xFFB71C1C)
private val ExpenseRedLight = Color(0xFFE53935)

/* =====================================================================
 * MAIN SCREEN COMPOSABLE
 * ===================================================================== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    transactionId: Long?,
    initialType: String? = null,
    initialCategory: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: AddTransactionViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // State untuk DatePicker
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = uiState.createdAt
    )

    // Set initial type & category dari bottom sheet
    LaunchedEffect(initialType, initialCategory) {
        initialType?.let { typeStr ->
            val type = runCatching { TransactionType.valueOf(typeStr) }.getOrNull()
            type?.let { viewModel.onTypeChange(it) }
        }
        initialCategory?.let { catStr ->
            val category = runCatching { TransactionCategory.valueOf(catStr) }.getOrNull()
            category?.let { viewModel.onCategoryChange(it) }
        }
    }

    LaunchedEffect(transactionId) {
        transactionId?.let { viewModel.loadTransaction(it) }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AddTransactionEvent.TransactionSaved -> onNavigateBack()
                is AddTransactionEvent.Error -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    val isExpense = uiState.type == TransactionType.EXPENSE
    val accentColor = if (isExpense) ExpenseRed else IncomeGreen
    val accentColorLight = if (isExpense) ExpenseRedLight else GreenLight

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditMode) "Edit Transaksi" else "Transaksi Baru",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            LoadingIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                /* ==================== HEADER GRADIENT (NOMINAL) ==================== */
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = if (isExpense)
                                    listOf(Color(0xFF7F0000), accentColorLight)
                                else
                                    listOf(GreenDark, GreenLight)
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 28.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Toggle Pengeluaran / Pemasukan
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            TransactionType.entries.forEach { type ->
                                val isSelected = uiState.type == type
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) Color.White.copy(alpha = 0.25f)
                                            else Color.Transparent
                                        )
                                        .clickable { viewModel.onTypeChange(type) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (type == TransactionType.EXPENSE) "💸 Pengeluaran" else "💰 Pemasukan",
                                        color = Color.White,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Nominal besar
                        Text(
                            text = "Rp",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 18.sp
                        )
                        BasicAmountInput(
                            value = uiState.amount,
                            onValueChange = viewModel::onAmountChange
                        )
                        if (uiState.amountError != null) {
                            Text(
                                text = uiState.amountError!!,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                /* ==================== FORM INPUTS ==================== */
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- INPUT DESKRIPSI ---
                    Text(
                        text = "Nama Transaksi",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = viewModel::onDescriptionChange,
                        placeholder = { Text("Contoh: Makan siang kantor") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // --- INPUT KATEGORI ---
                    Text(
                        text = "Kategori",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    val availableCategories = if (uiState.type == TransactionType.INCOME) {
                        listOf(TransactionCategory.SALARY) // Jika pemasukan, HANYA tampilkan Gaji
                    } else {
                        // Jika pengeluaran, tampilkan selain Gaji
                        listOf(
                            TransactionCategory.FOOD,
                            TransactionCategory.TRANSPORT,
                            TransactionCategory.BILLS,
                            TransactionCategory.OTHER
                        )
                    }

                    if (initialCategory != null) {
                        SelectedCategoryBadge(category = uiState.category, accentColor = accentColor)
                    } else {
                        CategoryGrid(
                            selectedCategory = uiState.category,
                            availableCategories = availableCategories,
                            onCategorySelected = viewModel::onCategoryChange,
                            accentColor = accentColor
                        )
                    }

                    // --- INPUT TANGGAL (DATE PICKER) ---
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tanggal Transaksi",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedCard(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = formatDisplayDate(uiState.createdAt),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Icon(
                                imageVector = Icons.Outlined.CalendarMonth,
                                contentDescription = "Pilih Tanggal",
                                tint = accentColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                /* ==================== BUTTON SAVE ==================== */
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Button(
                        onClick = { viewModel.saveTransaction() },
                        enabled = uiState.canSave,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = if (uiState.isEditMode) "Simpan Perubahan" else "Simpan Transaksi",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        /* ==================== DATE PICKER DIALOG ==================== */
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { timestamp ->
                                viewModel.onDateChange(timestamp)
                            }
                            showDatePicker = false
                        }
                    ) { Text("OK", color = accentColor) }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Batal", color = MaterialTheme.colorScheme.outline)
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

/* =====================================================================
 * FORMATTER UTILS
 * ===================================================================== */
private fun formatNumberWithDots(text: String): String {
    if (text.isEmpty()) return ""
    val clean = text.replace(".", "")
    val parsed = clean.toLongOrNull() ?: return text

    val str = parsed.toString()
    val result = StringBuilder()
    str.reversed().forEachIndexed { index, c ->
        if (index > 0 && index % 3 == 0) result.append('.')
        result.append(c)
    }
    return result.reverse().toString()
}

private fun formatDisplayDate(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des")
    return "${dateTime.dayOfMonth} ${monthNames[dateTime.monthNumber - 1]} ${dateTime.year}"
}

/* =====================================================================
 * AMOUNT INPUT COMPONENT
 * ===================================================================== */
@Composable
private fun BasicAmountInput(
    value: String,
    onValueChange: (String) -> Unit
) {
    androidx.compose.foundation.text.BasicTextField(
        value = formatNumberWithDots(value),
        onValueChange = { newValue ->
            onValueChange(newValue.replace(".", ""))
        },
        textStyle = androidx.compose.ui.text.TextStyle(
            color = Color.White,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                if (value.isEmpty()) {
                    Text(
                        "0",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
                innerTextField()
            }
        }
    )
}

/* =====================================================================
 * CATEGORY COMPONENTS
 * ===================================================================== */
@Composable
private fun CategoryGrid(
    selectedCategory: TransactionCategory,
    availableCategories: List<TransactionCategory>,
    onCategorySelected: (TransactionCategory) -> Unit,
    accentColor: Color
) {
    val categoryEmojis = mapOf(
        TransactionCategory.FOOD to "🍜",
        TransactionCategory.TRANSPORT to "🚗",
        TransactionCategory.BILLS to "🏠",
        TransactionCategory.SALARY to "💵",
        TransactionCategory.OTHER to "📦"
    )
    val chunked = availableCategories.chunked(3)
    chunked.forEach { rowItems ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rowItems.forEach { category ->
                val isSelected = selectedCategory == category
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) accentColor.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                        .border(
                            width = if (isSelected) 1.5.dp else 0.dp,
                            color = if (isSelected) accentColor else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onCategorySelected(category) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = categoryEmojis[category] ?: "📦", fontSize = 22.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = category.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) accentColor
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            repeat(3 - rowItems.size) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun SelectedCategoryBadge(
    category: TransactionCategory,
    accentColor: Color
) {
    val categoryEmojis = mapOf(
        TransactionCategory.FOOD to "🍜",
        TransactionCategory.TRANSPORT to "🚗",
        TransactionCategory.BILLS to "🏠",
        TransactionCategory.SALARY to "💵",
        TransactionCategory.OTHER to "📦"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = 0.10f))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = categoryEmojis[category] ?: "📦",
            fontSize = 28.sp
        )
        Column {
            Text(
                text = category.displayName,
                fontWeight = FontWeight.SemiBold,
                color = accentColor,
                fontSize = 15.sp
            )
            Text(
                text = "Dipilih dari menu cepat",
                style = MaterialTheme.typography.labelSmall,
                color = accentColor.copy(alpha = 0.6f)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(20.dp)
        )
    }
}