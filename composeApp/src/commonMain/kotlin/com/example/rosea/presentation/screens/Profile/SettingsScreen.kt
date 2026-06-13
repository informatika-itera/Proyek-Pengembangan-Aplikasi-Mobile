package com.example.rosea.presentation.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAccountInfo: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToTerms: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val scrollState = rememberScrollState()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val isPushEnabled by viewModel.isPushNotificationsEnabled.collectAsStateWithLifecycle()
    val isEmailEnabled by viewModel.isEmailNotificationsEnabled.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.language.collectAsStateWithLifecycle()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showLanguageDialog by remember { mutableStateOf(false) }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Language") },
            text = {
                Column {
                    val languages = listOf("English", "Indonesia")
                    languages.forEach { language ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setLanguage(language)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (language == currentLanguage),
                                onClick = {
                                    viewModel.setLanguage(language)
                                    showLanguageDialog = false
                                }
                            )
                            Text(text = language, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsGroup(title = "Account") {
                SettingsItem(
                    icon = Icons.Outlined.Person, 
                    label = "Account Information",
                    onClick = onNavigateToAccountInfo
                )
                SettingsItem(
                    icon = Icons.Outlined.Lock, 
                    label = "Password & Security",
                    onClick = onNavigateToSecurity
                )
                SettingsItem(
                    icon = Icons.Outlined.Language, 
                    label = "Language", 
                    value = currentLanguage,
                    onClick = { showLanguageDialog = true }
                )
            }

            SettingsGroup(title = "Notification") {
                SettingsSwitchItem(
                    icon = Icons.Outlined.Notifications, 
                    label = "Push Notifications", 
                    checked = isPushEnabled, 
                    onCheckedChange = { viewModel.setPushNotificationsEnabled(it) }
                )
                SettingsSwitchItem(
                    icon = Icons.Outlined.Email, 
                    label = "Email Notifications", 
                    checked = isEmailEnabled, 
                    onCheckedChange = { viewModel.setEmailNotificationsEnabled(it) }
                )
            }

            SettingsGroup(title = "General") {
                SettingsSwitchItem(
                    icon = Icons.Outlined.DarkMode, 
                    label = "Dark Mode", 
                    checked = isDarkMode, 
                    onCheckedChange = { viewModel.setDarkMode(it) }
                )
                SettingsItem(
                    icon = Icons.Outlined.Info, 
                    label = "About ROSÉA",
                    onClick = onNavigateToAbout
                )
                SettingsItem(
                    icon = Icons.Outlined.Description, 
                    label = "Terms of Service",
                    onClick = onNavigateToTerms
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        )
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp,
            tonalElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(
                width = 0.5.dp, 
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector, 
    label: String, 
    value: String? = null,
    onClick: () -> Unit = {}
) {
    ListItem(
        headlineContent = { 
            Text(
                label, 
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            ) 
        },
        leadingContent = { 
            Icon(
                icon, 
                null, 
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            ) 
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (value != null) {
                    Text(
                        value, 
                        style = MaterialTheme.typography.bodyMedium, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight, 
                    null, 
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).padding(horizontal = 4.dp).clickable { onClick() }
    )
}

@Composable
private fun SettingsSwitchItem(
    icon: ImageVector, 
    label: String, 
    checked: Boolean, 
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { 
            Text(
                label, 
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            ) 
        },
        leadingContent = { 
            Icon(
                icon, 
                null, 
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            ) 
        },
        trailingContent = {
            Switch(
                checked = checked, 
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).padding(horizontal = 4.dp)
    )
}
