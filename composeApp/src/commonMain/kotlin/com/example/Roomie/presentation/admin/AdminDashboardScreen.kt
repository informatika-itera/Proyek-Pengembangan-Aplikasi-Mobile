package com.example.Roomie.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.presentation.util.AppStrings
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppStrings.ADMIN_DASHBOARD) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is AdminUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is AdminUiState.Success -> AdminContent(state, viewModel)
                is AdminUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AdminContent(state: AdminUiState.Success, viewModel: AdminViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Ringkasan Sistem", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminStatCard(
                    label = "Pending", 
                    value = state.pendingCount.toString(), 
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    label = "High Urgency", 
                    value = state.highUrgencyCount.toString(), 
                    color = Color(0xFFF44336),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text(AppStrings.ADMIN_MANAGE_REPORTS, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(state.allReports) { report ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(report.category, fontWeight = FontWeight.Bold)
                        Text(report.status.name, color = MaterialTheme.colorScheme.primary)
                    }
                    Text(report.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.updateReportStatus(report.id, ReportStatus.IN_PROGRESS) },
                            enabled = report.status == ReportStatus.PENDING
                        ) {
                            Text("Proses")
                        }
                        OutlinedButton(
                            onClick = { viewModel.updateReportStatus(report.id, ReportStatus.DONE) },
                            enabled = report.status != ReportStatus.DONE
                        ) {
                            Text("Selesai")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineMedium, color = color, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}
