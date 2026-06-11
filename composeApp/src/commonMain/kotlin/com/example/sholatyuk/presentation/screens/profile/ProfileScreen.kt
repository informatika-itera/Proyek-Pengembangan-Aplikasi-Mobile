package com.example.sholatyuk.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sholatyuk.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val userName by viewModel.userName.collectAsState()
    val userBio by viewModel.userBio.collectAsState()
    val isAdzanEnabled by viewModel.isAdzanEnabled.collectAsState()
    val isLightModeEnabled by viewModel.isLightModeEnabled.collectAsState()

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Profil & Pengaturan", 
                        color = if (isLightModeEnabled) Color.Black else TextWhite, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 18.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Kembali", 
                            tint = if (isLightModeEnabled) DeepBlue else AccentYellow
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 1. BAGIAN HEADER PROFIL
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(AccentYellow),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = userName.split(" ")
                        .take(2)
                        .mapNotNull { it.firstOrNull()?.uppercase() }
                        .joinToString("")
                        .takeIf { it.isNotEmpty() } ?: "U"

                    Text(text = initials, color = DeepBlue, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = userName,
                    onValueChange = { viewModel.updateUserName(it) },
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground, 
                        fontSize = 24.sp, 
                        fontWeight = FontWeight.Bold, 
                        textAlign = TextAlign.Center
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentYellow, 
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent, 
                        unfocusedContainerColor = Color.Transparent,
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = userBio,
                    onValueChange = { viewModel.updateUserBio(it) },
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), 
                        fontSize = 14.sp, 
                        textAlign = TextAlign.Center
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentYellow, 
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent, 
                        unfocusedContainerColor = Color.Transparent,
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. BAGIAN PENGATURAN UTAMA
            Text(
                text = "PENGATURAN", 
                color = if (isLightModeEnabled) DeepBlue else AccentYellow, 
                fontSize = 12.sp, 
                fontWeight = FontWeight.Bold, 
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(), 
                shape = RoundedCornerShape(16.dp), 
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isLightModeEnabled) 2.dp else 0.dp)
            ) {
                Column {
                    SettingItemSwitch(
                        icon = Icons.Default.NotificationsActive, title = "Notifikasi Adzan",
                        isChecked = isAdzanEnabled, onCheckedChange = { viewModel.updateAdzan(it) }
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), 
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    SettingItemSwitch(
                        icon = Icons.Default.LightMode, title = "Tema Terang (Light Mode)",
                        isChecked = isLightModeEnabled, onCheckedChange = { viewModel.updateLightMode(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. BAGIAN TENTANG APLIKASI
            Text(
                text = "INFORMASI", 
                color = if (isLightModeEnabled) DeepBlue else AccentYellow, 
                fontSize = 12.sp, 
                fontWeight = FontWeight.Bold, 
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(), 
                shape = RoundedCornerShape(16.dp), 
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isLightModeEnabled) 2.dp else 0.dp)
            ) {
                Column {
                    SettingItemText(icon = Icons.Default.Info, title = "Versi Aplikasi", value = "v1.0.0 (Sprint 3)")
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), 
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    SettingItemText(icon = Icons.Default.BugReport, title = "Laporkan Masalah", value = "")
                }
            }
        }
    }
}

@Composable
fun SettingItemText(icon: ImageVector, title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = if (MaterialTheme.colorScheme.background == Color(0xFFF5F5F5)) DeepBlue else AccentYellow, 
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp)
        }
        if (value.isNotEmpty()) {
            Text(text = value, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 14.sp)
        }
    }
}

@Composable
fun SettingItemSwitch(icon: ImageVector, title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = if (MaterialTheme.colorScheme.background == Color(0xFFF5F5F5)) DeepBlue else AccentYellow, 
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp)
        }
        Switch(
            checked = isChecked, onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White, 
                checkedTrackColor = if (MaterialTheme.colorScheme.background == Color(0xFFF5F5F5)) DeepBlue else AccentYellow,
                uncheckedThumbColor = Color.Gray, 
                uncheckedTrackColor = Color.DarkGray
            )
        )
    }
}