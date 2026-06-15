package com.studyhub.presentation.screens.notification

import androidx.compose.foundation.background
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.studyhub.domain.model.NotifType
import com.studyhub.presentation.components.LoadingView
import com.studyhub.presentation.components.ErrorView
import com.studyhub.presentation.components.NotifHistoryCard
import com.studyhub.presentation.navigation.Screen
import com.studyhub.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotifHistoryScreen(navController: NavController) {
    val viewModel: NotifHistoryViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showClearConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
        viewModel.markAllRead()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Notifikasi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (uiState is NotifHistoryUiState.Success) {
                        val items = (uiState as NotifHistoryUiState.Success).items
                        if (items.isNotEmpty()) {
                            IconButton(onClick = { showClearConfirm = true }) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = "Hapus semua")
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background)) {
            when (val state = uiState) {
                is NotifHistoryUiState.Loading -> LoadingView()

                is NotifHistoryUiState.Error -> ErrorView(
                    message = state.message,
                    onRetry = { viewModel.loadHistory() }
                )

                is NotifHistoryUiState.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(Spacing.extraLarge)
                        ) {
                            Icon(
                                Icons.Default.NotificationsNone, "Kosong",
                                modifier = Modifier.size(72.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(Modifier.height(Spacing.normal))
                            Text(
                                "Belum ada notifikasi",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Notifikasi reminder akan muncul di sini",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                is NotifHistoryUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(Spacing.normal),
                        verticalArrangement = Arrangement.spacedBy(Spacing.small)
                    ) {
                        items(
                            items = state.items,
                            key = { it.id },
                            contentType = { "notif_item" }
                        ) { item ->
                            val itemId = item.id
                            val itemTaskId = item.taskId
                            
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { value ->
                                    if (value == SwipeToDismissBoxValue.EndToStart) {
                                        viewModel.deleteItem(itemId)
                                        true
                                    } else false
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                backgroundContent = {
                                    val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                        MaterialTheme.colorScheme.errorContainer
                                    } else Color.Transparent
                                    
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(color)
                                            .padding(end = Spacing.normal),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            Icons.Default.Delete, "Hapus",
                                            tint = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                },
                                enableDismissFromStartToEnd = false
                            ) {
                                NotifHistoryCard(
                                    item = item,
                                    onTap = {
                                        if (item.type == NotifType.POMODORO) {
                                            navController.navigate(Screen.Main.createRoute(openPomodoro = true)) {
                                                popUpTo(Screen.Main.route) { inclusive = true }
                                            }
                                        } else {
                                            navController.navigate(
                                                Screen.TaskDetail.createRoute(itemTaskId)
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Hapus Semua Riwayat") },
            text = { Text("Apakah Anda yakin ingin menghapus semua riwayat notifikasi?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAll()
                    showClearConfirm = false
                }) {
                    Text("Hapus Semua", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
