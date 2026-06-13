package com.example.masakuy.presentation.screens.profile

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masakuy.theme.OrangeMain

@Composable
fun ProfileScreen(
    isDarkMode: Boolean = false,
    onDarkModeToggle: (Boolean) -> Unit = {}
) {
    var notificationEnabled by remember { mutableStateOf(true) }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            Text(
                "Pengaturan",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(20.dp))

            // Profile card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("\uD83D\uDC68\u200D\uD83C\uDF73", fontSize = 32.sp)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Pengguna Masakuy", fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface)
                        Text("user@masakuy.id", fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tampilan card
            SettingCard {
                SettingIconRow(emoji = "\uD83C\uDF19",
                    title = "Mode Gelap",
                    trailing = {
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = onDarkModeToggle,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = OrangeMain
                            )
                        )
                    }
                )
                SettingDivider()
                SettingIconRow(emoji = "\uD83D\uDD14",
                    title = "Notifikasi Harian",
                    trailing = {
                        Switch(
                            checked = notificationEnabled,
                            onCheckedChange = { notificationEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = OrangeMain
                            )
                        )
                    }
                )
            }

            Spacer(Modifier.height(12.dp))

            // Offline & cache card
            SettingCard {
                SettingIconRow(emoji = "\uD83D\uDCBE",
                    title = "Cache Rekomendasi",
                    trailing = {
                        Surface(shape = RoundedCornerShape(20.dp),
                            color = OrangeMain.copy(alpha = 0.12f)) {
                            Text("Aktif", fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold, color = OrangeMain,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                        }
                    }
                )
                SettingDivider()
                SettingIconRow(emoji = "\u2764\uFE0F",
                    title = "Favorit tersedia offline",
                    trailing = {
                        Surface(shape = RoundedCornerShape(20.dp),
                            color = OrangeMain.copy(alpha = 0.12f)) {
                            Text("Aktif", fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold, color = OrangeMain,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                        }
                    }
                )
            }

            Spacer(Modifier.height(12.dp))

            // Tentang app card
            SettingCard {
                SettingInfoRow(emoji = "\uD83C\uDF73",
                    label = "Nama Aplikasi", value = "MasakuY")
                SettingDivider()
                SettingInfoRow(emoji = "\uD83C\uDFF7\uFE0F",
                    label = "Versi", value = "1.0.0")
                SettingDivider()
                SettingInfoRow(emoji = "\uD83C\uDF93",
                    label = "Dikembangkan oleh", value = "ITERA 2025/2026")
                SettingDivider()
                SettingInfoRow(emoji = "\uD83E\uDD16",
                    label = "AI Engine", value = "Gemini AI")
            }

            Spacer(Modifier.height(24.dp))

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) { Column(content = content) }
}

@Composable
private fun SettingDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
    )
}

@Composable
private fun SettingIconRow(
    emoji: String,
    title: String, trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 22.sp)
        Spacer(Modifier.width(12.dp))
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        trailing()
    }
}

@Composable
private fun SettingInfoRow(
    emoji: String,
    label: String, value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 22.sp)
        Spacer(Modifier.width(12.dp))
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface)
    }
}
