package com.example.travelplanner.presentation.screens.summary

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelplanner.core.util.LocalStrings
import com.example.travelplanner.domain.model.Expense
import com.example.travelplanner.presentation.screens.expenses.CategoryKey
import com.example.travelplanner.presentation.screens.expenses.ExpenseViewModel
import com.example.travelplanner.presentation.screens.expenses.formatRupiah
import org.koin.compose.viewmodel.koinViewModel

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

private fun String.toCatLabel(s: com.example.travelplanner.core.util.AppStrings): String = when (this) {
    CategoryKey.FOOD -> s.catFood
    CategoryKey.TOURISM -> s.catTourism
    CategoryKey.LODGING -> s.catLodging
    CategoryKey.TRANSPORT -> s.catTransport
    CategoryKey.ENTERTAINMENT -> s.catEntertainment
    else -> s.catOther
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripSummaryScreen(
    tripId: String,
    onNavigateBack: () -> Unit,
    onNavigateToExpenses: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: ExpenseViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val s = LocalStrings.current

    LaunchedEffect(tripId) {
        viewModel.initializeTrip(tripId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(if (s.isEnglish) "Trip Summary" else "Ringkasan Perjalanan", fontWeight = FontWeight.SemiBold) },
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
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.errorMessage != null) {
            Box(Modifier.fillMaxSize().padding(padding).padding(24.dp), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
                    Text(uiState.errorMessage ?: s.failedLoad, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    Button(onClick = { viewModel.initializeTrip(tripId) }) {
                        Text(if (s.isEnglish) "Retry" else "Coba Lagi")
                    }
                }
            }
        } else {
            val trip = uiState.trip
            if (trip == null) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(if (s.isEnglish) "Trip not found" else "Trip tidak ditemukan")
                }
            } else {
                val expenses = uiState.expenses
                val totalExpenses = uiState.totalExpenses

                LazyColumn(
                    modifier = modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // ── TRIP TITLE CARD ───────────────────────────────────
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Text(
                                        text = trip.destination,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Text(
                                    text = trip.duration.substringAfter("|"),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = trip.vibe.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                    }

                    // ── FINANCE TOTAL CARD ────────────────────────────────
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (s.isEnglish) "TOTAL TRIP EXPENDITURE" else "TOTAL PENGELUARAN TRIP",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = formatRupiah(totalExpenses),
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = if (s.isEnglish) "${expenses.size} expense items recorded" else "${expenses.size} item pengeluaran tercatat",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // ── DONUT CHART & CATEGORIES SPLIT ────────────────────
                    item {
                        val categorySums = remember(expenses) {
                            expenses.groupBy { it.kategori }
                                .mapValues { (_, list) -> list.sumOf { it.nominal } }
                                .toList()
                                .sortedByDescending { it.second }
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp
                        ) {
                            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text(
                                    text = s.expenseCategoryChart,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                if (totalExpenses == 0.0) {
                                    Text(
                                        text = if (s.isEnglish) "No expense items recorded yet" else "Belum ada item pengeluaran tercatat",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                        textAlign = TextAlign.Center
                                    )
                                } else {
                                    // Custom Donut Chart Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.size(100.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val animProgress = remember { Animatable(0f) }
                                            LaunchedEffect(expenses) {
                                                animProgress.animateTo(1f, animationSpec = tween(1000, easing = FastOutSlowInEasing))
                                            }

                                            Canvas(modifier = Modifier.size(100.dp)) {
                                                val strokeWidthPx = 10.dp.toPx()
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
                                                            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                                                        )
                                                    }
                                                    currentStartAngle += rawSweep
                                                }
                                            }
                                        }

                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            categorySums.take(4).forEach { (catKey, sum) ->
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
                                                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
                                                        Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                                    }
                                                    Text("$percentage%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color)
                                                }
                                            }
                                        }
                                    }

                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 8.dp))

                                    // Category list with progress bars
                                    categorySums.forEach { (catKey, sum) ->
                                        val color = categoryColors[catKey] ?: Color.Gray
                                        val label = catKey.toCatLabel(s)
                                        val percentage = (sum / totalExpenses).toFloat()
                                        val pctText = (sum / totalExpenses * 100).toInt()

                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Row(
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                                Text(formatRupiah(sum), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                            }
                                            LinearProgressIndicator(
                                                progress = { percentage },
                                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                                color = color,
                                                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── ITEMIZED LIST ─────────────────────────────────────
                    item {
                        Text(
                            text = if (s.isEnglish) "Expense Items" else "Rincian Pengeluaran",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (expenses.isEmpty()) {
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                tonalElevation = 1.dp
                            ) {
                                Text(
                                    text = if (s.isEnglish) "No expenses recorded yet." else "Belum ada pengeluaran tercatat.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(20.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(expenses) { expense ->
                            val color = categoryColors[expense.kategori] ?: Color.Gray
                            val icon = categoryIcons[expense.kategori] ?: Icons.Default.AccountBalanceWallet
                            val label = expense.kategori.toCatLabel(s)

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                tonalElevation = 1.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = color.copy(alpha = 0.12f),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(expense.namaItem, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f))
                                    }
                                    Text(
                                        text = formatRupiah(expense.nominal),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // ── BUTTONS ROW ───────────────────────────────────────
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            Button(
                                onClick = { onNavigateToExpenses(trip.id) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(if (s.isEnglish) "Manage Expenses" else "Kelola Pengeluaran", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = onNavigateToHome,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Home, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(if (s.isEnglish) "Back to Home" else "Kembali ke Beranda", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
