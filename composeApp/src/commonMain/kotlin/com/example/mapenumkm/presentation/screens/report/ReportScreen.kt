package com.example.mapenumkm.presentation.screens.report

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.mapenumkm.presentation.screens.history.formatAmount
import com.example.mapenumkm.presentation.screens.home.DashboardBottomNavigation
import com.example.mapenumkm.presentation.screens.home.SectionHeader
import com.example.mapenumkm.presentation.screens.home.StatCard
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToProduct: () -> Unit,
    onNavigateToTransaction: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: ReportViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    val greenPrimary = Color(0xFF16A34A)
    val greenLight = Color(0xFFDCFCE7)
    val blueAccent = Color(0xFF60A5FA)
    val blueLight = Color(0xFFE0F2FE)
    val yellowAccent = Color(0xFFFACC15)
    val yellowLight = Color(0xFFFEF9C3)
    val purpleAccent = Color(0xFFA78BFA)
    val purpleLight = Color(0xFFF3E8FF)

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(greenPrimary)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Laporan Penjualan",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        bottomBar = {
            DashboardBottomNavigation(
                selectedItem = 4,
                onDashboardClick = onNavigateToDashboard,
                onProdukClick = onNavigateToProduct,
                onTransaksiClick = onNavigateToTransaction,
                onRiwayatClick = onNavigateToHistory,
                onLaporanClick = {}
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFBFBFF))
        ) {
            Spacer(modifier = Modifier.height(8.dp))


            // Tabs for Daily, Weekly, Monthly
            TabRow(
                selectedTabIndex = uiState.selectedFilter.ordinal,
                containerColor = Color.Transparent,
                contentColor = greenPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[uiState.selectedFilter.ordinal]),
                        color = greenPrimary
                    )
                },
                divider = {}
            ) {
                ReportFilter.entries.forEach { filter ->
                    Tab(
                        selected = uiState.selectedFilter == filter,
                        onClick = { viewModel.onFilterSelected(filter) },
                        text = {
                            Text(
                                when (filter) {
                                    ReportFilter.DAILY -> "Harian"
                                    ReportFilter.WEEKLY -> "Mingguan"
                                    ReportFilter.MONTHLY -> "Bulanan"
                                },
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )
                }
            }

            // Date Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.onPreviousDate() }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous")
                }

                Row(
                    modifier = Modifier.clickable { showDatePicker = true },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = greenPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        formatSelectedDate(uiState.selectedDate, uiState.selectedFilter),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }

                IconButton(onClick = { viewModel.onNextDate() }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next")
                }
            }

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = uiState.selectedDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
                )
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date = Instant.fromEpochMilliseconds(millis)
                                    .toLocalDateTime(TimeZone.UTC).date
                                viewModel.onDateSelected(date)
                            }
                            showDatePicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
                    }
                ) {
                    DatePicker(
                        state = datePickerState,
                        colors = DatePickerDefaults.colors(
                            selectedDayContainerColor = greenPrimary,
                            todayContentColor = greenPrimary,
                            todayDateBorderColor = greenPrimary
                        )
                    )
                }
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                // Summary Cards
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            StatCard(
                                modifier = Modifier.weight(1f),
                                title = "Total Penjualan",
                                value = "Rp ${formatAmount(uiState.totalSales)}",
                                icon = Icons.AutoMirrored.Filled.TrendingUp,
                                iconBgColor = greenLight,
                                iconTint = greenPrimary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            StatCard(
                                modifier = Modifier.weight(1f),
                                title = "Transaksi",
                                value = uiState.totalTransactions.toString(),
                                icon = Icons.Default.Receipt,
                                iconBgColor = blueLight,
                                iconTint = blueAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            StatCard(
                                modifier = Modifier.weight(1f),
                                title = "Produk Terjual",
                                value = uiState.totalProductsSold.toString(),
                                icon = Icons.Default.Inventory2,
                                iconBgColor = purpleLight,
                                iconTint = purpleAccent
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            StatCard(
                                modifier = Modifier.weight(1f),
                                title = "Rata-rata per Transaksi",
                                value = "Rp ${formatAmount(uiState.averageTransactionValue)}",
                                icon = Icons.Default.Payments,
                                iconBgColor = yellowLight,
                                iconTint = yellowAccent
                            )
                        }
                    }
                }

                // Sales Graph Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Grafik Penjualan",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            if (uiState.graphData.isNotEmpty()) {
                                SalesGraph(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp),
                                    lineColor = greenPrimary,
                                    data = uiState.graphData
                                )
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(180.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Tidak ada data untuk grafik", color = Color.Gray)
                                }
                            }
                        }
                    }
                }

                // Produk Terlaris Section
                item {
                    SectionHeader(title = "Produk Terlaris", onLihatSemua = { /* Action */ })
                }

                if (uiState.topProducts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Belum ada data produk", color = Color.Gray)
                        }
                    }
                } else {
                    items(uiState.topProducts.take(5).size) { index ->
                        val product = uiState.topProducts[index]
                        TopProductItem(
                            product = product,
                            rank = index + 1
                        )
                    }
                }
                
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun TopProductItem(
    product: TopProduct,
    rank: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Badge
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when (rank) {
                            1 -> Color(0xFFFFD700) // Gold
                            2 -> Color(0xFFC0C0C0) // Silver
                            3 -> Color(0xFFCD7F32) // Bronze
                            else -> Color(0xFFF3F4F6)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rank.toString(),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (rank <= 3) Color.White else Color.Gray
                    )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Product Image
            Surface(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(8.dp),
                color = if (product.imageUrl == null) {
                    // Berikan warna background yang berbeda berdasarkan nama produk jika tidak ada foto
                    val colors = listOf(Color(0xFFDCFCE7), Color(0xFFE0F2FE), Color(0xFFF3E8FF), Color(0xFFFFEDD5))
                    colors[product.name.length % colors.size]
                } else {
                    Color(0xFFF3F4F6)
                }
            ) {
                if (product.imageUrl != null && product.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = Color.Gray.copy(alpha = 0.5f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Product Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
                Text(
                    text = "${product.quantity} terjual",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Total Sales
            Text(
                text = "Rp ${formatAmount(product.totalSales)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF16A34A)
                )
            )
        }
    }
}

fun formatSelectedDate(date: LocalDate, filter: ReportFilter): String {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    
    return when (filter) {
        ReportFilter.DAILY -> {
            if (date == today) "Hari Ini"
            else "${date.dayOfMonth} ${getMonthName(date.monthNumber)} ${date.year}"
        }
        ReportFilter.WEEKLY -> {
            if (date == today) "Minggu Ini"
            else "Sampai ${date.dayOfMonth} ${getMonthName(date.monthNumber)}"
        }
        ReportFilter.MONTHLY -> {
            "${getMonthName(date.monthNumber)} ${date.year}"
        }
    }
}

fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "Januari"
        2 -> "Februari"
        3 -> "Maret"
        4 -> "April"
        5 -> "Mei"
        6 -> "Juni"
        7 -> "Juli"
        8 -> "Agustus"
        9 -> "September"
        10 -> "Oktober"
        11 -> "November"
        12 -> "Desember"
        else -> ""
    }
}

