package com.studyhub.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DarkModeToggleItem(
    isDarkMode: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier,
        headlineContent = {
            Text(
                if (isDarkMode) "Mode Gelap" else "Mode Terang",
                style = MaterialTheme.typography.titleSmall
            )
        },
        supportingContent = {
            Text(
                if (isDarkMode) "Tampilan gelap aktif"
                else "Tampilan terang aktif",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingContent = {
            AnimatedContent(
                targetState = isDarkMode,
                transitionSpec = {
                    (scaleIn() + fadeIn()).togetherWith(scaleOut() + fadeOut())
                },
                label = "dark_mode_icon_anim"
            ) { dark ->
                Icon(
                    imageVector = if (dark) Icons.Default.DarkMode
                                  else Icons.Default.LightMode,
                    contentDescription = null,
                    tint = if (dark) MaterialTheme.colorScheme.primary
                           else Color(0xFFF59E0B),
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        trailingContent = {
            Switch(
                checked = isDarkMode,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    )
}
