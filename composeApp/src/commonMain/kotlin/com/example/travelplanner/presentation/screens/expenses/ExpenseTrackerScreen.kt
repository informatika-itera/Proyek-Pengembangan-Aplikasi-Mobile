package com.example.travelplanner.presentation.screens.expenses

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelplanner.core.util.LocalStrings
import com.example.travelplanner.domain.model.Expense
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import kotlinx.coroutines.delay

// ══════════════════════════════════════════════════════════════════════
//  CATEGORY CONSTANTS — stored in DB as fixed ID keys, never translated
//  UI labels are resolved from AppStrings at render time
// ══════════════════════════════════════════════════════════════════════

// Internal storage keys (never change — DB-stable)
object CategoryKey {
    const val FOOD          = "Konsumsi"
    const val TOURISM       = "Wisata"
    const val LODGING       = "Penginapan"
    const val TRANSPORT     = "Transportasi"
    const val ENTERTAINMENT = "Hiburan"
    const val OTHER         = "Lainnya"
}

// Category colors — constant regardless of language
private val categoryColors = mapOf(
    CategoryKey.FOOD          to Color(0xFFE63946),
    CategoryKey.TOURISM       to Color(0xFF0096C7),
    CategoryKey.LODGING       to Color(0xFF3D7A6F),
    CategoryKey.TRANSPORT     to Color(0xFFB8893A),
    CategoryKey.ENTERTAINMENT to Color(0xFF6A0572),
    CategoryKey.OTHER         to Color(0xFF7F8C8D)
)

private val categoryIcons = mapOf(
    CategoryKey.FOOD          to Icons.Default.Restaurant,
    CategoryKey.TOURISM       to Icons.Default.Place,
    CategoryKey.LODGING       to Icons.Default.Hotel,
    CategoryKey.TRANSPORT     to Icons.Default.DirectionsCar,
    CategoryKey.ENTERTAINMENT to Icons.Default.ConfirmationNumber,
    CategoryKey.OTHER         to Icons.Default.AccountBalanceWallet
)

// Currency formatter
fun formatRupiah(amount: Double): String {
    val amountInt = amount.toLong()
    if (amountInt == 0L) return "Rp 0"
    return "Rp " + amountInt.toString().reversed().chunked(3).joinToString(".").reversed()
}

