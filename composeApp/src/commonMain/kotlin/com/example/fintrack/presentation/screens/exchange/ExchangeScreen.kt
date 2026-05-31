package com.example.fintrack.presentation.screens.exchange

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintrack.presentation.theme.*
import com.example.fintrack.core.util.CurrencyFormatter
import org.koin.compose.viewmodel.koinViewModel

// ==================== EXCHANGE SCREEN ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExchangeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val inputAmount by viewModel.inputAmount.collectAsState()
    val baseCurrency by viewModel.baseCurrency.collectAsState()
    val targetCurrency by viewModel.targetCurrency.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Scaffold(
        containerColor = DarkBackground,
        topBar = { ExchangeTopBar() }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.fetchRates() },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextGray,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { onNavigateBack() }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Exchange Rates",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    BaseCurrencySelector(
                        selected = baseCurrency,
                        onSelected = { viewModel.updateBaseCurrency(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val isOffline = (uiState as? ExchangeUiState.Success)?.isOffline == true
                    if (isOffline) {
                        val dateStr = (uiState as? ExchangeUiState.Success)?.date ?: "Unknown Date"
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .background(Color(0xFFE6A23C).copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⚠️ Using cached rates from $dateStr",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFE6A23C),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "Retry",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = FinTrackGreen,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clickable { viewModel.fetchRates() }
                                        .padding(start = 12.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    ConverterCard(
                        uiState = uiState,
                        baseCurrency = baseCurrency,
                        targetCurrency = targetCurrency,
                        inputAmount = inputAmount,
                        onAmountChanged = { viewModel.onAmountChanged(it) },
                        onTargetSelected = { viewModel.updateTargetCurrency(it) },
                        onSwap = { viewModel.swapCurrencies() },
                        convertedAmount = { viewModel.convertedAmount() }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Live Rates",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                        if (uiState is ExchangeUiState.Success) {
                            Text(
                                "Updated ${(uiState as ExchangeUiState.Success).date}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                when (val state = uiState) {
                    is ExchangeUiState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(
                                        color = FinTrackGreen,
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Fetching live rates...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextGray
                                    )
                                }
                            }
                        }
                    }

                    is ExchangeUiState.Error -> {
                        item {
                            ErrorState(
                                message = state.message,
                                onRetry = { viewModel.fetchRates() }
                            )
                        }
                    }

                    is ExchangeUiState.Success -> {
                        items(state.rates, key = { it.currencyCode }) { rateItem ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 12.dp)
                            ) {
                                RateItem(
                                    item = rateItem,
                                    inputAmount = inputAmount.toDoubleOrNull() ?: 1.0,
                                    base = state.base
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== TOP BAR ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExchangeTopBar() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(FinTrackGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text("F", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("FinTrack", style = MaterialTheme.typography.titleLarge, color = TextWhite)
            }
        },
        actions = {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = TextGray)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
    )
}

// ==================== CONVERTER CARD ====================

@Composable
private fun ConverterCard(
    uiState: ExchangeUiState,
    baseCurrency: String,
    targetCurrency: String,
    inputAmount: String,
    onAmountChanged: (String) -> Unit,
    onTargetSelected: (String) -> Unit,
    onSwap: () -> Unit,
    convertedAmount: () -> Double?
) {
    val targetRate = (uiState as? ExchangeUiState.Success)
        ?.rates?.find { it.currencyCode == targetCurrency }?.rate
    val currencySymbol = CURRENCY_SYMBOLS[baseCurrency] ?: baseCurrency

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Amount", style = MaterialTheme.typography.labelMedium, color = TextGray)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = inputAmount,
                    onValueChange = onAmountChanged,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FinTrackGreen,
                        unfocusedBorderColor = DarkSurfaceVariant,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        cursorColor = FinTrackGreen,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    leadingIcon = {
                        Text(currencySymbol, color = TextGray, modifier = Modifier.padding(start = 4.dp))
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            CurrencyDisplay(
                code = baseCurrency,
                name = CURRENCY_DISPLAY_NAMES[baseCurrency] ?: baseCurrency
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(FinTrackGreen)
                    .clickable { onSwap() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.SwapVert,
                    contentDescription = "Swap currencies",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            InlineDropdown(
                selected = targetCurrency,
                options = SUPPORTED_CURRENCIES.filter { it != baseCurrency },
                onSelected = onTargetSelected
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "CONVERTED AMOUNT",
                style = MaterialTheme.typography.labelSmall,
                color = TextGray,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (targetRate != null) {
                Text(
                    CurrencyFormatter.formatCurrency(convertedAmount() ?: 0.0, targetCurrency),
                    style = MaterialTheme.typography.headlineLarge,
                    color = FinTrackGreen,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "1 $baseCurrency = $targetCurrency ${String.format("%.4f", targetRate)} • Live rate",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            } else {
                Text(
                    "—",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    if (uiState is ExchangeUiState.Loading) "Loading rate..." else "Rate unavailable",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
    }
}

// ==================== CURRENCY NAME MAP (UI) ====================

private val CURRENCY_DISPLAY_NAMES = mapOf(
    "USD" to "US Dollar",
    "IDR" to "Indonesian Rupiah",
    "EUR" to "Euro",
    "GBP" to "British Pound",
    "JPY" to "Japanese Yen",
    "SGD" to "Singapore Dollar",
    "AUD" to "Australian Dollar"
)

private val CURRENCY_SYMBOLS = mapOf(
    "USD" to "$",
    "IDR" to "Rp",
    "EUR" to "€",
    "GBP" to "£",
    "JPY" to "¥",
    "SGD" to "S$",
    "AUD" to "A$"
)

private val SUPPORTED_CURRENCIES = listOf("USD", "IDR", "EUR", "GBP", "JPY", "SGD", "AUD")

// ==================== INLINE DROPDOWN (used inside ConverterCard for Target) ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InlineDropdown(
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(14.dp),
            color = DarkSurfaceVariant
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(FinTrackGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        selected.take(1),
                        color = FinTrackGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(selected, style = MaterialTheme.typography.labelSmall, color = TextGray)
                    Text(
                        CURRENCY_DISPLAY_NAMES[selected] ?: selected,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextWhite,
                        fontWeight = FontWeight.Medium
                    )
                }
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = if (expanded) FinTrackGreen else TextGray
                )
            }
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(DarkCard)
        ) {
            options.forEach { currency ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (currency == selected)
                                            FinTrackGreen.copy(alpha = 0.2f)
                                        else
                                            DarkSurfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    currency.take(2),
                                    color = if (currency == selected) FinTrackGreen else TextGray,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    currency,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (currency == selected) FinTrackGreen else TextWhite,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    CURRENCY_DISPLAY_NAMES[currency] ?: currency,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextGray
                                )
                            }
                        }
                    },
                    onClick = {
                        onSelected(currency)
                        expanded = false
                    },
                    modifier = Modifier.background(
                        if (currency == selected) FinTrackGreen.copy(alpha = 0.08f) else Color.Transparent
                    )
                )
            }
        }
    }
}

