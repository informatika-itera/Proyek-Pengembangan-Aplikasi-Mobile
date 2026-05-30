package com.example.rewind.presentation.screens.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var showNameDialog by remember { mutableStateOf(false) }
    var showBioDialog by remember { mutableStateOf(false) }

    if (showNameDialog) {
        EditTextDialog(
            title = "Nama Pengguna",
            initialValue = uiState.userName,
            onConfirm = {
                viewModel.setUserName(it)
                showNameDialog = false
            },
            onDismiss = { showNameDialog = false }
        )
    }

    if (showBioDialog) {
        EditTextDialog(
            title = "Bio",
            initialValue = uiState.userBio,
            onConfirm = {
                viewModel.setUserBio(it)
                showBioDialog = false
            },
            onDismiss = { showBioDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.8f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                    )
                    IconButton(
                        onClick = onNavigateBack,
                        interactionSource = interactionSource,
                        modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            val infiniteTransition = rememberInfiniteTransition()
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.graphicsLayer(alpha = alpha))
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                SettingsSectionHeader("Profil")
            }
            item {
                SettingsClickableItem(
                    icon = Icons.Default.Person,
                    title = "Nama Pengguna",
                    subtitle = uiState.userName.ifEmpty { "Belum diisi" },
                    onClick = { showNameDialog = true }
                )
            }
            item {
                SettingsClickableItem(
                    icon = Icons.Default.Info,
                    title = "Bio",
                    subtitle = uiState.userBio.ifEmpty { "Belum diisi" },
                    onClick = { showBioDialog = true }
                )
            }

            item { Spacer(Modifier.height(8.dp)) }
            item { SettingsSectionHeader("Tampilan") }
            item {
                SettingsToggleItem(
                    icon = if (uiState.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                    title = "Mode Gelap",
                    subtitle = if (uiState.isDarkMode) "Aktif" else "Nonaktif",
                    checked = uiState.isDarkMode,
                    onCheckedChange = { viewModel.toggleDarkMode() }
                )
            }

            item { Spacer(Modifier.height(8.dp)) }
            item { SettingsSectionHeader("Preferensi") }
            item {
                SortBySelector(
                    currentSort = uiState.sortBy,
                    onSortChange = { viewModel.setSortBy(it) }
                )
            }

            item { Spacer(Modifier.height(8.dp)) }
            item { SettingsSectionHeader("Tentang") }
            item {
                SettingsInfoItem(
                    icon = Icons.Default.Movie,
                    title = "Rewind",
                    subtitle = "Film & Series Personal Tracker v1.0.0"
                )
            }
            item {
                SettingsInfoItem(
                    icon = Icons.Default.School,
                    title = "Mata Kuliah",
                    subtitle = "Pengembangan Aplikasi Mobile — ITERA 2025/2026"
                )
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsClickableItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SettingsInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortBySelector(currentSort: String, onSortChange: (String) -> Unit) {
    val options = listOf(
        "UPDATED_DESC" to "Terakhir Diupdate",
        "CREATED_DESC" to "Terbaru Ditambahkan",
        "TITLE_ASC" to "Judul (A-Z)",
        "RATING_DESC" to "Rating (Tertinggi)"
    )

    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.find { it.first == currentSort }?.second ?: "Terakhir Diupdate"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Sort, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Urutan Default", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    Text(selectedLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.8f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                )
                IconButton(
                    onClick = { expanded = true },
                    interactionSource = interactionSource,
                    modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { (key, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            onSortChange(key)
                            expanded = false
                        },
                        leadingIcon = {
                            if (key == currentSort) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EditTextDialog(
    title: String,
    initialValue: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text.trim()) }) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}