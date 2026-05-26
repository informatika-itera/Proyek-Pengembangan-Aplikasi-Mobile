package com.example.travelplanner.presentation.screens.expenses

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.travelplanner.domain.model.Expense
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.delay
import kotlin.math.sin

// Category visual colors matching Theme
private val categoryColors = mapOf(
    "Konsumsi"     to Color(0xFFE63946), // Red
    "Wisata"      to Color(0xFF0096C7), // Blue
    "Penginapan"   to Color(0xFF3D7A6F), // Slate Teal
    "Transportasi" to Color(0xFFB8893A), // Antique Gold
    "Hiburan"     to Color(0xFF6A0572), // Purple
    "Lainnya"      to Color(0xFF7F8C8D)  // Slate Gray
)

private val categoryIcons = mapOf(
    "Konsumsi"     to Icons.Default.Restaurant,
    "Wisata"      to Icons.Default.Place,
    "Penginapan"   to Icons.Default.Hotel,
    "Transportasi" to Icons.Default.DirectionsCar,
    "Hiburan"     to Icons.Default.ConfirmationNumber,
    "Lainnya"      to Icons.Default.AccountBalanceWallet
)

// Lightweight KMP Currency Formatter
fun formatRupiah(amount: Double): String {
    val amountInt = amount.toLong()
    if (amountInt == 0L) return "Rp 0"
    return "Rp " + amountInt.toString().reversed().chunked(3).joinToString(".").reversed()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrackerScreen(
    tripId: String?,
    onNavigateBack: () -> Unit,
    viewModel: ExpenseViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var inputText by remember { mutableStateOf("") }
    var showVoiceAssistantModal by remember { mutableStateOf(false) }

    // Initialize/sync database listener
    LaunchedEffect(tripId) {
        viewModel.initializeTrip(tripId ?: "")
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Catatan Pengeluaran",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                        uiState.trip?.let {
                            Text(
                                "Tujuan: ${it.destination}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            // Elegant Empty State when no trip exists
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.FlightTakeoff,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Belum Ada Perjalanan Aktif",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Silakan buat rencana liburan dengan AI terlebih dahulu agar Anda dapat mencatat pengeluaran di sini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            val trip = uiState.trip!!

            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp, start = 24.dp, end = 24.dp, top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // ── GLASSMORPHIC SUMMARY CARD ────────────────────────────
                    item {
                        ExpenseSummaryCard(
                            totalExpenses = uiState.totalExpenses,
                            expenses = uiState.expenses
                        )
                    }

                    // ── AI EXPENSE CHAT INPUT BOX ───────────────────────────
                    item {
                        AIInputBox(
                            inputText = inputText,
                            onValueChange = { inputText = it },
                            onSend = {
                                viewModel.addExpenseAI(inputText)
                                inputText = ""
                            },
                            onMicClick = {
                                com.example.travelplanner.core.util.VoiceInputManager.startVoiceInput { spokenText ->
                                    if (spokenText.startsWith("Error:")) {
                                        // Fallback to the premium simulator modal
                                        showVoiceAssistantModal = true
                                    } else {
                                        // Directly process the voice transcription with AI
                                        viewModel.addExpenseAI(spokenText)
                                    }
                                }
                            },
                            aiIsProcessing = uiState.aiIsProcessing
                        )
                    }

                    // Error indicator from AI if any
                    uiState.errorMessage?.let { error ->
                        item {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                    Text(error, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                                }
                            }
                        }
                    }

                    // ── CATEGORY FILTER CHIPS ────────────────────────────────
                    item {
                        CategoryFilters(
                            selectedFilter = uiState.activeCategoryFilter,
                            onFilterSelect = { viewModel.setCategoryFilter(it) }
                        )
                    }

                    // ── TRANSACTION ITEMS ────────────────────────────────────
                    if (uiState.filteredExpenses.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        modifier = Modifier.size(44.dp),
                                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Belum Ada Catatan Biaya",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(uiState.filteredExpenses, key = { it.id }) { expense ->
                            TransactionItem(
                                expense = expense,
                                onDelete = { viewModel.deleteExpense(expense.id) }
                            )
                        }
                    }
                }

                // ── VOICE ASSISTANT SIMULATOR MODAL ─────────────────────────
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
fun ExpenseSummaryCard(
    totalExpenses: Double,
    expenses: List<Expense>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Total Pengeluaran Liburan",
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

            // Stacked Bar Chart Title
            Text(
                "Proporsi Berdasarkan Kategori",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Canvas Stacked horizontal Bar Chart
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                if (totalExpenses == 0.0) {
                    // Fallback gray bar when empty spendings
                    drawRect(color = Color.LightGray.copy(alpha = 0.5f))
                    return@Canvas
                }

                // Group expenses by category
                val categorySums = expenses.groupBy { it.kategori }
                    .mapValues { it.value.sumOf { e -> e.nominal } }

                var startX = 0f
                val canvasWidth = size.width

                categorySums.forEach { (category, sum) ->
                    val color = categoryColors[category] ?: Color.Gray
                    val percentage = (sum / totalExpenses).toFloat()
                    val barWidth = canvasWidth * percentage

                    drawRect(
                        color = color,
                        topLeft = androidx.compose.ui.geometry.Offset(startX, 0f),
                        size = androidx.compose.ui.geometry.Size(barWidth, size.height)
                    )
                    startX += barWidth
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Custom Legend Wrap
            val activeCategories = expenses.map { it.kategori }.distinct()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                activeCategories.take(4).forEach { category ->
                    val color = categoryColors[category] ?: Color.Gray
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Text(
                            text = category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            fontSize = 9.sp
                        )
                    }
                }
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
    Card(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    "Catat Cepat via AI Gemini",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onValueChange,
                    placeholder = {
                        Text("Ketik: 'sarapan sushi 120k'...", style = MaterialTheme.typography.bodySmall)
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSend() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                )

                // Premium Microphone trigger
                IconButton(
                    onClick = onMicClick,
                    modifier = Modifier
                        .size(46.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "Simulate Voice Input",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Send button with spinner
                IconButton(
                    onClick = onSend,
                    enabled = inputText.isNotBlank() && !aiIsProcessing,
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            if (inputText.isNotBlank() && !aiIsProcessing) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                            CircleShape
                        )
                ) {
                    if (aiIsProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Kirim",
                            tint = if (inputText.isNotBlank()) Color.White else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryFilters(
    selectedFilter: String,
    onFilterSelect: (String) -> Unit
) {
    val filters = listOf("Semua", "Konsumsi", "Wisata", "Penginapan", "Transportasi", "Hiburan", "Lainnya")
    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            val isSelected = selectedFilter == filter
            Surface(
                modifier = Modifier
                    .clickable { onFilterSelect(filter) }
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = if (!isSelected) BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)) else null
            ) {
                Text(
                    text = filter,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun TransactionItem(
    expense: Expense,
    onDelete: () -> Unit
) {
    val color = categoryColors[expense.kategori] ?: Color.Gray
    val icon = categoryIcons[expense.kategori] ?: Icons.Default.AccountBalanceWallet

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Circular color badge icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        expense.namaItem,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        expense.kategori,
                        style = MaterialTheme.typography.labelSmall,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    formatRupiah(expense.nominal),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Hapus",
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
//  PREMIUM VOICE ASSISTANT OVERLAY SIMULATOR
// ══════════════════════════════════════════════════════════════════════

@Composable
fun VoiceAssistantSimulator(
    onDismiss: () -> Unit,
    onVoiceInputGenerated: (String) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    // Waveform micro-animations
    val scaleFactor1 by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val scaleFactor2 by infiniteTransition.animateFloat(
        initialValue = 1.3f, targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val scaleFactor3 by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Pulse circles backdrops
    val pulseSize by infiniteTransition.animateFloat(
        initialValue = 100f, targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    var simulatedSpeechText by remember { mutableStateOf("Mendengarkan suara Anda...") }
    var listeningCompleted by remember { mutableStateOf(false) }

    // Simulating natural voice transcription after 2.5 seconds
    LaunchedEffect(Unit) {
        delay(2200)
        simulatedSpeechText = "\"beli tiket ferry penyebrangan 180 ribu rupiah\""
        listeningCompleted = true
        delay(1400)
        onVoiceInputGenerated("beli tiket ferry penyebrangan 180 ribu rupiah")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable { onDismiss() }, // Click outer to dismiss
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Pulse Visualizer Ring
            Box(
                modifier = Modifier
                    .size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                // Pulsing Background Circle
                Box(
                    modifier = Modifier
                        .size(pulseSize.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFB8893A).copy(alpha = pulseAlpha))
                )

                // Static Microphone Core
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF1B3A5C), Color(0xFF3D7A6F))
                            )
                        )
                        .border(2.dp, Color(0xFFB8893A), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Sine Wave bar visualizers
            if (!listeningCompleted) {
                Row(
                    modifier = Modifier.height(60.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(scaleFactor1, scaleFactor2, scaleFactor3, scaleFactor2, scaleFactor1).forEach { scale ->
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height((35 * scale).dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFFB8893A))
                        )
                    }
                }
            } else {
                // Transcribed badge icon
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.Green,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Listening status & simulated speech bubble
            Text(
                text = if (!listeningCompleted) "Bicara sekarang..." else "Mentranskripsi...",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f),
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = simulatedSpeechText,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
}
