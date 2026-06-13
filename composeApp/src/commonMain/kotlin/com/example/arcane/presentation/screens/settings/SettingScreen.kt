package com.example.arcane.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import androidx.compose.runtime.rememberCoroutineScope
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.SelectionMode
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val favoriteGenre by viewModel.favoriteGenre.collectAsStateWithLifecycle()
    val readingGoal by viewModel.readingGoal.collectAsStateWithLifecycle()
    val profilePhotoPath by viewModel.profilePhotoPath.collectAsStateWithLifecycle()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showPhotoOptions by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val imagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = coroutineScope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let { viewModel.updateProfilePhoto(it) }
        }
    )

    // Bottom sheet pilihan foto
    if (showPhotoOptions) {
        ModalBottomSheet(
            onDismissRequest = { showPhotoOptions = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Foto Profil",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                ListItem(
                    headlineContent = { Text("Ambil dari Kamera") },
                    leadingContent = {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                    },
                    modifier = Modifier.clickable {
                        showPhotoOptions = false
                        imagePicker.launch()
                    }
                )
                ListItem(
                    headlineContent = { Text("Pilih dari Galeri") },
                    leadingContent = {
                        Icon(Icons.Default.Photo, contentDescription = null)
                    },
                    modifier = Modifier.clickable {
                        showPhotoOptions = false
                        imagePicker.launch()
                    }
                )
                if (profilePhotoPath.isNotBlank()) {
                    ListItem(
                        headlineContent = {
                            Text("Hapus Foto", color = MaterialTheme.colorScheme.error)
                        },
                        leadingContent = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        modifier = Modifier.clickable {
                            viewModel.clearProfilePhoto()
                            showPhotoOptions = false
                        }
                    )
                }
            }
        }
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            initialName = userName,
            initialGenre = favoriteGenre,
            initialGoal = readingGoal.toString(),
            onConfirm = { name, genre, goal ->
                viewModel.updateUserName(name)
                viewModel.updateFavoriteGenre(genre)
                goal.toIntOrNull()?.let { viewModel.updateReadingGoal(it) }
                showEditProfileDialog = false
            },
            onDismiss = { showEditProfileDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Pengaturan",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader(
                userName = userName.ifBlank { "Pembaca Arcane" },
                favoriteGenre = favoriteGenre.ifBlank { "Belum diatur" },
                profilePhotoPath = profilePhotoPath,
                onEditClick = { showEditProfileDialog = true },
                onPhotoClick = { showPhotoOptions = true }
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsGroup(title = "Profil") {
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Nama",
                    subtitle = userName.ifBlank { "Belum diatur" },
                    onClick = { showEditProfileDialog = true }
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                SettingsItem(
                    icon = Icons.Default.Category,
                    title = "Genre Favorit",
                    subtitle = favoriteGenre.ifBlank { "Belum diatur" },
                    onClick = { showEditProfileDialog = true }
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                SettingsItem(
                    icon = Icons.Default.Flag,
                    title = "Target Buku per Tahun",
                    subtitle = "$readingGoal buku",
                    onClick = { showEditProfileDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SettingsGroup(title = "Tampilan") {
                SettingsSwitchItem(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Mode",
                    subtitle = if (isDarkMode) "Aktif" else "Nonaktif",
                    checked = isDarkMode,
                    onCheckedChange = { viewModel.toggleDarkMode(it) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SettingsGroup(title = "Tentang") {
                SettingsItem(
                    icon = Icons.Default.Book,
                    title = "Aplikasi",
                    subtitle = "Arcane v1.0.0",
                    showArrow = false
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Pengembang",
                    subtitle = "B Square — ITERA 2026",
                    showArrow = false
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileHeader(
    userName: String,
    favoriteGenre: String,
    profilePhotoPath: String,
    onEditClick: () -> Unit,
    onPhotoClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { onPhotoClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (profilePhotoPath.isNotBlank()) {
                        AsyncImage(
                            model = profilePhotoPath,
                            contentDescription = "Foto Profil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = userName.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onPhotoClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Foto",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = userName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Genre favorit: $favoriteGenre",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            TextButton(onClick = onEditClick) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text("Edit Profil")
            }
        }
    }
}

@Composable
private fun EditProfileDialog(
    initialName: String,
    initialGenre: String,
    initialGoal: String,
    onConfirm: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var genre by remember { mutableStateOf(initialGenre) }
    var goal by remember { mutableStateOf(initialGoal) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Profil", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre Favorit") },
                    placeholder = { Text("Contoh: Fiction, Science") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = goal,
                    onValueChange = { goal = it },
                    label = { Text("Target Buku per Tahun") },
                    placeholder = { Text("Contoh: 12") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, genre, goal) }) {
                Text("Simpan", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
private fun SettingsGroup(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) { content() }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {},
    showArrow: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = showArrow) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
        if (showArrow) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}