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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mapenumkm.presentation.screens.home.DashboardBottomNavigation
import com.example.mapenumkm.presentation.screens.home.SectionHeader
import com.example.mapenumkm.presentation.screens.home.StatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToProduct: () -> Unit,
    onNavigateToTransaksi: () -> Unit,
    onNavigateToRiwayat: () -> Unit,
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Harian", "Mingguan", "Bulanan")
    
    val greenPrimary = Color(0xFF16A34A)
    val backgroundGray = Color(0xFFFBFBFF)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Laporan Penjualan",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Calendar action */ }) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Kalender",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = greenPrimary
                )
            )
        },
        bottomBar = {
            DashboardBottomNavigation(
                selectedItem = 4,
                onDashboardClick = onNavigateToDashboard,
                onProdukClick = onNavigateToProduct,
                onTransaksiClick = onNavigateToTransaksi,
                onRiwayatClick = onNavigateToRiwayat,
                onLaporanClick = {}
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundGray)
        ) {
            // Tabs
            item {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = greenPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = greenPrimary
                        )
                    },
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedTabIndex == index) greenPrimary else Color.Gray
                                    )
                                )
                            }
                        )
                    }
                }
            }

            // Date Selector
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* Prev Date */ }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color.Gray)
                    }
                    Text(
                        text = "20 Mei 2025",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    IconButton(onClick = { /* Next Date */ }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                    }
                }
            }

            // Stats Grid 2x2
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Total Penjualan",
                            value = "Rp 2.450.000",
                            icon = Icons.Default.Description,
                            iconBgColor = Color(0xFFDCFCE7),
                            iconTint = greenPrimary
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Total Transaksi",
                            value = "35",
                            icon = Icons.Default.ReceiptLong,
                            iconBgColor = Color(0xFFE0E7FF),
                            iconTint = Color(0xFF60A5FA)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Produk Terjual",
                            value = "120",
                            icon = Icons.Default.Inventory2,
                            iconBgColor = Color(0xFFFEF9C3),
                            iconTint = Color(0xFFFACC15)
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Rata-rata per Transaksi",
                            value = "Rp 70.000",
                            icon = Icons.Default.Payments,
                            iconBgColor = Color(0xFFFFEDD5),
                            iconTint = Color(0xFFF97316)
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
                        
                        SalesGraph(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            lineColor = greenPrimary
                        )
                    }
                }
            }

            // Produk Terlaris Section
            item {
                SectionHeader(title = "Produk Terlaris", onLihatSemua = { /* Action */ })
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun SalesGraph(modifier: Modifier = Modifier, lineColor: Color) {
    val points = listOf(0.1f, 0.3f, 0.25f, 0.5f, 0.35f, 0.65f, 0.75f, 0.95f, 0.7f, 1.0f)
    
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val spacing = width / (points.size - 1)
        
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

        val path = Path().apply {
            points.forEachIndexed { index, value ->
                val x = index * spacing
                val y = height - (value * height)
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
        points.forEachIndexed { index, value ->
            val x = index * spacing
            val y = height - (value * height)
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
    }
    
    // X-Axis Labels
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val labels = listOf("00:00", "06:00", "12:00", "18:00", "23:59")
        labels.forEach { label ->
            Text(label, fontSize = 10.sp, color = Color.Gray)
        }
    }
}
