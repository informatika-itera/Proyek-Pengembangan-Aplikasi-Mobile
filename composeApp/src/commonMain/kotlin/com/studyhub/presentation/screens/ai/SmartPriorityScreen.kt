package com.studyhub.presentation.screens.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.studyhub.data.local.AiUsageLimit
import com.studyhub.presentation.components.*
import com.studyhub.presentation.navigation.Screen
import com.studyhub.presentation.theme.Spacing
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartPriorityScreen(navController: NavController) {
    val viewModel: SmartPriorityViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadPriority() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Priority") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {

                is SmartPriorityUiState.Idle -> {}

                is SmartPriorityUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(Spacing.normal))
                        Text(
                            "AI sedang menganalisis tugas...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                is SmartPriorityUiState.QuotaExceeded -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Spacing.large),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Warning, null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(Spacing.normal))
                        Text(
                            "Batas penggunaan AI hari ini tercapai",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Maksimal ${AiUsageLimit.MAX_PRIORITY_PER_DAY}" +
                            " kali per hari. Coba lagi besok.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is SmartPriorityUiState.Success -> {
                    // Usage stats badge
                    AiUsageBadge(
                        usageStats = state.usageStats,
                        maxCalls = AiUsageLimit.MAX_PRIORITY_PER_DAY,
                        callType = "Priority"
                    )

                    if (state.fromCache) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = Spacing.normal
                                ),
                            color = MaterialTheme.colorScheme
                                .secondaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    Spacing.small
                                ),
                                horizontalArrangement = Arrangement
                                    .spacedBy(Spacing.extraSmall),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Cached, null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme
                                        .onSecondaryContainer
                                )
                                Text(
                                    "Dari cache — diperbarui setiap 6 jam",
                                    style = MaterialTheme.typography
                                        .labelSmall,
                                    color = MaterialTheme.colorScheme
                                        .onSecondaryContainer
                                )
                            }
                        }
                        Spacer(Modifier.height(Spacing.small))
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(Spacing.normal),
                        verticalArrangement = Arrangement.spacedBy(
                            Spacing.small
                        )
                    ) {
                        itemsIndexed(
                            items = state.prioritizedTasks,
                            key = { _, pair -> pair.first.id }
                        ) { index, (task, result) ->
                            StaggeredItem(index = index) {
                                PriorityTaskCard(
                                    task = task,
                                    result = result,
                                    onTap = {
                                        navController.navigate(
                                            Screen.TaskDetail
                                                .createRoute(task.id)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                is SmartPriorityUiState.Error -> {
                    Column(
                        modifier = Modifier.padding(Spacing.normal)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme
                                    .colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                "AI tidak tersedia: ${state.message}" +
                                "\nMenumpilkan urutan lokal.",
                                modifier = Modifier.padding(
                                    Spacing.medium
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme
                                    .onErrorContainer
                            )
                        }
                        Spacer(Modifier.height(Spacing.normal))
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(
                                Spacing.small
                            )
                        ) {
                            itemsIndexed(
                                items = state.fallbackTasks,
                                key = { _, task -> task.id }
                            ) { index, task ->
                                StaggeredItem(index = index) {
                                    TaskCard(
                                        task = task,
                                        onEdit = {},
                                        onDelete = {},
                                        onClick = {
                                            navController.navigate(
                                                Screen.TaskDetail
                                                    .createRoute(task.id)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
