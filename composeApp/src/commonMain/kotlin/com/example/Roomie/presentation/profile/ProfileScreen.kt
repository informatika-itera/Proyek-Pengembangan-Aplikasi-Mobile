package com.example.Roomie.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.presentation.util.AppStrings
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(AppStrings.PROFILE_TITLE) })
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is ProfileUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ProfileUiState.Success -> {
                    ProfileContent(state)
                }
                is ProfileUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileContent(state: ProfileUiState.Success) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header Info
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "MULYA Delani",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "123140019",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Teknik Informatika ITERA",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Text(
                text = AppStrings.PROFILE_STATS,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(AppStrings.PROFILE_TOTAL, state.reports.size.toString(), Modifier.weight(1f))
                StatCard(AppStrings.PROFILE_PROCESSED, (state.stats[ReportStatus.IN_PROGRESS] ?: 0).toString(), Modifier.weight(1f))
                StatCard(AppStrings.PROFILE_COMPLETED, (state.stats[ReportStatus.DONE] ?: 0).toString(), Modifier.weight(1f))
            }
        }

        item {
            Text(
                text = AppStrings.PROFILE_HISTORY,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(state.reports) { report ->
            Card(modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text(report.category) },
                    supportingContent = { Text(report.location) },
                    trailingContent = { 
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = when(report.status) {
                                ReportStatus.DONE -> MaterialTheme.colorScheme.primaryContainer
                                ReportStatus.IN_PROGRESS -> MaterialTheme.colorScheme.secondaryContainer
                                ReportStatus.PENDING -> MaterialTheme.colorScheme.tertiaryContainer
                            }
                        ) {
                            Text(
                                text = report.status.displayName,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.labelMedium)
        }
    }
}
