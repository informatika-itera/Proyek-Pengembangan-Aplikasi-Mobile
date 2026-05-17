package com.example.bridgebit.presentation.navigation

// --- SEMUA IMPORT WAJIB COMPOSE, ROUTING, DAN KOIN ---
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

// --- IMPORT LAYOUT SCREEN DAN PREFERENCES PROYEK ---
import com.example.bridgebit.data.local.datastore.UserPreferences
import com.example.bridgebit.presentation.screens.dashboard.DashboardScreen
import com.example.bridgebit.presentation.screens.detail.TranslationDetailScreen
import com.example.bridgebit.presentation.screens.workspace.WorkspaceScreen

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

        // 1. DASHBOARD SCREEN (RIWAYAT)
        composable<Route.Dashboard> {
            DashboardScreen(
                onNavigateToWorkspace = { navigationActions.navigateToWorkspace() },
                onNavigateToDetail = { id -> navigationActions.navigateToTranslationDetail(id) },
                onNavigateToAI = { navigationActions.navigateToAI() }
            )
        }

        // 2. WORKSPACE SCREEN (INPUT & EDIT TERJEMAHAN)
        composable<Route.Workspace> { backStackEntry ->
            val route: Route.Workspace = backStackEntry.toRoute()
            WorkspaceScreen(
                translationId = route.translationId, // Mengirimkan ID jika dalam mode Edit
                onNavigateBack = { navigationActions.navigateBack() }
            )
        }

        // 3. TRANSLATION DETAIL SCREEN (LAYAR KETIGA)
        composable<Route.TranslationDetail> { backStackEntry ->
            val route: Route.TranslationDetail = backStackEntry.toRoute()
            TranslationDetailScreen(
                translationId = route.translationId,
                onNavigateBack = { navigationActions.navigateBack() },
                onNavigateToEdit = { id -> navigationActions.navigateToWorkspace(id) } // Aksi Edit dirutekan ke Workspace
            )
        }

        // 4. VAULT SCREEN (SKELETON)
        composable<Route.Vault> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Phrase Vault Screen (Tempat Menyimpan Kosakata Penting)")
            }
        }

        // 5. INSIGHTS SCREEN (SKELETON)
        composable<Route.Insights> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Learning Insights Screen (Statistik & Grafik Belajar)")
            }
        }

        // 6. SETTINGS SCREEN (PROFIL, MODE, DAN SETTING SISTEM)
        composable<Route.Settings> {
            val userPreferences: UserPreferences = koinInject()
            val isDarkMode by userPreferences.isDarkMode.collectAsState(initial = false)
            val coroutineScope = rememberCoroutineScope()

            // State Komponen 1: Profil
            var profileName by remember { mutableStateOf("Ar'rauf Setiawan M. Jabar") }
            var profileEmail by remember { mutableStateOf("ar'rauf.123140032@student.itera.ac.id") }
            var profileNim by remember { mutableStateOf("123140032") }
            var isEditingProfile by remember { mutableStateOf(false) }

            // State Komponen 3: Pengaturan Tambahan
            var isNotificationEnabled by remember { mutableStateOf(true) }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Pengaturan",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // =========================================================
                // 👤 KOMPONEN 1: PROFIL (Bisa Edit Data Pengguna)
                // =========================================================
                item {
                    Text(text = "Akun & Profil", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (!isEditingProfile) {
                                // Mode Read (Tampilan Profil)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = profileName, style = MaterialTheme.typography.titleMedium)
                                        Text(text = profileEmail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                                        Text(text = "NIM: $profileNim", style = MaterialTheme.typography.bodySmall)
                                    }
                                    IconButton(onClick = { isEditingProfile = true }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Profil")
                                    }
                                }
                            } else {
                                // Mode Update (Formulir Pengubahan)
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
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = profileNim,
                                    onValueChange = { profileNim = it },
                                    label = { Text("NIM") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { isEditingProfile = false }) {
                                        Text("Simpan")
                                    }
                                    TextButton(onClick = { isEditingProfile = false }) {
                                        Text("Batal")
                                    }
                                }
                            }
                        }
                    }
                }

                // =========================================================
                // 🌓 KOMPONEN 2: MODE (Pengaturan Dark Mode)
                // =========================================================
                item {
                    Text(text = "Tampilan", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
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
                                    coroutineScope.launch {
                                        userPreferences.setDarkMode(checked)
                                    }
                                }
                            )
                        }
                    }
                }

                // =========================================================
                // ⚙️ KOMPONEN 3: SETTING (Notifikasi & Manajemen Data Terjemahan)
                // =========================================================
                item {
                    Text(text = "Aplikasi & Data", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {

                            // Opsi Notifikasi
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Icon(Icons.Default.Notifications, contentDescription = null)
                                    Text(text = "Notifikasi Pengingat", style = MaterialTheme.typography.bodyLarge)
                                }
                                Switch(
                                    checked = isNotificationEnabled,
                                    onCheckedChange = { isNotificationEnabled = it }
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            // Opsi Hapus Data Terjemahan
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
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
                                TextButton(onClick = { /* Menjalankan Aksi Clear Database */ }) {
                                    Text("Bersihkan", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// KUMPULAN FUNGSI NAVIGASI TYPE-SAFE
// ==========================================
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
        override fun navigateToAI(translationId: Long?, initialText: String?) {
            // Ditangani pada Sprint 3
        }
        override fun navigateToVault() { navController.navigate(Route.Vault) }
        override fun navigateToInsights() { navController.navigate(Route.Insights) }
        override fun navigateToSettings() { navController.navigate(Route.Settings) }
        override fun navigateBack() { navController.popBackStack() }
    }
}