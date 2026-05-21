package com.example.mapenumkm.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.mapenumkm.domain.model.NoteCategory
import com.example.mapenumkm.presentation.components.LoadingIndicator
import com.example.mapenumkm.presentation.theme.PurpleAccent
import mapenumkm.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToProductList: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToReport: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    val greenPrimary = Color(0xFF16A34A)
    val greenLight = Color(0xFFDCFCE7)
    val blueAccent = Color(0xFF60A5FA)
    val yellowAccent = Color(0xFFFACC15)
    val purpleAccent = PurpleAccent

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(greenPrimary)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = "Notifikasi",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        bottomBar = {
            DashboardBottomNavigation(
                selectedItem = 0,
                onDashboardClick = {},
                onProdukClick = onNavigateToProductList,
                onTransaksiClick = {},
                onRiwayatClick = onNavigateToHistory,
                onLaporanClick = onNavigateToReport
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFBFBFF))
        ) {
            // Greeting Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Halo, Owner",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "👋", fontSize = 20.sp)
                        }
                        Text(
                            text = "Semoga harimu menyenangkan!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                        color = Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Hari ini", style = MaterialTheme.typography.labelLarge)
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // Stats Grid 2x2
            item {
                val successState = uiState as? HomeUiState.Success
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Total Produk",
                            value = "${successState?.totalProducts ?: 0}",
                            icon = Icons.Default.Inventory2,
                            iconBgColor = greenLight,
                            iconTint = greenPrimary
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Nilai Inventaris",
                            value = "Rp ${successState?.totalStockValue?.toInt() ?: 0}",
                            icon = Icons.Default.Payments,
                            iconBgColor = Color(0xFFE0E7FF),
                            iconTint = blueAccent
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Stok Menipis",
                            value = "${successState?.lowStockCount ?: 0}",
                            icon = Icons.Default.PriorityHigh,
                            iconBgColor = Color(0xFFFEF9C3),
                            iconTint = yellowAccent
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Kategori",
                            value = "${NoteCategory.entries.size}",
                            icon = Icons.Default.Category,
                            iconBgColor = Color(0xFFF3E8FF),
                            iconTint = purpleAccent
                        )
                    }
                }
            }

            // Produk Terlaris Section
            item {
                SectionHeader(title = "Produk Terlaris", onLihatSemua = onNavigateToProductList)
            }

            when (val state = uiState) {
                is HomeUiState.Loading -> item { LoadingIndicator() }
                is HomeUiState.Success -> {
                    val topProducts = state.notes.sortedByDescending { it.id }.take(3) // Placeholder sorting for "best selling"
                    if (topProducts.isEmpty()) {
                        item {
                            Text("Belum ada data produk", modifier = Modifier.padding(horizontal = 20.dp), color = Color.Gray)
                        }
                    } else {
                        items(topProducts) { product ->
                            ProductItem(
                                name = product.title,
                                soldCount = 25, 
                                price = product.price,
                                imageUri = product.imageUri,
                                onClick = { onNavigateToDetail(product.id) }
                            )
                        }
                    }
                }
                else -> item { 
                    Text("Belum ada data produk", modifier = Modifier.padding(horizontal = 20.dp), color = Color.Gray)
                }
            }

            // Stok Menipis Section
            item {
                SectionHeader(title = "Stok Menipis", onLihatSemua = onNavigateToProductList)
            }

            // Contoh Item Stok Menipis
            when (val state = uiState) {
                is HomeUiState.Success -> {
                    val lowStockProducts = state.notes.filter { it.stock <= 5 }.take(2)
                    if (lowStockProducts.isEmpty()) {
                        item {
                            Text("Semua stok aman", modifier = Modifier.padding(horizontal = 20.dp), color = Color.Gray)
                        }
                    } else {
                        items(lowStockProducts) { product ->
                            StockWarningItem(
                                name = product.title,
                                stockRemaining = product.stock,
                                imageUri = product.imageUri,
                                onClick = { onNavigateToProductList() }
                            )
                        }
                    }
                }
                else -> {}
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontSize = 11.sp)
                Text(value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, onLihatSemua: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Text(
            "Lihat semua",
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF16A34A),
            modifier = Modifier.clickable { onLihatSemua() }
        )
    }
}

@Composable
fun ProductItem(name: String, soldCount: Int, price: Double, imageUri: String?, onClick: () -> Unit) {
    val imageRes = when {
        name.contains("Nasi goreng", ignoreCase = true) -> Res.drawable.nasi_goreng
        name.contains("Es teler", ignoreCase = true) -> Res.drawable.Es_teler
        name.contains("Es teh", ignoreCase = true) -> Res.drawable.Es_teh
        else -> null
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onClick() },
        color = Color.Transparent
    ) {
        Column {
            Row(
                modifier = Modifier.padding(vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF3F4F6)),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (imageRes != null) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(imageRes),
                            contentDescription = name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Image, contentDescription = null, tint = Color.LightGray)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("Terjual $soldCount", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Text(
                    "Rp ${price.toInt()}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
        }
    }
}

@Composable
fun StockWarningItem(name: String, stockRemaining: Int, imageUri: String?, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF3F4F6)),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color.LightGray)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text("Stok tersisa $stockRemaining", style = MaterialTheme.typography.bodySmall, color = Color.Red)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun DashboardBottomNavigation(
    selectedItem: Int = 0,
    onDashboardClick: () -> Unit,
    onProdukClick: () -> Unit,
    onTransaksiClick: () -> Unit,
    onRiwayatClick: () -> Unit,
    onLaporanClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedItem == 0,
            onClick = onDashboardClick,
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Dashboard", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF16A34A),
                selectedTextColor = Color(0xFF16A34A),
                indicatorColor = Color(0xFFDCFCE7),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = onProdukClick,
            icon = { Icon(Icons.Outlined.Inventory2, contentDescription = null) },
            label = { Text("Produk", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = selectedItem == 2,
            onClick = onTransaksiClick,
            icon = { Icon(Icons.Default.Receipt, contentDescription = null) },
            label = { Text("Transaksi", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = selectedItem == 3,
            onClick = onRiwayatClick,
            icon = { Icon(Icons.Outlined.History, contentDescription = null) },
            label = { Text("Riwayat", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = selectedItem == 4,
            onClick = onLaporanClick,
            icon = { Icon(Icons.Outlined.BarChart, contentDescription = null) },
            label = { Text("Laporan", fontSize = 10.sp) }
        )
    }
}
