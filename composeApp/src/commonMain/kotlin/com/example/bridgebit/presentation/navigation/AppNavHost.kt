package com.example.bridgebit.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import com.example.bridgebit.data.local.datastore.UserPreferences
import com.example.bridgebit.presentation.screens.dashboard.DashboardScreen
import com.example.bridgebit.presentation.screens.detail.TranslationDetailScreen
import com.example.bridgebit.presentation.screens.workspace.WorkspaceScreen
import com.example.bridgebit.presentation.screens.vault.VaultScreen
import com.example.bridgebit.presentation.screens.insights.InsightsScreen
import com.example.bridgebit.domain.usecase.ClearAllHistoryUseCase

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navigationActions = remember(navController) { createNavigationActions(navController) }

    NavHost(
        navController = navController,
        startDestination = Route.Dashboard,
        modifier = modifier
    ) {

        composable<Route.Dashboard> {
            DashboardScreen(
                onNavigateToWorkspace = { navigationActions.navigateToWorkspace() },
                onNavigateToDetail = { id -> navigationActions.navigateToTranslationDetail(id) },
                onNavigateToAI = { navigationActions.navigateToAI() }
            )
        }

        composable<Route.Workspace> { backStackEntry ->
            val route: Route.Workspace = backStackEntry.toRoute()
            WorkspaceScreen(
                translationId = route.translationId,
                onNavigateBack = { navigationActions.navigateBack() }
            )
        }

        composable<Route.TranslationDetail> { backStackEntry ->
            val route: Route.TranslationDetail = backStackEntry.toRoute()
            TranslationDetailScreen(
                translationId = route.translationId,
                onNavigateBack = { navigationActions.navigateBack() },
                onNavigateToEdit = { id -> navigationActions.navigateToWorkspace(id) }
            )
        }

        composable<Route.Vault> {
            VaultScreen(
                onNavigateToDetail = { id -> navigationActions.navigateToTranslationDetail(id) }
            )
        }

        composable<Route.Insights> {
            InsightsScreen()
        }

        composable<Route.Settings> {
            val userPreferences: UserPreferences = koinInject()
            val clearAllHistoryUseCase: ClearAllHistoryUseCase = koinInject()
            val notificationService: com.example.bridgebit.core.notification.NotificationService = koinInject()
            val isDarkMode by userPreferences.isDarkMode.collectAsState(initial = false)
            val coroutineScope = rememberCoroutineScope()

            var profileName by remember { mutableStateOf("Nama") }
            var profileEmail by remember { mutableStateOf("email@gmail.com") }
            var isEditingProfile by remember { mutableStateOf(false) }
            val isNotificationEnabled by userPreferences.isNotificationEnabled.collectAsState(initial = false)
            
            var showClearDialog by remember { mutableStateOf(false) }
            val snackbarHostState = remember { SnackbarHostState() }

            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                item {
                    Text(
                        text = "Pengaturan",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    Text(text = "Akun & Profil", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (!isEditingProfile) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = profileName, style = MaterialTheme.typography.titleMedium)
                                        Text(text = profileEmail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                                    }
                                    IconButton(onClick = { isEditingProfile = true }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Profil")
                                    }
                                }
                            } else {
                                OutlinedTextField(
                                    value = profileName,
                                    onValueChange = { profileName = it },
                                    label = { Text("Nama Lengkap") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = profileEmail,
                                    onValueChange = { profileEmail = it },
                                    label = { Text("Alamat Email") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { isEditingProfile = false }) { Text("Simpan") }
                                    TextButton(onClick = { isEditingProfile = false }) { Text("Batal") }
                                }
                            }
                        }
                    }
                }

                item {
                    Text(text = "Tampilan", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    coroutineScope.launch { userPreferences.setDarkMode(!isDarkMode) }
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Default.DarkMode, contentDescription = null)
                                Column {
                                    Text(text = "Mode Gelap", style = MaterialTheme.typography.bodyLarge)
                                    Text(
                                        text = if (isDarkMode) "Menggunakan tema gelap" else "Menggunakan tema terang",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { checked ->
                                    coroutineScope.launch { userPreferences.setDarkMode(checked) }
                                }
                            )
                        }
                    }
                }

                item {
                    Text(text = "Aplikasi & Data", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val isChecked = !isNotificationEnabled
                                        coroutineScope.launch { userPreferences.setNotificationEnabled(isChecked) }
                                        if (isChecked) {
                                            notificationService.requestPermission { granted ->
                                                if (granted) {
                                                    notificationService.showNotification("Notifikasi Aktif", "Anda akan menerima pengingat harian dari BridgeBit!")
                                                }
                                            }
                                        }
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Icon(Icons.Default.Notifications, contentDescription = null)
                                    Text(text = "Notifikasi Pengingat", style = MaterialTheme.typography.bodyLarge)
                                }
                                Switch(
                                    checked = isNotificationEnabled,
                                    onCheckedChange = null // Handled by Row click
                                )
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showClearDialog = true }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                    Column {
                                        Text(text = "Hapus Riwayat Terjemahan", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
                                        Text(text = "Data lokal akan dibersihkan permanen", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                                TextButton(onClick = { showClearDialog = true }) {
                                    Text("Bersihkan", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }

            if (showClearDialog) {
                AlertDialog(
                    onDismissRequest = { showClearDialog = false },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Warning, contentDescription = "Peringatan", tint = MaterialTheme.colorScheme.error)
                            Text("Hapus Riwayat?")
                        }
                    },
                    text = {
                        Text("Semua riwayat terjemahan (kecuali yang disimpan di Vault) akan dihapus secara permanen dan tidak dapat dikembalikan.")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showClearDialog = false
                                coroutineScope.launch {
                                    clearAllHistoryUseCase().onSuccess {
                                        snackbarHostState.showSnackbar("Riwayat berhasil dibersihkan")
                                    }.onFailure {
                                        snackbarHostState.showSnackbar("Gagal membersihkan riwayat")
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Ya, Hapus")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showClearDialog = false }) {
                            Text("Batal")
                        }
                    }
                )
            }
            }
        }
    }
}

private fun createNavigationActions(navController: NavHostController): NavigationActions {
    return object : NavigationActions {
        override fun navigateToDashboard() {
            navController.navigate(Route.Dashboard) { popUpTo(Route.Dashboard) { inclusive = true } }
        }
        override fun navigateToWorkspace(translationId: Long?) {
            navController.navigate(Route.Workspace(translationId))
        }
        override fun navigateToTranslationDetail(translationId: Long) {
            navController.navigate(Route.TranslationDetail(translationId))
        }
        override fun navigateToAI(translationId: Long?, initialText: String?) {}
        override fun navigateToVault() { navController.navigate(Route.Vault) }
        override fun navigateToInsights() { navController.navigate(Route.Insights) }
        override fun navigateToSettings() { navController.navigate(Route.Settings) }
        override fun navigateBack() { navController.popBackStack() }
    }
}