// ==================== BASE CURRENCY SELECTOR ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BaseCurrencySelector(
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            "Base Currency",
            style = MaterialTheme.typography.labelMedium,
            color = TextGray
        )
        Spacer(modifier = Modifier.height(8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = "$selected  —  ${CURRENCY_DISPLAY_NAMES[selected] ?: selected}",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FinTrackGreen,
                    unfocusedBorderColor = DarkSurfaceVariant,
                    focusedContainerColor = DarkSurfaceVariant,
                    unfocusedContainerColor = DarkSurfaceVariant,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                trailingIcon = {
                    Icon(
                        if (expanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = if (expanded) FinTrackGreen else TextGray
                    )
                },
                singleLine = true
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(DarkCard)
            ) {
                SUPPORTED_CURRENCIES.forEach { currency ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (currency == selected)
                                                FinTrackGreen.copy(alpha = 0.2f)
                                            else
                                                DarkSurfaceVariant
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        currency.take(2),
                                        color = if (currency == selected) FinTrackGreen else TextGray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        currency,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (currency == selected) FinTrackGreen else TextWhite,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        CURRENCY_DISPLAY_NAMES[currency] ?: currency,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextGray
                                    )
                                }
                            }
                        },
                        onClick = {
                            onSelected(currency)
                            expanded = false
                        },
                        modifier = Modifier.background(
                            if (currency == selected) FinTrackGreen.copy(alpha = 0.08f) else Color.Transparent
                        )
                    )
                }
            }
        }
    }
}

// ==================== CURRENCY DISPLAY ====================

@Composable
private fun CurrencyDisplay(code: String, name: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = DarkSurfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(FinTrackGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    code.take(1),
                    color = FinTrackGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(code, style = MaterialTheme.typography.labelSmall, color = TextGray)
                Text(
                    name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ==================== RATE ITEM ====================

@Composable
private fun RateItem(
    item: RateDisplayItem,
    inputAmount: Double,
    base: String
) {
    val formattedRate = when {
        item.rate >= 1_000 -> item.rate.toLong().toString()
            .reversed().chunked(3).joinToString(",").reversed()
        item.rate < 0.01 -> String.format("%.6f", item.rate)
        else -> String.format("%.4f", item.rate)
    }

    val convertedValue = inputAmount * item.rate
    val formattedConverted = CurrencyFormatter.formatCurrency(convertedValue, item.currencyCode)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FinTrackGreen.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    item.currencyCode.take(2),
                    color = FinTrackGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.currencyName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    item.pair,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    formattedRate,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.SemiBold
                )
                if (inputAmount != 1.0) {
                    Text(
                        "= $formattedConverted",
                        style = MaterialTheme.typography.labelSmall,
                        color = FinTrackGreen
                    )
                }
            }
        }
    }
}

// ==================== ERROR STATE ====================

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(ErrorRed.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Failed to Load Rates",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodySmall,
                color = TextGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FinTrackGreen,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}