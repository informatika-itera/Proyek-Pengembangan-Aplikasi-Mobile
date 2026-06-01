package com.mywallet.presentation.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mywallet.presentation.components.LoadingIndicator
import com.mywallet.presentation.components.TransactionItem
import com.mywallet.utils.formatCurrency
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: StatisticsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Analisis Keuangan",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 28.sp
                        )
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is StatisticsUiState.Loading -> LoadingIndicator()
            is StatisticsUiState.Success -> {
                StatisticsContent(state, onNavigateToDetail, Modifier.padding(padding))
            }
        }
    }
}

@Composable
fun StatisticsContent(
    state: StatisticsUiState.Success,
    onNavigateToDetail: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            var selectedTab by remember { mutableStateOf(0) }
            val tabs = listOf("7 Hari", "1 Bulan", "1 Tahun")
            
            Column {
                PrimaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                val currentData = when(selectedTab) {
                    0 -> state.weeklyData
                    1 -> state.monthlyData
                    else -> state.yearlyData
                }
                
                val chartTitle = when(selectedTab) {
                    0 -> "Aktivitas 7 Hari Terakhir"
                    1 -> "Aktivitas 1 Bulan Terakhir"
                    else -> "Aktivitas 1 Tahun Terakhir"
                }

                Text(chartTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                SimpleBarChart(currentData)
            }
        }

        item {
            Text("Transaksi Terbesar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(state.topExpenses) { transaction ->
            TransactionItem(
                transaction = transaction,
                onClick = { onNavigateToDetail(transaction.id) }
            )
        }
    }
}

@Composable
fun SummaryRow(income: Double, expense: Double) {
    val balance = income - expense
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Total Saldo",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    "Rp ${formatCurrency(balance)}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("Total Masuk", style = MaterialTheme.typography.labelSmall)
                    Text("Rp ${formatCurrency(income)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("Total Keluar", style = MaterialTheme.typography.labelSmall)
                    Text("Rp ${formatCurrency(expense)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun SimpleBarChart(data: List<DailyStat>) {
    if (data.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().height(150.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Belum ada data transaksi", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    val maxAmount = data.maxOfOrNull { it.amount }?.coerceAtLeast(1.0) ?: 1.0
    
    val displayData = remember(data) {
        if (data.size < 4) {
            data + List(4 - data.size) { DailyStat("", 0.0) }
        } else {
            data
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(16.dp)
        ) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                repeat(5) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                }
            }

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                displayData.forEach { stat ->
                    val barHeightFraction = (stat.amount / maxAmount).toFloat()
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        if (stat.amount > 0) {
                            Text(
                                text = "Rp ${formatCurrency(stat.amount)}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(barHeightFraction.coerceIn(0.01f, 0.85f))
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    if (stat.day.isEmpty()) Color.Transparent 
                                    else MaterialTheme.colorScheme.primary
                                )
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = stat.day,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
