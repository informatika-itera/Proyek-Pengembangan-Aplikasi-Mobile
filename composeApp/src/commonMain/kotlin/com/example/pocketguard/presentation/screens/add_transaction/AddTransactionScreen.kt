package com.example.pocketguard.presentation.screens.add_transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pocketguard.domain.model.TransactionCategory
import com.example.pocketguard.domain.model.TransactionType
import com.example.pocketguard.presentation.components.LoadingIndicator
import com.example.pocketguard.presentation.theme.PgDanger
import com.example.pocketguard.presentation.theme.PgPrimary
import com.example.pocketguard.presentation.theme.PgPrimaryLight
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    transactionId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: AddTransactionViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Memuat data secara otomatis jika dalam mode edit transaksi
    LaunchedEffect(transactionId) {
        transactionId?.let { viewModel.loadTransaction(it) }
    }

    // Mendengarkan event penyimpanan dari channel arsitektur Anda
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AddTransactionEvent.TransactionSaved -> onNavigateBack()
                is AddTransactionEvent.Error -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isEditMode) "Edit Transaksi" else "Transaksi Baru",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
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
                    .padding(horizontal = 18.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // 1. TYPE TOGGLE - Terhubung langsung ke viewModel::onTypeChange
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isExpense = uiState.type == TransactionType.EXPENSE
                    val isIncome = uiState.type == TransactionType.INCOME

                    // Pengeluaran Chip Button
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.onTypeChange(TransactionType.EXPENSE) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isExpense) Color(0xFFFEE8CC).copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isExpense) PgDanger else Color.Transparent
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Pengeluaran",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isExpense) PgDanger else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Pemasukan Chip Button
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.onTypeChange(TransactionType.INCOME) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isIncome) PgPrimaryLight else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isIncome) PgPrimary else Color.Transparent
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Pemasukan",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isIncome) PgPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 2. BIG AMOUNT DISPLAY - Terhubung ke uiState.amount & viewModel::onAmountChange
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = uiState.amount,
                        onValueChange = viewModel::onAmountChange,
                        textStyle = TextStyle(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = if (uiState.amountError != null) PgDanger else MaterialTheme.colorScheme.onSurface,
                            letterSpacing = (-1).sp
                        ),
                        placeholder = {
                            Text(
                                text = "Rp 0",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = PgPrimary,
                            unfocusedIndicatorColor = PgPrimary.copy(alpha = 0.5f),
                            errorIndicatorColor = PgDanger
                        ),
                        isError = uiState.amountError != null,
                        modifier = Modifier.fillMaxWidth(0.85f)
                    )

                    // Indikator Pesan Error Validasi Nominal dari ViewModel Anda
                    AnimatedVisibility(visible = uiState.amountError != null) {
                        uiState.amountError?.let {
                            Text(
                                text = it,
                                color = PgDanger,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // 3. FIELD DESCRIPTION - Terhubung ke uiState.description & viewModel::onDescriptionChange
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Nama transaksi",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = viewModel::onDescriptionChange,
                        placeholder = { Text("Contoh: Makan siang kantor") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PgPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }

                // 4. CATEGORY GRID SELECTOR - Terhubung ke uiState.category & viewModel::onCategoryChange
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Kategori",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val categories = TransactionCategory.entries
                    val chunkedCategories = categories.chunked(3) // Distribusi grid 3 kolom per baris

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        chunkedCategories.forEach { rowCategories ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowCategories.forEach { category ->
                                    val isSelected = uiState.category == category
                                    val emoji = when (category) {
                                        TransactionCategory.FOOD -> "🍜"
                                        TransactionCategory.TRANSPORT -> "🚗"
                                        TransactionCategory.BILLS -> "🏠"
                                        TransactionCategory.SALARY -> "💰"
                                        TransactionCategory.OTHER -> "📦"
                                    }

                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.onCategoryChange(category) },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) PgPrimaryLight else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        border = BorderStroke(
                                            width = 0.5.dp,
                                            color = if (isSelected) PgPrimary else MaterialTheme.colorScheme.outlineVariant
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$emoji ${category.displayName}",
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                                color = if (isSelected) PgPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }

                                // Penyeimbang kolom baris terakhir agar simetris
                                if (rowCategories.size < 3) {
                                    repeat(3 - rowCategories.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // 5. BOTTOM SAVE BUTTON - Terhubung ke viewModel.saveTransaction() & uiState.canSave
                Button(
                    onClick = { viewModel.saveTransaction() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(bottom = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PgPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = uiState.canSave
                ) {
                    Text(
                        text = if (uiState.isEditMode) "Perbarui Transaksi" else "Simpan Transaksi",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.canSave) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }
            }
        }
    }
}