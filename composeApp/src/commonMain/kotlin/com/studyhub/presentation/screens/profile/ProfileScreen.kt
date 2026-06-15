package com.studyhub.presentation.screens.profile

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.studyhub.core.util.SystemAppearance
import com.studyhub.presentation.components.LoadingView
import com.studyhub.presentation.components.ErrorView
import com.studyhub.presentation.components.ScreenHeader
import com.studyhub.presentation.navigation.Screen
import com.studyhub.presentation.theme.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.painterResource
import studyhub.composeapp.generated.resources.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is ProfileUiState.Loading -> LoadingView()
        is ProfileUiState.Error -> ErrorView(
            message = state.message,
            onRetry = { /* Combine flows will trigger on state change */ }
        )
        is ProfileUiState.Success -> {
            SystemAppearance(isDarkMode = state.isDarkMode)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(Spacing.large)
            ) {
                // ── Header ──
                item {
                    ScreenHeader {
                        Column {
                            Text(
                                "Profile",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "Your study dashboard",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                // ── Profile Card ──
                item {
                    Box(modifier = Modifier.padding(horizontal = Spacing.normal)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(Spacing.normal)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.normal)
                                ) {
                                    Surface(
                                        modifier = Modifier.size(52.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.School,
                                                contentDescription = "Major Icon",
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            state.userName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            "${state.major} · Year 2",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            "Lv ${state.level}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            "Scholar",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(Modifier.height(Spacing.normal))

                                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                                    BadgeChip(
                                        text = "${state.dayStreak}-Day Streak",
                                        icon = "🔥",
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )

                                    BadgeChip(
                                        text = "${state.completionRate}% Rate",
                                        icon = "📈",
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Stats Grid ──
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = Spacing.normal),
                        verticalArrangement = Arrangement.spacedBy(Spacing.small)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                            StatCard(
                                modifier = Modifier.weight(1f),
                                label = "Total Tasks",
                                value = state.totalTasks.toString(),
                                icon = Icons.AutoMirrored.Filled.Assignment,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                iconColor = MaterialTheme.colorScheme.primary
                            )
                            StatCard(
                                modifier = Modifier.weight(1f),
                                label = "Completed",
                                value = state.doneTasks.toString(),
                                icon = Icons.Default.CheckCircle,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                iconColor = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                            StatCard(
                                modifier = Modifier.weight(1f),
                                label = "In Progress",
                                value = state.inProgressTasks.toString(),
                                icon = Icons.Default.TrendingUp,
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                iconColor = MaterialTheme.colorScheme.tertiary
                            )
                            StatCard(
                                modifier = Modifier.weight(1f),
                                label = "Study Hours",
                                value = "${state.totalStudyHours}h",
                                icon = Icons.Default.Schedule,
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                iconColor = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // ── Report Shortcut ──
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.normal),
                        onClick = { navController.navigate(Screen.Report.route) },
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(Spacing.normal),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                            ) {
                                Surface(
                                    modifier = Modifier.size(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Assessment, null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        "Laporan Belajar",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        "Lihat analisis lengkap belajarmu",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // ── Subject Breakdown ──
                item {
                    Column(modifier = Modifier.padding(horizontal = Spacing.normal)) {
                        Text(
                            "Subject Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Task distribution across subjects",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(Spacing.normal))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(Spacing.normal), verticalArrangement = Arrangement.spacedBy(Spacing.normal)) {
                                state.subjectBreakdown.forEach { stat ->
                                    key(stat.name) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                                            Box(
                                                Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primary)
                                            )
                                            Text(
                                                stat.name,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.width(80.dp),
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            LinearProgressIndicator(
                                                progress = { stat.count.toFloat() / state.totalTasks.coerceAtLeast(1) },
                                                modifier = Modifier.weight(1f).height(6.dp).clip(CircleShape),
                                                color = MaterialTheme.colorScheme.primary,
                                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            Text(
                                                stat.count.toString(),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                                if (state.subjectBreakdown.isEmpty()) {
                                    Text(
                                        "No subject data available",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Settings ──
                item {
                    Column(modifier = Modifier.padding(horizontal = Spacing.normal)) {
                        Text(
                            "Settings",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(Spacing.normal))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column {
                                SettingsToggleItem(
                                    title = "Dark Mode",
                                    icon = Icons.Default.DarkMode,
                                    checked = state.isDarkMode,
                                    onCheckedChange = { viewModel.toggleDarkMode() }
                                )

                                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.normal), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

                                SettingsToggleItem(
                                    title = "Notifikasi",
                                    subtitle = "Aktifkan pengingat tugas",
                                    icon = Icons.Default.Notifications,
                                    checked = state.notificationEnabled,
                                    onCheckedChange = { viewModel.toggleNotification(it) }
                                )

                                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.normal), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

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
                    Spacer(Modifier.height(Spacing.large))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(Res.drawable.logo_studyhub),
                            contentDescription = "StudyHub Logo",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )

                        Spacer(Modifier.height(Spacing.small))
                        Text(
                            "Study Hub v1.0.0",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Catat. Fokus. Raih Tujuan.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                    Spacer(Modifier.height(Spacing.large))
                }
            }
        }
    }
}

@Composable
fun BadgeChip(
    text: String,
    icon: String,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        shape = CircleShape,
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, contentColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 14.sp)
            Spacer(Modifier.width(4.dp))
            Text(
                text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
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
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = label, tint = iconColor, modifier = Modifier.size(18.dp))
            }
            Column {
                Text(
                    value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
            .padding(Spacing.normal),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.normal)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(MaterialTheme.shapes.small)
                .background(if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}