@Composable
fun SalesGraph(
    modifier: Modifier = Modifier,
    lineColor: Color,
    data: List<ChartData>
) {
    val maxValue = data.maxOfOrNull { it.value }?.takeIf { it > 0 } ?: 1f
    
    Column(modifier = modifier) {
        Canvas(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val width = size.width
            val height = size.height
            val spacing = width / (data.size - 1).coerceAtLeast(1)
            
            // Draw grid lines (horizontal)
            val gridLines = 4
            for (i in 0 until gridLines) {
                val y = height - (height / (gridLines - 1)) * i
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            if (data.size > 1) {
                val path = Path().apply {
                    data.forEachIndexed { index, chartData ->
                        val x = index * spacing
                        val y = height - (chartData.value / maxValue * height)
                        if (index == 0) moveTo(x, y) else lineTo(x, y)
                    }
                }
                
                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                // Draw Area Fill
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(lineColor.copy(alpha = 0.3f), Color.Transparent)
                    )
                )

                // Draw Line
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 3.dp.toPx())
                )
                
                // Draw Points
                data.forEachIndexed { index, chartData ->
                    val x = index * spacing
                    val y = height - (chartData.value / maxValue * height)
                    drawCircle(
                        color = lineColor,
                        radius = 4.dp.toPx(),
                        center = Offset(x, y)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            } else if (data.size == 1) {
                val x = width / 2
                val y = height - (data[0].value / maxValue * height)
                drawCircle(
                    color = lineColor,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
        
        // X-Axis Labels
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Show fewer labels if there are too many data points
            val labelCount = if (data.size > 6) 5 else data.size
            
            for (i in 0 until labelCount) {
                val index = if (labelCount > 1) (i * (data.size - 1)) / (labelCount - 1) else 0
                Text(
                    text = data[index].label,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier.widthIn(max = 40.dp)
                )
            }
        }
    }
}
