package com.example.travelplanner.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelplanner.core.util.LocalStrings
import com.example.travelplanner.presentation.screens.home.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToTrips: () -> Unit = {},
    onNavigateToExpenses: () -> Unit = {},
    onNavigateToHelp: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val s = LocalStrings.current

    LaunchedEffect(Unit) {
        viewModel.loadRecentTrips()
        viewModel.loadUserProfile()
    }

    val tripCount = uiState.recentTrips.size
    val destinationCount = uiState.recentTrips.map { it.destination }.distinct().size
    val countryCount = remember(uiState.recentTrips) {
        if (uiState.recentTrips.isEmpty()) 0
        else uiState.recentTrips.map { getCountryForDestination(it.destination) }.distinct().size
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var editNameInput by remember { mutableStateOf("") }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = if (s.isEnglish) "Edit Profile Name" else "Ubah Nama Profil",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editNameInput,
                        onValueChange = { editNameInput = it },
                        label = { Text(if (s.isEnglish) "Name" else "Nama") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editNameInput.isNotBlank()) {
                            viewModel.updateProfile(editNameInput.trim())
                            showEditDialog = false
                        }
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (s.isEnglish) "Save" else "Simpan")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditDialog = false }
                ) {
                    Text(if (s.isEnglish) "Cancel" else "Batal")
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(s.profileTitle, fontWeight = FontWeight.SemiBold, letterSpacing = 0.3.sp)
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = s.settingsTitle,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── HERO PROFILE CARD ──────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                onClick = {
                    editNameInput = uiState.userProfile.name
                    showEditDialog = true
                }
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.size(80.dp).clip(CircleShape)
                            .background(Brush.linearGradient(
                                listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            )),
                        contentAlignment = Alignment.Center
                    ) {
                        val initials = remember(uiState.userProfile.name) {
                            val parts = uiState.userProfile.name.trim().split(" ")
                            if (parts.size >= 2) {
                                (parts[0].take(1) + parts[1].take(1)).uppercase()
                            } else if (parts.isNotEmpty() && parts[0].isNotBlank()) {
                                parts[0].take(2).uppercase()
                            } else {
                                "TF"
                            }
                        }
                        Text(initials, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                uiState.userProfile.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = if (s.isEnglish) "Edit Name" else "Ubah Nama",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                        Surface(shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.secondaryContainer) {
                            Text(s.premiumTraveler,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                        }
                    }
                }
            }

            // ── STATS ROW ──────────────────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ProfileStatCard("$tripCount",         s.statTrips,     Icons.Default.Map,        Modifier.weight(1f))
                ProfileStatCard("$destinationCount",  s.statCities,    Icons.Default.LocationOn, Modifier.weight(1f))
                ProfileStatCard("$countryCount",      s.statCountries, Icons.Default.Public,     Modifier.weight(1f))
            }

            // ── MENU PROFIL ────────────────────────────────────────────
            Text(s.menuAccountActivity, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.2.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp))

            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp) {
                Column {
                    ProfileMenuItem(icon = Icons.Default.FavoriteBorder, label = s.menuFavorites,
                        subtitle = s.menuFavoritesSub, onClick = onNavigateToTrips)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                    ProfileMenuItem(icon = Icons.Default.History, label = s.menuHistory,
                        subtitle = s.menuHistorySub, onClick = onNavigateToTrips)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                    ProfileMenuItem(icon = Icons.Default.AccountBalanceWallet, label = s.menuFinanceSummary,
                        subtitle = s.menuFinanceSummarySub, onClick = onNavigateToExpenses)
                }
            }

            // ── MENU LAINNYA ───────────────────────────────────────────
            Text(s.menuOthers, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.2.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp))

            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Default.Help,
                        label = s.menuHelp,
                        subtitle = s.menuHelpSub,
                        onClick = onNavigateToHelp
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                    ProfileMenuItem(
                        icon = Icons.Default.Info,
                        label = s.menuAbout,
                        subtitle = s.menuAboutSub,
                        onClick = onNavigateToAbout
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(s.appVersion,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth(), lineHeight = 16.sp)
            Spacer(Modifier.height(80.dp))
        }
    }
}

// ── STAT CARD ──────────────────────────────────────────────────────────

@Composable
private fun ProfileStatCard(value: String, label: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer) {
        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp))
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
        }
    }
}

// ── MENU ITEM ──────────────────────────────────────────────────────────

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxWidth().padding(0.dp),
        color = Color.Transparent,
        onClick = onClick) {
        Row(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Surface(shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(18.dp))
        }
    }
}

private fun getCountryForDestination(destination: String): String {
    val q = destination.lowercase().trim()
    return when {
        q.contains("singapore") || q.contains("singapura") -> "Singapore"
        q.contains("kuala lumpur") || q.contains("malaysia") -> "Malaysia"
        q.contains("phuket") || q.contains("bangkok") || q.contains("thailand") -> "Thailand"
        q.contains("tokyo") || q.contains("japan") || q.contains("jepang") -> "Japan"
        q.contains("seoul") || q.contains("korea") -> "South Korea"
        q.contains("london") || q.contains("uk") || q.contains("inggris") || q.contains("united kingdom") -> "United Kingdom"
        q.contains("paris") || q.contains("prancis") || q.contains("france") -> "France"
        q.contains("new york") || q.contains("usa") || q.contains("amerika") || q.contains("united states") -> "United States"
        q.contains("dubai") || q.contains("uea") || q.contains("uae") || q.contains("arab") -> "United Arab Emirates"
        else -> "Indonesia"
    }
}

