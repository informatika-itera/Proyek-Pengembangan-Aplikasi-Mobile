package com.studyhub.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.studyhub.core.util.SystemAppearance
import com.studyhub.presentation.components.StudyHubHeader
import com.studyhub.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when (val state = uiState) {
            is ProfileUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is ProfileUiState.Error -> {
                Text(state.message, modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
            }
            is ProfileUiState.Success -> {
                SystemAppearance(isDarkMode = state.isDarkMode)
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // ── Header ──
                    item {
                        StudyHubHeader(
                            title = "Profile",
                            subtitle = {
                                Text("Your study dashboard", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // ── Profile Card ──
                    item {
                        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E0D4))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(52.dp),
                                            shape = RoundedCornerShape(14.dp),
                                            color = Color(0xFF5F5E5A)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.School, "Tingkat Pendidikan", tint = Color.White, modifier = Modifier.size(28.dp))
                                            }
                                        }
                                        
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                state.userName,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color(0xFF2C2416)
                                            )
                                            Text(
                                                "${state.major} · Year 2",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                                color = Color(0xFF888888)
                                            )
                                        }
                                        
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                "Lv ${state.level}",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                                                color = Color(0xFFB8860B)
                                            )
                                            Text(
                                                "Scholar",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF888888)
                                            )
                                        }
                                    }
                                    
                                    Spacer(Modifier.height(16.dp))
                                    
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFFFFF3E0),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD180))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("🔥", fontSize = 14.sp)
                                                Spacer(Modifier.width(4.dp))
                                                Text(
                                                    "7-Day Streak", 
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = Color(0xFFE65100)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Stats Grid ──
                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                StatCard(
                                    modifier = Modifier.weight(1f),
                                    label = "Total Tasks",
                                    value = state.totalTasks.toString(),
                                    icon = Icons.AutoMirrored.Filled.Assignment,
                                    color = Color(0xFFF1EBE0),
                                    iconColor = Color(0xFF8B7355)
                                )
                                StatCard(
                                    modifier = Modifier.weight(1f),
                                    label = "Completed",
                                    value = state.doneTasks.toString(),
                                    icon = Icons.Default.CheckCircle,
                                    color = Color(0xFFE1F5EE),
                                    iconColor = Color(0xFF10B981)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                StatCard(
                                    modifier = Modifier.weight(1f),
                                    label = "In Progress",
                                    value = state.inProgressTasks.toString(),
                                    icon = Icons.Default.TrendingUp,
                                    color = Color(0xFFE6F1FB),
                                    iconColor = Color(0xFF3B82F6)
                                )
                                StatCard(
                                    modifier = Modifier.weight(1f),
                                    label = "Study Hours",
                                    value = "${state.totalStudyHours}h",
                                    icon = Icons.Default.Schedule,
                                    color = Color(0xFFFAEEDA),
                                    iconColor = Color(0xFFFBBF24)
                                )
                            }
                        }
                    }

                    // ── Subject Breakdown ──
                    item {
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            Text("Subject Breakdown", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text("Task distribution across subjects", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = Color(0xFF888888))
                            Spacer(Modifier.height(16.dp))
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E0D4))
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    state.subjectBreakdown.forEach { stat ->
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                            Box(Modifier.size(10.dp).clip(CircleShape).background(stat.color))
                                            Text(stat.name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), modifier = Modifier.width(80.dp), color = Color(0xFF2C2416))
                                            LinearProgressIndicator(
                                                progress = { stat.count.toFloat() / state.totalTasks.coerceAtLeast(1) },
                                                modifier = Modifier.weight(1f).height(5.dp).clip(CircleShape),
                                                color = stat.color,
                                                trackColor = Color(0xFFF2EDE4)
                                            )
                                            Text(stat.count.toString(), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF2C2416))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Settings ──
                    item {
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            Text("Settings", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Spacer(Modifier.height(16.dp))
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E0D4))
                            ) {
                                Column {
                                    SettingsToggleItem(
                                        title = "Dark Mode",
                                        icon = Icons.Default.DarkMode,
                                        checked = state.isDarkMode,
                                        onCheckedChange = { viewModel.toggleDarkMode() }
                                    )
                                    
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color(0xFFE8E0D4))
                                    
                                    SettingsToggleItem(
                                        title = "Notifikasi",
                                        subtitle = "Aktifkan pengingat tugas",
                                        icon = Icons.Default.Notifications,
                                        checked = state.notificationEnabled,
                                        onCheckedChange = { viewModel.toggleNotification(it) }
                                    )

                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color(0xFFE8E0D4))

                                    SettingsToggleItem(
                                        title = "Smart Reminder AI",
                                        subtitle = if (state.notificationEnabled) "Waktu reminder adaptif oleh AI" else "Aktifkan notifikasi terlebih dahulu",
                                        icon = Icons.Default.AutoAwesome,
                                        checked = state.isAiReminderEnabled && state.notificationEnabled,
                                        onCheckedChange = { if (state.notificationEnabled) viewModel.toggleAiReminder(it) },
                                        enabled = state.notificationEnabled
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Study Hub v1.0.0",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF888888)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    iconColor: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E0D4)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, label, tint = iconColor, modifier = Modifier.size(18.dp))
            }
            Column {
                Text(value, style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp), fontWeight = FontWeight.Bold, color = Color(0xFF2C2416))
                Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color(0xFF888888))
            }
        }
    }
}

@Composable
fun SettingsToggleItem(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(if (enabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, title, tint = if (enabled) MaterialTheme.colorScheme.primary else Color.Gray, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = if (enabled) Color.Unspecified else Color.Gray)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