// ══════════════════════════════════════════════════════════════════════
//  SCREEN
// ══════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrackerScreen(
    tripId: String?,
    onNavigateBack: () -> Unit,
    onNavigateToTrips: (() -> Unit)? = null,
    viewModel: ExpenseViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val s = LocalStrings.current
    var inputText by remember { mutableStateOf("") }
    var showVoiceAssistantModal by remember { mutableStateOf(false) }

    LaunchedEffect(tripId) { viewModel.initializeTrip(tripId ?: "") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(s.expenseTitle, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                        uiState.trip?.let {
                            Text("${s.destinationCity}: ${it.destination}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = s.back)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.trip == null) {
            Box(Modifier.fillMaxSize().padding(padding).padding(32.dp),
                contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FlightTakeoff, contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(s.noActiveTripTitle, style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(s.noActiveTripBody, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                    if (onNavigateToTrips != null) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(onClick = onNavigateToTrips) { Text(s.goToTrips) }
                    }
                }
            }
        } else {
            Box(modifier = modifier.fillMaxSize().padding(padding)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp, start = 24.dp, end = 24.dp, top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    item {
                        ExpenseSummaryCard(
                            totalExpenses = uiState.totalExpenses,
                            expenses = uiState.expenses
                        )
                    }
                    item {
                        ExpenseInsightsCard(
                            expenses = uiState.expenses,
                            totalExpenses = uiState.totalExpenses
                        )
                    }
                    item {
                        AIInputBox(
                            inputText = inputText,
                            onValueChange = { inputText = it },
                            onSend = { viewModel.addExpenseAI(inputText); inputText = "" },
                            onMicClick = {
                                com.example.travelplanner.core.util.VoiceInputManager.startVoiceInput { spokenText ->
                                    if (spokenText.startsWith("Error:")) showVoiceAssistantModal = true
                                    else viewModel.addExpenseAI(spokenText)
                                }
                            },
                            aiIsProcessing = uiState.aiIsProcessing
                        )
                    }
                    uiState.errorMessage?.let { error ->
                        item {
                            Surface(shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.ErrorOutline, contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error)
                                    Text(error, style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer)
                                }
                            }
                        }
                    }
                    item {
                        CategoryFilters(
                            selectedFilter = uiState.activeCategoryFilter,
                            onFilterSelect = { viewModel.setCategoryFilter(it) }
                        )
                    }
                    if (uiState.filteredExpenses.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null,
                                        modifier = Modifier.size(44.dp),
                                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(s.noExpenses, style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    } else {
                        items(uiState.filteredExpenses, key = { it.id }) { expense ->
                            TransactionItem(expense = expense,
                                onDelete = { viewModel.deleteExpense(expense.id) })
                        }
                    }
                }
                if (showVoiceAssistantModal) {
                    VoiceAssistantSimulator(
                        onDismiss = { showVoiceAssistantModal = false },
                        onVoiceInputGenerated = { simulatedText ->
                            viewModel.addExpenseAI(simulatedText)
                            showVoiceAssistantModal = false
                        }
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
//  COMPONENTS
// ══════════════════════════════════════════════════════════════════════

@Composable
fun ExpenseSummaryCard(totalExpenses: Double, expenses: List<Expense>) {
    val s = LocalStrings.current
    val isEn = s.seeAll == "See All"
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(expenses) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1200, easing = FastOutSlowInEasing)
        )
    }

    val categorySums = remember(expenses) {
        expenses.groupBy { it.kategori }.mapValues { it.value.sumOf { e -> e.nominal } }
    }

    val density = androidx.compose.ui.platform.LocalDensity.current
    val strokeWidthPx = remember(density) { with(density) { 12.dp.toPx() } }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = s.expenseSummaryTitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatRupiah(totalExpenses),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = s.expenseCategoryChart,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Donut Chart on Left
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        if (totalExpenses == 0.0) {
                            drawArc(
                                color = Color.LightGray.copy(alpha = 0.35f),
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = strokeWidthPx)
                            )
                        } else {
                            var currentStartAngle = -90f
                            val gap = if (categorySums.size > 1) 3f else 0f
                            categorySums.forEach { (cat, sum) ->
                                val color = categoryColors[cat] ?: Color.Gray
                                val rawSweep = 360f * (sum / totalExpenses).toFloat() * animProgress.value
                                val sweepAngle = (rawSweep - gap).coerceAtLeast(0f)
                                if (sweepAngle > 0f) {
                                    drawArc(
                                        color = color,
                                        startAngle = currentStartAngle,
                                        sweepAngle = sweepAngle,
                                        useCenter = false,
                                        style = Stroke(
                                            width = strokeWidthPx,
                                            cap = StrokeCap.Round
                                        )
                                    )
                                }
                                currentStartAngle += rawSweep
                            }
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isEn) "Spent" else "Terpakai",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (totalExpenses >= 1_000_000) {
                                "${(totalExpenses / 1_000_000).toString().take(4)}M"
                            } else if (totalExpenses >= 1_000) {
                                "${(totalExpenses / 1_000).toInt()}k"
                            } else {
                                totalExpenses.toInt().toString()
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 14.sp
                        )
                    }
                }

                // Legend on Right
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (totalExpenses == 0.0) {
                        Text(
                            text = if (isEn) "No expense data yet" else "Belum ada data pengeluaran",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                    } else {
                        categorySums.forEach { (catKey, sum) ->
                            val color = categoryColors[catKey] ?: Color.Gray
                            val label = catKey.toCatLabel(s)
                            val percentage = (sum / totalExpenses * 100).toInt()
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    text = "$percentage%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseInsightsCard(expenses: List<Expense>, totalExpenses: Double) {
    val s = LocalStrings.current
    val isEn = s.seeAll == "See All"

    if (expenses.isEmpty()) return

    val categorySums = remember(expenses) {
        expenses.groupBy { it.kategori }.mapValues { it.value.sumOf { e -> e.nominal } }
    }
    val highestCategory = remember(categorySums) {
        categorySums.maxByOrNull { it.value }
    }

    // Anomaly detector
    val largestTransaction = remember(expenses) {
        expenses.maxByOrNull { it.nominal }
    }
    val hasAnomaly = remember(expenses, totalExpenses, largestTransaction) {
        largestTransaction != null && 
        expenses.size >= 2 && 
        largestTransaction.nominal > (totalExpenses * 0.45) && 
        largestTransaction.nominal > 100_000
    }

    // Budget advice logic
    val lodgingSum = categorySums[CategoryKey.LODGING] ?: 0.0
    val transportSum = categorySums[CategoryKey.TRANSPORT] ?: 0.0
    val lodgingAndTransportRatio = if (totalExpenses > 0.0) (lodgingSum + transportSum) / totalExpenses else 0.0

    val budgetStatusText = when {
        lodgingAndTransportRatio > 0.70 -> {
            if (isEn) "Accommodation & transport make up over 70% of your expenses. Keep an eye on secondary spending like food and retail!"
            else "Akomodasi & transportasi memakan >70% anggaran. Batasi pos belanja opsional seperti konsumsi/hiburan agar aman!"
        }
        totalExpenses > 3_000_000 -> {
            if (isEn) "You have solid trip investments. Review category summaries to ensure no category is leaking funds unnecessarily."
            else "Investasi liburan Anda cukup besar. Evaluasi ringkasan kategori untuk memastikan dana tersalurkan efektif."
        }
        else -> {
            if (isEn) "Outstanding budget management! Your expenses are well-balanced and safe. Safe travels! ✓"
            else "Manajemen pengeluaran luar biasa! Distribusi anggaran sangat seimbang dan sehat. Selamat berlibur! ✓"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (isEn) "Smart Expense Resume" else "Resume Keuangan Cerdas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            // 1. Highest spending
            highestCategory?.let { (catKey, sum) ->
                val percentage = (sum / totalExpenses * 100).toInt()
                val color = categoryColors[catKey] ?: Color.Gray
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isEn) "Highest Expenditure Category" else "Pengeluaran Terbanyak",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
                            Text(
                                text = "${catKey.toCatLabel(s)} ($percentage%)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Text(
                        text = formatRupiah(sum),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            }

            // 2. Anomaly Alert (if exists)
            if (hasAnomaly && largestTransaction != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f))
                        .border(0.5.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Column {
                        Text(
                            text = if (isEn) "Expenditure Anomaly Detected" else "Terdeteksi Anomali Pengeluaran",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isEn) {
                                "Single item \"${largestTransaction.namaItem}\" costs ${formatRupiah(largestTransaction.nominal)}, consuming ${(largestTransaction.nominal / totalExpenses * 100).toInt()}% of the entire trip's expenses."
                            } else {
                                "Item tunggal \"${largestTransaction.namaItem}\" memakan biaya ${formatRupiah(largestTransaction.nominal)} atau sekitar ${(largestTransaction.nominal / totalExpenses * 100).toInt()}% dari total pengeluaran liburan."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 3. Smart Tips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = budgetStatusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun AIInputBox(
    inputText: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onMicClick: () -> Unit,
    aiIsProcessing: Boolean
) {
    val s = LocalStrings.current
    Card(shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Text(s.aiInputTitle, style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = inputText, onValueChange = onValueChange,
                    placeholder = { Text(s.aiInputPlaceholder, style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSend() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                )
                IconButton(onClick = onMicClick,
                    modifier = Modifier.size(46.dp).background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)) {
                    Icon(Icons.Default.Mic, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onSend, enabled = inputText.isNotBlank() && !aiIsProcessing,
                    modifier = Modifier.size(46.dp).background(
                        if (inputText.isNotBlank() && !aiIsProcessing) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant, CircleShape)) {
                    if (aiIsProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Send, contentDescription = s.aiInputSend,
                            tint = if (inputText.isNotBlank()) Color.White else Color.Gray, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryFilters(selectedFilter: String, onFilterSelect: (String) -> Unit) {
    val s = LocalStrings.current
    // Display labels paired with their storage keys
    val filters = listOf(
        "" to s.catAll,
        CategoryKey.FOOD          to s.catFood,
        CategoryKey.TOURISM       to s.catTourism,
        CategoryKey.LODGING       to s.catLodging,
        CategoryKey.TRANSPORT     to s.catTransport,
        CategoryKey.ENTERTAINMENT to s.catEntertainment,
        CategoryKey.OTHER         to s.catOther
    )
    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { (key, label) ->
            val isSelected = selectedFilter == key
            Surface(
                modifier = Modifier.clickable { onFilterSelect(key) }.padding(vertical = 4.dp),
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) MaterialTheme.colorScheme.secondary
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = if (!isSelected) BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)) else null
            ) {
                Text(label, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.onSecondary
                            else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun TransactionItem(expense: Expense, onDelete: () -> Unit) {
    val s = LocalStrings.current
    val color = categoryColors[expense.kategori] ?: Color.Gray
    val icon  = categoryIcons[expense.kategori]  ?: Icons.Default.AccountBalanceWallet
    val label = expense.kategori.toCatLabel(s)

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(expense.namaItem, style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Text(label, style = MaterialTheme.typography.labelSmall,
                        color = color, fontWeight = FontWeight.Bold)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(formatRupiah(expense.nominal), style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = s.delete,
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
//  VOICE ASSISTANT SIMULATOR
// ══════════════════════════════════════════════════════════════════════

@Composable
fun VoiceAssistantSimulator(onDismiss: () -> Unit, onVoiceInputGenerated: (String) -> Unit) {
    val s = LocalStrings.current
    val infiniteTransition = rememberInfiniteTransition()
    val scale1 by infiniteTransition.animateFloat(initialValue = 0.8f, targetValue = 1.6f,
        animationSpec = infiniteRepeatable(tween(600, easing = LinearEasing), RepeatMode.Reverse), label = "s1")
    val scale2 by infiniteTransition.animateFloat(initialValue = 1.3f, targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse), label = "s2")
    val scale3 by infiniteTransition.animateFloat(initialValue = 0.5f, targetValue = 1.8f,
        animationSpec = infiniteRepeatable(tween(500, easing = LinearEasing), RepeatMode.Reverse), label = "s3")
    val pulseSize by infiniteTransition.animateFloat(initialValue = 100f, targetValue = 180f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Restart), label = "ps")
    val pulseAlpha by infiniteTransition.animateFloat(initialValue = 0.4f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Restart), label = "pa")

    var simulatedText by remember { mutableStateOf(s.voiceListening) }
    var completed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(2200)
        simulatedText = "\"beli tiket ferry penyebrangan 180 ribu rupiah\""
        completed = true
        delay(1400)
        onVoiceInputGenerated("beli tiket ferry penyebrangan 180 ribu rupiah")
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.85f)).clickable { onDismiss() },
        contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)) {
            Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(pulseSize.dp).clip(CircleShape)
                    .background(Color(0xFFB8893A).copy(alpha = pulseAlpha)))
                Box(modifier = Modifier.size(100.dp).clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF1B3A5C), Color(0xFF3D7A6F))))
                    .border(2.dp, Color(0xFFB8893A), CircleShape),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            if (!completed) {
                Row(modifier = Modifier.height(60.dp), horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    listOf(scale1, scale2, scale3, scale2, scale1).forEach { sc ->
                        Box(modifier = Modifier.width(6.dp).height((35 * sc).dp)
                            .clip(RoundedCornerShape(3.dp)).background(Color(0xFFB8893A)))
                    }
                }
            } else {
                Icon(Icons.Default.CheckCircle, contentDescription = null,
                    tint = Color.Green, modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(if (!completed) s.voiceSpeakNow else s.voiceTranscribing,
                style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f),
                letterSpacing = 1.2.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Text(simulatedText, style = MaterialTheme.typography.titleMedium,
                color = Color.White, fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center, lineHeight = 24.sp)
        }
    }
}

// ── Extension: translate category storage key → display label ─────────
private fun String.toCatLabel(s: com.example.travelplanner.core.util.AppStrings): String = when (this) {
    CategoryKey.FOOD          -> s.catFood
    CategoryKey.TOURISM       -> s.catTourism
    CategoryKey.LODGING       -> s.catLodging
    CategoryKey.TRANSPORT     -> s.catTransport
    CategoryKey.ENTERTAINMENT -> s.catEntertainment
    CategoryKey.OTHER         -> s.catOther
    else -> this
}
