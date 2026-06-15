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
import com.studyhub.presentation.components.LoadingView
import com.studyhub.presentation.components.ErrorView
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
                title = { Text("Smart Priority", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Perbarui")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val state = uiState) {

                is SmartPriorityUiState.Idle -> {}

                is SmartPriorityUiState.Loading -> LoadingView()

                is SmartPriorityUiState.QuotaExceeded -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Spacing.extraLarge),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Warning, contentDescription = "Kuota Habis",
                            modifier = Modifier.size(72.dp),
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
                        Spacer(Modifier.height(Spacing.large))
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Kembali")
                        }
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
                                .padding(horizontal = Spacing.normal),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(
                                modifier = Modifier.padding(Spacing.small),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.extraSmall),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Cached, contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    "Dari cache — diperbarui setiap 6 jam",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                        Spacer(Modifier.height(Spacing.small))
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(Spacing.normal),
                        verticalArrangement = Arrangement.spacedBy(Spacing.small),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(
                            items = state.prioritizedTasks,
                            key = { _, pair -> pair.first.id },
                            contentType = { _, _ -> "prioritized_task" }
                        ) { index, (task, result) ->
                            val taskId = task.id
                            StaggeredItem(index = index) {
                                PriorityTaskCard(
                                    task = task,
                                    result = result,
                                    onTap = {
                                        navController.navigate(
                                            Screen.TaskDetail.createRoute(taskId)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                is SmartPriorityUiState.Error -> ErrorView(
                    message = state.message,
                    onRetry = { viewModel.refresh() }
                )
            }
        }
    }
}
